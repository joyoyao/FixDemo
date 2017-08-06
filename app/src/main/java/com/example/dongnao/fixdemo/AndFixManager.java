package com.example.dongnao.fixdemo;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alipay.euler.andfix.annotation.MethodReplace;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Created by tucheng on 16/10/4.
 */

public class AndFixManager {
    private Context context;
    private File optFile;
    public AndFixManager(Context context) {
        this.context = context;
        HandlerNative.init(Build.VERSION.SDK_INT);

    }

    public void fix(File file, final ClassLoader classLoader, List<String> list)
    {
        optFile=new File(context.getFilesDir(),file.getName());
        if(optFile.exists())
        {
            optFile.delete();
        }

        try {
            final DexFile dexFile=DexFile.loadDex(file.getAbsolutePath(),optFile.getAbsolutePath(),Context.MODE_PRIVATE);
            ClassLoader classLoader1=new ClassLoader() {
                @Override
                protected Class<?> findClass(String className) throws ClassNotFoundException {
                    Class clazz=dexFile.loadClass(className,this);
                    if(clazz==null)
                    {
                        clazz=Class.forName(className);
                    }
                    return clazz;
                }
            };


            Enumeration<String> entry=dexFile.entries();
            while (entry.hasMoreElements())
            {
                String key=entry.nextElement();
                if(!list.contains(key))
                {
                    continue;
                }
                Class realClazz=dexFile.loadClass(key,classLoader1);
                if(realClazz!=null)
                {

                   fixClass(realClazz,classLoader);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void fixClass(Class realClazz, ClassLoader classLoader) {
        Method[] methods=realClazz.getMethods();
        for (Method needMethod:methods)
        {
            MethodReplace methodReplace=needMethod.getAnnotation(MethodReplace.class);
            if(methodReplace==null)
            {
                continue;
            }
            Log.i("dongnao","找到替换方法   "+methodReplace.toString()+"  clazz 对象  "+realClazz.toString());
            String clazz=methodReplace.clazz();
            String methodName=methodReplace.method();
            replaceMehod(classLoader,clazz,methodName,realClazz,needMethod);



        }





    }

    private void replaceMehod(ClassLoader classLoader, String clazz, String methodName, Class realClazz,Method method) {

        try {
            Class srcClazz=Class.forName(clazz);
            if(srcClazz!=null)
            {
                Method src=srcClazz.getDeclaredMethod(methodName,method.getParameterTypes());
                HandlerNative.replaceMethod(src,method);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


}
