# Sharendar - 仕様書 (Spec)

## 概要
Sharendar はスケジュール管理・共有と簡易画像リアクション・コメント機能を備えた Spring Boot アプリケーション。主な機能はカレンダー表示・1日予定管理、画像へのいいね/リアクション・コメント、ユーザ登録/認証。

## 実行方法
- ビルド / 実行: プロジェクトルートで `gradle bootRun`
- 設定: `schedule/src/main/resources/application.properties` を参照（デフォルト: H2 in-memory, ポート 80）
  - ファイル: [schedule/src/main/resources/application.properties](schedule/src/main/resources/application.properties)

## 主要パッケージとファイル
- コントローラ
  - カレンダー: [`oit.is.team4.schedule.controller.CalendarController`](schedule/src/main/java/oit/is/team4/schedule/controller/CalendarController.java)
  - サンプル画像のリアクション: [`oit.is.team4.schedule.controller.LikeController`](schedule/src/main/java/oit/is/team4/schedule/controller/LikeController.java)
  - コメント追加: [`oit.is.team4.schedule.controller.AddCommentController`](schedule/src/main/java/oit/is/team4/schedule/controller/AddCommentController.java)
  - ユーザ登録フォーム: [`oit.is.team4.schedule.controller.RegistController`](schedule/src/main/java/oit/is/team4/schedule/controller/RegistController.java)
  - 管理者承認: [`oit.is.team4.schedule.controller.AdminPendingController`](schedule/src/main/java/oit/is/team4/schedule/controller/AdminPendingController.java)
  - その他: [`oit.is.team4.schedule.controller.Sample1Controller`](schedule/src/main/java/oit/is/team4/schedule/controller/Sample1Controller.java)

- セキュリティ設定
  - [`oit.is.team4.schedule.security.scheduleAuthConfiguration`](schedule/src/main/java/oit/is/team4/schedule/security/scheduleAuthConfiguration.java)

- エンティティ / リポジトリ
  - PendingUser: [`schedule/src/main/java/oit/is/team4/schedule/model/PendingUser.java`](schedule/src/main/java/oit/is/team4/schedule/model/PendingUser.java)
  - PendingUserRepository: [`schedule/src/main/java/oit/is/team4/schedule/repository/PendingUserRepository.java`](schedule/src/main/java/oit/is/team4/schedule/repository/PendingUserRepository.java)
  - Comment / CommentRepository: [`schedule/src/main/java/oit/is/team4/schedule/model/Comment.java`](schedule/src/main/java/oit/is/team4/schedule/model/Comment.java), [`schedule/src/main/java/oit/is/team4/schedule/repository/CommentRepository.java`](schedule/src/main/java/oit/is/team4/schedule/repository/CommentRepository.java)
  - ImageLike: [`schedule/src/main/java/oit/is/team4/schedule/model/ImageLike.java`](schedule/src/main/java/oit/is/team4/schedule/model/ImageLike.java)

- テンプレート / 静的ファイル
  - サンプル画像画面: [schedule/src/main/resources/templates/sampleimage.html](schedule/src/main/resources/templates/sampleimage.html)
  - 登録画面: [schedule/src/main/resources/templates/registuser.html](schedule/src/main/resources/templates/registuser.html)
  - カレンダー / 日別予定: [schedule/src/main/resources/templates/sample1.html](schedule/src/main/resources/templates/sample1.html), [schedule/src/main/resources/templates/schedule_day.html](schedule/src/main/resources/templates/schedule_day.html)
  - 静的 index: [schedule/src/main/resources/static/index.html](schedule/src/main/resources/static/index.html)

- 初期データ
  - `data.sql` に会員データの INSERT がある
    - ファイル: [schedule/src/main/resources/data.sql](schedule/src/main/resources/data.sql)

## 主な機能仕様 (エンドポイント)
- GET /calendar
  - 表示: 月間カレンダー（パラメータ: year, month - 任意）
  - 実装: [`CalendarController.showCalendar`](schedule/src/main/java/oit/is/team4/schedule/controller/CalendarController.java)

- 日別予定表示/追加
  - GET /schedule/day (テンプレートで日付を表示) — 実装箇所を参照
  - POST /addplan
    - フォームにより year/month/day, start_time, end_time, title 等を受け取り保存

