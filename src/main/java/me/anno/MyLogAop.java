package me.anno;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class MyLogAop {
    @Around("@annotation(me.anno.MyLog)")
    public Object myLogAop(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        MyLog annotation = method.getAnnotation(MyLog.class);

        long start = System.currentTimeMillis();

        Thread.sleep(2000);

        try {
            return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        } finally {
            long end = System.currentTimeMillis();

            System.out.println(">>> Spend time: " + (end - start));
        }
    }
}