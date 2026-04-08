package com.mipt.sudarkingeorgiy.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/** Логирует вызовы методов в service*/
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.mipt.sudarkingeorgiy.service..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("[Service] Start: {} args: {}", methodName, Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            log.error("[Service] End: {} -> exception: {}", methodName, t.getMessage(), t);
            throw t;
        }

        if (result != null) {
            log.info("[Service] End: {} -> result: {}", methodName, formatResult(result));
        } else {
            log.info("[Service] End: {} -> result: (void/null)", methodName);
        }
        return result;
    }

    private static String formatResult(Object result) {
        if (result instanceof Collection<?> col) {
            return "Collection(size=" + col.size() + ")";
        }
        if (result instanceof Map<?, ?> map) {
            return "Map(size=" + map.size() + ")";
        }
        if (result instanceof Optional<?> opt) {
            return opt.isPresent() ? "Optional(" + opt.get() + ")" : "Optional.empty";
        }
        return String.valueOf(result);
    }
}

