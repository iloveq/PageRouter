package com.sohu.hy.utils;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.sohu.hy.utils.Constants.*;



public class TypeUtils {

    private Types types;
    private TypeMirror parcelableType;
    private TypeMirror serializableType;
    private TypeMirror listType;

    public TypeUtils(Types types, Elements elements) {
        this.types = types;
        parcelableType = elements.getTypeElement(PARCELABLE).asType();
        serializableType = elements.getTypeElement(SERIALIZABLE).asType();
        listType = elements.getTypeElement(LIST).asType();
    }

    public String getTypeImportName(Element element) {
        TypeMirror typeMirror = element.asType();
        switch (typeMirror.toString()) {
            case BYTE:
            case SHORT:
            case INTEGER:
            case LONG:
            case FLOAT:
            case DOUBEL:
            case BOOLEAN:
            case CHAR:
            case STRING:
            case LIST:
                return typeMirror.toString();
            default:
                // Other side, maybe the PARCELABLE or SERIALIZABLE or OBJECT.
                if (types.isSubtype(typeMirror, parcelableType)) {
                    // PARCELABLE
                    return PARCELABLE;
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    // SERIALIZABLE
                    return SERIALIZABLE;
                } else {
                    return "";
                }
        }
    }

    public String getTypeName(Element element) {
        TypeMirror typeMirror = element.asType();
        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().name().toLowerCase();
        }
        return typeMirror.toString();

    }

    public int typeExchange(Element element) {
        TypeMirror typeMirror = element.asType();
        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }
        switch (typeMirror.toString()) {
            case BYTE:
                return TypeKind.BYTE.ordinal();
            case SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBEL:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return TypeKind.CHAR.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            case StringArrayList:
                return TypeKind.StringArrayList.ordinal();
            case IntegerArrayList:
                return TypeKind.IntegerArrayList.ordinal();
            default:
                // Other side, maybe the PARCELABLE or SERIALIZABLE or OBJECT.
                if (types.isSubtype(typeMirror, parcelableType)) {
                    // PARCELABLE
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    // SERIALIZABLE
                    return TypeKind.SERIALIZABLE.ordinal();
                }  {
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }

    public enum TypeKind {
        // Base type
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        CHAR,
        FLOAT,
        DOUBLE,

        // Other type
        STRING,
        SERIALIZABLE,
        PARCELABLE,
        OBJECT,
        StringArrayList,
        IntegerArrayList
    }
}
