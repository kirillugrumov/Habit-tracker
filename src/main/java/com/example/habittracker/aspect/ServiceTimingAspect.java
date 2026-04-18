package com.example.habittracker.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceTimingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceTimingAspect.class);

    @Around("execution(* com.example.habittracker.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            log.info("Executed {}.{} in {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    durationMs);
            return result;
        } catch (Throwable ex) {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            log.warn("Failed {}.{} after {} ms: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    durationMs,
                    ex.getMessage());
            throw ex;
        }
    }
}
