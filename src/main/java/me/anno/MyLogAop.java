package me.anno;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Component
public class MyLogAop {
    @DeclareError(
            "@annotation(me.anno.MyLog) && " +
                    "execution(* *(..)) && " +
                    "!execution(java.lang.String+ *(..))"
    )
    static final String wrongSignatureError =
            "Method annotated with @MyLog must return String type";     // it's not working..

    private final StringRedisTemplate redisTemplate;

    public MyLogAop(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(me.anno.MyLog)")
    public Object myLogAop(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String uuid = UUID.randomUUID().toString();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        MyLog annotation = method.getAnnotation(MyLog.class);

        long start = System.currentTimeMillis();
        pushEvent(uuid, uuid);
        Thread.sleep(2000);

        try {
            proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
            return uuid;
        } finally {
            long end = System.currentTimeMillis();
            System.out.println(">>> return value: " + uuid);
            System.out.println(">>> spend time: " + (end - start));
            pushEvent(uuid + ":time", (end - start) + "ms");
        }
    }

    private void pushEvent(String key, String value){
        redisTemplate.opsForList().leftPush(key, value);
    }
}