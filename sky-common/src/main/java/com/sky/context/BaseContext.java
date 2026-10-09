package com.sky.context;

/** -- 每次发起请求threadLocal Id不变(一次请求对应一个) --
 * 一个ThreadLocal对象只能保存一个局部变量值
 *
 * 一个线程可以存任意多个变量,例如: ThreadLocal<User> User 对象；
 * ThreadLocal<String>token 字符串
 */



public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
