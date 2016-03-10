package com.distrace.utils;

/**
 * Wrapper around logging system.
*/
public class Log {
    public static void info(String msg){
        System.out.println(msg);
    }

    public static void err(){

    }

    public static void debug(){

    }

    public static void error(String msg){
        System.err.println(msg);
    }
}
