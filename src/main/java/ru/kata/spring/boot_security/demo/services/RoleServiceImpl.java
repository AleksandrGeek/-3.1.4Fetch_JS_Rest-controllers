package ru.kata.spring.boot_security.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.entities.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleDao.getAllRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleDao.getRoleById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with id: {}", id);  // ✅ Только ошибка!
                    return new IllegalArgumentException("Role not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleDao.findByName(name)
                .orElseThrow(() -> {
                    log.error("Role not found with name: {}", name);  // ✅ Только ошибка!
                    return new IllegalArgumentException("Role not found with name: " + name);
                });
    }

    @Override
    @Transactional
    public Role createRole(String roleName) {
        // Проверяем, есть ли уже такая роль
        try {
            return findByName(roleName);  // Если есть - возвращаем
        } catch (IllegalArgumentException e) {
            // Создаем новую роль
            Role role = new Role(roleName);
            roleDao.save(role);
            log.info("Created new role: {}", roleName);  // ✅ Важно! Создание роли
            return role;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getRolesByIds(Set<Long> roleIds) {
        log.debug("Getting roles by IDs: {}", roleIds);

        Set<Role> roles = new HashSet<>();
        Set<Long> notFoundIds = new HashSet<>();

        for (Long roleId : roleIds) {
            try {
                Role role = getRoleById(roleId);
                roles.add(role);
            } catch (IllegalArgumentException e) {
                notFoundIds.add(roleId);
                log.warn("Role not fount with id: {}", roleId);
            }
        }

        if (!notFoundIds.isEmpty()) {
            log.error("Role not Found with ids: {}", notFoundIds);
            throw new IllegalArgumentException("Role not found: " + notFoundIds);
        }
        return roles;
    }

}