package com.intheeast.dto;

import com.intheeast.entity.Comment;

public class CommentMapper {

    // Comment 엔티티를 CommentDto로 변환하는 메서드
    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setPostId(comment.getPost().getId());
        commentDto.setUserId(comment.getUser().getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setText(comment.getText());
        commentDto.setIpAddress(comment.getIpAddress());

        return commentDto;
    }
}