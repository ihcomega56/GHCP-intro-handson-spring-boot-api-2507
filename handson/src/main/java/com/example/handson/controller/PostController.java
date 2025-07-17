package com.example.handson.controller;

import com.example.handson.model.Post;
import com.example.handson.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 投稿（Post）に関するREST APIエンドポイントを提供するコントローラークラス。
 * 
 * このクラスは投稿管理システムのHTTP API層を担当し、投稿の作成、取得、
 * 更新、削除などの基本的なCRUD操作と、下書き・公開状態の管理機能を
 * RESTful APIとして提供します。
 * 
 * 全てのエンドポイントは"/api/posts"パスの下に配置されており、
 * JSON形式でのデータ交換をサポートします。
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    /** 投稿ビジネスロジックを処理するサービス */
    private final PostService postService;
    
    /**
     * PostControllerのコンストラクタ。
     * 
     * 依存性注入によりPostServiceインスタンスを受け取ります。
     * 
     * @param postService 投稿に関するビジネスロジックを提供するサービス
     */
    public PostController(PostService postService) {
        this.postService = postService;
    }
    
    /**
     * 新しい下書き投稿を作成します。
     * 
     * リクエストボディから投稿コンテンツを取得し、新しい下書き投稿を作成します。
     * 作成された投稿は自動的に下書き状態に設定されます。
     * 
     * @param payload リクエストボディ（"content"キーに投稿内容を含むMapオブジェクト）
     * @return 作成された投稿オブジェクトを含むResponseEntity（201 Created）、
     *         またはコンテンツが無効な場合は400 Bad Request
     */
    @PostMapping("/drafts")
    public ResponseEntity<Post> createDraft(@RequestBody Map<String, String> payload) {
        var content = payload.get("content");
        
        if (content == null) {
            return ResponseEntity.badRequest().build();
        }
        
        var createdPost = postService.createDraft(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    
    /**
     * 指定されたIDの下書き投稿を公開状態に変更します。
     * 
     * 下書き状態の投稿を公開状態に変更し、公開日時を設定します。
     * 既に公開済みの投稿や存在しない投稿に対しては操作を実行しません。
     * 
     * @param id 公開する投稿のID
     * @return 公開処理が成功した場合は更新された投稿オブジェクトを含むResponseEntity（200 OK）、
     *         指定されたIDの投稿が存在しないか公開できない場合は404 Not Found
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
     * 投稿IDに基づいて投稿の詳細情報を取得します。
     * 下書き状態、公開状態に関わらず投稿が存在すれば取得できます。
     * 
     * @param id 取得する投稿のID
     * @return 指定されたIDの投稿オブジェクトを含むResponseEntity（200 OK）、
     *         指定されたIDの投稿が存在しない場合は404 Not Found
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
     * 投稿を完全に削除します。下書き状態、公開状態に関わらず削除されます。
     * 
     * @param id 削除する投稿のID
     * @return 削除が成功した場合は204 No Content、
     *         指定されたIDの投稿が存在しない場合は404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postService.deletePost(id) 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.notFound().build();
    }
    
    /**
     * 全ての公開済み投稿を取得します。
     * 
     * 下書き状態ではない、公開済みの投稿のみを一覧で取得します。
     * 
     * @return 公開済み投稿のリストを含むResponseEntity（200 OK）
     */
    @GetMapping("/published")
    public ResponseEntity<List<Post>> getAllPublishedPosts() {
        var posts = postService.getAllPublishedPosts();
        return ResponseEntity.ok(posts);
    }
    
    /**
     * 全ての下書き投稿を取得します。
     * 
     * 下書き状態の投稿のみを一覧で取得します。
     * 
     * @return 下書き投稿のリストを含むResponseEntity（200 OK）
     */
    @GetMapping("/drafts")
    public ResponseEntity<List<Post>> getAllDraftPosts() {
        var posts = postService.getAllDraftPosts();
        return ResponseEntity.ok(posts);
    }
}
