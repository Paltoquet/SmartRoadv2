package com.westfolk.smartroadv2;

import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Lucas on 27/11/2016.
 */

public class Utils {

    public void writeToFile(String data, String fileName) throws IOException {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(path, fileName);

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(data.getBytes());
            stream.close();
            Log.i("Utils", "Written "+ file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String readFile(String FileName) {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        final File file = new File(path, FileName);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Utils", "Read SmartRoad.json " + text.toString());

        return text.toString();
    }
}
