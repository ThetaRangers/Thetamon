package it.thetarangers.thetamon.utilities;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUnzipper {

    public Boolean unzip(File file, String outputDir) {

        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            // Treat zip entries as an iterator
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(outputDir, entry.getName());

                // If the entry is a directory, create it in outputDir as well
                if (entry.isDirectory()) {
                    if (!entryDestination.mkdir())
                        return false;

                } else {

                    try (InputStream inputStream = zipFile.getInputStream(entry);
                         BufferedInputStream in = new BufferedInputStream(inputStream);
                         FileOutputStream fileOutputStream = new FileOutputStream(entryDestination);
                         BufferedOutputStream out = new BufferedOutputStream(fileOutputStream)) {

                        // If the entry is a file, copy it in outputDir
                        IOUtils.copy(in, out);

                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
