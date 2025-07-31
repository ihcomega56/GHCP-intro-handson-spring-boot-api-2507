package com.example.handson.model;

import java.time.Instant;

/**
 * 投稿を表すモデルクラス。
 * 投稿は下書き状態か公開状態のいずれかで、様々な検索条件によりフィルタリングできます。
 */
public class Post {
    private Long id;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;
    private boolean isDraft;

    /**
     * デフォルトコンストラクタ。
     * 作成日時は現在時刻に設定され、初期状態は下書きとなります。
     */
    public Post() {
        this.createdAt = Instant.now();
        this.isDraft = true;
    }

    /**
     * 指定された内容で投稿を作成するコンストラクタ。
     * 作成日時と更新日時は現在時刻に設定され、初期状態は下書きとなります。
     *
     * @param content 投稿内容
     */
    public Post(String content) {
        this.content = content;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.isDraft = true;
    }

    /**
     * 投稿IDを取得します。
     *
     * @return 投稿ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 投稿IDを設定します。
     *
     * @param id 投稿ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 投稿内容を取得します。
     *
     * @return 投稿内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 投稿内容を設定します。
     * 内容が更新されると更新日時も現在時刻に更新されます。
     *
     * @param content 投稿内容
     */
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    /**
     * 作成日時を取得します。
     *
     * @return 作成日時
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 作成日時を設定します。
     *
     * @param createdAt 作成日時
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 更新日時を取得します。
     *
     * @return 更新日時
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 更新日時を設定します。
     *
     * @param updatedAt 更新日時
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 公開日時を取得します。
     *
     * @return 公開日時
     */
    public Instant getPublishedAt() {
        return publishedAt;
    }

    /**
     * 公開日時を設定します。
     *
     * @param publishedAt 公開日時
     */
    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * 投稿が下書き状態かどうかを返します。
     *
     * @return 下書き状態の場合はtrue、そうでない場合はfalse
     */
    public boolean isDraft() {
        return isDraft;
    }

    /**
     * 投稿の下書き状態を設定します。
     * 下書きから公開状態に変更する場合で、公開日時が未設定の場合は、公開日時を現在時刻に設定します。
     *
     * @param draft 下書き状態の場合はtrue、そうでない場合はfalse
     */
    public void setDraft(boolean draft) {
        isDraft = draft;
        if (!draft && publishedAt == null) {
            publishedAt = Instant.now();
        }
    }

    /**
     * 指定された検索条件にこの投稿が一致するかどうかを判定します。
     * 下書き状態または未公開の投稿は常に検索条件に一致しません。
     *
     * @param searchParams 検索条件
     * @return 検索条件に一致する場合はtrue、そうでない場合はfalse
     */
    public boolean matchesSearchCriteria(SearchParams searchParams) {
        // 下書きや未公開の投稿は検索対象外
        if (isDraft || publishedAt == null) {
            return false;
        }
        
        // 検索条件がnullの場合は常に一致
        if (searchParams == null) {
            return true;
        }
        
        // すべての条件を満たすかどうかをチェック
        boolean matchesKeywordCondition = matchesKeyword(searchParams.getContentKeyword());
        boolean matchesTimeRangeCondition = isPublishedInTimeRange(
                searchParams.getPublishedAfter(), 
                searchParams.getPublishedBefore());
        
        return matchesKeywordCondition && matchesTimeRangeCondition;
    }
    
    /**
     * 投稿内容が指定されたキーワードを含むかどうかを判定します。
     *
     * @param keyword 検索キーワード
     * @return キーワードを含む場合はtrue、そうでない場合はfalse
     */
    private boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        
        if (content == null) {
            return false;
        }
        
        return content.toLowerCase().contains(keyword.toLowerCase());
    }
    
    /**
     * 投稿が指定された公開日時の範囲内かどうかを判定します。
     *
     * @param after この日時以降に公開
     * @param before この日時以前に公開
     * @return 範囲内の場合はtrue、そうでない場合はfalse
     */
    private boolean isPublishedInTimeRange(Instant after, Instant before) {
        return (after == null || !publishedAt.isBefore(after))
            && (before == null || !publishedAt.isAfter(before));
    }
    
    /**
     * 投稿の検索条件を表す内部クラス。
     */
    public static class SearchParams {
        private String contentKeyword;
        private Instant publishedAfter;
        private Instant publishedBefore;
        
        /**
         * 検索キーワードを取得します。
         *
         * @return 検索キーワード
         */
        public String getContentKeyword() {
            return contentKeyword;
        }
        
        /**
         * 検索キーワードを設定します。
         *
         * @param contentKeyword 検索キーワード
         */
        public void setContentKeyword(String contentKeyword) {
            this.contentKeyword = contentKeyword;
        }
        
        /**
         * 開始日時を取得します。この日時以降に公開された投稿が検索対象となります。
         *
         * @return 開始日時
         */
        public Instant getPublishedAfter() {
            return publishedAfter;
        }
        
        /**
         * 開始日時を設定します。
         *
         * @param publishedAfter 開始日時
         */
        public void setPublishedAfter(Instant publishedAfter) {
            this.publishedAfter = publishedAfter;
        }
        
        /**
         * 終了日時を取得します。この日時以前に公開された投稿が検索対象となります。
         *
         * @return 終了日時
         */
        public Instant getPublishedBefore() {
            return publishedBefore;
        }
        
        /**
         * 終了日時を設定します。
         *
         * @param publishedBefore 終了日時
         */
        public void setPublishedBefore(Instant publishedBefore) {
            this.publishedBefore = publishedBefore;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Post post = (Post) o;
        return id != null ? id.equals(post.id) : post.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isDraft=" + isDraft +
                '}';
    }
}
