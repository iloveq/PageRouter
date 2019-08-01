package com.sohu.hy;

import com.sohu.hy.annotation.Args;
import com.sohu.hy.model.BundleInfo;
import com.sohu.hy.utils.Constants;
import com.sohu.hy.utils.Logger;
import com.sohu.hy.utils.StringUtils;
import com.sohu.hy.utils.TypeUtils;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

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

        for (Map.Entry<TypeElement, List<Element>> entry : objectAndFields.entrySet()){
            TypeElement object = entry.getKey();

            PackageElement packageElement = (PackageElement) object.getEnclosingElement();
            String className = object.getSimpleName().toString();
            String classPath = packageElement.getQualifiedName().toString() + "." + className;

            List<Element> fields = entry.getValue();
            List<BundleInfo> bundleInfos = new ArrayList<>();

            for (Element element : fields) {
                Args fieldConfig = element.getAnnotation(Args.class);
                String fieldName = element.getSimpleName().toString();
                BundleInfo info = new BundleInfo();
                info.fieldName = fieldName;
                info.fieldTypeName = typeUtils.getTypeName(element);
                info.fieldMethodName = "set"+StringUtils.toUpperCaseFirstOne(fieldName);
                info.fieldType = typeUtils.typeExchange(element);
                info.fieldImportName = typeUtils.getTypeImportName(element);
                bundleInfos.add(info);
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
            builder.append("import android.content.Intent;");
            builder.append("\n");
            for (BundleInfo info : bundleInfos) {
                if (info.fieldImportName.isEmpty())continue;
                builder.append("import ")
                        .append(info.fieldImportName)
                        .append(";")
                        .append("\n");
            }
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
                    "        \n" +
                    "        public Builder() {\n" +
                    "            this.args = new Bundle();\n" +
                    "        }\n").append("\n");


            for (BundleInfo info : bundleInfos) {

                builder.append("        public ").append(className).append("Bundle.Builder ")
                        .append(info.fieldMethodName)
                        .append("(").append(info.fieldTypeName).append(" ").append(info.fieldName).append("){")
                        .append("\n")

                        .append("            ")
                        .append(buildPutDoc(info.fieldType,"\""+info.fieldName+"\"",info.fieldName))

                        .append("\n")
                        .append("            return this;\n" +
                                "        }")
                        .append("\n")
                ;
            }




            builder.append("        public Bundle bundle() {\n" +
                    "            return args;\n" +
                    "        }\n" +
                    "\n" +
                    "    }").append("\n");


            /*********************** bind-fun **********************/

            for (BundleInfo info : bundleInfos) {
                builder.append("    public static void bind(").append(className).append(" target) {\n" +
                        "        Intent intent = target.getIntent();\n" +
                        "        if (intent==null)return;\n" +
                        "        Bundle source = intent.getExtras();\n" +
                        "        if (source==null)return;\n")

                        .append("       if (source.containsKey(\"").append(info.fieldName).append("\")) {\n" +
                                "            target.").append(info.fieldName)
                        .append(" = (").append(info.fieldTypeName)
                        .append(buildGetDoc(info.fieldType,info.fieldName))

                        .append("\n" +
                                "        } else {\n" +
                                "            throw new IllegalStateException(\"").append(info.fieldName)
                        .append(" is required, but not found in the bundle.\");\n" +
                                "        }");
                builder.append("\n" +
                        "    }");
            }





            builder.append("}");


            try {
                JavaFileObject source = filer.createSourceFile(Constants.PACKAGE_NAME
                        + "." + className+"Bundle");
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

    private String buildGetDoc(int type, String originalValue){
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
        }

        return doc;
    }

    private String buildPutDoc(int type, String originalKey, String originalValue){
        String doc = "";
        switch (TypeUtils.TypeKind.values()[type]) {
            case BOOLEAN:
                doc = "args.putBoolean(" + originalKey+ "," + originalValue +");";
                break;
            case BYTE:
                doc = "args.putByte("+ originalKey+ "," + originalValue + ");";
                break;
            case SHORT:
                doc = "args.putShort("+ originalKey+ "," + originalValue + ");";
                break;
            case INT:
                doc = "args.putInt("+ originalKey+ "," + originalValue + ");";
                break;
            case LONG:
                doc = "args.putLong("+ originalKey+ "," + originalValue + ");";
                break;
            case CHAR:
                doc = "args.putChar("+ originalKey+ "," + originalValue + ");";
                break;
            case FLOAT:
                doc = "args.putFloat("+ originalKey+ "," + originalValue + ");";
                break;
            case DOUBLE:
                doc = "args.putDouble("+ originalKey+ "," + originalValue + ");";
                break;
            case STRING:
                doc = "args.putString("+ originalKey+ "," + originalValue + ");";
                break;
            case SERIALIZABLE:
                doc = "args.putSerializable("+ originalKey+ "," + originalValue + ");";
                break;
            case PARCELABLE:
                doc = "args.putParcelable("+ originalKey+ "," + originalValue + ");";
                break;
        }

        return doc;
    }



}
