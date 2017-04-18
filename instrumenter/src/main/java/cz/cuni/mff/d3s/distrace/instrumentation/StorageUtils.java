package cz.cuni.mff.d3s.distrace.instrumentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Various utilities method which may be used at the instrumentation time to exchange some information between the
 * thread on the same node
 */
public class StorageUtils {
    private static HashMap<String, ArrayList<Object>> listStorage = new HashMap<>();
    private static HashMap<String, HashMap<Object, Object>> mapStorage = new HashMap<>();

    public static synchronized void addToList(String storageName, Object value){
        if(!listStorage.containsKey(storageName)){
            listStorage.put(storageName, new ArrayList<>());
        }
        listStorage.get(storageName).add(value);
    }

    public static synchronized void addToMap(String storageName, Object key, Object value){
        if(!mapStorage.containsKey(storageName)){
            mapStorage.put(storageName, new HashMap<>());
        }
        mapStorage.get(storageName).put(key, value);
    }

    public static synchronized ArrayList<Object> getList(String storageName){
        if(!listStorage.containsKey(storageName)){
            listStorage.put(storageName, new ArrayList<>());
        }
        return listStorage.get(storageName);
    }

    public static synchronized HashMap<Object, Object> getMap(String storageName){
        if(!mapStorage.containsKey(storageName)){
            mapStorage.put(storageName, new HashMap<>());
        }
        return mapStorage.get(storageName);
    }
}
