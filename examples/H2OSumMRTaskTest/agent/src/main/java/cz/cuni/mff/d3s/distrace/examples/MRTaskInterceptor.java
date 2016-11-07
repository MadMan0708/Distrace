package cz.cuni.mff.d3s.distrace.examples;


import net.bytebuddy.implementation.bind.annotation.This;
import water.H2O;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class MRTaskInterceptor {


        public static void map(@This Object o){
            try {
               Class clz =  o.getClass().getClassLoader().loadClass("water.H2O");
                Method m = clz.getDeclaredMethod("getIpPortString");
                Field f = o.getClass().getDeclaredField("sum");
                f.setAccessible(true);
               System.out.println("Map was called on node: " + m.invoke(null) +  " sum so far" + f.get(o));

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

}
