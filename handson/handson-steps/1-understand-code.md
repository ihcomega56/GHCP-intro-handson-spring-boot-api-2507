## この章でやること

ソースコードコメントが全くないアプリケーションの中身を理解しましょう！

## 手順

### GitHub サイト上で実行

1. [ソースコードのリポジトリ](https://github.com/ihcomega56/GHCP-intro-handson2504)にアクセスします。
1. 画面右上、サーチウィンドウ横の Copilot アイコンをクリックします。
1. チャット入力欄にコンテキストとしてリポジトリが追加されていることを確認し、アプリケーションについて質問をします。
    ![copilot-attatchment](images/1-copilot-attatchment.png)

### プロンプト例

- これはどのようなリポジトリですか？

<summary>回答例</summary>
<pre>
<code>
このリポジトリ（ihcomega56/GHCP-intro-handson2504）は、Javaで構成されているリポジトリであり、100% Javaで書かれています。リポジトリ名と構成から推測すると、このリポジトリはGitHub Copilotのイントロやハンズオンに関連する内容を含んでいる可能性があります。

より具体的な内容を知りたい場合、リポジトリのREADMEファイルやプロジェクトファイルを確認することで、詳細な情報を得られる可能性があります。必要であれば、リポジトリの内容を検索したり、特定のファイルを探すこともできます！
</code>
</pre>
</details>

- Webアプリケーションのソースコードを探して、アプリケーションの概要を説明してください。

<summary>回答例</summary>
<pre>
<code>
このリポジトリには、Spring Bootフレームワークを使用したJavaベースのWebアプリケーションが含まれています。以下にアプリケーションの概要を示します。

概要

1. アプリケーションのエントリーポイント:

- HandsonApplication.java がアプリケーションのエントリーポイントです。このファイルは Spring Boot を使用してアプリケーションを起動するメインクラスです。
- [コードはこちら](https://github.com/ihcomega56/GHCP-intro-handson2504/blob/6867eb16be9db5605a877b300be74859e1d847d2/handson/src/main/java/com/example/handson/HandsonApplication.java):
```java
@SpringBootApplication
public class HandsonApplication {
    public static void main(String[] args) {
        SpringApplication.run(HandsonApplication.class, args);
    }
}
```

1. テストファイル:

- HandsonApplicationTests.java は Spring Boot のコンテキストロードを確認するテストクラスです。
- [テストコードはこちら](https://github.com/ihcomega56/GHCP-intro-handson2504/blob/6867eb16be9db5605a877b300be74859e1d847d2/handson/src/test/java/com/example/handson/HandsonApplicationTests.java).

1. EADMEファイルの説明:

- リポジトリの目的は GitHub Copilot を活用するためのハンズオン資料とソースコードを提供することです。
- 具体的なハンズオン用のソースコードや資料は、handsonディレクトリ内にあると記載されています。
- [README全文はこちら](https://github.com/ihcomega56/GHCP-intro-handson2504/blob/6867eb16be9db5605a877b300be74859e1d847d2/README.md).

リポジトリの構造やコードなど、さらに詳しい情報が必要であればお知らせください！
</code>
</pre>
</details>

他にも色々質問してみてください。皆さんが得る答えは同じではありません。また、同じことを問うても質問の表現や順番によっても回答が変わります。

- Spring Bootアプリケーションのビジネスロジックはどこにありますか？
- `PostController.java` はどのような処理をしていますか？
- `Post.java` にリファクタリングの余地はありますか？
- ユニットテストを追加するとしたら、Controller, Model, Serviceのうちどこから着手すべきですか？
- その他、思いついた質問を何でも自由に！

### Visual Studio Code で実行

1. GitHub Codespaces（推奨）またはローカルのVisual Studio Codeで本リポジトリを開きます。
1. 画面上部サーチウィンドウ横の Copilot アイコンをクリックします。
1. GitHub サイトで実施したのと同様に色々な質問をします。
1. コマンド・チャット変数・チャット参加者を試します。 :bulb: 使える機能は[チートシート](https://code.visualstudio.com/docs/copilot/reference/copilot-vscode-features)を参照してください。
    - `@workspace /explain` を実行
    - `@vscode /search Java Controller` を実行
    - `#file:1-understand-code.md これはどのようなファイルですか` を実行

## 余裕がある方向けの追加ワーク

1. 投げかけてみた質問や回答をTeamsで簡単に共有してください。