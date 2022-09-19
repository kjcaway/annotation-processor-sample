package me.anno;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MyLogAnnotationFilter extends AnnotationTypeFilter {

    public MyLogAnnotationFilter(
            Class<? extends Annotation> annotationType) {
        super(annotationType);
    }

    @Override
    protected boolean matchSelf(MetadataReader metadataReader) {
        System.out.println("MyLogAnnotationFilter matchSelf Start");
        if (super.matchSelf(metadataReader)) {
            return true;
        }
        String className = metadataReader.getClassMetadata().getClassName();
        try {
            Class<?> clazz = ClassUtils.forName(className, getClass().getClassLoader());
            if (annotationExistsOnAMethod(clazz)) {
                System.out.printf("1: matches %s\n", className);
                return true; // matches 'Hoge.java' and 'Foo.java'
            }

            for (Method m : clazz.getDeclaredMethods()) {
                if (annotationExistsOnAMethod(m.getReturnType())) {
                    System.out.printf("2: matches %s\n", className);
                    return true; // matches 'Baz.java' and 'Fuga.java'
                }
            }

        } catch (ClassNotFoundException e) {
            // ignore
        }
        return false;
    }

    private boolean annotationExistsOnAMethod(Class<?> clazz) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (AnnotationUtils.findAnnotation(m, this.getAnnotationType()) != null) {
                return true;
            }
        }
        return false;
    }
}