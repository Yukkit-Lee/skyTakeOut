package com.sky.anno;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: 44219
 * @since: 2026/10/13 10:55
 * @description:none
 */
@Retention(RetentionPolicy.RUNTIME)  //运行时生效
@Target(ElementType.METHOD)  //在方法上生效
public @interface AutoFill {

    // TODO  codex看到这里给我解释一下为什么要这样做 以及他这个语法是为什么(重点解释)？
    OperationType value();
}
