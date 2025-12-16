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

## 開発運用ルール（リポジトリ既定）
- 仕様書更新: 本ファイル `docs/specs.md` を更新
- タスク計画: `docs/tasks.md` を参照・更新
- 実装完了時: `docs/reports/done/done_YYYY-MM-DD_実装内容.md` を作成

## 参照ファイル一覧（主要）
- schedule/src/main/java/oit/is/team4/schedule/controller/RegistController.java
- schedule/src/main/java/oit/is/team4/schedule/controller/AdminPendingController.java
- schedule/src/main/java/oit/is/team4/schedule/security/scheduleAuthConfiguration.java
- schedule/src/main/java/oit/is/team4/schedule/model/PendingUser.java
- schedule/src/main/java/oit/is/team4/schedule/repository/PendingUserRepository.java
- schedule/src/main/resources/templates/admin/pending.html
- schedule/src/main/resources/templates/registuser.html
- schedule/src/main/resources/application.properties
