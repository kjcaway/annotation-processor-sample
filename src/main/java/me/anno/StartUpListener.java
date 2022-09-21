package me.anno;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class StartUpListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        System.out.println("=======================");
        System.out.println("start annotation scan..");
        System.out.println("=======================");
        annotationScan();
    }

    private void annotationScan(){
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);

        provider.addIncludeFilter(new AnnotationTypeFilter(MyLog.class));

        Set<BeanDefinition> beanDefs = provider.findCandidateComponents("me.javaexample.javademo");
        for (BeanDefinition bd : beanDefs) {
            if (bd instanceof AnnotatedBeanDefinition) {
                AnnotationMetadata metadata = ((AnnotatedBeanDefinition) bd).getMetadata();
                Set<MethodMetadata> methodMetadatas = metadata.getAnnotatedMethods(MyLog.class.getCanonicalName());
                for (MethodMetadata methodMetadata : methodMetadatas) {
                    if(!methodMetadata.getReturnTypeName().equals(String.class.getCanonicalName())){
                        String errorMessage = "Error for Class " + bd.getBeanClassName() + "." +
                                " Method annotated " + MyLog.class.getCanonicalName() + " must be return " + String.class.getCanonicalName() + "." +
                                " But " + methodMetadata.getMethodName() + " method return " + methodMetadata.getReturnTypeName() + ".";
                        throw new ApplicationContextException(errorMessage);
                    }
                }
            }
        }
    }
}