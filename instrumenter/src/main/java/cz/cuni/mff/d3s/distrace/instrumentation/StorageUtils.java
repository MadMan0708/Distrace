package cz.cuni.mff.d3s.distrace.instrumentation;

import java.util.*;

/**
 * Various utilities method which may be used at the instrumentation time to exchange some information between the
 * thread on the same node
 */
public class StorageUtils {
    private static Map<String, ArrayList<Object>> listStorage =
            Collections.synchronizedMap(new HashMap<String, ArrayList<Object>>());
    private static Map<String, HashMap<Object, Object>> mapStorage =
            Collections.synchronizedMap(new HashMap<String, HashMap<Object, Object>>());

    /**
     * Store object to the list with the specified name
     *
     * @param storageName list name
     * @param value       object to insert
     */
    public static void addToList(String storageName, Object value) {
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
    public static void addToMap(String storageName, Object key, Object value) {
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
    public static ArrayList<Object> getList(String storageName) {
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
    public static HashMap<Object, Object> getMap(String storageName) {
        if (!mapStorage.containsKey(storageName)) {
            mapStorage.put(storageName, new HashMap<>());
        }
        return mapStorage.get(storageName);
    }
}
