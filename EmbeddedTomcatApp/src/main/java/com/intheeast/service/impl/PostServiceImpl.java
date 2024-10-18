package com.intheeast.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import com.intheeast.dto.PostDto;
import com.intheeast.dto.PostMapper;
import com.intheeast.entity.Post;
import com.intheeast.service.DefaultTextFilter;
import com.intheeast.service.EntityCallback;
import com.intheeast.service.PostService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Inject
    private DefaultTextFilter textFilter;

    @PersistenceContext
    private EntityManager entityManager;

    // 게시글 작성자(userId)로 게시글 조회
    @Override
    @Transactional(readOnly = true)
    public List<Post> findPostsByUserID(Long userId) {
        String jpql = "SELECT p FROM Post p WHERE p.user.id = :userId";
        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // PostDto로 변환하는 메서드
    @Override
    @Transactional(readOnly = true)
    public List<PostDto> findPostsByUserId(Long userId) {
        List<Post> posts = findPostsByUserID(userId);
        return posts.stream()
            .map(PostMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public PostDto findPostById(Long postId) {
        Post post = entityManager.find(Post.class, postId);
        return PostMapper.toDto(post);
    }

    @Override
    public void post(final Post post, final EntityCallback<Post> callback) {
        preparePost(post);
        callback.post(post);
    }

    private void preparePost(final Post post) {
        post.setWeb(cleanupWebUrl(post.getWeb()));
        post.setText(textFilter.filter(post.getText()));
    }

    private String cleanupWebUrl(String webUrl) {
        try {
            return UriComponentsBuilder.fromUriString(webUrl).build().toUriString();
        } catch (Exception e) {
            return webUrl;  // 기본적으로 원래 URL을 반환
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Post findById(Long id, final EntityCallback<Post> callback) {
        return callback.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findAll(final EntityCallback<Post> callback) {
        return entityManager.createQuery("SELECT p FROM Post p", Post.class).getResultList();
    }

    @Override
    public void delete(Post post, final EntityCallback<Post> callback) {
        callback.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findPostsByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return entityManager.createQuery("SELECT p FROM Post p ORDER BY p.creationDate DESC", Post.class)
            .setFirstResult(offset)
            .setMaxResults(pageSize)
            .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countPosts() {
        return entityManager.createQuery("SELECT COUNT(p) FROM Post p", Long.class).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> searchPostsByName(String name, int page, int pageSize) {
        String jpql = "SELECT p FROM Post p WHERE p.name LIKE :name";
        return entityManager.createQuery(jpql, Post.class)
            .setParameter("name", "%" + name + "%")
            .setFirstResult((page - 1) * pageSize)
            .setMaxResults(pageSize)
            .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countPostsByName(String name) {
        String jpql = "SELECT COUNT(p) FROM Post p WHERE p.name LIKE :name";
        return entityManager.createQuery(jpql, Long.class)
            .setParameter("name", "%" + name + "%")
            .getSingleResult();
    }

    @Override
    public void update(Post entity, EntityCallback<Post> callback) {
        callback.update(entity);
    }
}