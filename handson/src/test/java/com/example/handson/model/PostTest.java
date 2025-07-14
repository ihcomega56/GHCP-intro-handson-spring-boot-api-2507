package com.example.handson.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * テストクラス：Postモデルの各機能をテスト
 */
public class PostTest {

    /**
     * デフォルトコンストラクタのテスト
     */
    @Test
    @DisplayName("デフォルトコンストラクタで作成時は下書き状態であること")
    public void testDefaultConstructor() {
        // 実行
        Post post = new Post();
        
        // 検証
        assertNull(post.getId());
        assertNull(post.getContent());
        assertNotNull(post.getCreatedAt());
        assertNull(post.getUpdatedAt());
        assertNull(post.getPublishedAt());
        assertTrue(post.isDraft());
    }
    
    /**
     * 引数ありコンストラクタのテスト
     */
    @Test
    @DisplayName("引数ありコンストラクタで作成時はコンテンツが設定され下書き状態であること")
    public void testConstructorWithContent() {
        // 準備
        String content = "テスト投稿";
        
        // 実行
        Post post = new Post(content);
        
        // 検証
        assertNull(post.getId());
        assertEquals(content, post.getContent());
        assertNotNull(post.getCreatedAt());
        assertNotNull(post.getUpdatedAt());
        assertNull(post.getPublishedAt());
        assertTrue(post.isDraft());
    }
    
