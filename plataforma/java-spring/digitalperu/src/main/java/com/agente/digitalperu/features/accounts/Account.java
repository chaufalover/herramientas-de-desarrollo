package com.agente.digitalperu.features.accounts;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "account_dni", length = 20, unique = true)
    private String dni;

    @Column(name = "account_ruc", length = 20, unique = true)
    private String ruc;

    @NotBlank(message = "Name is required")
    @Column(name = "account_name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Last name is required")
    @Column(name = "account_lastname", nullable = false, length = 100)
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(name = "account_email", nullable = false, unique = true, length = 150)
    private String email;

    @Pattern(regexp = "\\d{9,15}", message = "Phone must contain only digits (9–15)")
    @Column(name = "account_phone", length = 15)
    private String phone;

    @Column(name = "account_addres", length = 255)
    private String addres;

    @Column(name = "account_registration_date", nullable = false)
    private LocalDate registrationDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 10)
    private AccountStatus status;

    @NotBlank
    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @NotBlank
    @Column(name = "account_username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank
    @Size(min = 8, message = "Password must have at least 8 characters")
    @Column(name = "account_password", nullable = false)
    private String password;
    
    @PrePersist
    public void prePersist() {
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
    }
}
