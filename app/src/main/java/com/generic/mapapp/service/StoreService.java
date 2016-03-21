package com.generic.mapapp.service;


import com.generic.mapapp.domain.Store;
import com.generic.mapapp.domain.StoreType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gabriel on 20/03/16.
 */
public class StoreService {

    private static final List<Store> STORES = Arrays.asList(new Store(1, "Store 1", new StoreType(1, "Retailer"), -34.657916, -58.468907),
                                                            new Store(2, "Store 2", new StoreType(2, "Mass storage"), -34.656641, -58.467791));

    private static final List<StoreType>  TYPES = Arrays.asList(new StoreType(1,"Retailer"), new StoreType(2,"Mass storage"), new StoreType(3,"Other"));

    public List<Store> getStores(List<StoreType> criteria) {
        List<Store> result = new ArrayList<>();

        for (Store store: STORES) {
            if (criteria.contains(store.getType())) { result.add(store); }
        }

        return result;
    }

    public List<Store> getStores() {
        return STORES;
    }

    public List<StoreType> getStoreTypes() {
        return new ArrayList<StoreType>(TYPES);
    }

    public StoreType findType(Integer id) {
        StoreType result = null;

        for (StoreType type : TYPES) {
            if (type.getId() == id) result = type;
        }

        return result;
    }
}