- 画像リアクション / コメント
  - POST /sampleimage/react
    - パラメータ: type (例: "laugh" 等)
    - 実装: [`LikeController.reactSampleImage`](schedule/src/main/java/oit/is/team4/schedule/controller/LikeController.java)
    - モデル: `ImageLike`（`new ImageLike(filename, 0, 0, 0)` で初期化される）
  - POST /sampleimage/comment
    - パラメータ: author (任意), text (必須)
    - 実装: [`AddCommentController.postComment`](schedule/src/main/java/oit/is/team4/schedule/controller/AddCommentController.java)
    - モデル: `Comment`（filename, author, text, createdAt 等）

- ユーザ登録 / 認証
  - GET /registuser, POST /auth/registuser
    - 実装: [`RegistController`](schedule/src/main/java/oit/is/team4/schedule/controller/RegistController.java)
  - セキュリティ: in-memory ユーザ定義あり（例: `やに`, `まっちょ`）
    - 実装: [`scheduleAuthConfiguration.userDetailsService`](schedule/src/main/java/oit/is/team4/schedule/security/scheduleAuthConfiguration.java)
  - GET /admin/pending
    - 表示: 管理者用の申請一覧（ROLE_ADMIN 必須）
    - 実装: [`AdminPendingController.listPending`](schedule/src/main/java/oit/is/team4/schedule/controller/AdminPendingController.java)
  - POST /admin/pending/approve/{id}
    - 動作: 指定申請を承認して実ユーザを作成
    - 備考: PendingUser.password（ハッシュ済）をそのまま UserDetails に渡して InMemoryUserDetailsManager.createUser を実行、その後申請を削除
    - 実装: [`AdminPendingController.approve`](schedule/src/main/java/oit/is/team4/schedule/controller/AdminPendingController.java)
  - POST /admin/pending/reject/{id}
    - 動作: 指定申請を却下（削除）
    - 実装: [`AdminPendingController.reject`](schedule/src/main/java/oit/is/team4/schedule/controller/AdminPendingController.java)

## 画像アップロードとリアクション（追加仕様）

### 画像アップロード（概要）
- 概要: ユーザは予定作成画面または専用画面から画像をアップロードできる。
- 保存先:
  - アプリ起点の uploads/ ディレクトリにファイルを保存（例: project-root/uploads/）
  - 静的配信設定: `WebConfig.addResourceHandlers` で `/uploads/**` を `file:uploads/` にマップすること
    - 実装ファイル例: `schedule/src/main/java/oit/is/team4/schedule/security/WebConfig.java`
- DB 保存（image テーブル、または ImageRecord エンティティ）
  - 必須カラム/フィールド:
    - id (自動採番)
    - image_name (保存ファイル名、例: UUID_元ファイル名)
    - original_name (元ファイル名)
    - uploader (ユーザ名)
    - scheduled_time (画像が紐づく日時、nullable)
    - uploaded_at (タイムスタンプ)
  - 実装箇所: `schedule/src/main/java/oit/is/team4/schedule/mapper/ImageMapper.java` または対応する Repository

### 画像リアクション（概要）
- ユーザは各画像に対してリアクション（like, laugh, sad 等）を行える。
- リアクションの保存先:
  - `image_reaction_log` テーブル（エンティティ: ImageReactionLog）
  - カラム例: id, image_id, user_name, reaction_type, created_at
  - リポジトリ: `schedule/src/main/java/oit/is/team4/schedule/repository/ImageReactionLogRepository.java`
- 集計表示:
  - ImageLike 等のモデルで集計値（heartCount, laughCount 等）を保持・表示するか、動的クエリで集計して表示する。

### 日付枠の虹色表示ルール（新機能）
- 目的: 同一画像に対して「24時間以内」に2ユーザ以上のリアクションがあった場合、その画像が紐づく日付セル（カレンダー、日別表示のヘッダ・時間枠）を虹色（.rainbow-day）でハイライトする。
- 判定ロジック:
  1. ある画像(image_id)について、現在時刻から過去24時間以内に記録された distinct user_name の数を集計する。
  2. distinct user count >= 2 なら、その画像の scheduled_time に対応する日付を虹色ハイライト対象とする。
- 実装案:
  - 表示時に動的に判定する方法（DB で集計クエリを発行）
    - メリット: 常に最新の状態を表示
    - デメリット: 表示負荷が高い可能性あり（多数の画像・高トラフィック時）
  - リアクション登録時に集計カラムを更新する方法（イベントでフラグ更新）
    - メリット: 表示負荷を軽減
    - デメリット: 一貫性・同期の手間（同時更新対策が必要）
  - 負荷対策:
    - Redis 等で短期キャッシュする
    - バッチジョブで集計し結果をキャッシュする

