package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.entities.Role;

import java.util.List;
import java.util.Set;


public interface RoleService {
    List<Role> getAllRoles();

    Role getRoleById(Long id);

    Role findByName(String name);

    Role createRole(String roleName);

    Set<Role> getRolesByIds(Set<Long> roleIds);
}

