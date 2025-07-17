package com.example.handson.model;

import java.time.Instant;

/**
 * 投稿（Post）を表すエンティティクラス。
 * 
 * このクラスは投稿システムにおける投稿データを管理します。
 * 投稿には作成日時、更新日時、公開日時などのタイムスタンプ情報と、
 * 下書き状態管理機能が含まれています。
 * 
 * 投稿は作成時に自動的に下書き状態に設定され、明示的に公開操作を
 * 行うことで一般公開されます。
 */
public class Post {
    /** 投稿の一意識別子 */
    private Long id;
    
    /** 投稿のコンテンツ内容 */
    private String content;
    
    /** 投稿の作成日時 */
    private Instant createdAt;
    
    /** 投稿の最終更新日時 */
    private Instant updatedAt;
    
    /** 投稿の公開日時（下書き状態の場合はnull） */
    private Instant publishedAt;
    
    /** 下書き状態フラグ（true: 下書き、false: 公開済み） */
    private boolean isDraft;

    /**
     * デフォルトコンストラクタ。
     * 
     * 新しい投稿インスタンスを作成し、作成日時を現在時刻に設定、
     * 下書き状態をtrueに初期化します。
     */
    public Post() {
        this.createdAt = Instant.now();
        this.isDraft = true;
    }

    /**
     * コンテンツ指定コンストラクタ。
     * 
     * 指定されたコンテンツで新しい投稿インスタンスを作成し、
     * 作成日時と更新日時を現在時刻に設定、下書き状態をtrueに初期化します。
     * 
     * @param content 投稿のコンテンツ内容
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
     * @return 投稿の一意識別子
     */
    public Long getId() {
        return id;
    }

    /**
     * 投稿IDを設定します。
     * 
     * @param id 設定する投稿の一意識別子
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 投稿コンテンツを取得します。
     * 
     * @return 投稿のコンテンツ内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 投稿コンテンツを設定します。
     * 
     * コンテンツを設定すると同時に、更新日時を現在時刻に自動更新します。
     * 
     * @param content 設定する投稿のコンテンツ内容
     */
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    /**
     * 投稿の作成日時を取得します。
     * 
     * @return 投稿が作成された日時
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 投稿の作成日時を設定します。
     * 
     * @param createdAt 設定する作成日時
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 投稿の最終更新日時を取得します。
     * 
     * @return 投稿が最後に更新された日時
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 投稿の最終更新日時を設定します。
     * 
     * @param updatedAt 設定する更新日時
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 投稿の公開日時を取得します。
     * 
     * @return 投稿が公開された日時（下書き状態の場合はnull）
     */
    public Instant getPublishedAt() {
        return publishedAt;
    }

    /**
     * 投稿の公開日時を設定します。
     * 
     * @param publishedAt 設定する公開日時
     */
    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * 投稿が下書き状態かどうかを判定します。
     * 
     * @return 下書き状態の場合true、公開済みの場合false
     */
    public boolean isDraft() {
        return isDraft;
    }

    /**
     * 投稿の下書き状態を設定します。
     * 
     * 下書き状態をfalse（公開）に変更し、かつ公開日時が未設定の場合は、
     * 公開日時を現在時刻に自動設定します。
     * 
     * @param draft 設定する下書き状態（true: 下書き、false: 公開）
     */
    public void setDraft(boolean draft) {
        isDraft = draft;
        if (!draft && publishedAt == null) {
            publishedAt = Instant.now();
        }
    }

