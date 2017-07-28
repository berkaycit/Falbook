package com.falbookv4.helloteam.falbook.classes;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    /* Kamera da fotoğraf çekince tutulan cacheleri siler */

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getCameraPicLocation(Context context) throws IOException {

        File cacheDir = context.getCacheDir();

        if (isExternalStorageWritable()) {
            cacheDir = context.getExternalCacheDir();
        }

        File dir = new File(cacheDir, "EasyImage");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static void clearCameraPic(Context context) {
        List<File> tempFiles = new ArrayList<>();
        File[] files = new File[0];
        try {
            files = getCameraPicLocation(context).listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file : files) {
            file.delete();
        }
    }

    /* Normal cacheleri siler */

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }




}
