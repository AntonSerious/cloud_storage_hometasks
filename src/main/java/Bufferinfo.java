import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Bufferinfo {
    public static void main(String[] args) throws IOException {
        FileChannel channel = new RandomAccessFile("server" + File.separator + "test-file.txt", "rw").getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(100);
        channel.read(buffer);
        buffer.flip();

//        while(buffer.hasRemaining()){
//            System.out.print((char) buffer.get());
//        }
//        System.out.println();
//
//        System.out.println(buffer);
//        byte[] byteBuf = new byte[100];
//        int pos = 0;
//        while (buffer.hasRemaining()){
//            byteBuf[pos++] = buffer.get();
//        }
//        System.out.println(new String(byteBuf, StandardCharsets.UTF_8));

        System.out.println("BUFFER SAMPLE");
        ByteBuffer b1 = ByteBuffer.allocate(5);
        System.out.println(b1);
        b1.put((byte) 1);
        b1.put((byte) 2);
        b1.put((byte) 3);

        System.out.println(b1);
        b1.flip();
        System.out.println(b1);


        System.out.println(b1);

        b1.position(0);
        b1.limit(b1.capacity());
        b1.put((byte) 4);
        b1.put((byte) 5);
        b1.put((byte) 6);
        b1.put((byte) 7);
        b1.flip();
        while(b1.hasRemaining()){
            System.out.println(b1.get());
        }

    }
}
