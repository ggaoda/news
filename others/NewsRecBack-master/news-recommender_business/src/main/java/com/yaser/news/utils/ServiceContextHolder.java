package com.yaser.news.utils;

public class ServiceContextHolder {
    private static final ThreadLocal<ServiceContext> threadLocal = new ThreadLocal<>();

    public static ServiceContext getContext() {
        return threadLocal.get();
    }

    public static boolean hasLogin() {
        return threadLocal.get() != null;
    }

    public static void setContext(ServiceContext sc) {
        if (threadLocal.get() != null) {
            threadLocal.remove();
        }
        threadLocal.set(sc);
    }

    public static void cleanUpContext() {
        if (threadLocal.get() != null) {
            threadLocal.remove();
        }
    }
}
