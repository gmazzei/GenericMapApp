package com.generic.mapapp.service;

import android.content.Context;
import android.util.Log;

import com.generic.mapapp.MapsApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by gabriel on 22/03/16.
 */
public class CacheService {

    private static final CacheService INSTANCE = new CacheService();

    public static CacheService get() {
        return INSTANCE;
    }

    private CacheService() {}


    public String get(String key) {
        Context context = MapsApplication.getAppContext();
        File file = new File(context.getFilesDir(), key);

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
            Log.i(this.getClass().getName(), "Failed to read file: " + key);
            e.printStackTrace();
        }

        return text.toString();
    }

    public void put(String key, String value) {
        Context context = MapsApplication.getAppContext();
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(key, Context.MODE_PRIVATE);
            outputStream.write(value.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.i(this.getClass().getName(), "Failed to write file: " + key);
            e.printStackTrace();
        }
    }

}