- 表示実装例:
  - カレンダー画面:
    - 日付セルに `.rainbow-day` クラスを付与し、CSS でグラデーションアニメを適用
    - ホバーで当該日の画像サムネイル一覧をポップアップ表示
  - 日別表示:
    - 該当時刻ブロック内のサムネイルや日付ヘッダに虹色枠を適用

- DB クエリ例（概念）
```sql
-- 過去24時間にリアクションしたユニークユーザ数を取得
SELECT image_id, COUNT(DISTINCT user_name) AS uniq_users
FROM image_reaction_log
WHERE created_at >= DATEADD('HOUR', -24, CURRENT_TIMESTAMP)
GROUP BY image_id
HAVING COUNT(DISTINCT user_name) >= 2;
```

## 表示振る舞いの注意点
- 画像ファイル名に日本語や特殊文字がある場合、URL エンコードを行うか保存時にファイル名を UUID 等に置換することを推奨する。
- 静的配信で画像が見えることを先に確認する（ブラウザから `/uploads/{filename}` で直接アクセス）。
- テンプレート側では Thymeleaf で安全に値を取り出す（例: `#maps.get(imagesByHour, h)`）こと。

## 参照ファイル
- 画像保存: `schedule/src/main/java/oit/is/team4/schedule/mapper/ImageMapper.java`（DB 操作）
- 静的配信: `schedule/src/main/java/oit/is/team4/schedule/security/WebConfig.java`
- リアクション記録: `schedule/src/main/java/oit/is/team4/schedule/repository/ImageReactionLogRepository.java`
- 画像モデル: `schedule/src/main/java/oit/is/team4/schedule/model/ImageRecord.java`（存在する場合）
- 日別テンプレート: `schedule/src/main/resources/templates/schedule_day.html`

## 開発運用ルール（リポジトリ既定）
- 仕様書更新: 本ファイル `docs/specs.md` を更新
- タスク計画: `docs/tasks.md` を参照・更新
- 実装完了時: `docs/reports/done/done_YYYY-MM-DD_実装内容.md` を作成

## 参照 / 追加メモ
- 実装時は `docs/tasks.md` に詳細タスクを記載して段階的に進めること
- 実装フェーズでは `docs/reports/done/` に完了報告を残すこと

## データモデル（想定）
- Member
  - カラム例: id, userName, email
  - 初期データ: [schedule/src/main/resources/data.sql](schedule/src/main/resources/data.sql)
- ImageLike
  - フィールド: filename, heartCount, laughCount, ...（ソースを参照）
  - 参照: [`LikeController`](schedule/src/main/java/oit/is/team4/schedule/controller/LikeController.java)
- Comment
  - フィールド: id, filename, author, text, createdAt
  - 参照: [`AddCommentController`](schedule/src/main/java/oit/is/team4/schedule/controller/AddCommentController.java)

## ビルド / ランタイム要件
- Java 21（toolchain 指定）: `schedule/build.gradle`
  - ファイル: [schedule/build.gradle](schedule/build.gradle)
- Spring Boot 3.x
- H2 データベース（デフォルトはインメモリ）

## テスト方針
- 単体テスト: コントローラの主要処理（POST のバリデーション、リダイレクト）をスプリングの MockMvc 等でテスト
- インテグレーション: H2 を使った起動テスト（例: `gradle test` に統合）
- 新機能テスト追加案:
  - 画像アップロードの保存確認テスト（ファイル存在 + DB レコード）
  - リアクション登録から24時間以内のユニークユーザ数判定ロジックの単体テスト

## 参照ファイル一覧（主要）
- schedule/src/main/java/oit/is/team4/schedule/controller/RegistController.java
- schedule/src/main/java/oit/is/team4/schedule/controller/AdminPendingController.java
- schedule/src/main/java/oit/is/team4/schedule/security/scheduleAuthConfiguration.java
- schedule/src/main/java/oit/is/team4/schedule/model/PendingUser.java
- schedule/src/main/java/oit/is/team4/schedule/repository/PendingUserRepository.java
- schedule/src/main/resources/templates/admin/pending.html
- schedule/src/main/resources/templates/registuser.html
- schedule/src/main/resources/application.properties
