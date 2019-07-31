package com.sohu.hy;

import com.sohu.hy.annotation.Args;
import com.sohu.hy.model.AutoBundleInfo;
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

public class AutoBundleProcessor extends AbstractProcessor {

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
            List<AutoBundleInfo> autoBundleInfos = new ArrayList<>();

            for (Element element : fields) {
                Args fieldConfig = element.getAnnotation(Args.class);
                String fieldName = element.getSimpleName().toString();
                AutoBundleInfo info = new AutoBundleInfo();
                info.fieldName = fieldName;
                info.fieldTypeName = typeUtils.getTypeName(element);
                info.fieldMethodName = "set"+StringUtils.toUpperCaseFirstOne(fieldName);
                info.fieldType = typeUtils.typeExchange(element);
                info.fieldImportName = typeUtils.getTypeImportName(element);
                autoBundleInfos.add(info);
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
            for (AutoBundleInfo info : autoBundleInfos) {
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


            for (AutoBundleInfo info : autoBundleInfos) {

                builder.append("        public ").append(className).append("Bundle.Builder ")
                        .append(info.fieldMethodName)
                        .append("(").append(info.fieldTypeName).append(" ").append(info.fieldName).append("){")
                        .append("\n")

                        .append("            ")
                        .append(buildPutStatement(info.fieldType,"\""+info.fieldName+"\"",info.fieldName))

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

            for (AutoBundleInfo info : autoBundleInfos) {
                builder.append("    public static void bind(").append(className).append(" target) {\n" +
                        "        Intent intent = target.getIntent();\n" +
                        "        if (intent==null)return;\n" +
                        "        Bundle source = intent.getExtras();\n" +
                        "        if (source==null)return;\n")

                        .append("       if (source.containsKey(\"").append(info.fieldName).append("\")) {\n" +
                                "            target.").append(info.fieldName)
                        .append(" = (").append(info.fieldTypeName)
                        .append(buildGetStatement(info.fieldType,info.fieldName))

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

    private String buildGetStatement(int type,String originalValue){
        String statement = "";
        switch (TypeUtils.TypeKind.values()[type]) {
            case BOOLEAN:
                statement = ") source.getBoolean(" + "\"" + originalValue + "\");";
                break;
            case BYTE:
                statement = ") source.getByte(" + "\"" + originalValue + "\");";
                break;
            case SHORT:
                statement = ") source.getShort(" + "\"" + originalValue + "\");";
                break;
            case INT:
                statement = ") source.getInt(" + "\"" + originalValue + "\");";
                break;
            case LONG:
                statement = ") source.getLong(" + "\"" + originalValue + "\");";
                break;
            case CHAR:
                statement = ") source.getChar(" + "\"" + originalValue + "\");";
                break;
            case FLOAT:
                statement = ") source.getFloat(" + "\"" + originalValue + "\");";
                break;
            case DOUBLE:
                statement = ") source.getDouble(" + "\"" + originalValue + "\");";
                break;
            case STRING:
                statement = ") source.getString(" + "\"" + originalValue + "\");";
                break;
            case SERIALIZABLE:
                statement = ") source.getSerializable(" + "\"" + originalValue + "\");";
                break;
            case PARCELABLE:
                statement = ") source.getParcelable(" + "\"" + originalValue + "\");";
                break;
        }

        return statement;
    }

    private String buildPutStatement(int type,String originalKey,String originalValue){
        String statement = "";
        switch (TypeUtils.TypeKind.values()[type]) {
            case BOOLEAN:
                statement = "args.putBoolean(" + originalKey+ "," + originalValue +");";
                break;
            case BYTE:
                statement = "args.putByte("+ originalKey+ "," + originalValue + ");";
                break;
            case SHORT:
                statement = "args.putShort("+ originalKey+ "," + originalValue + ");";
                break;
            case INT:
                statement = "args.putInt("+ originalKey+ "," + originalValue + ");";
                break;
            case LONG:
                statement = "args.putLong("+ originalKey+ "," + originalValue + ");";
                break;
            case CHAR:
                statement = "args.putChar("+ originalKey+ "," + originalValue + ");";
                break;
            case FLOAT:
                statement = "args.putFloat("+ originalKey+ "," + originalValue + ");";
                break;
            case DOUBLE:
                statement = "args.putDouble("+ originalKey+ "," + originalValue + ");";
                break;
            case STRING:
                statement = "args.putString("+ originalKey+ "," + originalValue + ");";
                break;
            case SERIALIZABLE:
                statement = "args.putSerializable("+ originalKey+ "," + originalValue + ");";
                break;
            case PARCELABLE:
                statement = "args.putParcelable("+ originalKey+ "," + originalValue + ");";
                break;
        }

        return statement;
    }



}
