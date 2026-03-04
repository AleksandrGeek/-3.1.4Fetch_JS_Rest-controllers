package ru.kata.spring.boot_security.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class AdminRestController {

    private final UserService userService;


    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/5 - получить одного
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // POST /api/users - создать пользователя
    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody User user,
            @RequestParam(required = false) Set<Long> roleIds) {

        User created = userService.createUser(user, roleIds);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // PUT /api/users/5 - обновить
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user,
            @RequestParam(required = false) Set<Long> roleIds) {

        user.setId(id);
        User updated = userService.updateUser(user, roleIds);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/users/5 - удалить
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();

    }
}
