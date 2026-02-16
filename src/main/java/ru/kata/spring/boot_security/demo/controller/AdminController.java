package ru.kata.spring.boot_security.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {


    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService,
                           RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String adminPage(@AuthenticationPrincipal User user,
                            @RequestParam(required = false) String role,
                            Model model) {

        // Получаем пользователей в зависимости от выбранной роли
        List<User> users;
        if (role == null || role.equals("ALL")) {
            users = userService.getAllUsers();
        } else {
            users = userService.getUsersByRole(role);
        }

        model.addAttribute("users", users);
        model.addAttribute("user", user);
        model.addAttribute("selectedRole", role);
        model.addAttribute("allRoles", roleService.getAllRoles());

        return "admin";
    }

    // ✅ CRUD операции
    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin_new";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user,
                             @RequestParam(required = false) Set<Long> roleIds) {
        User createdUser = userService.createUser(user, roleIds);
        log.info("Создан пользователь с id: {}", createdUser.getId());
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin_edit";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user,
                             @RequestParam(required = false) Set<Long> roleIds) {
        if (roleIds == null) roleIds = new HashSet<>();
        userService.updateUser(user, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}