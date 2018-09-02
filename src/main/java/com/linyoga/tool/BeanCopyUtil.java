package com.linyoga.tool;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.linyoga.annotation.Convert;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * 对象复制工具类
 *
 * 功能描述：
 * 1.对象A复制给复制B，其中List，Set集合属性不能复制
 * 2.若加上注解@Convert,并配置转换后的类型，则可对list和set集合属性复制
 *
 * 原理：
 * 利用spring中的BeanUtil.copyProperties进行属性复制
 * @author Kris
 * @date 2018/06/22
 */
public class BeanCopyUtil {

    /**
     * 复制到相同属性的bean里
     *
     * @param before 需转换的对象
     * @param after  目标对象的class类
     */
    public static <T, E> T copyBean(E before, Class<T> after) {
        return copyBeanByIgnore(before, after, null);
    }

    /**
     * 复制到相同属性的bean里,忽略指定属性
     *
     * @param before           需转换的对象
     * @param after            目标对象的class类
     * @param ignoreProperties 忽略指定属性
     */
    public static <T, E> T copyBeanByIgnore(E before, Class<T> after, String[] ignoreProperties) {
        if (null == before) {
            return null;
        }
        T afterObject = null;
        try {
            afterObject = after.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(String.format("class %s: cause cannot create an instance of a class "
                    , after.getSimpleName())
                    + e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Error: Cannot execute a private method. in %s.  Cause:",
                    after.getSimpleName())
                    + e);
        }
        BeanUtils.copyProperties(before, afterObject, ignoreProperties);
        convertProperity(before, afterObject, ignoreProperties);
        return (T) afterObject;
    }

    /**
     * 将list集合中的元素替换成after目标对象
     *
     * @param beforeList 需转换的对象列表
     * @param after      目标对象的class类
     */
    public static <T> List<T> copyListBean(List beforeList, Class<T> after) {
        return copyListBeanByIgnore(beforeList, after, null);
    }

    /**
     * 将list集合中的元素替换成after目标对象 忽略指定属性
     *
     * @param beforeList       需转换的对象列表
     * @param after            目标对象的class类
     * @param ignoreProperties 忽略指定属性
     */
    public static <T> List<T> copyListBeanByIgnore(List beforeList, Class<T> after, String[] ignoreProperties) {
        if (null == beforeList || beforeList.isEmpty()) {
            return null;
        }
        List<T> tList = Lists.newLinkedList();
        beforeList.stream().forEach(before -> {
            tList.add(copyBeanByIgnore(before, after, ignoreProperties));
        });
        return tList;
    }

    /**
     * 转换带@Convert的属性
     */
    public static <T, E> void convertProperity(T before, E after, String[] ignoreProperties) {
        //获取转换对象中list类型的属性
        Field[] beforeFileds = before.getClass().getDeclaredFields();
        try {
            for (Field field : beforeFileds) {
                //判断是否有Convert注解
                Convert convert = field.getAnnotation(Convert.class);
                //无注解则跳过
                if (null == convert) {
                    continue;
                }

                //判断该属性是否需忽略
                List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);
                if (null != ignoreList && ignoreList.contains(field.getName())) {
                    continue;
                }

                String convertAttr = convert.name();
                if (Strings.isNullOrEmpty(convertAttr)) {
                    //若注解中name属性无值,则获取注解的name值即目标对象属性名
                    convertAttr = field.getName();
                }
                //判断field是否为非公用属性
                if (!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }

                //获取目标对象对应的属性Filed
                Field afterFiled = after.getClass().getDeclaredField(convertAttr);
                //判断afterFiled是否为非公用属性
                if (!Modifier.isPublic(field.getModifiers())) {
                    afterFiled.setAccessible(true);
                }
                //获取属性中的泛型
                if (field.getType().isAssignableFrom(List.class) || field.getType().isAssignableFrom(Set.class)) {

                    //获取list类型的属性值
                    List list = field.getType().isAssignableFrom(Set.class)
                            ? Lists.newArrayList((Set) field.get(before))
                            : (List) field.get(before);
                    if (null == list || list.isEmpty()) {
                        continue;
                    }
                    //转换
                    afterFiled.set(after, copyListBeanByIgnore(list, getGenericType(afterFiled), convert.ignorePropertry()));
                } else {
                    afterFiled.set(after, copyBeanByIgnore(field.get(before), convert.className(), convert.ignorePropertry()));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Error: Cannot execute a private method. in %s.  Cause:",
                    after.getClass().getSimpleName())
                    + e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(String.format("Error: NoSuchField in %s.  Cause:", "SQLCondition") + e);
        }
    }

    /**
     * 获取属性的泛型
     */
    public static Class getGenericType(Field field) {
        Class fieldClazz = field.getType();
        //判断是否为list或则set集合
        if (fieldClazz.isAssignableFrom(List.class) || fieldClazz.isAssignableFrom(Set.class)) {
            //获取属性的泛型
            Type type = field.getGenericType();
            if (null == type) {
                return null;
            }
            //判断是不是泛型参数的类型
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                //获取泛型中的class
                Class genericClazz = (Class) pt.getActualTypeArguments()[0];
                return genericClazz;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        /**
         * copyListBeanByIgnore 所需参数
         * 1、转换对象中的list类型属性值
         *      遍历属性判断是否为list类型，判断是否为忽略的属性值
         *          则获取该属性值
         *      判断是否有注解
         *          无注解，则不转换
         *          有注解但无属性值，则按该属性名到目标对象中找到list属性
         *          有，则获取对应list属性值。
         *              获得的值：
         *                  属性Filed
         *                  忽略的值
         * 2、目标对象list的泛型的class
         *      根据filed获取泛型中的class
         * 3、忽略属性值转换
         *      忽略的值
         */
    }
}
