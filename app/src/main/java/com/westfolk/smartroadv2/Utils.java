package com.westfolk.smartroadv2;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public void writeToFileWithoutErase(String data, String fileName) throws IOException {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(path, fileName);

        try {
            FileWriter stream = new FileWriter(file, true);
            stream.write(data);
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
        Log.i("Utils", "Read" + text.toString());

        return text.toString();
    }

    public JSONObject readFileJSON (String FileName, String path) {

        //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
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
        Log.i("Utils", "Read" + text.toString());

        JSONObject obj = null;
        try {
            obj = new JSONObject(text.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public JSONObject readFileJSON(InputStream inputStream) {
        //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Utils", "Read" + text.toString());

        JSONObject obj = null;
        try {
            obj = new JSONObject(text.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static String getDateFromSecond(long seconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        //return formatter.format(new Date((seconds-3600)*1000));
        return formatter.format(new Date((seconds)*1000));
    }

}
