package com.example.handson.controller;

import com.example.handson.model.Post;
import com.example.handson.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 投稿に関するRESTコントローラーです。
 * 投稿の作成、公開、取得、削除などの操作を提供します。
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    private final PostService postService;
    
    /**
     * コンストラクタです。
     * 
     * @param postService 投稿サービス
     */
    public PostController(PostService postService) {
        this.postService = postService;
    }
    
    /**
     * 下書き投稿を作成します。
     * 
     * @param payload リクエストボディ（"content"キーに投稿内容が含まれる必要があります）
     * @return 作成された投稿
     */
    @PostMapping("/drafts")
    public ResponseEntity<Post> createDraft(@RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        
        if (content == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Post createdPost = postService.createDraft(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    
    /**
     * 下書き投稿を公開状態に変更します。
     * 
     * @param id 公開する投稿のID
     * @return 公開された投稿、または投稿が見つからない場合は404エラー
     */
    @PutMapping("/drafts/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long id) {
        return Optional.ofNullable(postService.publishPost(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 指定されたIDの投稿を取得します。
     * 
     * @param id 取得する投稿のID
     * @return 投稿、または投稿が見つからない場合は404エラー
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return Optional.ofNullable(postService.getPost(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 指定されたIDの投稿を削除します。
     * 
     * @param id 削除する投稿のID
     * @return 削除成功時は204（No Content）、投稿が見つからない場合は404エラー
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postService.deletePost(id) 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.notFound().build();
    }
    
    /**
     * すべての公開済み投稿を取得します。
     * 
     * @return 公開済み投稿のリスト
     */
    @GetMapping("/published")
    public ResponseEntity<List<Post>> getAllPublishedPosts() {
        List<Post> posts = postService.getAllPublishedPosts();
        return ResponseEntity.ok(posts);
    }
    
    /**
     * すべての下書き投稿を取得します。
     * 
     * @return 下書き投稿のリスト
     */
    @GetMapping("/drafts")
    public ResponseEntity<List<Post>> getAllDraftPosts() {
        List<Post> posts = postService.getAllDraftPosts();
        return ResponseEntity.ok(posts);
    }
    
    /**
     * 投稿を検索します。
     * クエリパラメータを使用してフィルタリング条件を指定できます。
     * 
     * @param keyword キーワード検索
     * @param beforeId 指定ID以前の投稿のみ取得
     * @param afterId 指定ID以降の投稿のみ取得
     * @param fromDate 指定日付以降の投稿のみ取得（yyyy-MM-dd形式）
     * @param toDate 指定日付以前の投稿のみ取得（yyyy-MM-dd形式）
     * @param isDraft 下書き状態でフィルタリング
     * @param minWordCount 最小ワード数
     * @param maxWordCount 最大ワード数
     * @return 検索条件に一致する投稿のリスト
     */
    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long beforeId,
            @RequestParam(required = false) Long afterId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Boolean isDraft,
            @RequestParam(required = false) Integer minWordCount,
            @RequestParam(required = false) Integer maxWordCount) {
        
        List<Post> posts = postService.searchPostsWithFilters(
                keyword, beforeId, afterId, fromDate, toDate, isDraft, minWordCount, maxWordCount);
        
        return ResponseEntity.ok(posts);
    }
}
