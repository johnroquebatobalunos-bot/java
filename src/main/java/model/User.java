package model;

/** Represents a registered student or administrator. */
public class User {
    public enum Role {
        STUDENT,
        ADMIN
    }

    private final int id;
    private final String fullName;
    private final String username;
    private final Role role;

    public User(int id, String fullName, String username, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}
