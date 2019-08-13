package proxy;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import proxy.Jdk.Agent;
import proxy.Jdk.Game;
import proxy.Jdk.IGame;
import proxy.cglib.Customer;
import proxy.cglib.MiddleBussiness;
import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;

public class Main {


    public static void main(String[] args) {
            jdk();
         System.out.println("---------------");
         cglib();
    }



    public static void jdk(){
//        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles","true");
        Game g = new Game();
        Agent agent = new Agent(g);
        IGame game = (IGame) Proxy.newProxyInstance(Game.class.getClassLoader(), new Class[]{IGame.class}, agent);
        game.play();

    }



    public static void cglib(){
//        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "d://cglib");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Customer.class);
        enhancer.setCallback(new MiddleBussiness());
        Customer customer = (Customer) enhancer.create();
        customer.buyCar();
    }


}
