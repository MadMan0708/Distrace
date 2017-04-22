package cz.cuni.mff.d3s.distrace.instrumentation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Various utilities method which may be used at the instrumentation time to exchange some information between the
 * thread on the same node
 */
public class StorageUtils {
    private static HashMap<String, ArrayList<Object>> listStorage = new HashMap<>();
    private static HashMap<String, HashMap<Object, Object>> mapStorage = new HashMap<>();

    /**
     * Store object to the list with the specified name
     *
     * @param storageName list name
     * @param value       object to insert
     */
    public static synchronized void addToList(String storageName, Object value) {
        if (!listStorage.containsKey(storageName)) {
            listStorage.put(storageName, new ArrayList<>());
        }
        listStorage.get(storageName).add(value);
    }


    /**
     * Store key-value par into the hash map with the specified name
     *
     * @param storageName name of the hash map
     * @param key         key to insert
     * @param value       value to insert
     */
    public static synchronized void addToMap(String storageName, Object key, Object value) {
        if (!mapStorage.containsKey(storageName)) {
            mapStorage.put(storageName, new HashMap<>());
        }
        mapStorage.get(storageName).put(key, value);
    }

    /**
     * Get list with the specified name
     *
     * @param storageName list name
     * @return associated list
     */
    public static synchronized ArrayList<Object> getList(String storageName) {
        if (!listStorage.containsKey(storageName)) {
            listStorage.put(storageName, new ArrayList<>());
        }
        return listStorage.get(storageName);
    }

    /**
     * Get hash map with the specified name
     *
     * @param storageName hash map name
     * @return associated hash map
     */
    public static synchronized HashMap<Object, Object> getMap(String storageName) {
        if (!mapStorage.containsKey(storageName)) {
            mapStorage.put(storageName, new HashMap<>());
        }
        return mapStorage.get(storageName);
    }
}
