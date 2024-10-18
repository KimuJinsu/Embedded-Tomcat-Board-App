package com.intheeast.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    private Long postId; // ID of the associated post

    private Long userId; // ID of the user who made the comment

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String text;

    private String ipAddress;
}