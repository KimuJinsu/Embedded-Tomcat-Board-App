package com.intheeast.controller;

import com.intheeast.dto.PostDto;
import com.intheeast.entity.Comment;
import com.intheeast.entity.FileEntity;
import com.intheeast.entity.Post;
import com.intheeast.entity.User;
import com.intheeast.service.CommentService;
import com.intheeast.service.EntityCallback;
import com.intheeast.service.PostService;
import com.intheeast.service.UserService;
import com.intheeast.utils.Utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityCallback<Post> postCallbackImpl;

    @Autowired
    private EntityCallback<Comment> commentCallbackImpl;
    
    @Autowired
    private UserService userService;  // UserService 주입
    
    // properties 파일에 적용 예상
    private static final int PAGE_SIZE = 10; // 한 페이지에 표시할 게시글 수

    
    
    
    
    
 // PostController.java
    @GetMapping("/user/{userId}/posts")
    public String getPostsByUser(@PathVariable Long userId, Model model) {
        List<PostDto> posts = postService.findPostsByUserId(userId);
        model.addAttribute("posts", posts);
        return "post/userPosts"; // 작성자 게시글 목록을 보여줄 뷰 이름
    }
    
    // 게시글 목록 및 검색 기능 추가!
    public String listPosts(
            @RequestParam(value = "page", defaultValue = "1") int page, 
            @RequestParam(value = "search", required = false) String search, 
            Model model) {

        // 검색 여부에 따라 처리
        if (search != null && !search.isEmpty()) {
            List<Post> posts = postService.searchPostsByName(search, page, PAGE_SIZE);
            long totalPosts = postService.countPostsByName(search);
            int totalPages = (int) Math.ceil((double) totalPosts / PAGE_SIZE);
            
            model.addAttribute("posts", posts);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("search", search); // 검색어 전달
        } else {
            // 기존 게시글 목록 조회
            long totalPosts = postService.countPosts();
            int totalPages = (int) Math.ceil((double) totalPosts / PAGE_SIZE);
            List<Post> posts = postService.findPostsByPage(page, PAGE_SIZE);

            model.addAttribute("posts", posts);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
        }

        return "post/list";
    }


    // 게시글 상세 조회 및 댓글 표시
    @GetMapping("/{id}")
    public String getPostById(@PathVariable("id") Long id, Model model) {
        Post post = postService.findById(id, postCallbackImpl);
        if (post == null) {
            return "post/not_found";
        }

        List<FileEntity> files = post.getFiles();

        List<Comment> comments = commentService.findAllCommentsByPost(post, commentCallbackImpl);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("files", files);
        model.addAttribute("comment", new Comment()); // 새 댓글 작성 폼을 위해 빈 객체 추가
        return "post/detail";
    }

    // 게시글 작성
    @GetMapping("/new")
    public String showNewPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/new";
    }

    
//////////////////////////////새로 추가한 부분//////////////////////////////////////
    @PostMapping
    @Transactional // 이 부분 추가
    public String createPost(@Valid @ModelAttribute("post") Post post, 
                             BindingResult result,
                             HttpServletRequest request) {
        if (result.hasErrors()) {
            return "post/new";
        }

        // 클라이언트 IP 주소 설정
        String clientIp = Utilities.getClientIp(request);
        post.setIpAddress(clientIp);
        
        // 사용자 정보를 기반으로 User 객체 생성
        User user = new User();
        user.setUsername(post.getName()); // 이름을 사용자 이름으로 설정
        user.setEmail(post.getEmail()); // 이메일 설정

        // User를 먼저 저장
        userService.save(user); // User 저장 후, ID가 생성됨

        // 저장된 User ID를 Post에 설정
        post.setUser(user); // Post에 User 객체 설정

        // 게시글 저장
        postService.post(post, postCallbackImpl);
        
        return "redirect:/posts";
    }
//////////////////////////////새로 추가한 부분//////////////////////////////////////
    
    
    
    
    // 게시글 삭제
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("postId") Long postId) {
        Post post = postService.findById(postId, postCallbackImpl);
        if (post != null) {
            postService.delete(post, postCallbackImpl);
        }
        return "redirect:/posts";
    }

    
    
