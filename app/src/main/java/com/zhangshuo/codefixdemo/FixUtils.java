package com.zhangshuo.codefixdemo;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 作者: 张杰
 * 时间: 2018/11/17
 * 描述:
 */
public class FixUtils {

    //读取已经修复完成的dex文件
    public boolean loadFixedDex(Context context, String path) {
        File file = new File(path);
        if (!file.exists()) return false;

        //1、先获取已经加载的ClassLoader
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        //2、反射得到已经加载的 pathList属性
        Object pathList = getPathList(pathClassLoader);
        if (pathList == null) return false;
        //3、反射得到已经加载的 dexElements
        Object dexElements = getElements(pathList);

        //4、加载补丁
        DexClassLoader dexClassLoader = new DexClassLoader(file.getAbsolutePath(), context.getCacheDir().getAbsolutePath(), null, pathClassLoader);
        //5、得到补丁中的pathList属性
        Object fixPathList = getPathList(dexClassLoader);
        if (fixPathList == null) return false;
        //6、得到补丁中的 dexElements;
        Object fixDexElements = getElements(fixPathList);

        //7、把补丁中的dexElements插入到已经存在的DexElements
        Object newDezElements = insertDexElements(dexElements, fixDexElements);
        if (newDezElements == null) return false;

        //8、重新赋值给系统的dexElements
        try {
            Field field = pathList.getClass().getDeclaredField("dexElements");
            field.setAccessible(true);
            field.set(pathList, newDezElements);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    //合并
    private Object insertDexElements(Object oldDexElements, Object fixDexElements) {
        //如果不是数组，则返回
        if (!oldDexElements.getClass().isArray() || !fixDexElements.getClass().isArray())
            return null;
        //获取长度
        int oldLength=Array.getLength(oldDexElements);
        int newLength=Array.getLength(fixDexElements);
        //创建新的数组
        Object newElements = Array.newInstance(oldDexElements.getClass().getComponentType(),oldLength+newLength) ;
        // 先插入修复好的fixDexElements
        for(int i=0;i<newLength;i++){
            Array.set(newElements, i, Array.get(fixDexElements, i));
        }
        // 插入旧的dexElements
        for(int i=newLength;i<(oldLength+newLength);i++){
            Array.set(newElements, i, Array.get(oldDexElements, i-newLength));
        }
        return newElements;
    }

    //通过反射，获取Elements属性
    private Object getElements(Object object) {
        try {
            Class<?> c = object.getClass();
            Field fElements = c.getDeclaredField("dexElements");
            fElements.setAccessible(true);
            Object obj = fElements.get(object);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //通过反射，获取PathList属性
    private Object getPathList(BaseDexClassLoader baseDexClassLoader) {
        try {
            Class<?> clazz = Class.forName("dalvik.system.BaseDexClassLoader");
            //获取变量
            Field field = clazz.getDeclaredField("pathList");
            field.setAccessible(true);
            return field.get(baseDexClassLoader);

        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

}
