package com.example.testnaviapp;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    private final static String FILE_EXT = ".zip";

    @SuppressWarnings("deprecation")
    public static File zipFile(Context context, File file) {
        try {
            // make zip file
            String zipFileName = file.getName();
            int lastIndex = zipFileName.lastIndexOf(".");
            if (lastIndex != -1) {
                zipFileName = zipFileName.substring(0, lastIndex);
            }
            zipFileName += FILE_EXT;

            // write to zip
            ZipOutputStream zipWriter = new ZipOutputStream(context.openFileOutput(zipFileName,
                    Context.MODE_WORLD_READABLE));
            zip(file, zipWriter);
            zipWriter.close();

            return new File(Environment.getExternalStorageDirectory() + "/" + zipFileName);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void zip(File file, ZipOutputStream zipWriter) throws IOException {
        if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                zip(innerFile, zipWriter);
            }
        } else {
            byte[] buffer = new byte[1024];
            FileInputStream reader = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipWriter.putNextEntry(zipEntry);

            int len;
            while ((len = reader.read(buffer)) > 0) {
                zipWriter.write(buffer, 0, len);
            }

            zipWriter.closeEntry();
            reader.close();
        }
    }

}
