package proxy.Jdk;

import proxy.cglib.Customer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Agent implements InvocationHandler {


    public IGame game;


    public Agent(IGame game) {
        this.game = game;
    }

    public void agent() {
        System.out.println("挂机");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        agent();
        return method.invoke(game);
    }
}
