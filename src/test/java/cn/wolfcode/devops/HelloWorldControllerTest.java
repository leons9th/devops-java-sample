package cn.wolfcode.devops;

import cn.wolfcode.devops.HelloWorldController;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelloWorldControllerTest {

    @Test
    public void testSayHello() {
        assertEquals("Hello DevOps...", new HelloWorldController().sayHello());
    }
}
