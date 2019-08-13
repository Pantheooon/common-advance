package proxy.cglib;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MiddleBussiness implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        System.out.println("中间商赚差价");
        return methodProxy.invokeSuper(o, objects);
    }
}
