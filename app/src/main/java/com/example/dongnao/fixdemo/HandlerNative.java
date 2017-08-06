package com.example.dongnao.fixdemo;

import java.lang.reflect.Method;

/**
 * Created by tucheng on 16/10/4.
 */

public class HandlerNative {
    static {
        System.loadLibrary("native-lib");
    }

    public static  native  void init(int api);

    public  static  native  void replaceMethod(Method src,Method dest);
}
