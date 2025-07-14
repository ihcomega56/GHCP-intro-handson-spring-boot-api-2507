## この章でやること

`PostController.java` をリファクタリングするヒントを貰ったり、実際にソースコードを改善したりしましょう！
この改善は次のワークに繋がります。

## 手順

1. Copilot Chatにリファクタリングの相談をします。
    - `#sym:searchPostsWithFilters(String keyword, Long beforeId, Long afterId, String fromDate, String toDate, Boolean isDraft, Integer minWordCount, Integer maxWordCount)` の可読性を高めたいです。どのような修正ができますか？
1. Copilot を使ってリファクタリングを行います。コード補完、Chat、Agent Modeなどを自由に活用してみてください。Copilotの回答に疑問があるときは質問もしてみてください。
    - `searchPostsWithFilters` の動作を担保できるユニットテストはありますか？
    - `searchPostsWithFilters` のユニットテストを追加してください。
    - filter処理でStream APIを使ってください。
    - Stream APIを使った処理について分かりやすく解解してください。
    - 日付処理では `java.time` APIを使ってください。

## 余裕がある方向けの追加ワーク

1. `Post.java` の改善も行います。コード補完、Chat、Agent Modeなどを自由に活用してみてください。
    - `#file:Post.java` にリファクタリングの余地はありますか？Java 21を使っています。
    - `Optional` を導入して `null` 安全性を高めてください。
    - `Optional` とは何ですか？どのように活用できますか？通常の `null` チェックと何が異なりますか？
    - `Record` クラスに書き換えてください。
    - `String.format` を使って複数行の文字列の可読性を上げてください。