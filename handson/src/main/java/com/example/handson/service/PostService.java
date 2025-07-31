package com.example.handson.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.handson.model.Post;

@Service
public class PostService {
    // デモ用にシンプルなインメモリストレージを使用しています
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    /**
     * 投稿を指定されたフィルター条件で検索します
     * 
     * @param keyword 検索キーワード
     * @param beforeId 指定ID以前の投稿のみ取得
     * @param afterId 指定ID以降の投稿のみ取得
     * @param fromDate 指定日付以降の投稿のみ取得（yyyy-MM-dd形式）
     * @param toDate 指定日付以前の投稿のみ取得（yyyy-MM-dd形式）
     * @param isDraft 下書き状態でフィルタリング
     * @param minWordCount 最小ワード数
     * @param maxWordCount 最大ワード数
     * @return フィルター条件に一致する投稿のリスト
     */
    public List<Post> searchPostsWithFilters(String keyword, Long beforeId, Long afterId, String fromDate,
            String toDate, Boolean isDraft, Integer minWordCount, Integer maxWordCount) {
        
        // 日付文字列をInstantに変換
        Instant fromInstant = parseDate(fromDate);
        Instant toInstant = parseDate(toDate);
        
        return posts.values().stream()
            .filter(post -> matchesDraftStatus(post, isDraft))
            .filter(post -> matchesKeyword(post, keyword))
            .filter(post -> matchesIdRange(post, beforeId, afterId))
            .filter(post -> matchesDateRange(post, fromInstant, toInstant))
            .filter(post -> matchesWordCount(post, minWordCount, maxWordCount))
            .collect(Collectors.toList());
    }
    
    /**
     * 文字列形式の日付をInstantに変換します
     * 
     * @param dateString yyyy-MM-dd形式の日付文字列
     * @return 変換されたInstant、変換できない場合はnull
     */
    private Instant parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateString).toInstant();
        } catch (ParseException e) {
            // ログ出力などのエラーハンドリングを追加するとよい
            return null;
        }
    }
    
    /**
     * 投稿が指定された下書き状態に一致するか確認します
     * 
     * @param post 投稿
     * @param isDraft 下書き状態
     * @return 条件に一致する場合はtrue
     */
    private boolean matchesDraftStatus(Post post, Boolean isDraft) {
        return isDraft == null || post.isDraft() == isDraft;
    }
    
    /**
     * 投稿が指定されたキーワードを含むか確認します
     * 
     * @param post 投稿
     * @param keyword 検索キーワード
     * @return 条件に一致する場合はtrue
     */
    private boolean matchesKeyword(Post post, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        return post.getContent() != null && 
               post.getContent().toLowerCase().contains(keyword.toLowerCase());
    }
    
    /**
     * 投稿のIDが指定された範囲内かを確認します
     * 
     * @param post 投稿
     * @param beforeId 上限ID（これより小さい）
     * @param afterId 下限ID（これより大きい）
     * @return 条件に一致する場合はtrue
     */
    private boolean matchesIdRange(Post post, Long beforeId, Long afterId) {
        return (beforeId == null || post.getId() < beforeId) 
               && (afterId == null || post.getId() > afterId);
    }
    
    /**
     * 投稿の日付が指定された範囲内かを確認します
     * 
     * @param post 投稿
     * @param fromInstant 開始日時
     * @param toInstant 終了日時
     * @return 条件に一致する場合はtrue
     */
    private boolean matchesDateRange(Post post, Instant fromInstant, Instant toInstant) {
        return (fromInstant == null || (post.getCreatedAt() != null && 
                                     !post.getCreatedAt().isBefore(fromInstant)))
               && (toInstant == null || (post.getCreatedAt() != null && 
                                     !post.getCreatedAt().isAfter(toInstant)));
    }
    
    /**
     * 投稿の単語数が指定された範囲内かを確認します
     * 
     * @param post 投稿
     * @param minWordCount 最小単語数
     * @param maxWordCount 最大単語数
     * @return 条件に一致する場合はtrue
     */
    private boolean matchesWordCount(Post post, Integer minWordCount, Integer maxWordCount) {
        if (post.getContent() == null) {
            return minWordCount == null;
        }
        
        int wordCount = countWords(post.getContent());
        
        return (minWordCount == null || wordCount >= minWordCount)
             && (maxWordCount == null || wordCount <= maxWordCount);
    }
    
    /**
     * 文字列内の単語数をカウントします
     * 
     * @param content テキスト内容
     * @return 単語数
     */
    private int countWords(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        return content.split("\\s+").length;
    }

    /**
     * 新しい下書き投稿を作成します
     * 
     * @param content 投稿内容
     * @return 作成された投稿
     */
    public Post createDraft(String content) {
        Post post = new Post(content);
        post.setId(idGenerator.getAndIncrement());
        posts.put(post.getId(), post);
        return post;
    }
    
    /**
     * 下書き状態の投稿を公開します
     * 
     * @param id 投稿ID
     * @return 公開された投稿、IDが存在しないか既に公開済みの場合はnull
     */
    public Post publishPost(Long id) {
        Post post = posts.get(id);
        if (post != null && post.isDraft()) {
            post.setDraft(false);
            post.setPublishedAt(new Date().toInstant());
            return post;
        }
        return null;
    }
    
    /**
     * 投稿を削除します
     * 
     * @param id 投稿ID
     * @return 削除に成功した場合はtrue、IDが存在しない場合はfalse
     */
    public boolean deletePost(Long id) {
        return posts.remove(id) != null;
    }
    
    /**
     * 指定されたIDの投稿を取得します
     * 
     * @param id 投稿ID
     * @return 投稿、IDが存在しない場合はnull
     */
    public Post getPost(Long id) {
        return posts.get(id);
    }
    
    /**
     * 公開済みの全投稿を取得します
     * 
     * @return 公開済み投稿のリスト
     */
    public List<Post> getAllPublishedPosts() {
        return posts.values().stream()
                .filter(post -> !post.isDraft())
                .collect(Collectors.toList());
    }
    
    /**
     * 下書き状態の全投稿を取得します
     * 
     * @return 下書き投稿のリスト
     */
    public List<Post> getAllDraftPosts() {
        return posts.values().stream()
                .filter(Post::isDraft)
                .collect(Collectors.toList());
    }
}
