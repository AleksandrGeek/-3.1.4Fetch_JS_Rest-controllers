package ru.kata.spring.boot_security.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;


import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    private static final String ADMIN_VIEW = "admin";
    private static final String ADMIN_NEW_VIEW = "admin_new";
    private static final String ADMIN_EDIT_VIEW = "admin_edit";
    private static final String REDIRECT_ADMIN = "redirect:/admin";


    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    /**
     * Displays admin panel with optional role filtering
     *
     * @param user authenticated user
     * @param role optional role filter (ADMIN/USER/ALL)
     * @return ModelAndView with admin page
     */
    @GetMapping
    public ModelAndView adminPage(@AuthenticationPrincipal User user,
                                  @RequestParam(required = false)
                                  String role) {
        log.info("Rendering admin page for user: {}, role filter: {}",
                user.getEmail(), role);

        ModelAndView modelAndView = new ModelAndView(ADMIN_VIEW);

        try {
            List<User> users;
            if (role == null || role.equals("ALL")) {
                users = userService.getAllUsers();
                log.debug(
                        "Displaying all users, count: {}",
                        users.size());
            } else {
                users = userService.getUsersByRole(role);
                log.debug(
                        "Displaying users with role: {}, count: {}",
                        role, users.size());
            }

            modelAndView.addObject("users", users);
            modelAndView.addObject("user", user);
            modelAndView.addObject("selectedRole", role);
            modelAndView.addObject("allRoles", roleService.getAllRoles());

        } catch (Exception e) {
            log.error(
                    "Error loading admin page: {}",
                    e.getMessage(), e);
            modelAndView.addObject("errorMessage",
                    "Error loading user data. Please try again. ");
        }

        return modelAndView;
    }

    /**
     * Shows form for creating new user
     *
     * @return ModelAndView new user form
     */
    @GetMapping("/new")
    public ModelAndView newUser() {
        log.debug("Rendering new user form");

        ModelAndView modelAndView = new ModelAndView(ADMIN_NEW_VIEW);
        modelAndView.addObject("user", new User());
        modelAndView.addObject("allRoles", roleService.getAllRoles());

        return modelAndView;
    }

    /**
     * Creates new user
     *
     * @param user               user data from form
     * @param roleIds            selected role IDs
     * @param redirectAttributes for flash messages
     * @return redirect to admin page
     */
    @PostMapping("/create")
    public ModelAndView createUser(@ModelAttribute User user,
                                   @RequestParam(required = false)
                                   Set<Long> roleIds,
                                   RedirectAttributes redirectAttributes) {

        log.info("Creating new user with email: {}", user.getEmail());

        try {
            User createdUser = userService.createUser(user, roleIds);
            log.info("User created successfully with id: {}",
                    createdUser.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("User %s %s created successfully!",
                            createdUser.getFirstName(), createdUser.getLastName()));

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getMessage());
            // return form with user data
            ModelAndView modelAndView = new ModelAndView(ADMIN_NEW_VIEW);
            modelAndView.addObject("user", user);
            modelAndView.addObject("allRoles", roleService.getAllRoles());
            modelAndView.addObject("errorMessage", e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error creating user: {}",
                    e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred. Please try again. ");
        }

        return new ModelAndView(REDIRECT_ADMIN);
    }

    /**
     * Shows edit form for existing user
     *
     * @return ModelAndView with edit form or redirect
     * @Param id user ID (as request parameter)
     * @Param redirectAttributes for flash messages
     **/
    @GetMapping("/edit")
    public ModelAndView editUser(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Editing user with id: {}", id);

        try {
            User user = userService.getUserById(id);
            log.debug("User found: {} {}",
                    user.getFirstName(), user.getLastName());

            ModelAndView modelAndView = new ModelAndView(ADMIN_EDIT_VIEW);

            modelAndView.addObject("user", user);
            modelAndView.addObject("allRoles", roleService.getAllRoles());

            return modelAndView;
        } catch (IllegalArgumentException e) {
            log.warn("User not found with id: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("User with id %d not found", id));
            return new ModelAndView(REDIRECT_ADMIN);

        } catch (Exception e) {
            log.error("Error loading user for edit: {}",
                    e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error loading user data. Please try again.");
            return new ModelAndView(REDIRECT_ADMIN);
        }
    }

    /**
     * Updates existing user
     *
     * @return redirect to admin page
     * @Param user update user data
     * @Param roleIds selected role IDs
     * @Param redirectAttributes for flash messages
     */
    @PostMapping("/update")
    public ModelAndView updateUser(@ModelAttribute User user,
                                   @RequestParam(required = false)
                                   Set<Long> roleIds,
                                   RedirectAttributes redirectAttributes) {
        log.info("Updating user with id: {}", user.getId());

        try {
            if (roleIds == null) {
                roleIds = new HashSet<>();
                log.debug("No roles selected for user: {}",
                        user.getId());
            }

            userService.updateUser(user, roleIds);
            log.info("User updated successfully: {} {} (id: {})",
                    user.getFirstName(), user.getLastName(),
                    user.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("User %s %s updated successfully",
                            user.getFirstName(), user.getLastName()));


        } catch (IllegalArgumentException e) {
            log.warn("Validation error: updating user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(
                    "errorMessage", e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error updating user: {}",
                    e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred. Please try again");
        }

        return new ModelAndView(REDIRECT_ADMIN);

    }

    /**
     * Deletes user
     *
     * @return redirect to admin page
     * @Param id user ID (as request parameter)
     * @Param redirectAttributes for flash messages
     */
    @PostMapping("/delete")
    public ModelAndView deleteUser(@RequestParam("id") Long id,
                                   RedirectAttributes redirectAttributes) {
        log.info("Deleting user with id: {}", id);

        try {
            // Get user info before deletion for logging
            User user = userService.getUserById(id);
            String userInfo = String.format("%s %s (%s)",
                    user.getFirstName(), user.getLastName(),
                    user.getEmail());

            userService.deleteUser(id);
            log.info("User deleted successfully: {} (id: {})",
                    userInfo, id);


            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("User %s deleted successfully", userInfo));


        } catch (IllegalArgumentException e ) {
            log.warn("User not found with id: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("User with id %d not found", id));
        } catch (Exception e ) {
             log.error("Error deleting user: {}", e.getMessage(), e);
             redirectAttributes.addFlashAttribute("errorMessage",
                     "An unexpected error occurred while deleting user.");
        }

        return new ModelAndView(REDIRECT_ADMIN);

    }

}






