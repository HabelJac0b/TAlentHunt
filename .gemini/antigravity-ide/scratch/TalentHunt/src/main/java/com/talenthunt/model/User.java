package com.talenthunt.model;

import com.talenthunt.exception.ValidationException;
import com.talenthunt.util.ValidationUtil;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User Entity representing contestants, judges, administrators, and audience.
 * Fulfills Phase 1: Core Application Setup and Design (CLO1).
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Attributes (encapsulated with getters/setters and direct access compatibility)
    public int id;
    public String name;
    public String email;
    public String password;
    public String role; // CONTESTANT, JUDGE, AUDIENCE, ADMIN
    public String avatarUrl;
    public String bio;
    public Timestamp createdAt;

    // Default Constructor
    public User() {
        this.role = "CONTESTANT";
        this.avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=300&q=80";
    }

    // Parameterized Constructor
    public User(int id, String name, String email, String password, String role, String avatarUrl, String bio) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role != null ? role.toUpperCase() : "CONTESTANT";
        this.avatarUrl = avatarUrl != null ? avatarUrl : "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=300&q=80";
        this.bio = bio;
    }

    // Full Constructor
    public User(int id, String name, String email, String password, String role, String avatarUrl, String bio, Timestamp createdAt) {
        this(id, name, email, password, role, avatarUrl, bio);
        this.createdAt = createdAt;
    }

    // Convenience Constructor for registration
    public User(String name, String email, String password, String role) {
        this(0, name, email, password, role, null, null);
    }

    // Business & Validation Methods
    public void validate() throws ValidationException {
        ValidationUtil.validateRegistration(this.name, this.email, this.password);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isJudge() {
        return "JUDGE".equalsIgnoreCase(this.role);
    }

    public boolean isContestant() {
        return "CONTESTANT".equalsIgnoreCase(this.role);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role.toUpperCase() : "CONTESTANT";
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    /**
     * Phase 3: "Represent user data format using JSON (simple structure)" - CLO 2
     * Converts User object to a JSON formatted string.
     * Note: Sensitive fields like password are deliberately excluded.
     */
    public String toJson() {
        return "{" +
                "\"id\":" + id + "," +
                "\"name\":\"" + escapeJson(name) + "\"," +
                "\"email\":\"" + escapeJson(email) + "\"," +
                "\"role\":\"" + escapeJson(role) + "\"," +
                "\"avatarUrl\":\"" + escapeJson(avatarUrl != null ? avatarUrl : "") + "\"," +
                "\"bio\":\"" + escapeJson(bio != null ? bio : "") + "\"," +
                "\"createdAt\":" + (createdAt != null ? "\"" + escapeJson(createdAt.toString()) + "\"" : "null") +
                "}";
    }

    /**
     * Reconstructs a User object from a JSON string.
     */
    public static User fromJson(String json) {
        return com.talenthunt.util.JsonUtil.jsonToUser(json);
    }

    private static String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
