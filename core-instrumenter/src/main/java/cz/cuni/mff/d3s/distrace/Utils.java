package cz.cuni.mff.d3s.distrace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Various helper methods
 */
public class Utils {

    public static String convertToJavaName(String name){
        return name.replaceAll("/", ".");
    }


    public static InputStream openClassfile(String classname, ClassLoader cl) {
        String cname = classname.replace('.', '/') + ".class";
        if (cl == null)
            return null;        // not found
        else
            return cl.getResourceAsStream(cname);
    }

    public static byte[] getBytesFromClassFile(String classname, ClassLoader cl) {
        try {
            InputStream inputStream = openClassfile(classname, cl);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int reads = inputStream.read();
            while (reads != -1) {
                baos.write(reads);
                reads = inputStream.read();
            }
            return baos.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
