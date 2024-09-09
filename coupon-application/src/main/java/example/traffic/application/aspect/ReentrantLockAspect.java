package example.traffic.application.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Aspect
@Component
public class ReentrantLockAspect {

    Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Around("@annotation(lockSync)")
    public Object doLock(ProceedingJoinPoint joinPoint, LockSync lockSync) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        ReentrantLock lock = lockMap.computeIfAbsent(methodName, k -> new ReentrantLock());

        boolean acquired = false;
        try {
            acquired = lock.tryLock(lockSync.timeout(), TimeUnit.MILLISECONDS);
            if (acquired) {
                return joinPoint.proceed();
            } else {
                throw new RuntimeException("Unable to acquire lock for method: " + methodName);
            }
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }

    }
}
