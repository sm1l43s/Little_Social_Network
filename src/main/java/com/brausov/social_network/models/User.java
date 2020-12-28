package com.brausov.social_network.models;

import com.brausov.social_network.util.StringUtils;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name", length = 30)
    private String firstName;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String avatar;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(name = "registration_date")
    private Date registrationDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "last_entered")
    private Date lastEntered;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<User> usersFollowers;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<User> usersFollowing;

    public User() {
    }

    public User(String lastName, String firstName, String email, String password, Date registrationDate) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.registrationDate = registrationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastEntered() {
        return lastEntered;
    }

    public void setLastEntered(Date lastEntered) {
        this.lastEntered = lastEntered;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<User> getUsersFollowers() {
        return usersFollowers;
    }

    public void setUsersFollowers(Set<User> usersFollowers) {
        this.usersFollowers = usersFollowers;
    }

    public Set<User> getUsersFollowing() {
        return usersFollowing;
    }

    public void setUsersFollowing(Set<User> usersFollowing) {
        this.usersFollowing = usersFollowing;
    }

    public void addFollowers(User user) {
        this.getUsersFollowers().add(user);
    }

    public void removeFollowers(User user) {this.getUsersFollowers().remove(user); }

    public void addFollowing(User user) {
        this.getUsersFollowing().add(user);
    }

    public void removeFollowing(User user) {
        this.getUsersFollowing().remove(user);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", registrationDate=" + registrationDate +
                ", role=" + role.getAuthority() +
                ", lastEntered=" + lastEntered +
                ", usersFollowers=" + usersFollowers.size() +
                ", usersFollowing=" + usersFollowing.size() +
                '}';
    }
}
