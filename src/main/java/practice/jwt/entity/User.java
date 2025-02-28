package practice.jwt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "jwt_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = "ROLE_USER";
    }

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
