package com.example.handson.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.handson.model.Post;

/**
 * PostServiceのテストクラス
 * サービス層のビジネスロジックを検証します
 * インメモリのデータストアを使用するため、テスト実行後は状態がリセットされます
 */
class PostServiceTest {

    private PostService postService;

    /**
     * 各テスト実行前に新しいPostServiceインスタンスを作成します
     * これによりテスト間の独立性が保たれます
     */
    @BeforeEach
    void setUp() {
        postService = new PostService();
    }

    /**
     * 下書き作成機能のテスト
     * - 正しいコンテンツで新しい下書きが作成されることを確認
     * - IDが自動生成されることを確認
     * - 下書きフラグが正しく設定されることを確認
     * - 作成日時が設定されることを確認
     */
    @Test
    void createDraft_ShouldCreateNewPostWithCorrectValues() {
        // Given
        String content = "Test content";
        
        // When
        Post createdPost = postService.createDraft(content);
        
        // Then
        assertNotNull(createdPost);
        assertNotNull(createdPost.getId());
        assertEquals(content, createdPost.getContent());
        assertTrue(createdPost.isDraft());
        assertNotNull(createdPost.getCreatedAt());
        assertNotNull(createdPost.getUpdatedAt());
        assertNull(createdPost.getPublishedAt());
    }
    
    /**
     * 下書き公開機能のテスト
     * - 下書きが正しく公開状態に変更されることを確認
     * - 公開日時が設定されることを確認
     */
    @Test
    void publishPost_ShouldChangePostFromDraftToPublished() {
        // Given
        Post draft = postService.createDraft("Test content");
        Long id = draft.getId();
        
        // When
        Post publishedPost = postService.publishPost(id);
        
        // Then
        assertNotNull(publishedPost);
        assertEquals(id, publishedPost.getId());
        assertFalse(publishedPost.isDraft());
        assertNotNull(publishedPost.getPublishedAt());
    }
    
    /**
     * 存在しない投稿IDで下書き公開を試みた場合のテスト
     * - nullが返されることを確認
     */
    @Test
    void publishPost_ShouldReturnNullForNonExistingPost() {
        // When
        Post publishedPost = postService.publishPost(999L);
        
        // Then
        assertNull(publishedPost);
    }
    
    /**
     * 既に公開済みの投稿を再度公開しようとした場合のテスト
     * - nullが返されることを確認
     */
    @Test
    void publishPost_ShouldReturnNullForAlreadyPublishedPost() {
        // Given
        Post draft = postService.createDraft("Test content");
        Long id = draft.getId();
        postService.publishPost(id);
        
        // When
        Post result = postService.publishPost(id);
        
        // Then
        assertNull(result);
    }
    
    /**
     * 投稿削除機能のテスト
     * - 投稿が正しく削除されることを確認
     * - 削除後はその投稿IDで取得できないことを確認
     */
    @Test
    void deletePost_ShouldRemovePostAndReturnTrue() {
        // Given
        Post post = postService.createDraft("Test content");
        Long id = post.getId();
        
        // When
        boolean result = postService.deletePost(id);
        
        // Then
        assertTrue(result);
        assertNull(postService.getPost(id));
    }
    
    /**
     * 存在しない投稿IDで削除を試みた場合のテスト
     * - falseが返されることを確認
     */
    @Test
    void deletePost_ShouldReturnFalseForNonExistingPost() {
        // When
        boolean result = postService.deletePost(999L);
        
        // Then
        assertFalse(result);
    }
    
    /**
     * 投稿取得機能のテスト
     * - 正しい投稿IDで投稿が取得できることを確認
     */
    @Test
    void getPost_ShouldReturnCorrectPost() {
        // Given
        Post post = postService.createDraft("Test content");
        Long id = post.getId();
        
        // When
        Post retrievedPost = postService.getPost(id);
        
        // Then
        assertNotNull(retrievedPost);
        assertEquals(id, retrievedPost.getId());
        assertEquals("Test content", retrievedPost.getContent());
    }
    
    /**
     * 存在しない投稿IDで取得を試みた場合のテスト
     * - nullが返されることを確認
     */
    @Test
    void getPost_ShouldReturnNullForNonExistingPost() {
        // When
        Post retrievedPost = postService.getPost(999L);
        
        // Then
        assertNull(retrievedPost);
    }
    
    /**
     * 公開済み投稿一覧取得機能のテスト
     * - 公開済み投稿のみが返されることを確認
     * - 下書き投稿が含まれないことを確認
     */
    @Test
    void getAllPublishedPosts_ShouldReturnOnlyPublishedPosts() {
        // Given
        Post draft1 = postService.createDraft("Draft 1");
        Post draft2 = postService.createDraft("Draft 2");
        Post published1 = postService.createDraft("Published 1");
        Post published2 = postService.createDraft("Published 2");
        
        postService.publishPost(published1.getId());
        postService.publishPost(published2.getId());
        
        // When
        List<Post> publishedPosts = postService.getAllPublishedPosts();
        
        // Then
        assertEquals(2, publishedPosts.size());
        for (Post post : publishedPosts) {
            assertFalse(post.isDraft());
            assertNotNull(post.getPublishedAt());
        }
    }
    
    /**
     * 下書き投稿一覧取得機能のテスト
     * - 下書き投稿のみが返されることを確認
     * - 公開済み投稿が含まれないことを確認
     */
    @Test
    void getAllDraftPosts_ShouldReturnOnlyDraftPosts() {
        // Given
        Post draft1 = postService.createDraft("Draft 1");
        Post draft2 = postService.createDraft("Draft 2");
        Post published1 = postService.createDraft("Published 1");
        Post published2 = postService.createDraft("Published 2");
        
        postService.publishPost(published1.getId());
        postService.publishPost(published2.getId());
        
        // When
        List<Post> draftPosts = postService.getAllDraftPosts();
        
        // Then
        assertEquals(2, draftPosts.size());
        for (Post post : draftPosts) {
            assertTrue(post.isDraft());
            assertNull(post.getPublishedAt());
        }
    }
}
