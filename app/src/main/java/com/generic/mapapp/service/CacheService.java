package com.generic.mapapp.service;

import android.content.Context;
import android.util.Log;

import com.generic.mapapp.intefaces.Closure;
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
    private static final long TIME_TO_LIVE = 24*60*60*1000; //One day

    public static CacheService get() {
        return INSTANCE;
    }

    private CacheService() {}


    public String get(String key, Closure apiRequest) {
        String value;

        if (exists(key)) {
            value = readFile(key);
        } else {
            value = (String)apiRequest.call();
            put(key, value);
        }

        return value;
    }

    public boolean exists(String key) {
        Context context = MapsApplication.getAppContext();
        File file = new File(context.getFilesDir(), key);
        boolean validData = System.currentTimeMillis() - file.lastModified() < TIME_TO_LIVE;
        return file.exists() && validData;
    }

    public String readFile(String name) {
        Context context = MapsApplication.getAppContext();
        File file = new File(context.getFilesDir(), name);

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
            Log.i(this.getClass().getName(), "Failed to read file: " + name);
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
