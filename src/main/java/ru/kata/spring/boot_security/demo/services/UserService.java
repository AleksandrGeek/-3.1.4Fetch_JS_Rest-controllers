package ru.kata.spring.boot_security.demo.services;


import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;
import java.util.Set;

public interface UserService {


    User createUser(User user, Set<Long> roleIds);

    User getUserById(Long id);

    List<User> getAllUsers();

    void updateUser(User user, Set<Long> roleIds);

    void deleteUser(Long id);

    User findByEmail(String email);

    List<User> getUsersByRole(String role);
}

