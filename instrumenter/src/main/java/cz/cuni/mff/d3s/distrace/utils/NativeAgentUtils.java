package cz.cuni.mff.d3s.distrace.utils;

/**
 * Various utilities methods implemented by native agent jni.
 */
public class NativeAgentUtils {

    /**
     * Get type one UUID
     * @return type 1 UUID
     */
    public static native String getTypeOneUUID();

    /**
     * Get type one UUID as 32 characters long hex string
     * @return type one UUID as 32 characters long hex string
     */
    public static String getTypeOneUUIDHex(){
       return getTypeOneUUID().replaceAll("-", "");
    }
}
