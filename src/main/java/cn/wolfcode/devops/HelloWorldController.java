package cn.wolfcode.devops;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 * @author Leon
 */
@RestController
public class HelloWorldController {

    @GetMapping("/")
    public String sayHello() {
        return "Hello DevOps...";
    }
}
