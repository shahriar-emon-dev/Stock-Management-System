/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package stock_management_system.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String role;

    /** No-arg constructor (if you still need it) */
    public User() { }

    /** 
     * Full constructor matching your controller’s usage:
     * @param id           user's database ID
     * @param username     login username
     * @param passwordHash stored hash (we pass null in-memory)
     * @param fullName     first + last name
     * @param email        email address
     * @param phone        phone number
     * @param address      postal address
     * @param role         “admin” or “staff”
     */
    public User(int id,
                String username,
                String passwordHash,
                String fullName,
                String email,
                String phone,
                String address,
                String role) {
        this.id           = id;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.fullName     = fullName;
        this.email        = email;
        this.phone        = phone;
        this.address      = address;
        this.role         = role;
    }

    // —— Getters & setters —— //

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