//////////////////////////////새로 추가한 부분//////////////////////////////////////
    
    // 댓글 추가
    @PostMapping("/{postId}/comments")
    @Transactional // 이 부분 추가
    public String addCommentToPost(@PathVariable("postId") Long postId, 
                                   @Valid @ModelAttribute("comment") Comment comment, 
                                   BindingResult result, 
                                   HttpServletRequest request, 
                                   Model model) {
        if (result.hasErrors()) {
            return "redirect:/posts/" + postId;
        }

        Post post = postService.findById(postId, postCallbackImpl);
        if (post == null) {
            return "post/not_found";
        }

        // IP 주소 설정
        String clientIp = Utilities.getClientIp(request);
        comment.setIpAddress(clientIp);
        
        // 사용자 정보를 기반으로 User 객체 생성
        User user = new User();
        user.setUsername(comment.getName()); // 이름을 댓글 작성자로 설정
        user.setEmail(comment.getEmail()); // 이메일 설정

        // User를 먼저 저장
        userService.save(user); // User 저장 후, ID가 생성됨

        // 저장된 User ID를 Comment에 설정
        comment.setUser(user); // Comment에 User 객체 설정

        comment.setPost(post); // 댓글에 Post 객체 설정
        commentService.post(comment, commentCallbackImpl);

        return "redirect:/posts/" + postId;
    }
    
    //////////////////////////////새로 추가한 부분//////////////////////////////////////

    // 댓글 수정 폼으로 이동
    @GetMapping("/{postId}/comments/{commentId}/edit")
    public String showEditCommentForm(@PathVariable("postId") Long postId,
                                      @PathVariable("commentId") Long commentId,
                                      Model model) {
        Post post = postService.findById(postId, postCallbackImpl);
        if (post == null) {
            return "post/not_found";
        }

        Comment comment = commentService.findById(commentId, commentCallbackImpl);
        if (comment == null) {
            return "post/not_found";
        }

        model.addAttribute("post", post);
        model.addAttribute("comment", comment);
        return "post/edit_comment";  // 댓글 수정 폼으로 이동
    }

    // 댓글 수정
    @PostMapping("/{postId}/comments/{commentId}/edit")
    public String updateComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId,
                                @Valid @ModelAttribute("comment") Comment comment,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "post/edit_comment";  // 에러가 발생하면 수정 폼으로 다시 이동
        }

        Post post = postService.findById(postId, postCallbackImpl);
        if (post == null) {
            return "post/not_found";
        }

        Comment existingComment = commentService.findById(commentId, commentCallbackImpl);
        if (existingComment == null) {
            return "post/not_found";
        }

        // 변경된 필드만 업데이트
        if (!existingComment.getName().equals(comment.getName())) {
            existingComment.setName(comment.getName());
        }
        if (!existingComment.getEmail().equals(comment.getEmail())) {
            existingComment.setEmail(comment.getEmail());
        }
        if (!existingComment.getText().equals(comment.getText())) {
            existingComment.setText(comment.getText());
        }

        // 코멘트 업데이트
        commentService.update(existingComment, commentCallbackImpl);
        return "redirect:/posts/" + postId;  // 수정된 게시글 페이지로 리다이렉트
    }


    // 댓글 삭제
    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId) {
        Post post = postService.findById(postId, postCallbackImpl);
        if (post == null) {
            return "post/not_found";
        }

        Comment comment = commentService.findById(commentId, commentCallbackImpl);
        if (comment != null) {
            commentService.delete(comment, commentCallbackImpl);
        }

        return "redirect:/posts/" + postId;
    }
    
    @GetMapping("/search")
    public String searchPosts(@RequestParam("search") String search, 
                              @RequestParam(value = "page", defaultValue = "1") int page, 
                              Model model) {
    	// UTF-8로 인코딩된 검색어가 잘 들어오는지 확인
        System.out.println("Search term: " + search);

        long totalPosts = postService.countPostsByName(search);
        int totalPages = (int) Math.ceil((double) totalPosts / PAGE_SIZE);

        List<Post> posts = postService.searchPostsByName(search, page, PAGE_SIZE);

        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);
        return "post/list";
    }

    @GetMapping("/{id}/edit")
    public String showEditPostForm(@PathVariable("id") Long id, Model model) {
        Post post = postService.findById(id, postCallbackImpl);
        if (post == null) {
            return "post/not_found";  // 게시글이 없을 경우 에러 페이지로 이동
        }

        model.addAttribute("post", post);  // 수정할 게시글을 모델에 추가
        return "post/edit";  // 수정 폼을 렌더링할 JSP로 이동
    }
    
    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable("id") Long id, 
                             @Valid @ModelAttribute("post") Post post, 
                             BindingResult result, 
                             HttpServletRequest request, 
                             Model model) {
        if (result.hasErrors()) {
            return "post/edit";  // 에러가 발생하면 수정 폼으로 다시 이동
        }

        String clientIp = Utilities.getClientIp(request);  // 클라이언트 IP 가져오기

        Post existingPost = postService.findById(post.getId(), postCallbackImpl);
        if (existingPost == null) {
            return "post/not_found";
        }

        // 변경된 값만 설정
        if (!existingPost.getName().equals(post.getName())) {
            existingPost.setName(post.getName());
        }
        if (!existingPost.getTitle().equals(post.getTitle())) {
            existingPost.setTitle(post.getTitle());
        }
        if (!existingPost.getWeb().equals(post.getWeb())) {
            existingPost.setWeb(post.getWeb());
        }
        if (!existingPost.getText().equals(post.getText())) {
            existingPost.setText(post.getText());
        }
        if (!existingPost.getIpAddress().equals(clientIp)) {
            existingPost.setIpAddress(clientIp);
        }

        // 게시글 업데이트
        postService.update(existingPost, postCallbackImpl);

        return "redirect:/posts/" + id;  // 수정된 게시글 페이지로 리다이렉트
    }

    
 
}