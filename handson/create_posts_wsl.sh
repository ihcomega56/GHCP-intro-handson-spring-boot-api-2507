#!/bin/bash

# WSL/Linux環境用の投稿作成スクリプト
# 10件の下書き投稿を作成し、そのうち5件を公開する

# 色付きの出力用関数
print_info() {
    echo -e "\033[36m$1\033[0m"  # シアン色
}

print_success() {
    echo -e "\033[32m$1\033[0m"  # 緑色
}

print_error() {
    echo -e "\033[31m$1\033[0m"  # 赤色
}

# 結果を保存する配列
post_ids=()

print_info "スクリプトを開始します..."

# サーバーの状態確認
print_info "サーバーの状態を確認中..."
if ! curl -s --connect-timeout 5 http://localhost:8080/api/posts/published > /dev/null; then
    print_error "エラー: サーバーに接続できません。Spring Bootアプリケーションが起動していることを確認してください。"
    exit 1
fi
print_success "サーバーが正常に動作しています。"

# jqがインストールされているかチェック
if ! command -v jq &> /dev/null; then
    print_info "jqがインストールされていません。簡易的なJSONパースを使用します。"
    USE_JQ=false
else
    USE_JQ=true
fi

# 10件の下書き投稿を作成
for i in {1..10}
do
    print_info "下書き投稿 $i を作成中..."
    
    response=$(curl -s -X POST http://localhost:8080/api/posts/drafts \
        -H "Content-Type: application/json" \
        -d "{\"content\": \"投稿${i}です。これは下書き状態の投稿内容です。\"}")
    
    # HTTPステータスコードをチェック
    http_status=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/posts/drafts \
        -H "Content-Type: application/json" \
        -d "{\"content\": \"投稿${i}です。これは下書き状態の投稿内容です。\"}")
    
    if [ "$http_status" != "201" ]; then
        print_error "エラー: 下書き投稿 $i の作成に失敗しました。HTTPステータス: $http_status"
        exit 1
    fi
    
    # レスポンスからIDを抽出
    if [ "$USE_JQ" = true ]; then
        id=$(echo "$response" | jq -r '.id')
    else
        # jqが利用できない場合の簡易的なJSONパース
        id=$(echo "$response" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')
    fi
    
    if [ -z "$id" ] || [ "$id" = "null" ]; then
        print_error "エラー: 投稿IDの抽出に失敗しました。レスポンス: $response"
        exit 1
    fi
    
    post_ids+=($id)
    print_success "下書き投稿 $i が作成されました。ID: $id"
    
    # 日時に差をつけるために待機
    sleep 1
done

print_success "10件の下書き投稿が作成されました。"
echo "-------------------------------------"

# 最初の5件を公開
for i in {0..4}
do
    id=${post_ids[$i]}
    print_info "投稿 ID: $id を公開中..."
    
    http_status=$(curl -s -o /dev/null -w "%{http_code}" -X PUT http://localhost:8080/api/posts/drafts/$id/publish)
    
    if [ "$http_status" != "200" ]; then
        print_error "エラー: 投稿 ID: $id の公開に失敗しました。HTTPステータス: $http_status"
    else
        print_success "投稿 ID: $id が公開されました。"
    fi
    
    # 日時に差をつけるために待機
    sleep 1
done

echo "-------------------------------------"
print_success "5件の投稿が公開されました。"
print_info "残りの5件は下書き状態のままです。"

# 公開済み投稿の確認
echo "-------------------------------------"
print_info "公開済み投稿一覧:"
published_response=$(curl -s -X GET http://localhost:8080/api/posts/published)

if [ "$USE_JQ" = true ]; then
    echo "$published_response" | jq '.'
else
    echo "$published_response"
fi

# 下書き投稿の確認
echo "-------------------------------------"
print_info "下書き投稿一覧:"
draft_response=$(curl -s -X GET http://localhost:8080/api/posts/drafts)

if [ "$USE_JQ" = true ]; then
    echo "$draft_response" | jq '.'
else
    echo "$draft_response"
fi

echo "-------------------------------------"
print_success "スクリプトが完了しました。"
