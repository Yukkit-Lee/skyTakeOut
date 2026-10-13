package com.sky.aspect;

import com.sky.anno.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author: 44219
 * @since: 2026/10/13 11:00
 * @description:自动填充切面实现类   (service实现类中可避免冗余代码)
 */
@Component
@Aspect //AOP类
public class AutoFillAspect {

    // 定义切入点表达式
    @Pointcut("@annotation(com.sky.anno.AutoFill)")
    private void autoFillAnnoPointCut() {
    }


    // 定义通知实现方法
    @Before("autoFillAnnoPointCut()")
    // TODO codex看到这里给我解释下为什么around方法返回值得是object
    public Object autoFillData(JoinPoint joinPoint) {
        // 获取拦截到的操作类型(INSERT / UPDATE)  ------- 反射获取方法名及注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType type = autoFill.value();

        // 获取到被拦截到的方法参数即实体对象(菜品 / 员工 )
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return null;
        Object entity = args[0];  //规定此类方法实体对象都在第一个参数位置

        //准备数据
        Long currentId = BaseContext.getCurrentId();
        LocalDateTime curTime = LocalDateTime.now();

        //反射赋值
        if (type == OperationType.INSERT) {
            try {
                // getDeclaredMethod()中写(原类中方法名称,参数.class )这里通过定义常量来写
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, curTime);  // 正式赋值
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, curTime);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, curTime);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
