package org.example.final_graduation.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class HomeController {
    @GetMapping("")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/401")
    public String get401() {
        return "admin/401";
    }

    @GetMapping("/404")
    public String get404() {
        return "admin/404";
    }

    @GetMapping("/500")
    public String get500() {
        return "admin/500";
    }

}
