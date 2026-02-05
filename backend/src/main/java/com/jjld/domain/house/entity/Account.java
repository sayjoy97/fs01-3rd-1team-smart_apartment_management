package com.jjld.domain.house.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "householder_email", nullable = false, unique = true)
    private String householderEmail;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean firstLogin;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;
}
