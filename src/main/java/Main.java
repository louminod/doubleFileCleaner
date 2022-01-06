import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class Main {
    static int doublons = 0;
    static int filescounter = 0;

    public static void main(String[] args) {
        List<String> extensions = List.of("mov", "jpg", "jpeg", "png", "mp4", "heic", "pdf", "mkv", "mp3", "svg");
        List<String> newExtensions = new ArrayList<>();
        List<String> checksums = new ArrayList<>();

        String path = "/Volumes/Disque Dur/doublon/";
        // String path = "/Users/lmm/Desktop/imageCleaner/";
        // String path = "/Volumes/Disque Dur/";


        try {

            System.out.println(Files.find(Paths.get(path),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile()).count() + " fichiers à explorer");

            Files.find(Paths.get(path), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .forEach(file -> {
                        String[] elements = file.getFileName().toString().split("\\.");
                        String extension = elements[elements.length - 1].toLowerCase(Locale.ROOT);
                        if (extensions.contains(extension)) {
                            try {
                                String checksum = getFileChecksum(file.toFile());
                                if (checksums.contains(checksum)) {
                                    doublons++;
                                } else {
                                    checksums.add(checksum);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            if (!newExtensions.contains(extension) && extension.length() < 10) {
                                newExtensions.add(extension);
                            }
                        }
                        filescounter++;
                        if (filescounter % 100 == 0) {
                            System.out.println(filescounter + " fichiers parcourus");
                        }
                    });

            System.out.println("RESULT : " + doublons + " doublons");
            newExtensions.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");

        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }


        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}