    /**
     * コンテンツ設定時に更新日時が更新されることをテスト
     */
    @Test
    @DisplayName("コンテンツ設定時に更新日時が更新されること")
    public void testSetContentUpdatesTimestamp() {
        // 準備
        Post post = new Post();
        Instant originalTimestamp = post.getUpdatedAt();
        if (originalTimestamp != null) {
            // 時間差を確保するため少し待機
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // 実行
        post.setContent("更新後のコンテンツ");
        
        // 検証
        assertNotNull(post.getUpdatedAt());
        if (originalTimestamp != null) {
            assertTrue(post.getUpdatedAt().isAfter(originalTimestamp));
        }
    }
    
    /**
     * 下書き状態解除時に公開日時が設定されることをテスト
     */
    @Test
    @DisplayName("下書き状態解除時に公開日時が自動設定されること")
    public void testSetDraftFalseUpdatesPublishedAt() {
        // 準備
        Post post = new Post("テスト投稿");
        assertNull(post.getPublishedAt());
        
        // 実行
        post.setDraft(false);
        
        // 検証
        assertFalse(post.isDraft());
        assertNotNull(post.getPublishedAt());
    }
    
    /**
     * 公開日時が既に設定されている場合、下書き状態解除時に公開日時が変更されないことをテスト
     */
    @Test
    @DisplayName("公開日時が既存の場合は下書き状態解除時に公開日時が変更されないこと")
    public void testSetDraftFalsePreservesExistingPublishedAt() {
        // 準備
        Post post = new Post("テスト投稿");
        Instant originalPublishedAt = Instant.now().minus(1, ChronoUnit.DAYS);
        post.setPublishedAt(originalPublishedAt);
        
        // 実行
        post.setDraft(false);
        
        // 検証
        assertFalse(post.isDraft());
        assertEquals(originalPublishedAt, post.getPublishedAt());
    }
    
    /**
     * 検索条件とのマッチングテスト - 下書き状態の投稿はマッチしないこと
     */
    @Test
    @DisplayName("下書き状態の投稿は検索条件にマッチしないこと")
    public void testMatchesSearchCriteria_DraftPostsNeverMatch() {
        // 準備
        Post post = new Post("テスト投稿");
        post.setDraft(true);
        Post.SearchParams params = new Post.SearchParams();
        
        // 実行と検証
        assertFalse(post.matchesSearchCriteria(params));
    }
    
    /**
     * 検索条件とのマッチングテスト - 公開状態でキーワードが含まれる場合はマッチすること
     */
    @Test
    @DisplayName("公開状態でキーワードが含まれる場合は検索条件にマッチすること")
    public void testMatchesSearchCriteria_KeywordMatch() {
        // 準備
        Post post = new Post("このテスト投稿はサンプルです");
        post.setDraft(false);
        Post.SearchParams params = new Post.SearchParams();
        params.setContentKeyword("サンプル");
        
        // 実行と検証
        assertTrue(post.matchesSearchCriteria(params));
    }
    
    /**
     * 検索条件とのマッチングテスト - 公開状態でもキーワードが含まれない場合はマッチしないこと
     */
    @Test
    @DisplayName("公開状態でもキーワードが含まれない場合は検索条件にマッチしないこと")
    public void testMatchesSearchCriteria_KeywordNotMatch() {
        // 準備
        Post post = new Post("このテスト投稿はサンプルです");
        post.setDraft(false);
        Post.SearchParams params = new Post.SearchParams();
        params.setContentKeyword("見つからない");
        
        // 実行と検証
        assertFalse(post.matchesSearchCriteria(params));
    }
    
    /**
     * 検索条件とのマッチングテスト - 公開日時の範囲チェック（指定日時以降）
     */
    @Test
    @DisplayName("公開日時が指定された日時以降の場合は検索条件にマッチすること")
    public void testMatchesSearchCriteria_PublishedAfter() {
        // 準備
        Post post = new Post("テスト投稿");
        post.setDraft(false);
        Instant now = Instant.now();
        Instant beforeNow = now.minus(1, ChronoUnit.HOURS);
        Post.SearchParams params = new Post.SearchParams();
        params.setPublishedAfter(beforeNow);
        
        // 実行と検証
        assertTrue(post.matchesSearchCriteria(params));
    }
    
    /**
     * 検索条件とのマッチングテスト - 公開日時の範囲チェック（指定日時以前）
     */
    @Test
    @DisplayName("公開日時が指定された日時以前の場合は検索条件にマッチすること")
    public void testMatchesSearchCriteria_PublishedBefore() {
        // 準備
        Post post = new Post("テスト投稿");
        post.setDraft(false);
        Instant now = Instant.now();
        Instant afterNow = now.plus(1, ChronoUnit.HOURS);
        Post.SearchParams params = new Post.SearchParams();
        params.setPublishedBefore(afterNow);
        
        // 実行と検証
        assertTrue(post.matchesSearchCriteria(params));
    }
    
    /**
     * 検索条件とのマッチングテスト - 公開日時の範囲外（指定日時より前）の場合はマッチしないこと
     */
    @Test
    @DisplayName("公開日時が指定された日時より前の場合は検索条件にマッチしないこと")
    public void testMatchesSearchCriteria_PublishedBeforeNotMatch() {
        // 準備
        Post post = new Post("テスト投稿");
        post.setDraft(false);
        Instant now = Instant.now();
        Instant afterNow = now.plus(1, ChronoUnit.HOURS);
        post.setPublishedAt(afterNow);
        Post.SearchParams params = new Post.SearchParams();
        params.setPublishedBefore(now);
        
        // 実行と検証
        assertFalse(post.matchesSearchCriteria(params));
    }
    
    /**
     * 検索条件とのマッチングテスト - 複合条件（キーワードと日時範囲）すべてにマッチする場合
     */
    @Test
    @DisplayName("複合条件（キーワードと日時範囲）すべてにマッチする場合")
    public void testMatchesSearchCriteria_ComplexMatch() {
        // 準備
        Post post = new Post("このテスト投稿はサンプルです");
        post.setDraft(false);
        Instant now = Instant.now();
        Instant beforeNow = now.minus(1, ChronoUnit.HOURS);
        Instant afterNow = now.plus(1, ChronoUnit.HOURS);
        
        Post.SearchParams params = new Post.SearchParams();
        params.setContentKeyword("サンプル");
        params.setPublishedAfter(beforeNow);
        params.setPublishedBefore(afterNow);
        
        // 実行と検証
        assertTrue(post.matchesSearchCriteria(params));
    }
    
    /**
     * Equals メソッドのテスト - 同じIDの場合はequalsがtrueを返すこと
     */
    @Test
    @DisplayName("同じIDを持つ投稿は等しいとみなされること")
    public void testEquals_SameId() {
        // 準備
        Post post1 = new Post("投稿1");
        Post post2 = new Post("投稿2");
        Long id = 1L;
        post1.setId(id);
        post2.setId(id);
        
        // 実行と検証
        assertEquals(post1, post2);
        assertEquals(post1.hashCode(), post2.hashCode());
    }
    
    /**
     * Equals メソッドのテスト - 異なるIDの場合はequalsがfalseを返すこと
     */
    @Test
    @DisplayName("異なるIDを持つ投稿は等しくないとみなされること")
    public void testEquals_DifferentId() {
        // 準備
        Post post1 = new Post("投稿1");
        Post post2 = new Post("投稿2");
        post1.setId(1L);
        post2.setId(2L);
        
        // 実行と検証
        assertNotEquals(post1, post2);
        assertNotEquals(post1.hashCode(), post2.hashCode());
    }
    
    /**
     * Equals メソッドのテスト - IDがnullの場合はequalsがfalseを返すこと
     */
    @Test
    @DisplayName("IDがnullの投稿同士の比較で等しくないとみなされること")
    public void testEquals_NullId() {
        // 準備
        Post post1 = new Post("投稿1");
        Post post2 = new Post("投稿2");
        
        // 実行と検証
        // 両方nullの場合は等しいとみなす
        assertEquals(post1, post2);
        assertEquals(post1.hashCode(), post2.hashCode());
        
        // 片方だけnullの場合は等しくないとみなす
        post1.setId(1L);
        assertNotEquals(post1, post2);
        assertNotEquals(post1.hashCode(), post2.hashCode());
    }
    
    /**
     * toString メソッドのテスト
     */
    @Test
    @DisplayName("toStringメソッドがIDとコンテンツと下書き状態を含む文字列を返すこと")
    public void testToString() {
        // 準備
        Post post = new Post("テスト投稿");
        post.setId(1L);
        
        // 実行
        String result = post.toString();
        
        // 検証
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("content='テスト投稿'"));
        assertTrue(result.contains("isDraft=true"));
    }
}
