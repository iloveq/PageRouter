package com.sohu.hy;

import com.sohu.hy.annotation.Route;
import com.sohu.hy.utils.Constants;
import com.sohu.hy.utils.Logger;

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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

public class RouterDispatcherProcessor extends AbstractProcessor {

    private Logger logger;
    private Filer filer;
    private Map<String,String> routes = new HashMap<>();
    private List<String> impList = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        logger = new Logger(processingEnv.getMessager());
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Route.class);
        if (elements.isEmpty()) return false;
        prepare(elements);
        generateJavaFile();
        return true;
    }

    private void prepare(Set<? extends Element> elements) {
        for (Element element : elements) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
                String className = classElement.getSimpleName().toString();
                String classPath = packageElement.getQualifiedName().toString() + "." + className;
                logger.info("routerPath:" + classPath);
                Route annotation = element.getAnnotation(Route.class);
                String routePath = annotation.path();
                routes.put(routePath,className);
                impList.add(classPath);
            }
        }
    }

    private void generateJavaFile() {
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
        for (String classPath : impList) {
             builder.append("import ")
                    .append(classPath)
                    .append(";")
                    .append("\n");
        }

        builder.append("\n");
        builder.append("public final class RouterDispatcher {\n" +
                "\n" +
                "    public static void go(Context ctx,String path,Bundle extras, Uri uri){ \n" +
                "        if(ctx==null)return;\n" +
                "        Intent intent = null;\n" +
                "        switch (path){");
        builder.append("\n");

        for(Map.Entry<String, String> entry : routes.entrySet()){
             String k = entry.getKey();
             String v = entry.getValue();
             builder
                    .append("            case \"").append(k).append("\":")
                    .append("\n")
                    .append("               intent = new Intent(ctx, ")
                    .append(v)
                    .append(".class);")
                    .append("\n")
                    .append("            break;").append("\n");
        }

        builder.append("\n");

        builder.append("        }\n" +
                "        if (intent == null) return;\n" +
                "        if (extras != null) {\n" +
                "            intent.putExtras(extras);\n" +
                "        }\n" +
                "        if (!(ctx instanceof Activity)) {\n" +
                "            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);\n" +
                "        }\n" +
                "        if (uri != null){\n" +
                "            intent.setData(uri);\n" +
                "        }\n" +
                "        ctx.startActivity(intent);\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "}");



        try {
            JavaFileObject source = filer.createSourceFile(Constants.PACKAGE_NAME
                    + "." + "RouterDispatcher");
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        logger.info(">>>" + "end" + "<<<");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Route.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }



}
