package me.anno;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
public class MyLogProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // decide what annotation process
        return Set.of(MyLogProcessor.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_11;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> eles = roundEnv.getElementsAnnotatedWith(MyLog.class);
        for(Element element: eles){
            Name name = element.getSimpleName();
            if(element.getKind() == ElementKind.INTERFACE){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "me.anno.MyLog annotation cannot be used on " + name);
            } else{
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + name);
            }

            TypeElement typeElement = (TypeElement) element;
            ClassName className = ClassName.get(typeElement);

            MethodSpec getMember = MethodSpec.methodBuilder("getMember")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String.class)
                    .addStatement("return $S", "me.anno.MyLog")
                    .build();

            TypeSpec MyLog = TypeSpec.classBuilder("me.anno.MyLog")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(getMember)
                    .build();

            Filer filer = processingEnv.getFiler(); // filer interface can make source file
            try {
                JavaFile.builder(className.packageName(), MyLog)
                        .build()
                        .writeTo(filer);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR:" + e);
            }
        }
        return true; // if it is true, processing is over, not chaining
    }

}
