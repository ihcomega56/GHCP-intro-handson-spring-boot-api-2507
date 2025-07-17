package com.example.handson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Bootハンズオンアプリケーションのメインクラス。
 * 
 * このクラスはSpring Bootアプリケーションのエントリーポイントとして機能し、
 * 投稿（Post）管理システムのWebアプリケーションを起動します。
 * 
 * {@code @SpringBootApplication}アノテーションにより、以下の機能が自動的に有効化されます：
 * <ul>
 * <li>自動設定（Auto-configuration）</li>
 * <li>コンポーネントスキャン</li>
 * <li>設定クラスの登録</li>
 * </ul>
 */
@SpringBootApplication
public class HandsonApplication {

    /**
     * デフォルトコンストラクタ。
     */
    public HandsonApplication() {
        // Spring Bootによって自動的に呼び出されます
    }

    /**
     * アプリケーションのメインエントリーポイント。
     * 
     * Spring Bootアプリケーションを起動し、組み込みWebサーバーを開始します。
     * アプリケーションは指定されたコマンドライン引数を使用して起動されます。
     * 
     * @param args コマンドライン引数の配列
     */
    public static void main(String[] args) {
        SpringApplication.run(HandsonApplication.class, args);
    }
}
