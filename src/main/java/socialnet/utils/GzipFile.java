package socialnet.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

@NoArgsConstructor
@Slf4j
public class GzipFile {
    private static final int BUFFER_SIZE = 1024;

    public void unGZipToFile(final InputStream fileInputStream, String filename) {

        byte[] buffer = new byte[BUFFER_SIZE];

        try (GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(getDstFileName(filename))) {

            int length = 0;
            while ((length = gzipInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    public void unGZipToFile(final String filename) {

        byte[] buffer = new byte[BUFFER_SIZE];

        try (GZIPInputStream gzipInputStream = new GZIPInputStream(Files.newInputStream(Paths.get(filename)));
             FileOutputStream fileOutputStream = new FileOutputStream(getDstFileName(filename))) {

            int length = 0;
            while ((length = gzipInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    private String getDstFileName(final String srcFileName) {
        return srcFileName.substring(0, srcFileName.lastIndexOf("."));
    }
}
