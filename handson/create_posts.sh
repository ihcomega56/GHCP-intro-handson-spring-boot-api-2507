#!/bin/bash

# 結果を保存する配列
post_ids=()

# 10件の下書き投稿を作成
for i in {1..10}
do
  echo "下書き投稿 $i を作成中..."
  response=$(curl -s -X POST http://localhost:8080/api/posts/drafts \
    -H "Content-Type: application/json" \
    -d "{\"content\": \"投稿${i}です。\"}")
  
  # レスポンスからIDを抽出 (JSONパース)
  id=$(echo $response | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
  post_ids+=($id)
  echo "下書き投稿 $i が作成されました。ID: $id"
  
  # 日時に差をつけるために待機
  sleep 1
done

echo "10件の下書き投稿が作成されました。"
echo "-------------------------------------"

# 最初の5件を公開
for i in {0..4}
do
  id=${post_ids[$i]}
  echo "投稿 ID: $id を公開中..."
  curl -s -X PUT http://localhost:8080/api/posts/drafts/$id/publish
  echo "投稿 ID: $id が公開されました。"
  
  # 日時に差をつけるために待機
  sleep 1
done

echo "-------------------------------------"
echo "5件の投稿が公開されました。"
echo "残りの5件は下書き状態のままです。"

# 公開済み投稿の確認
echo "-------------------------------------"
echo "公開済み投稿一覧:"
curl -s -X GET http://localhost:8080/api/posts/published | json_pp

# 下書き投稿の確認
echo "-------------------------------------"
echo "下書き投稿一覧:"
curl -s -X GET http://localhost:8080/api/posts/drafts | json_pp
