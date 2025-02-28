package practice.jwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    /* AdminController로 이관됨
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
    */
}
