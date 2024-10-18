package com.intheeast.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long id;

    private Long userId; // User ID to associate the post with a user

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @Email
    private String email;

    @Size(min = 1, max = 120)
    private String title;

    @Size(max = 250)
    private String web;

    @NotBlank
    private String text;

    private String ipAddress;

    private List<CommentDto> commentList;
}