    /**
     * 指定された検索条件にこの投稿がマッチするかを判定します。
     * 
     * 下書き状態または公開日時が未設定の投稿は、検索条件に関わらず
     * マッチしません。公開済み投稿のみが検索対象となります。
     * 
     * @param searchParams 検索条件を含むパラメータオブジェクト（nullの場合は全ての公開済み投稿がマッチ）
     * @return 検索条件にマッチする場合true、そうでなければfalse
     */
    public boolean matchesSearchCriteria(SearchParams searchParams) {
        if (this.isDraft || this.publishedAt == null) {
            return false;
        }
        
        if (searchParams == null) {
            return true;
        }
        
        if (searchParams.getContentKeyword() != null && !searchParams.getContentKeyword().isEmpty()) {
            if (this.content == null) {
                return false;
            }
            
            String contentLower = this.content.toLowerCase();
            String keywordLower = searchParams.getContentKeyword().toLowerCase();
            
            if (!contentLower.contains(keywordLower)) {
                return false;
            }
        }
        
        if (searchParams.getPublishedAfter() != null) {
            if (this.publishedAt.isBefore(searchParams.getPublishedAfter())) {
                return false;
            }
        }
        
        if (searchParams.getPublishedBefore() != null) {
            if (this.publishedAt.isAfter(searchParams.getPublishedBefore())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 投稿検索のための検索条件を保持するクラス。
     * 
     * このクラスは投稿検索時に使用される各種フィルタリング条件を
     * カプセル化します。キーワード検索、公開日時範囲による絞り込み
     * などの条件を設定できます。
     */
    public static class SearchParams {
        /** コンテンツ内検索キーワード */
        private String contentKeyword;
        
        /** 検索対象の公開日時下限（この日時以降に公開された投稿を対象） */
        private Instant publishedAfter;
        
        /** 検索対象の公開日時上限（この日時以前に公開された投稿を対象） */
        private Instant publishedBefore;
        
        /**
         * デフォルトコンストラクタ。
         * 
         * 全ての検索条件が未設定の状態でSearchParamsインスタンスを作成します。
         */
        public SearchParams() {
            // 全ての条件が未設定のインスタンスを作成
        }
        
        /**
         * コンテンツ検索キーワードを取得します。
         * 
         * @return 設定された検索キーワード
         */
        public String getContentKeyword() {
            return contentKeyword;
        }
        
        /**
         * コンテンツ検索キーワードを設定します。
         * 
         * @param contentKeyword 投稿コンテンツ内で検索するキーワード
         */
        public void setContentKeyword(String contentKeyword) {
            this.contentKeyword = contentKeyword;
        }
        
        /**
         * 公開日時の下限を取得します。
         * 
         * @return 検索対象とする公開日時の下限
         */
        public Instant getPublishedAfter() {
            return publishedAfter;
        }
        
        /**
         * 公開日時の下限を設定します。
         * 
         * @param publishedAfter この日時以降に公開された投稿を検索対象とする日時
         */
        public void setPublishedAfter(Instant publishedAfter) {
            this.publishedAfter = publishedAfter;
        }
        
        /**
         * 公開日時の上限を取得します。
         * 
         * @return 検索対象とする公開日時の上限
         */
        public Instant getPublishedBefore() {
            return publishedBefore;
        }
        
        /**
         * 公開日時の上限を設定します。
         * 
         * @param publishedBefore この日時以前に公開された投稿を検索対象とする日時
         */
        public void setPublishedBefore(Instant publishedBefore) {
            this.publishedBefore = publishedBefore;
        }
    }

    /**
     * このオブジェクトと指定されたオブジェクトが等しいかどうかを判定します。
     * 
     * 投稿オブジェクトの等価性は、投稿IDに基づいて判定されます。
     * 
     * @param o 比較対象のオブジェクト
     * @return オブジェクトが等しい場合true、そうでなければfalse
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;
        return id != null ? id.equals(post.id) : post.id == null;
    }

    /**
     * このオブジェクトのハッシュコードを返します。
     * 
     * ハッシュコードは投稿IDに基づいて計算されます。
     * 
     * @return このオブジェクトのハッシュコード値
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * この投稿オブジェクトの文字列表現を返します。
     * 
     * 投稿ID、コンテンツ、下書き状態を含む文字列を生成します。
     * 
     * @return 投稿オブジェクトの文字列表現
     */
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isDraft=" + isDraft +
                '}';
    }
}
