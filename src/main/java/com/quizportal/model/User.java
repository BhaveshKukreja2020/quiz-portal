package com.quizportal.model;

import java.time.LocalDateTime;

/**
 * Model representing a portal user (student or admin).
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;          // BCrypt hash
    private String role;              // "student" | "admin"
    private LocalDateTime createdAt;

    public User() {}

    public User(int id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // ---- Getters & Setters ----
    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getName()                   { return name; }
    public void setName(String name)          { this.name = name; }

    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }

    public String getPassword()               { return password; }
    public void setPassword(String password)  { this.password = password; }

    public String getRole()                   { return role; }
    public void setRole(String role)          { this.role = role; }

    public LocalDateTime getCreatedAt()                      { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)        { this.createdAt = createdAt; }

    public boolean isAdmin()   { return "admin".equalsIgnoreCase(role); }
    public boolean isStudent() { return "student".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', role='" + role + "'}";
    }
}
