package com.brausov.social_network.models;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "longtext")
    private String message;

    @Column(nullable = false)
    private int likes = 0;

    private Date date;

    private Time time;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String image;

    @ManyToOne
    private User user;

    @OneToMany(fetch = FetchType.EAGER, cascade =  CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<User> usersWhoLiked;

    public Post() {

    }

    public Post(String message, Date date, Time time, User user) {
        this.message = message;
        this.date = date;
        this.time = time;
        this.user = user;
    }

    public Post(String message, Date date, Time time, String image, User user) {
        this.message = message;
        this.date = date;
        this.time = time;
        this.image = image;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }

    public Set<User> getUsersWhoLiked() {
        return usersWhoLiked;
    }

    public void setUsersWhoLiked(Set<User> usersWhoLiked) {
        this.usersWhoLiked = usersWhoLiked;
    }

    public void likePost(User user) {
        this.getUsersWhoLiked().add(user);
    }

    public void disslikePost(User user) {
        this.getUsersWhoLiked().remove(user);
    }

    public int countLikes(List<Post> posts) {
        int likes = 0;
        for (Post post: posts) {
            likes += post.getLikes();
        }
        return likes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", likes=" + likes +
                ", date=" + date +
                ", time=" + time +
                ", user=" + user.getId() +
                '}';
    }
}
