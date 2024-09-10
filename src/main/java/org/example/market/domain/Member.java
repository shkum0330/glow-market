package org.example.market.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // ROLE_USER, ROLE_ADMIN 등

    @Getter
    public enum Role {
        ADMIN("ROLE_ADMIN"),
        SELLER("ROLE_SELLER"),
        BUYER("ROLE_BUYER");

        private final String description;

        Role(String description) {
            this.description = description;
        }
    }

    public Member(Long id, String username, String password,Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Builder
    public Member(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
