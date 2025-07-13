## サンプルアプリケーションの実行準備

### 必要な環境

- JDK 21以上
- 【任意】 Gradle 8.4以上 ※Gradle Wrapperでもかまいません。サンプルコマンドでも使用しています。

### 実行方法（Windows PowerShell）

1. ハンズオンディレクトリに移動します。
    ```powershell
    cd \path\to\GHCP-intro-handson2504\handson\
    ```

2. 必要な依存関係をインストールします。
    ```powershell
    .\gradlew.bat build
    ```

3. アプリケーションを起動します。
    ```powershell
    .\gradlew.bat bootRun
    ```

4. アプリケーションが起動したら、以下のURLでAPIを利用できます。
    ```
    http://localhost:8080/api/posts
    ```

### 実行方法（Mac, WSL2 / Linux）

1. ハンズオンディレクトリに移動します。
    ```bash
    cd /workspaces/GHCP-intro-handson2504/handson/
    ```

2. 必要な依存関係をインストールします。
    ```bash
    ./gradlew build
    ```

3. アプリケーションを起動します。
    ```bash
    ./gradlew bootRun
    ```

4. アプリケーションが起動したら、以下のURLでAPIを利用できます。
    ```
    http://localhost:8080/api/posts
    ```

---

## API実行のサンプルcurlコマンド

### 1. 下書き投稿の作成
```bash
curl -X POST http://localhost:8080/api/posts/drafts \
-H "Content-Type: application/json" \
-d '{"content": "This is a draft post."}'
```

### 2. 下書き投稿の公開
```bash
curl -X PUT http://localhost:8080/api/posts/drafts/{id}/publish
```

### 3. 投稿の取得
```bash
curl -X GET http://localhost:8080/api/posts/{id}
```

### 4. 投稿の削除
```bash
curl -X DELETE http://localhost:8080/api/posts/{id}
```

### 5. 公開済み投稿の一覧取得
```bash
curl -X GET http://localhost:8080/api/posts/published
```

### 6. 下書き投稿の一覧取得
```bash
curl -X GET http://localhost:8080/api/posts/drafts
```

## 下書きを10件投稿、そのうち5件を公開するスクリプト

プロジェクトには、10件の下書き投稿を作成して、そのうち5件を公開状態にするシェルスクリプト `create_posts.sh` が用意されています。

### スクリプトの実行方法（Windows PowerShell）

```powershell
# PowerShellでスクリプトを実行
.\create_posts.ps1
```

Windows環境では、シェルスクリプト（.sh）の代わりにPowerShellスクリプト（.ps1）を使用します。
プロジェクトには `create_posts.ps1` が用意されています。

### スクリプトの実行方法（Mac, WSL2 / Linux）

```bash
# スクリプトに実行権限を付与（初回のみ）
chmod +x create_posts.sh

# スクリプトを実行
./create_posts.sh
```

