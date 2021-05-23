import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;


public class IOUUtils {
    public static void main(String[] args) throws IOException, InterruptedException {
        Path testPath = Path.of("server", "dir1", "dir3", "1.txt");
        Path serverPath = Path.of("server");
//
//        Path path1 = Path.of("server", "dir1", "dir3", "test-file.txt");
        Path path2 = Path.of("server", "dir1", "dir3", "test-file2.txt");
//
//        System.out.println(path1);
//        System.out.println(path2);
//
//        String root = "client";
//        Path dirPath = Path.of(root, "1.txt");
//
//        testPath.toAbsolutePath().iterator().forEachRemaining(System.out::println);
//        WatchService service = FileSystems.getDefault().newWatchService();
//        testPath.register(service, StandardWatchEventKinds.ENTRY_CREATE,
//                StandardWatchEventKinds.ENTRY_DELETE,
//                StandardWatchEventKinds.ENTRY_MODIFY
//        );
//        WatchKey key;
//        String notification = "Event type: %s. File: %s";
//        while ((key = service.take()) != null){
//            for (WatchEvent event : key.pollEvents()){
//                System.out.printf(notification, event.kind(), event.context() + "\n");
//            }
//            key.reset();
//        }
//        new Thread(() -> {
//            String notification = "Event type: %s. File: %s";
//            while(true){
//                try{
//                    var key= service.take();
//                    if (key.isValid()){
//                        var events = key.pollEvents();
//                        for(WatchEvent<?> event : events){
//                            System.out.printf(notification, event.kind(), event.context()+"\n");
//                        }
//                        key.reset();
//                    }
//                } catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        System.out.println("1.txt exists " + Files.exists(testPath));
//        System.out.println("5.txt exists " + Files.exists(testPath));
//
//        Path newPath = Path.of("server", "dir1", "dir3", "newfile-1.txt");
//        if(!Files.exists(newPath)){
//            Files.createFile(newPath);
//        }
//
//        Files.writeString(newPath, "NEW String", StandardOpenOption.WRITE);
//        Files.writeString(newPath, "NEW String2", StandardOpenOption.APPEND);
//        Files.writeString(path2, "NEW String2", StandardOpenOption.CREATE);
//
//        Files.delete(path2);
//
//        Files.createDirectories(Path.of("server", "dir4", "dir5", "dir6" ));
//        Files.walkFileTree(Path.of("server", "dir4", "dir5"), new FileVisitor<Path>() {
//            @Override
//            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//                System.out.println("pre - " + dir.getFileName());
//                return FileVisitResult.CONTINUE;
//            }
//
//            @Override
//            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                System.out.println("visit file - " + file.getFileName());
//                return FileVisitResult.CONTINUE;
//            }
//
//            @Override
//            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//                System.out.println("visit file failed - " + file.getFileName());
//                return FileVisitResult.TERMINATE;
//            }
//
//            @Override
//            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                System.out.println("post - " + dir.getFileName());
//                return FileVisitResult.CONTINUE;
//            }
//        });

        //search
//        Files.walkFileTree(Path.of(""), new SimpleFileVisitor<>(){
//            @Override
//            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                if("test-file.txt".equals(file.getFileName().toString())){
//                    System.out.println(file.getFileName() + " is founded. Path: " + file.toAbsolutePath());
//                    return FileVisitResult.CONTINUE;
//                }
//                return FileVisitResult.CONTINUE;
//            }
//        });
//
//        Files.find(Path.of(""), 10, new BiPredicate<Path, BasicFileAttributes>() {
//            @Override
//            public boolean test(Path path, BasicFileAttributes basicFileAttributes) {
//                return "test-file.txt".equals(path.getFileName().toString());
//            }
//        }).forEach(System.out::println);
        Path server = Path.of("server", "test-file.txt");
        Files.readAllLines(server).stream().forEach(System.out::println);
        System.out.println("--------------------------------");

        Files.newBufferedReader(server).lines().forEach(System.out::println);
        System.out.println("--------------------------------");

        byte[] bytes = Files.readAllBytes(server);
        for (byte b : bytes){
            System.out.print((char) b);
        }

    }
}
