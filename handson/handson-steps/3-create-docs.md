## この章でやること

テキストベースの設計書を作成し、`Post.java` の仕様をドキュメント化しましょう！

## 手順

1. Copilot Chat にドキュメンテーションの相談をします。
    - Mermaid記法を使ってドキュメントを作成したいです。このクラスに対しどのような設計図が描けますか？
    - クラス図を作成してください。
    - `matchesSearchCriteria` メソッドのシーケンス図を作成してください。
1. Copilot Chat の回答を`docs/diagrams.md`に反映させます。
1. 【Mermaid記法の確認ができる環境限定】右上の`Open Preview to the Side`![mermaid-preview](images/3-mermaid-preview.png)ボタンをクリックします。 :bulb: 本リポジトリの　Codespaces では Dev Container を使って予め Mermaid 記法のプレビューが出来る拡張機能を入れています。他の環境では同様の拡張機能のインストールが必要です。

## 余裕がある方向けの追加ワーク

1. Copilot Chat で `PostController.java` の OpenAPI ドキュメントを作成します。
1. [Swagger Editor](https://editor.swagger.io/) に生成されたドキュメントをペーストし、API仕様書を確認します。