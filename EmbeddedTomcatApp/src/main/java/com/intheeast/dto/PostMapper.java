package com.intheeast.dto;

import java.util.stream.Collectors;

import com.intheeast.entity.Post;

public class PostMapper {

    // Post 엔티티를 PostDto로 변환하는 메서드
    public static PostDto toDto(Post post) {
        if (post == null) {
            return null;
        }

        // Post 엔티티의 필드를 PostDto에 매핑
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setUserId(post.getUser().getId());
        postDto.setName(post.getName());
        postDto.setEmail(post.getEmail());
        postDto.setTitle(post.getTitle());
        postDto.setWeb(post.getWeb());
        postDto.setText(post.getText());
        postDto.setIpAddress(post.getIpAddress());

        // 필요 시 댓글 목록도 변환해서 추가
        if (post.getCommentList() != null) {
            postDto.setCommentList(
                post.getCommentList().stream()
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList())
            );
        }

        return postDto;
    }
}