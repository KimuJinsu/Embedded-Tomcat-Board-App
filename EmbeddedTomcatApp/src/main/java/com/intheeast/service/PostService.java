package com.intheeast.service;


import java.util.List;

import com.intheeast.dto.PostDto;
import com.intheeast.entity.Post;
import com.intheeast.service.impl.PostCallbackImpl;

public interface PostService extends EntityService<Post, EntityCallback<Post>> {
	
//	Post findById(Long postId);
	
	List<Post> findPostsByPage(int page, int pageSize); // 페이지별 게시글 조회
    long countPosts(); // 전체 게시글 개수 조회
    
    // 검색 기능 추가
    List<Post> searchPostsByName(String name, int page, int pageSize);

    long countPostsByName(String name);
    
    PostDto findPostById(Long postId);
    
    List<PostDto> findPostsByUserId(Long userId);
    
	List<Post> findPostsByUserID(Long userId);
}
