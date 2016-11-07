package cz.cuni.mff.d3s.distrace;

import com.rits.cloning.Cloner;
import com.rits.cloning.ObjenesisInstantiationStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Various helper methods
 */
public class Utils {

    public static String convertToJavaName(String name){
        return name.replaceAll("/", ".");
    }


    private static InputStream openClassfile(String classname, ClassLoader cl) {
        String cname = classname.replace('.', '/') + ".class";
        if (cl == null)
            return null;
        else
            return cl.getResourceAsStream(cname);
    }

    @SuppressWarnings("unused")
    public static void triggerLoading(String className, ClassLoader cl){
        //IDEA: We could create deep copy of class loader cl and load the class with that copy
        // this would ensure that we don't change the original class loading mechanisms
        // but we would be able to get the bytecode of the class
        Cloner cloner =  new Cloner(new ObjenesisInstantiationStrategy());
        ClassLoader clone = cloner.deepClone(cl);
        try {
           Class.forName(className.replace('/','.'), true, clone);
        } catch (ClassNotFoundException ignored) {
        }
    }

    @SuppressWarnings("unused")
    public static byte[] getBytesFromClassFile(String classname, ClassLoader cl) {
        try {
            InputStream inputStream = openClassfile(classname, cl);
            if(inputStream != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int reads = inputStream.read();
                while (reads != -1) {
                    baos.write(reads);
                    reads = inputStream.read();
                }
                return baos.toByteArray();
            }else{
                return null;
            }
        }catch (IOException e){
            return null;
        }
    }

}
