package ru.kata.spring.boot_security.demo.dao;


import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;
import java.util.Optional;


public interface UserDao {
    User createUser(User user);

    Optional<User> getById(Long id);

    List<User> getAll();

    // ✅ Возвращаем обновленного пользователя
    User update(User user);

    void delete(Long id);

    Optional<User> findByEmail(String email);

    List<User> findByRole(String roleName);

}