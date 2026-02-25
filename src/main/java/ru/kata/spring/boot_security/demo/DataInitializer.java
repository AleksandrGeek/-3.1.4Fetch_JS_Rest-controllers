//package ru.kata.spring.boot_security.demo;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import ru.kata.spring.boot_security.demo.entities.Role;
//import ru.kata.spring.boot_security.demo.entities.User;
//import ru.kata.spring.boot_security.demo.services.RoleService;
//import ru.kata.spring.boot_security.demo.services.UserService;
//
//import javax.annotation.PostConstruct;
//import java.util.Set;
//
//@Slf4j
//@Component
//public class DataInitializer {
//
//
//    // ✅ сервисы
//    private final UserService userService;
//    private final RoleService roleService;
//
//    public DataInitializer(UserService userService, RoleService roleService) {
//        this.userService = userService;
//        this.roleService = roleService;
//    }
//
//    @PostConstruct
//    @Transactional
//    public void init() {
//        log.info("=== STARTING DATA INITIALIZATION ===");
//
//
//        // 1. Создаем роли через RoleService
//        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
//        Role userRole = createRoleIfNotExists("ROLE_USER");
//        log.info("✅ Roles ready: ADMIN, USER");
//
//        // 2. Создаем админа через UserService (пароль передаем сырой!)
//        if (userService.findByEmail("admin@test.com") == null) {
//            User admin = new User();
//            admin.setFirstName("Admin");
//            admin.setLastName("Adminov");
//            admin.setAge(30);
//            admin.setEmail("admin@test.com");
//            admin.setPassword("admin");
//
//            userService.createUser(admin, Set.of(adminRole.getId(), userRole.getId()));
//            log.info("Admin user created: {} (id: {})", admin.getUsername(), admin.getId());
//        }
//
//        // 3. Создаем пользователя через UserService
//        if (userService.findByEmail("user@test.com") == null) {
//            User user = new User();
//            user.setFirstName("user");
//            user.setLastName("Userov");
//            user.setAge(25);
//            user.setEmail("user@test.com");
//            user.setPassword("user");
//
//            userService.createUser(user, Set.of(userRole.getId()));
//            log.info("Regular user created: {} (id: {})", user.getUsername(), user.getId());
//        }
//
//        log.info("=== DATA INITIALIZATION COMPLETE ===");
//
//
//    }
//
//    private Role createRoleIfNotExists(String roleName) {
//        try {
//            return roleService.findByName(roleName);
//        } catch (Exception e) {
//            return roleService.createRole(roleName);
//        }
//    }
//}