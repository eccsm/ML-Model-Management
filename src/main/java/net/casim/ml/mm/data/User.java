package net.casim.ml.mm.data;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles;

    public void addRole(String role) {
        if (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("USER")) {
            this.roles.add(role);
        } else {
            throw new IllegalArgumentException("Invalid role. Only 'ADMIN' or 'USER' roles are allowed.");
        }
    }
}
