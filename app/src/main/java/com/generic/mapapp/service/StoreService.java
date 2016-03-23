package com.generic.mapapp.service;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.generic.mapapp.MapsApplication;
import com.generic.mapapp.domain.Store;
import com.generic.mapapp.domain.StoreType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StoreService {


    private CacheService cacheService;
    private RestTemplate restTemplate;
    private Gson gson;

    public StoreService() {
        cacheService = CacheService.get();
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        gson = new Gson();
    }

    public List<Store> getStores() {
        String json = get("http://private-32930-storesapi.apiary-mock.com/stores", "stores");
        List<Store> stores = gson.fromJson(json, new TypeToken<ArrayList<Store>>() {}.getType());
        return stores;
    }

    public List<Store> getStores(List<StoreType> criteria) {
        List<Store> stores = getStores();
        List<Store> result = new ArrayList<>();

        for (Store store: stores) {
            if (criteria.contains(store.getType())) { result.add(store); }
        }

        return result;
    }



    public List<StoreType> getStoreTypes() {
        String json = get("http://private-dfd17e-storetypesapi.apiary-mock.com/types", "types");
        List<StoreType> storeTypes = gson.fromJson(json, new TypeToken<ArrayList<StoreType>>() {}.getType());
        return storeTypes;
    }

    public StoreType findType(Integer id) {
        List<StoreType> types = getStoreTypes();
        StoreType result = null;

        for (StoreType type : types) {
            if (type.getId() == id) result = type;
        }

        return result;
    }




    private String get(final String url, final String key) {

        AsyncTask task = new AsyncTask<Object, Object, String>() {

            @Override
            protected String doInBackground(Object... params) {
                try {
                    String json = restTemplate.getForObject(url, String.class);
                    cacheService.put(key, json);
                    return json;

                } catch (Exception ex) {
                    Log.i(this.getClass().getName(), "GET failed.");
                    ex.printStackTrace();
                    String json = cacheService.get(key);
                    return json;
                }

            }
        };

        try {
            task = task.execute();
            Object result = task.get();
            return (String) result;
        } catch (Exception ex) {
            Log.i(this.getClass().getName(), "GET failed");
            ex.printStackTrace();
        }

        return null;
        
    }

}
