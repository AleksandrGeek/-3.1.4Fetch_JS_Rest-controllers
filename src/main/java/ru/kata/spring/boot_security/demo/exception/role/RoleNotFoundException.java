package ru.kata.spring.boot_security.demo.exception.role;



import java.util.Set;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Long id) {
        super("Role not found with id: " + id);
    }

   public  RoleNotFoundException(Set<Long> ids) {
        super("Roles not found with ids " + ids);
   }
    public RoleNotFoundException(String message) {
        super(message);
    }
}
