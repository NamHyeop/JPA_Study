package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class testController {
    @GetMapping("/please")
    public String testme(){
        log.info("test");
        return "testfile";
    }

//    @RequestMapping("/")
//    public String home(){
//        log.info("home controller");
//        return "home";
//    }
}

//@Controller
//@Slf4j
//public class HomeController {
//    @RequestMapping("/")
//    public String home(){
//        log.info("home controller");
//        return "home";
//    }
//}
