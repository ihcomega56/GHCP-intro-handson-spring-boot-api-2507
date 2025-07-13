# PowerShellスクリプト: 下書きを10件投稿し、そのうち5件を公開する

# 結果を保存する配列
$post_ids = @()

# 10件の下書き投稿を作成
for ($i = 1; $i -le 10; $i++) {
    Write-Host "下書き投稿 $i を作成中..."
    $jsonBody = @{
        content = "投稿${i}です。これは下書き状態の投稿内容です。"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/posts/drafts" -Method Post -ContentType "application/json" -Body $jsonBody
    $post_ids += $response.id
    Write-Host "下書き投稿 $i が作成されました。ID: $($response.id)"
    
    # 日時に差をつけるために待機
    Start-Sleep -Seconds 1
}

Write-Host "10件の下書き投稿が作成されました。"
Write-Host "-------------------------------------"

# 最初の5件を公開
for ($i = 0; $i -lt 5; $i++) {
    $id = $post_ids[$i]
    Write-Host "投稿 ID: $id を公開中..."
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/posts/drafts/$id/publish" -Method Put
    Write-Host "投稿 ID: $id が公開されました。"
    
    # 日時に差をつけるために待機
    Start-Sleep -Seconds 1
}

Write-Host "-------------------------------------"
Write-Host "5件の投稿が公開されました。"
Write-Host "残りの5件は下書き状態のままです。"

# 公開済み投稿の確認
Write-Host "-------------------------------------"
Write-Host "公開済み投稿一覧:"
$publishedPosts = Invoke-RestMethod -Uri "http://localhost:8080/api/posts/published" -Method Get
$publishedPosts | ConvertTo-Json -Depth 3

# 下書き投稿の確認
Write-Host "-------------------------------------"
Write-Host "下書き投稿一覧:"
$draftPosts = Invoke-RestMethod -Uri "http://localhost:8080/api/posts/drafts" -Method Get
$draftPosts | ConvertTo-Json -Depth 3
