package com.sohu.hy.utils;



public class Constants {

    public static final String PACKAGE_NAME = "com.sohu.generate";
    public static final String EXPLAIN = " Do not modify!";
    // Log
    static final String PREFIX_OF_LOGGER = "AptLogger";

    // Java type
    private static final String LANG = "java.lang";
    private static final String UTIL = "java.util";
    public static final String BYTE = LANG + ".Byte";
    public static final String LIST = UTIL + ".List";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String CHAR = LANG + ".Character";
    public static final String STRING = LANG + ".String";
    public static final String StringArrayList = "java.util.ArrayList<java.lang.String>";
    public static final String IntegerArrayList = "java.util.ArrayList<java.lang.Integer>";
    public static final String SERIALIZABLE = "java.io.Serializable";
    public static final String PARCELABLE = "android.os.Parcelable";

    public interface AnnotationPath {

        String ROUTER = "com.sohu.hy.annotation.Route;";

    }

}
