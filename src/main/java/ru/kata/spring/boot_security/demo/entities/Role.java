package ru.kata.spring.boot_security.demo.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "roles")
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "users")  // ← обязательно исключить!
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // ✅ Добавляем обратную связь
    @ManyToMany(mappedBy = "roles")  // ссылается на поле roles в классе User
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return id != null && Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

