package com.AAZl3l4.common.configuration;

import com.AAZl3l4.common.pojo.AopLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// AOP配置
@Component
@Aspect
@Slf4j
public class AopConfig {

    // AOP日志方法
    @Around("@annotation(aopLog)")
    public Object Aoplog(ProceedingJoinPoint pjp, AopLog aopLog) throws Throwable {

        String desc = aopLog.value();
        // 方法名
        String method = pjp.getSignature().toShortString();
        // 参数
        Object[] args = pjp.getArgs();

        //获取执行耗时
        long start = System.currentTimeMillis();
        // 执行方法
        Object result = pjp.proceed();
        long end = System.currentTimeMillis();
        long time = end - start;

        // 返回值
        String returnValue = result == null ? "null" : result.toString();

        log.info("描述：{}，方法名：{}，参数：{}，返回值：{}，耗时：{}ms", desc, method, args, returnValue,time);

        return result;
    }
}
