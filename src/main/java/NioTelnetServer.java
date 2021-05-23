import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class NioTelnetServer {
    public static final String LS_COMMAND = "\tls   view all files and directories\n\r";
    public static final String MKDIR_COMMAND = "\tmkdir   create directory\n\r";
    public static final String CHANGE_NICKNAME_COMMAND = "\tnick   change nickname \n\r";

    private final ByteBuffer buffer = ByteBuffer.allocate(512);

    public NioTelnetServer() throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(5678));
        server.configureBlocking(false);
        // OP_ACCEPT, OP_READ, OP_WRITE

        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started");

        while (server.isOpen()){
            selector.select();

            var selectionKeys = selector.selectedKeys();
            var iterator = selectionKeys.iterator();

            while (iterator.hasNext()){
                var key = iterator.next();
                if(key.isAcceptable()){
                    handleAccept(key, selector);
                } else if(key.isReadable()){
                    handleRead(key, selector);
                }
                iterator.remove();
            }
        }
    }

    private void handleRead(SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = ((SocketChannel) key.channel());
        SocketAddress client = channel.getRemoteAddress();
        int readBytes = channel.read(buffer);
        if(readBytes < 0){
            channel.close();
            return;
        } else if (readBytes == 0){
            return;
        }

        buffer.flip();
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()){
            sb.append((char) buffer.get());
        }

        buffer.clear();

        //TODO
        //touch [filename] - создание файла
        // mkdir [dirname] - создание директории
        // cd [path] - перемещение по каталогу (.. и ~)
        // rm [filename | dirname] - удаление файла или папки
        // copy [src] [target] - копирование файла или папки
        // cut [filename] - просмотр содержимого
        // вывод nickname в начале строки
        // вывод текущей директории

        if(key.isValid()){
            String command = sb.toString().replace("\n", "").replace("\r", ""); //тут для моей системы вохможно другие символы
//            String command = sb.toString(); //тут для моей системы вохможно другие символы
            System.out.println(command);
            if("--help".equals(command)){
                sendMessage(LS_COMMAND, selector, client);
                sendMessage(MKDIR_COMMAND, selector, client);
                sendMessage(CHANGE_NICKNAME_COMMAND, selector, client);
            }
            if ("ls".equals(command)){
                sendMessage(getFileList().concat("\n"), selector, client);
            }


            if ("exit".equals(command)){
                System.out.println("Client logged out. IP: " + channel.getRemoteAddress());
            }
            else{
                sendMessage("Unrecognized command", selector, client);
            }
        }
    }

    private String getFileList() {
        return String.join(" ", new File("server").list());
    }

    private void sendMessage(String message, Selector selector, SocketAddress client) throws IOException {
        for(SelectionKey key : selector.keys()){
            if(key.isValid() && key.channel() instanceof SocketChannel){
                SocketChannel clientChannel = ((SocketChannel) key.channel());

                if(clientChannel.getRemoteAddress().equals(client)){
                    (clientChannel).write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
                }
            }
        }
    }

    private void handleAccept(SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();

        channel.configureBlocking(false);
        System.out.println("Client accepted. IP: " + channel.getRemoteAddress());

        channel.register(selector, SelectionKey.OP_READ, "some attach");

        channel.write(ByteBuffer.wrap("Hello user!\n\r".getBytes(StandardCharsets.UTF_8)));
        channel.write(ByteBuffer.wrap("Enter '--help' for support info!\n\r".getBytes(StandardCharsets.UTF_8)));

    }

    public static void main(String[] args) throws IOException {
        new NioTelnetServer();
    }
}
