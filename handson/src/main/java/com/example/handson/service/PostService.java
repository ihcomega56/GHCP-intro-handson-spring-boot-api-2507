package com.example.handson.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.handson.model.Post;

/**
 * 投稿（Post）に関するビジネスロジックを提供するサービスクラス。
 * 
 * このクラスは投稿の作成、更新、削除、検索などの基本的なCRUD操作と、
 * 下書き・公開状態の管理、複合条件による投稿検索機能を提供します。
 * 
 * デモ用途のため、データの永続化にはインメモリストレージ（ConcurrentHashMap）を
 * 使用しています。本番環境では適切なデータベースアクセス層に置き換える必要があります。
 */
@Service
public class PostService {
    /** デモ用にシンプルなインメモリストレージを使用しています */
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    
    /** 投稿IDの自動生成用カウンタ */
    private final AtomicLong idGenerator = new AtomicLong(1L);

    /**
     * デフォルトコンストラクタ。
     * 
     * PostServiceインスタンスを作成し、内部のデータストレージとIDジェネレータを初期化します。
     */
    public PostService() {
        // Spring DIコンテナによって自動的に呼び出されます
    }

    /**
     * 複数の条件を指定して投稿を検索します。
     * 
     * 指定された条件に基づいて投稿をフィルタリングし、マッチする投稿のリストを返します。
     * 全ての条件はAND条件で評価されます。
     * 
     * @param keyword コンテンツ内で検索するキーワード（部分一致、大文字小文字区別なし）
     * @param beforeId この投稿ID未満の投稿を対象とする
     * @param afterId この投稿IDより大きい投稿を対象とする
     * @param fromDate この日付以降に作成された投稿を対象とする（yyyy-MM-dd形式）
     * @param toDate この日付以前に作成された投稿を対象とする（yyyy-MM-dd形式）
     * @param isDraft 下書き状態フィルタ（true: 下書きのみ、false: 公開済みのみ、null: 全て）
     * @param minWordCount 最小文字数（この文字数以上のコンテンツを持つ投稿を対象）
     * @param maxWordCount 最大文字数（この文字数以下のコンテンツを持つ投稿を対象）
     * @return 検索条件にマッチする投稿のリスト
     */
    public List<Post> searchPostsWithFilters(String keyword, Long beforeId, Long afterId, String fromDate,
            String toDate, Boolean isDraft, Integer minWordCount, Integer maxWordCount) {
        List<Post> result = new java.util.ArrayList<>();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date fromD = null;
        java.util.Date toD = null;
        try {
            if (fromDate != null && !fromDate.isEmpty())
                fromD = sdf.parse(fromDate);
            if (toDate != null && !toDate.isEmpty())
                toD = sdf.parse(toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<Long, Post> entry : posts.entrySet()) {
            Post p = entry.getValue();
            boolean match = true;
            if (isDraft != null && p.isDraft() != isDraft)
                match = false;
            if (match && keyword != null && !keyword.isEmpty()) {
                if (p.getContent() == null || !p.getContent().toLowerCase().contains(keyword.toLowerCase()))
                    match = false;
            }
            if (match && beforeId != null) {
                if (p.getId() >= beforeId)
                    match = false;
            }
            if (match && afterId != null) {
                if (p.getId() <= afterId)
                    match = false;
            }
            if (match && fromD != null) {
                if (p.getCreatedAt() == null || p.getCreatedAt().isBefore(fromD.toInstant()))
                    match = false;
            }
            if (match && toD != null) {
                if (p.getCreatedAt() == null || p.getCreatedAt().isAfter(toD.toInstant()))
                    match = false;
            }
            if (match && minWordCount != null) {
                if (p.getContent() == null || p.getContent().split("\\s+").length < minWordCount)
                    match = false;
            }
            if (match && maxWordCount != null) {
                if (p.getContent() == null || p.getContent().split("\\s+").length > maxWordCount)
                    match = false;
            }
            if (match)
                result.add(p);
        }
        return result;
    }

    /**
     * 新しい下書き投稿を作成します。
     * 
     * 指定されたコンテンツで新しい投稿を作成し、下書き状態で保存します。
     * 投稿には自動的に一意のIDが割り当てられます。
     * 
     * @param content 投稿のコンテンツ内容
     * @return 作成された投稿オブジェクト
     */
    public Post createDraft(String content) {
        Post post = new Post(content);
        post.setId(idGenerator.getAndIncrement());
        posts.put(post.getId(), post);
        return post;
    }
    
    /**
     * 指定されたIDの下書き投稿を公開状態に変更します。
     * 
     * 対象の投稿が存在し、かつ下書き状態の場合のみ公開処理を実行します。
     * 公開処理では下書きフラグをfalseに設定し、公開日時を現在時刻に設定します。
     * 
     * @param id 公開する投稿のID
     * @return 公開処理が成功した場合は更新された投稿オブジェクト、失敗した場合はnull
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
     * 指定されたIDの投稿を削除します。
     * 
     * @param id 削除する投稿のID
     * @return 削除が成功した場合true、指定されたIDの投稿が存在しない場合false
     */
    public boolean deletePost(Long id) {
        return posts.remove(id) != null;
    }
    
    /**
     * 指定されたIDの投稿を取得します。
     * 
     * @param id 取得する投稿のID
     * @return 指定されたIDの投稿オブジェクト、存在しない場合はnull
     */
    public Post getPost(Long id) {
        return posts.get(id);
    }
    
    /**
     * 全ての公開済み投稿を取得します。
     * 
     * 下書き状態ではない（isDraft = false）投稿のみを抽出して返します。
     * 
     * @return 公開済み投稿のリスト
     */
    public List<Post> getAllPublishedPosts() {
        return posts.values().stream()
                .filter(post -> !post.isDraft())
                .collect(Collectors.toList());
    }
    
    /**
     * 全ての下書き投稿を取得します。
     * 
     * 下書き状態（isDraft = true）の投稿のみを抽出して返します。
     * 
     * @return 下書き投稿のリスト
     */
    public List<Post> getAllDraftPosts() {
        return posts.values().stream()
                .filter(Post::isDraft)
                .collect(Collectors.toList());
    }
}
