package com.sohu.hy;

import com.sohu.hy.annotation.Args;
import com.sohu.hy.model.BundleInfo;
import com.sohu.hy.utils.Constants;
import com.sohu.hy.utils.Logger;
import com.sohu.hy.utils.StringUtils;
import com.sohu.hy.utils.TypeUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

public class BundleParserProcessor extends AbstractProcessor {

    private Logger logger;
    private Filer filer;
    Types types;
    Elements elementUtils;
    TypeUtils typeUtils;

    Map<TypeElement, List<Element>> objectAndFields = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        logger = new Logger(processingEnv.getMessager());
        filer = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = new TypeUtils(types, elementUtils);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Args.class);
        if (elements.isEmpty()) return false;
        try {
            prepare(elements);
        } catch (Exception e) {
            logger.error(e.getCause());
        }
        generateJavaFile();
        return true;
    }

    private void prepare(Set<? extends Element> elements) throws IllegalAccessException {
        for (Element element : elements) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            if (element.getModifiers().contains(Modifier.PRIVATE)) {
                throw new IllegalAccessException("The inject fields CAN NOT BE 'private'!!! please check field ["
                        + element.getSimpleName() + "] in class [" + enclosingElement.getQualifiedName() + "]");
            }

            if (objectAndFields.containsKey(enclosingElement)) {
                objectAndFields.get(enclosingElement).add(element);
            } else {
                List<Element> bundleFields = new ArrayList<>();
                bundleFields.add(element);
                objectAndFields.put(enclosingElement, bundleFields);
            }

        }

    }

    private void generateJavaFile() {
        if (objectAndFields.isEmpty()) return;

        for (Map.Entry<TypeElement, List<Element>> entry : objectAndFields.entrySet()) {
            TypeElement object = entry.getKey();

            PackageElement packageElement = (PackageElement) object.getEnclosingElement();
            String className = object.getSimpleName().toString();
            String classPath = packageElement.getQualifiedName().toString() + "." + className;

            List<Element> fields = entry.getValue();
            List<BundleInfo> infoList = new ArrayList<>();

            for (Element element : fields) {
                Args fieldConfig = element.getAnnotation(Args.class);
                String fieldName = element.getSimpleName().toString();
                BundleInfo info = new BundleInfo();
                info.fieldName = fieldName;
                info.isThrowError = fieldConfig.required();
                info.fieldTypeName = typeUtils.getTypeName(element);
                info.fieldMethodName = "set" + StringUtils.toUpperCaseFirstOne(fieldName);
                info.fieldType = typeUtils.typeExchange(element);
                info.fieldImportName = typeUtils.getTypeImportName(element);
                infoList.add(info);
            }

            StringBuilder builder = new StringBuilder();
            builder.append("package ");
            builder.append(Constants.PACKAGE_NAME);
            builder.append(";");
            builder.append("\n");
            builder.append("\n");

            builder.append("/**");
            builder.append("\n");
            builder.append(Constants.EXPLAIN);
            builder.append("\n");
            builder.append(" */");
            builder.append("\n");

            builder.append("import android.os.Bundle;");
            builder.append("\n");
            builder.append("import android.net.Uri;");
            builder.append("\n");
            builder.append("import android.app.Activity;");
            builder.append("\n");
            builder.append("import android.content.Context;");
            builder.append("\n");
            builder.append("import android.content.Intent;");
            builder.append("\n");

            builder.append("import ")
                    .append(classPath)
                    .append(";")
                    .append("\n");

            builder.append("\n");
            builder.append("public final class ").append(className).append("Bundle {\n" +
                    "\n");
            builder.append("\n");


            /********************** builder ***********************/


            builder.append("    public static final class Builder {\n" +
                    "\n" +
                    "        private final Bundle args;\n" +
                    "        private Uri uri;\n" +
                    "        public Builder() {\n" +
                    "            this.args = new Bundle();\n" +
                    "        }\n").append("\n");


            for (BundleInfo info : infoList) {

                builder.append("        public ").append(className).append("Bundle.Builder ")
                        .append(info.fieldMethodName)
                        .append("(").append(info.fieldTypeName).append(" ").append(info.fieldName).append("){")
                        .append("\n")

                        .append("            ")
                        .append(buildPutDoc(info.fieldType, "\"" + info.fieldName + "\""
                                , info.fieldName,className,info.fieldTypeName))

                        .append("\n")
                        .append("            return this;\n" +
                                "        }")
                        .append("\n")
                ;
            }

            builder.append("        public ").append(className).append("Bundle.Builder withUri(Uri uri){\n" +
                    "            this.uri = uri;\n" +
                    "            return this;\n" +
                    "        }");

            builder.append("\n");
            builder.append("\n");

            builder.append("        public void lunch(Context ctx) {\n" +
                    "            if (ctx==null)return;\n" +
                    "            Intent intent = new Intent(ctx,").append(className).append(".class);\n" +
                    "            if (!(ctx instanceof Activity)) {\n" +
                    "                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);\n" +
                    "            }\n" +
                    "            if (uri!=null){\n" +
                    "                intent.setData(uri);\n" +
                    "            }\n" +
                    "            if(args!=null){\n" +
                    "                intent.putExtras(args);\n" +
                    "            }\n" +
                    "            ctx.startActivity(intent);\n" +
                    "        }");

            builder.append("\n");

            builder.append("        public void lunch(Context ctx,Class clazz) {\n" +
                    "            if (ctx==null)return;\n" +
                    "            Intent intent = new Intent(ctx,clazz);\n" +
                    "            if (!(ctx instanceof Activity)) {\n" +
                    "                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);\n" +
                    "            }\n" +
                    "            if (uri!=null){\n" +
                    "                intent.setData(uri);\n" +
                    "            }\n" +
                    "            if(args!=null){\n" +
                    "                intent.putExtras(args);\n" +
                    "            }\n" +
                    "            ctx.startActivity(intent);\n" +
                    "        }");

            builder.append("\n");


            builder.append("        public Bundle bundle() {\n" +
                    "            return args;\n" +
                    "        }\n" +
                    "\n" +
                    "    }").append("\n").append("\n");


            /*********************** bind-fun **********************/


            builder.append("    public static void bind(").append(className).append(" target) {\n" +
                    "        Intent intent = target.getIntent();\n" +
                    "        if (intent==null)return;\n" +
                    "        Bundle source = intent.getExtras();\n" +
                    "        if (source==null)return;\n");
            for (BundleInfo info : infoList) {

                if(info.isThrowError){
                    builder.append("        if (source.containsKey(\"").append(info.fieldName).append("\")) {\n" +
                            "            target.").append(info.fieldName)
                            .append(" = (").append(info.fieldTypeName)
                            .append(buildGetDoc(info.fieldType, info.fieldName,className,info.fieldTypeName))

                            .append("\n" +
                                    "        } else {\n" +
                                    "            throw new IllegalStateException(\"").append(info.fieldName)
                            .append(" is required, but not found in the bundle.\");\n" +
                                    "        }")
                            .append("\n");
                }else {
                    builder.append("        if (source.containsKey(\"").append(info.fieldName).append("\")) {\n" +
                            "            target.").append(info.fieldName)
                            .append(" = (").append(info.fieldTypeName)
                            .append(buildGetDoc(info.fieldType, info.fieldName,className,info.fieldTypeName))

                            .append("\n" +
                                    "        }")
                            .append("\n");
                }


            }

            builder.append("\n" +
                    "    }");


            builder.append("}");


            try {
                JavaFileObject source = filer.createSourceFile(Constants.PACKAGE_NAME
                        + "." + className + "Bundle");
                Writer writer = source.openWriter();
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }


        logger.info(">>>" + "end" + "<<<");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Args.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private String buildGetDoc(int type, String originalValue,String className,String typeName) {
        String doc = "";
        switch (TypeUtils.TypeKind.values()[type]) {
            case BOOLEAN:
                doc = ") source.getBoolean(" + "\"" + originalValue + "\");";
                break;
            case BYTE:
                doc = ") source.getByte(" + "\"" + originalValue + "\");";
                break;
            case SHORT:
                doc = ") source.getShort(" + "\"" + originalValue + "\");";
                break;
            case INT:
                doc = ") source.getInt(" + "\"" + originalValue + "\");";
                break;
            case LONG:
                doc = ") source.getLong(" + "\"" + originalValue + "\");";
                break;
            case CHAR:
                doc = ") source.getChar(" + "\"" + originalValue + "\");";
                break;
            case FLOAT:
                doc = ") source.getFloat(" + "\"" + originalValue + "\");";
                break;
            case DOUBLE:
                doc = ") source.getDouble(" + "\"" + originalValue + "\");";
                break;
            case STRING:
                doc = ") source.getString(" + "\"" + originalValue + "\");";
                break;
            case SERIALIZABLE:
                doc = ") source.getSerializable(" + "\"" + originalValue + "\");";
                break;
            case PARCELABLE:
                doc = ") source.getParcelable(" + "\"" + originalValue + "\");";
                break;
            case StringArrayList:
                doc = ") source.getStringArrayList(" + "\"" + originalValue + "\");";
                break;
            case IntegerArrayList:
                doc = ") source.getIntegerArrayList(" + "\"" + originalValue + "\");";
                break;
            default:
                logger.error("buildPutDoc - the @Args field \""+originalValue+"\" in "+className+" Bundle类型不支持: "+typeName);
                break;
        }

        return doc;
    }

    private String buildPutDoc(int type, String originalKey, String originalValue,String className,String typeName) {
        String doc = "";
        switch (TypeUtils.TypeKind.values()[type]) {
            case BOOLEAN:
                doc = "args.putBoolean(" + originalKey + "," + originalValue + ");";
                break;
            case BYTE:
                doc = "args.putByte(" + originalKey + "," + originalValue + ");";
                break;
            case SHORT:
                doc = "args.putShort(" + originalKey + "," + originalValue + ");";
                break;
            case INT:
                doc = "args.putInt(" + originalKey + "," + originalValue + ");";
                break;
            case LONG:
                doc = "args.putLong(" + originalKey + "," + originalValue + ");";
                break;
            case CHAR:
                doc = "args.putChar(" + originalKey + "," + originalValue + ");";
                break;
            case FLOAT:
                doc = "args.putFloat(" + originalKey + "," + originalValue + ");";
                break;
            case DOUBLE:
                doc = "args.putDouble(" + originalKey + "," + originalValue + ");";
                break;
            case STRING:
                doc = "args.putString(" + originalKey + "," + originalValue + ");";
                break;
            case SERIALIZABLE:
                doc = "args.putSerializable(" + originalKey + "," + originalValue + ");";
                break;
            case PARCELABLE:
                doc = "args.putParcelable(" + originalKey + "," + originalValue + ");";
                break;
            case StringArrayList:
                doc = "args.putStringArrayList(" + originalKey + "," + originalValue + ");";
                break;
            case IntegerArrayList:
                doc = "args.putIntegerArrayList(" + originalKey + "," + originalValue + ");";
                break;
            default:
                logger.error("buildPutDoc - the @Args field \""+originalValue+"\" in "+className+" Bundle类型不支持: "+typeName);
                break;
        }

        return doc;
    }


}
