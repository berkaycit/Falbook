package com.falbookv4.helloteam.falbook.classes;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
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

    /* şifreleme */
    public static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }

    public static boolean isFileExist(String fileName){
        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FBT/");

            if(dir.exists()){
                final File myFile = new File(dir, fileName + ".txt");

                if(myFile.exists()){
                    return true;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static String readFile(String fileName){
        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FBT/" );

        //File sdcard = Environment.getExternalStorageDirectory();

        //text file ı al
        File file = new File(dir, fileName + ".txt");

        //text i oku
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }


}
