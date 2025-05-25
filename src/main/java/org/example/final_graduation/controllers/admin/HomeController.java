package org.example.final_graduation.controllers.admin;

import jakarta.servlet.http.HttpSession;
import org.example.final_graduation.entities.Employee;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.example.final_graduation.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class HomeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("")
    public String index(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
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

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboards/index";
    }

    @ResponseBody
    @GetMapping("/dashboard/stats")
    public java.util.Map<String, Object> dashboardStats(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate
    ) {
        return dashboardService.getDashboardStats(startDate, endDate);
    }
}
