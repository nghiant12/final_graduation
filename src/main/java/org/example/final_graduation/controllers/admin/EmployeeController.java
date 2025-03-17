package org.example.final_graduation.controllers.admin;

import org.example.final_graduation.entities.Employee;
import org.example.final_graduation.entities.Role;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.example.final_graduation.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Danh sách nhân viên (Chỉ hiển thị nhân viên có role_id = 1)
    @GetMapping("")
    public String index(Model model) {
        List<Employee> employees = employeeRepository.findByRoleId(1);
        model.addAttribute("employees", employees);
        model.addAttribute("employee", new Employee());
        return "admin/employees/index";
    }

    // Thêm nhân viên mới
    @PostMapping("/add")
    public String add(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        // Kiểm tra trùng tên nhân viên
        if (employeeRepository.existsByUsername(employee.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "redirect:/admin/employees";
        }

        // Kiểm tra trùng email
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
            return "redirect:/admin/employees";
        }

        // Đặt mặc định role_id = 1 nếu chưa có
        Role defaultRole = roleRepository.findById(1).orElse(null);
        if (defaultRole != null) {
            employee.setRole(defaultRole);
        }

        // Lưu nhân viên mới
        employeeRepository.save(employee);
        redirectAttributes.addFlashAttribute("success", "Thêm nhân viên thành công!");
        return "redirect:/admin/employees";
    }

    // Cập nhật nhân viên (từ modal)
    @PostMapping("/update")
    public String edit(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employee.getId());
        if (existingEmployeeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên để cập nhật.");
            return "redirect:/admin/employees";
        }

        Employee existingEmployee = existingEmployeeOpt.get();

        // Kiểm tra trùng email với nhân viên khác
        if (!existingEmployee.getEmail().equals(employee.getEmail()) && employeeRepository.existsByEmail(employee.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
            return "redirect:/admin/employees";
        }

        // Cập nhật thông tin
        existingEmployee.setFullname(employee.getFullname());
        existingEmployee.setUsername(employee.getUsername());
        existingEmployee.setPhoneNumber(employee.getPhoneNumber());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setAddress(employee.getAddress());

        employeeRepository.save(existingEmployee);

        redirectAttributes.addFlashAttribute("success", "Cập nhật nhân viên thành công!");
        return "redirect:/admin/employees";
    }

    // Xóa nhân viên
    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nhân viên không tồn tại!");
            return "redirect:/admin/employees";
        }

        employeeRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Xóa nhân viên thành công!");
        return "redirect:/admin/employees";
    }
}