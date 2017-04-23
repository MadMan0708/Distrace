package cz.cuni.mff.d3s.distrace.instrumentation;

import java.util.*;

/**
 * Various utilities method which may be used at the instrumentation time to exchange some information between the
 * thread on the same node
 */
public class StorageUtils {
    private static Map<String, ArrayList<Object>> listStorage = new HashMap<>();
    private static Map<String, HashMap<Object, Object>> mapStorage = new HashMap<>();

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
     * Remove object to the list with the specified name
     *
     * @param storageName list name
     * @param value       object to remove
     */
    public static synchronized void removeFromList(String storageName, Object value) {
        if (!listStorage.containsKey(storageName)) {
            listStorage.put(storageName, new ArrayList<>());
        }
        listStorage.get(storageName).remove(value);
    }

    /**
     * Check whether the list contains object
     *
     * @param storageName list name
     * @param value       object to check for presence
     */
    public static synchronized boolean listContains(String storageName, Object value) {
        if (!listStorage.containsKey(storageName)) {
            listStorage.put(storageName, new ArrayList<>());
        }
        return listStorage.get(storageName).contains(value);
    }

    /**
     * Store key-value pair into the hash map with the specified name
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
     * Remove key-value pair from the hash map with the specified name
     *
     * @param storageName name of the hash map
     * @param key         key to remove
     */
    public static synchronized void removeFromMap(String storageName, Object key) {
        if (!mapStorage.containsKey(storageName)) {
            mapStorage.put(storageName, new HashMap<>());
        }
        mapStorage.get(storageName).remove(key);
    }

    /**
     * Check key-value pair for presence in the hash map with the specified name
     *
     * @param storageName name of the hash map
     * @param key         key to to check for presence
     */
    public static synchronized boolean mapContains(String storageName, Object key) {
        if (!mapStorage.containsKey(storageName)) {
            mapStorage.put(storageName, new HashMap<>());
        }
        return mapStorage.get(storageName).containsKey(key);
    }

}
