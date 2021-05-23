import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

public class NioTelnetServer {
    public static final String LS_COMMAND = "\tls   view all files and directories\n\r";
    public static final String MKDIR_COMMAND = "\tmkdir   create directory\n\r";
    public static final String CHANGE_NICKNAME_COMMAND = "\tnick   change nickname \n\r";
    public static Path currentDir;
    public static Path rootDir;

    private final ByteBuffer buffer = ByteBuffer.allocate(512);
    private static ByteBuffer ReadFileBuffer = ByteBuffer.allocate(1024);

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
            System.out.println(command);

            String[] commands = command.split(" ");

            if("--help".equals(commands[0])){
                sendMessage(LS_COMMAND, selector, client);
                sendMessage(MKDIR_COMMAND, selector, client);
                sendMessage(CHANGE_NICKNAME_COMMAND, selector, client);
            }
            if ("ls".equals(commands[0])){
                sendMessage(getFileList().concat("\n"), selector, client);
            }
            if("touch".equals(commands[0])){
                Files.createFile(Path.of(currentDir.toString(), commands[1]));
                sendMessage("File is created: " + commands[1] + "\r\n" + currentDir + ": ", selector, client);
            }
            if("mkdir".equals(commands[0])){
                try {
                    Files.createDirectory(Path.of(currentDir.toString(), commands[1]));
                    sendMessage("Directory is created: " + commands[1] + "\r\n" + currentDir + ": ", selector, client);

                } catch (FileAlreadyExistsException e) {
                    sendMessage("Directory is already created: " + commands[1]+"\r\n" + currentDir + ": " , selector, client);
                }
            }
//            else{
//                sendMessage("Unrecognized command", selector, client);
//            }
            if(("cd".equals(commands[0]))){
                if("..".equals(commands[1])){
                    dirStepUp();
                    sendMessage(currentDir.toString() + ": " , selector, client);
                }else if("~".equals(commands[1])){
                    dirStepToRoot();
                    sendMessage(currentDir.toString() + ": " , selector, client);
                }else{
                    changeDir(commands[1], selector, client);
                }
            }
            if("rm".equals(commands[0])){
                if(Files.isDirectory(Path.of(currentDir.toString(), commands[1]))){
                    deleteDirectory((Path.of(currentDir.toString(), commands[1])).toString());
                    sendMessage("Directory is deleted: " + commands[1] + "\r\n" + currentDir + ": ", selector, client);
                }else
                Files.delete(Path.of(currentDir.toString(), commands[1]));
                sendMessage("File is deleted: " + commands[1] + "\r\n" + currentDir + ": ", selector, client);
            }
            if("cut".equals(commands[0])){
                byte[] bytes = Files.readAllBytes(Path.of(currentDir.toString(), commands[1]));

                StringBuilder file_content = new StringBuilder();
                for (byte b : bytes){
                    file_content.append((char) b);
                }
                System.out.print(file_content);
                sendMessage(file_content.toString(), selector, client);
            }




            if ("exit".equals(commands[0])){
                System.out.println("Client logged out. IP: " + channel.getRemoteAddress());
            }

        }
    }

    private void changeDir(String newDir, Selector selector, SocketAddress client ) throws IOException {
        if(Files.exists(Path.of(currentDir.toString(), newDir))){
            currentDir = Path.of(currentDir.toString(), newDir);
            sendMessage(currentDir.toString() + ": " , selector, client);
        }else{
            sendMessage("Wrong directory\r\n"  , selector, client);
        }
    }

    public static void deleteDirectory(String filePath)throws IOException {

        Path path = Path.of(filePath);

        Files.walkFileTree(path,
                new SimpleFileVisitor<>() {

                    // delete directories or folders
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir,
                                                              IOException exc)
                            throws IOException {
                        Files.delete(dir);
                        System.out.printf("Directory is deleted : %s%n", dir);
                        return FileVisitResult.CONTINUE;
                    }

                    // delete files
                    @Override
                    public FileVisitResult visitFile(Path file,
                                                     BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        System.out.printf("File is deleted : %s%n", file);
                        return FileVisitResult.CONTINUE;
                    }
                }
        );

    }

    private static void dirStepUp() {
        if(!currentDir.equals(rootDir)) {
            currentDir = currentDir.getParent();
        }
    }
    private static void dirStepToRoot() {
        currentDir = rootDir;
    }


    private String getFileList() {
        return String.join(" ", new File("server").list());
    }

    private void sendMessage(String message, Selector selector, SocketAddress client) throws IOException {
        for(SelectionKey key : selector.keys()){
            if(key.isValid() && key.channel() instanceof SocketChannel){
                SocketChannel clientChannel = ((SocketChannel) key.channel());

                if(clientChannel.getRemoteAddress().equals(client)){
                    (clientChannel).write(ByteBuffer.wrap((message + "\r\n").getBytes(StandardCharsets.UTF_8)));
                }
            }
        }
    }

    private void handleAccept(SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();

        channel.configureBlocking(false);
        System.out.println("Client accepted. IP: " + channel.getRemoteAddress());
        channel.register(selector, SelectionKey.OP_READ, "some attach");

        rootDir = Path.of("Server", "User's dir");
        currentDir = Path.of("Server", "User's dir");
        if(!Files.exists(currentDir)){
            Files.createDirectory(currentDir);
        }

        channel.write(ByteBuffer.wrap("Hello user!\n\r".getBytes(StandardCharsets.UTF_8)));
        channel.write(ByteBuffer.wrap(("Your current directory on Server is: " + currentDir.toString() + "\r\n").getBytes(StandardCharsets.UTF_8)));
        channel.write(ByteBuffer.wrap("Enter '--help' for support info!\n\r".getBytes(StandardCharsets.UTF_8)));
        channel.write(ByteBuffer.wrap((currentDir.toString() + ": ").getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) throws IOException {
        new NioTelnetServer();
    }
}
