package org.example.final_graduation.controllers.admin.products.attributes;

import org.example.final_graduation.entities.Account;
import org.example.final_graduation.repositories.products.attributes.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    // Hiển thị danh sách tài khoản có role là "user"
    @GetMapping("")
    public String index(Model model) {
        List<Account> accounts = accountRepository.findAccountsByRole("user"); // Chỉ lấy tài khoản có quyền "user"
        model.addAttribute("accounts", accounts);
        model.addAttribute("account", new Account());
        return "admin/attributes/accounts/index"; // Trả về trang quản lý tài khoản
    }

    // Thêm tài khoản mới
    @PostMapping("/add")
    public String add(@ModelAttribute Account account, RedirectAttributes redirectAttributes) {
        if (accountRepository.existsByUsername(account.getUsername()) ||
                accountRepository.existsByEmail(account.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Username hoặc email đã tồn tại!");
            return "redirect:/admin/accounts";
        }

        accountRepository.save(account);
        redirectAttributes.addFlashAttribute("success", "Thêm khách hàng thành công!");
        return "redirect:/admin/accounts";
    }

    // Hiển thị form chỉnh sửa tài khoản
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Account accountEdit = accountRepository.findByID(id);

        if (accountEdit == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
            return "redirect:/admin/accounts";
        }

        model.addAttribute("account", accountEdit);
        List<Account> accounts = accountRepository.findAll();
        model.addAttribute("accounts", accounts);
        return "admin/attributes/accounts/index";
    }

    // Cập nhật thông tin tài khoản
//    @PostMapping("/update")
//    public String edit(@ModelAttribute Account account, RedirectAttributes redirectAttributes) {
//        if (accountRepository.existsByUsername(account.getUsername()) ||
//                accountRepository.existsByEmail(account.getEmail())) {
//            redirectAttributes.addFlashAttribute("error", "Username hoặc email đã tồn tại!");
//            return "redirect:/admin/accounts";
//        }
//
//        accountRepository.save(account);
//        redirectAttributes.addFlashAttribute("success", "Cập nhật khách hàng thành công!");
//        return "redirect:/admin/accounts";
//    }
    @PostMapping("/update")
    public String edit(@ModelAttribute Account account, RedirectAttributes redirectAttributes) {
        Account existingAccount = accountRepository.findById(account.getId()).orElse(null);

        if (existingAccount == null) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản không tồn tại!");
            return "redirect:/admin/accounts";
        }

        // Kiểm tra username và email nhưng không cho phép thay đổi
        boolean usernameExists = accountRepository.existsByUsername(account.getUsername()) &&
                !existingAccount.getUsername().equals(account.getUsername());

        if (usernameExists) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "redirect:/admin/accounts";
        }

        boolean emailExists = accountRepository.existsByEmail(account.getEmail()) &&
                !existingAccount.getEmail().equals(account.getEmail());

        if (emailExists) {
            redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
            return "redirect:/admin/accounts";
        }

        // Giữ nguyên username, email và mật khẩu
        account.setUsername(existingAccount.getUsername());
        account.setEmail(existingAccount.getEmail());
        account.setPassword(existingAccount.getPassword()); // Giữ nguyên mật khẩu

        // Cập nhật thông tin khác
        accountRepository.save(account);
        redirectAttributes.addFlashAttribute("success", "Cập nhật tài khoản thành công!");
        return "redirect:/admin/accounts";
    }

}