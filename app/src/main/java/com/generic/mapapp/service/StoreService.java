package com.generic.mapapp.service;


import com.generic.mapapp.R;
import com.generic.mapapp.intefaces.Closure;
import com.generic.mapapp.domain.Store;
import com.generic.mapapp.domain.StoreType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


public class StoreService {

    private static final StoreService INSTANCE = new StoreService();

    public static StoreService get() {
        return INSTANCE;
    }

    private StoreService() {
        cacheService = CacheService.get();
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        gson = new Gson();
    }


    private CacheService cacheService;
    private RestTemplate restTemplate;
    private Gson gson;


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

        Closure apiCall = new Closure() {

            @Override
            public Object call() {
                String json = restTemplate.getForObject(url, String.class);
                return json;
            }

        };

        String json = cacheService.get(key, apiCall);
        return json;

    }

}
