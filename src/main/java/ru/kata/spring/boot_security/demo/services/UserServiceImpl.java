package ru.kata.spring.boot_security.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.exception.user.DuplicatedEmailException;
import ru.kata.spring.boot_security.demo.exception.user.EmptyEmailException;
import ru.kata.spring.boot_security.demo.exception.user.UserNotFoundException;
import ru.kata.spring.boot_security.demo.exception.user.ValidationException;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User createUser(User user, Set<Long> roleIds) {
        log.info("Creating user with email: {}", user.getEmail());

        // 1. Проверка email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new EmptyEmailException();
        }
        //2. Проверка на дубликат
        if (findByEmail(user.getEmail()) != null ) {
            throw new DuplicatedEmailException(user.getEmail());
        }

        validateUserData(user);

        // 4. Password encryption
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. Getting roles
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(roleService.getRolesByIds(roleIds));
        }

        // 4. Save
        User savedUser = userDao.createUser(user);
        log.info("User successfully created: {} {} (id: {})",
                savedUser.getFirstName(), savedUser.getLastName(), savedUser.getId());

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        validateId(id);
        return userDao.getById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.debug("Getting all users list");
        return userDao.getAll();
    }

    @Override
    @Transactional
    public User updateUser(User user, Set<Long> roleIds) {
        log.info("Updating user with id: {}", user.getId());

        validateId(user.getId());

        // 1. Check existence
        User existingUser = getUserById(user.getId());

        // 2. Email check (if changed)
        if (isEmailChanged(existingUser, user.getEmail())) {
            checkEmailUniqueness(user.getEmail(), user.getId());
            existingUser.setEmail(user.getEmail());
        }

        // 3. Update fields
        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        if (user.getAge() != null) {
            existingUser.setAge(user.getAge());
        }

        // 4. Password update
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            log.debug("Password updated for user: {}", user.getEmail());
        }

        // 5. Roles update
        if (roleIds != null) {
            existingUser.setRoles(roleService.getRolesByIds(roleIds));
            log.debug("Roles updated for user: {}", user.getEmail());
        }

        // 6. Save
        User updatedUser = userDao.update(existingUser);
        log.info("User successfully updated: {} {} (id: {})",
                updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getId());
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        validateId(id);

        if (userDao.getById(id).isEmpty()) {
            log.error("User with id {} not found", id);
            throw new UserNotFoundException(id);
        }

        userDao.delete(id);
        log.info("User with id {} successfully deleted", id);
    }

    @Override
    public User findByEmail(String email) {
        log.debug("Searching user by email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            log.warn("Search with empty email");
            return null;
        }

        return userDao.findByEmail(email).orElse(null);
    }


    // ========== HELPER METHODS ==========

    /**
     * Validates user data before creation
     */
    private void validateUserData(User user) {
        if (user == null) {
            throw new ValidationException("User object cannot be null");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new ValidationException("First name cannot be empty");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new ValidationException("Last name cannot be empty");
        }
        if (user.getAge() == null || user.getAge() <= 0) {
            throw new ValidationException("Age must be positive");
        }
    }

    /**
     * Validates user ID
     */
    private void validateId(Long id) {
        if (id == null) {
            throw new ValidationException("ID cannot be null");
        }
        if (id <= 0) {
            throw new ValidationException("ID must be positive");
        }
    }

    /**
     * Checks if email is unique
     *
     * @param email         email to check
     * @param currentUserId current user ID (null for creation)
     */
    private void checkEmailUniqueness(String email, Long currentUserId) {
        userDao.findByEmail(email).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(currentUserId)) {
                log.warn("Email already in use: {}", email);
                throw new DuplicatedEmailException(email);
            }
        });
    }

    /**
     * Checks if email has changed
     */
    private boolean isEmailChanged(User existingUser, String newEmail) {
        return newEmail != null && !newEmail.equals(existingUser.getEmail());
    }

}