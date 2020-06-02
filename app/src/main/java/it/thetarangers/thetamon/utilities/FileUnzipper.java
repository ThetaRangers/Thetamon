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

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(outputDir, entry.getName());

                if (entry.isDirectory()) {
                    if (!entryDestination.mkdir())
                        return false;

                } else {

                    try (InputStream inputStream = zipFile.getInputStream(entry);
                         BufferedInputStream in = new BufferedInputStream(inputStream);
                         FileOutputStream fileOutputStream = new FileOutputStream(entryDestination);
                         BufferedOutputStream out = new BufferedOutputStream(fileOutputStream)) {

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
