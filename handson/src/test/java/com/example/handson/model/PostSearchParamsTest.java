package com.example.handson.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * テストクラス：Post.SearchParamsクラスの機能をテスト
 */
public class PostSearchParamsTest {

    /**
     * コンテンツキーワード設定のテスト
     */
    @Test
    @DisplayName("コンテンツキーワードが正しく設定できること")
    public void testSetContentKeyword() {
        // 準備
        Post.SearchParams params = new Post.SearchParams();
        String keyword = "テストキーワード";
        
        // 実行
        params.setContentKeyword(keyword);
        
        // 検証
        assertEquals(keyword, params.getContentKeyword());
    }
    
    /**
     * 公開日時（以降）設定のテスト
     */
    @Test
    @DisplayName("公開日時（以降）が正しく設定できること")
    public void testSetPublishedAfter() {
        // 準備
        Post.SearchParams params = new Post.SearchParams();
        Instant now = Instant.now();
        
        // 実行
        params.setPublishedAfter(now);
        
        // 検証
        assertEquals(now, params.getPublishedAfter());
    }
    
    /**
     * 公開日時（以前）設定のテスト
     */
    @Test
    @DisplayName("公開日時（以前）が正しく設定できること")
    public void testSetPublishedBefore() {
        // 準備
        Post.SearchParams params = new Post.SearchParams();
        Instant now = Instant.now();
        
        // 実行
        params.setPublishedBefore(now);
        
        // 検証
        assertEquals(now, params.getPublishedBefore());
    }
    
    /**
     * 検索条件組み合わせのテスト
     */
    @Test
    @DisplayName("複数の検索条件を組み合わせて設定できること")
    public void testCombinedSearchParams() {
        // 準備
        Post.SearchParams params = new Post.SearchParams();
        String keyword = "テストキーワード";
        Instant afterDate = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant beforeDate = Instant.now();
        
        // 実行
        params.setContentKeyword(keyword);
        params.setPublishedAfter(afterDate);
        params.setPublishedBefore(beforeDate);
        
        // 検証
        assertEquals(keyword, params.getContentKeyword());
        assertEquals(afterDate, params.getPublishedAfter());
        assertEquals(beforeDate, params.getPublishedBefore());
    }
}
