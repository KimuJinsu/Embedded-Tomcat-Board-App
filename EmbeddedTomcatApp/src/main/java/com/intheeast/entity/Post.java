package com.intheeast.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "POST_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user; // 작성자

    @Size(min = 1, max = 100)
    private String name;

    @Size(min = 1, max = 100)
    @Email
    private String email;

    @Column(name = "ip_addr")
    private String ipAddress;

    @Size(min = 1, max = 120)
    private String title;

    @Size(max = 250)
    private String web;

    @Lob
    private String text;

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> files = new ArrayList<>();

    public void addComment(Comment comment) {
        comment.setPost(this);
        commentList.add(comment);
    }

    public void removeComment(Comment comment) {
        comment.setPost(null);
        commentList.remove(comment);
    }

    public void addFile(FileEntity file) {
        file.setPost(this);
        files.add(file);
    }

    public void removeFile(FileEntity file) {
        file.setPost(null);
        files.remove(file);
    }

    public List<FileEntity> getFiles() {
        return files;
    }
}