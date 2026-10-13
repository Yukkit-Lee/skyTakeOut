package com.sky.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author: 44219
 * @since: 2026/10/13 11:00
 * @description:自动填充切面实现类
 */
@Component
@Aspect //AOP类
public class AutoFillAspect {

    // 定义切入点表达式
    @Pointcut("@annotation(com.sky.anno.AutoFill)")
    private void autoFillAnnoPointCut(){}


    // 定义通知实现方法
    @Before("autoFillAnnoPointCut()")
    // TODO codex看到这里给我解释下为什么around方法返回值得是object
    public Object autoFillData(JoinPoint joinPoint){
        // 获取拦截到的操作类型(INSERT / UPDATE)

        // 获取到被拦截到的方法参数即实体对象(菜品 / 员工 )

        return null;
    }
}
