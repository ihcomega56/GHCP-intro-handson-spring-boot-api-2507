package com.example.handson.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.handson.model.Post;
import com.example.handson.service.PostService;

/**
 * PostControllerのテストクラス
 * RESTエンドポイントの動作を検証し、適切なHTTPステータスコードやレスポンスが返されることを確認します
 * サービス層はモック化して、コントローラー層の単体テストに集中します
 */
@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    /**
     * 下書き作成エンドポイントが正常に動作することを検証するテスト
     * - 有効なリクエストでHTTP 201 Createdと作成された投稿が返されること
     */
    @Test
    void createDraft_ShouldReturnCreatedStatusAndPost() {
        // Given
        String content = "Test content";
        Map<String, String> payload = new HashMap<>();
        payload.put("content", content);
        
        Post post = new Post(content);
        post.setId(1L);
        
        when(postService.createDraft(content)).thenReturn(post);
        
        // When
        ResponseEntity<Post> response = postController.createDraft(payload);
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(post, response.getBody());
    }
    
    /**
     * 下書き公開エンドポイントが正常に動作することを検証するテスト
     * - 存在する投稿IDでHTTP 200 OKと公開された投稿が返されること
     */
    @Test
    void publishPost_ShouldReturnOkStatusAndPost() {
        // Given
        Long id = 1L;
        Post post = new Post("Test content");
        post.setId(id);
        post.setDraft(false);
        
        when(postService.publishPost(id)).thenReturn(post);
        
        // When
        ResponseEntity<Post> response = postController.publishPost(id);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(post, response.getBody());
    }
    
    /**
     * 投稿取得エンドポイントが正常に動作することを検証するテスト
     * - 存在する投稿IDでHTTP 200 OKと投稿が返されること
     */
    @Test
    void getPost_ShouldReturnOkStatusAndPost() {
        // Given
        Long id = 1L;
        Post post = new Post("Test content");
        post.setId(id);
        
        when(postService.getPost(id)).thenReturn(post);
        
        // When
        ResponseEntity<Post> response = postController.getPost(id);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(post, response.getBody());
    }
    
    /**
     * 投稿削除エンドポイントが正常に動作することを検証するテスト
     * - 存在する投稿IDでHTTP 204 No Contentが返されること
     */
    @Test
    void deletePost_ShouldReturnNoContentWhenSuccessful() {
        // Given
        Long id = 1L;
        when(postService.deletePost(id)).thenReturn(true);
        
        // When
        ResponseEntity<Void> response = postController.deletePost(id);
        
        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
    
    /**
     * 公開済み投稿一覧取得エンドポイントが正常に動作することを検証するテスト
     * - HTTP 200 OKと公開済み投稿のリストが返されること
     */
    @Test
    void getAllPublishedPosts_ShouldReturnOkStatusAndPosts() {
        // Given
        List<Post> posts = Arrays.asList(
            new Post("Post 1"),
            new Post("Post 2")
        );
        
        when(postService.getAllPublishedPosts()).thenReturn(posts);
        
        // When
        ResponseEntity<List<Post>> response = postController.getAllPublishedPosts();
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts, response.getBody());
    }
    
    /**
     * 下書き投稿一覧取得エンドポイントが正常に動作することを検証するテスト
     * - HTTP 200 OKと下書き投稿のリストが返されること
     */
    @Test
    void getAllDraftPosts_ShouldReturnOkStatusAndPosts() {
        // Given
        List<Post> posts = Arrays.asList(
            new Post("Draft 1"),
            new Post("Draft 2")
        );
        
        when(postService.getAllDraftPosts()).thenReturn(posts);
        
        // When
        ResponseEntity<List<Post>> response = postController.getAllDraftPosts();
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts, response.getBody());
    }
}
