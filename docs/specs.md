# Sharendar 仕様書（spec.md）

## 1. 概要
Sharendar は, Spring Boot を用いたスケジュール管理・共有アプリケーションである. 月間カレンダー表示, 日別予定管理, 画像アップロード, 画像へのリアクション・コメント, ユーザ登録・認証（管理者承認フロー含む）を提供する.

本仕様書は, 現在の実装と追加仕様（虹色ハイライト）を統合して, 開発・レビュー・テストの基準（DoD）として利用する.

---

## 2. 実行方法
### 2.1 ビルド・起動
- プロジェクトルートで実行する.
- 起動コマンド.
  - `gradle bootRun`

### 2.2 設定
- 設定ファイル.
  - `schedule/src/main/resources/application.properties`
- DB はデフォルトで H2（in-memory）を想定する. ポートや DB 設定は `application.properties` に従う.

---

## 3. 主要ディレクトリ構成
- `schedule/src/main/java/oit/is/team4/schedule/`
  - `controller/` 画面遷移・入力受付（HTTP）
  - `service/` 判定ロジック（集計, ハイライト判定など）
  - `repository/` JPA Repository（リアクションログ, コメント, 承認待ちユーザなど）
  - `mapper/` MyBatis Mapper（予定, 画像レコードなど）
  - `model/` Entity / DTO
  - `security/` セキュリティ設定, WebConfig
- `schedule/src/main/resources/templates/` Thymeleaf テンプレート
- `schedule/src/main/resources/static/` 静的ファイル
- `schedule/src/main/resources/schema.sql` スキーマ
- `schedule/src/main/resources/data.sql` 初期データ

---

## 4. 画面・機能一覧
### 4.1 月間カレンダー
- 月間カレンダーを表示し, 各日付から日別ページへ遷移できる.
- 必要に応じて, 虹色ハイライト対象の日付セルを強調表示する.

### 4.2 日別ページ（1日の予定）
- 指定日の予定を時間帯ごとに表示する.
- 指定日（または指定日時）に紐づくアップロード画像を表示する.
- 画像リンクを押下すると, 画像リアクション・コメントページへ遷移できる.

### 4.3 画像リアクション・コメント
- 画像単位で, リアクション（例: heart, like, laugh）を登録できる.
- 画像単位で, コメントの投稿と一覧表示ができる.
- リアクションは集計値表示（ImageLike 等）と, 操作ログ（ImageReactionLog）を保持する.

### 4.4 ユーザ登録・認証（管理者承認）
- 一般ユーザは登録申請を行い, 管理者が承認すると利用可能となる.
- 管理者は pending 一覧から承認・却下を行える.

---

## 5. エンドポイント仕様（主要）
### 5.1 カレンダー
#### GET `/calendar`
- 概要: 月間カレンダー表示.
- パラメータ（任意）.
  - `year`（例: 2025）
  - `month`（例: 12）
- レスポンス: `calendar.html`

### 5.2 日別ページ
#### GET `/schedule/day`
- 概要: 指定日の予定・画像を表示.
- パラメータ（必須）.
  - `date`（例: `2025-12-01`）
- レスポンス: `schedule_day.html`

#### POST `/addplan`
- 概要: 予定追加.
- 代表パラメータ（フォーム）.
  - `year`, `month`, `day`
  - `start_time`, `end_time`
  - `title`
- 動作: DB に保存し, 日別ページへリダイレクトする.

### 5.3 画像リアクション
#### POST `/sampleimage/react`
- 概要: 指定画像へリアクションを登録する.
- パラメータ.
  - `filename`（必須, 画像ファイル名）
  - `type`（必須, 例: `heart`, `like`, `laugh`）
- 動作.
  - 集計（ImageLike）を更新する.
  - ログ（ImageReactionLog）を1件保存する.
  - `/sampleimage?filename=...` にリダイレクトする.

### 5.4 画像コメント
#### GET `/sampleimage`
- 概要: 指定画像のリアクション・コメント画面を表示する.
- パラメータ（任意）.
  - `filename`（未指定時は `sample.png` を表示する）
- レスポンス: `sampleimage.html`

#### POST `/sampleimage/comment`
- 概要: 指定画像へコメントを投稿する.
- パラメータ.
  - `filename`（必須）
  - `text`（必須）
- 動作.
  - Comment を保存する（author は Principal から取得する. 未ログイン等は匿名）.
  - `/sampleimage?filename=...` にリダイレクトする.

### 5.5 ユーザ登録・管理者承認
#### GET `/registuser`
- 概要: 登録フォーム表示.
- レスポンス: `registuser.html`

#### POST `/auth/registuser`
- 概要: 登録申請を pending に保存する.

#### GET `/admin/pending`
- 概要: 管理者用, 申請一覧表示（ROLE_ADMIN 必須）.
- レスポンス: `admin/pending.html`

#### POST `/admin/pending/approve/{id}`
- 概要: 申請承認. InMemoryUserDetailsManager に実ユーザを作成し, 申請を削除する.

#### POST `/admin/pending/reject/{id}`
- 概要: 申請却下. 申請を削除する.

### 5.6 削除機能（画像・コメント・予定）

#### POST `/sampleimage/delete`
- 概要: 指定画像の削除。画像の所有者のみ実行可能（所有者でない場合はエラー）。
- パラメータ（フォーム）
  - `filename`（必須） — 削除対象の保存ファイル名（DB の `image_name` または filename）
  - CSRF トークン（フォーム内に hidden）
- 動作
  1. `filename` が空ならカレンダーへリダイレクト。
  2. DB の Image レコードを取得し、リクエストユーザが uploader/owner か検証する。
  3. 所有者であれば、関連する Comment、ImageLike、ImageReactionLog 等の関連データを削除し、Image レコードを削除する。
  4. ファイルシステム上の `uploads/filename` を存在すれば削除する（削除失敗でも DB の状態は優先して処理する）。
  5. フラッシュメッセージをセットして `/calendar` にリダイレクト。権限がない場合は `/sampleimage?filename=...` にエラーメッセージ付きでリダイレクト。
- セキュリティ
  - 所有者チェックを必須とする（管理者に特別扱いをする場合は別途明示）。
- 実装参照
  - [AddCommentController.java](schedule/src/main/java/oit/is/team4/schedule/controller/AddCommentController.java) の `/sampleimage/delete` 実装例
  - テンプレート側は `sampleimage.html` に削除フォームがある（hidden に filename と CSRF を含める）。

#### POST `/sampleimage/comment/delete`
- 概要: コメント削除。コメントの投稿者本人、または管理者（ROLE_ADMIN）に削除権限がある。
- パラメータ（フォーム）
  - `id`（必須） — 削除するコメントの ID
  - CSRF トークン
- 動作
  1. 指定 ID の Comment を検索。存在しなければ `/sampleimage` にエラーフラッシュを付けてリダイレクト。
  2. ログインユーザ名と Comment.author を比較、またはユーザに ROLE_ADMIN が付与されているか判定。
  3. 権限があればコメントを削除し、成功メッセージをフラッシュして `/sampleimage?filename=<コメントの filename>` にリダイレクト。権限がない場合はエラーメッセージで同画面へリダイレクト。
- 実装参照
  - [AddCommentController.java](schedule/src/main/java/oit/is/team4/schedule/controller/AddCommentController.java) の `/sampleimage/comment/delete` 実装例
  - `sampleimage.html` では投稿者または管理者にのみ削除ボタンを表示する条件がある（テンプレート参照）。

#### POST `/schedule/delete`
- 概要: 予定（schedule レコード）削除。予定の所有者のみ実行可能。
- パラメータ（フォーム）
  - `id`（必須） — 削除対象の予定 ID
  - `date`（戻り先の日付、フォームで送られる） — 削除後のリダイレクト先指定
  - CSRF トークン
- 動作
  1. ID で予定を取得。存在しなければ日別ページへリダイレクト。
  2. 現在のログインユーザと schedule.userName を比較して所有者チェックを行う。
  3. 所有者であれば DB から削除し、フラッシュメッセージを付与して `/schedule/day?date=<date>` にリダイレクト。権限がない場合はエラーフラッシュで同ページへリダイレクト。
- 実装参照
  - [ScheduleController.java](schedule/src/main/java/oit/is/team4/schedule/controller/ScheduleController.java) の `/schedule/delete` 実装例
  - テンプレート（`schedule_day.html`）では削除ボタンを予定の所有者のみ表示している。


---

## 6. 画像アップロード仕様
### 6.1 保存先
- ファイルはアプリ起点の `uploads/` に保存する（例: `project-root/uploads/`）.
- 静的配信は `WebConfig.addResourceHandlers` により, `/uploads/**` を `file:uploads/` にマッピングする.

### 6.2 DB 保存（ImageRecord / image テーブル）
- 画像のメタデータは DB に保存する.
- 想定フィールド.
  - `id`（自動採番）
  - `image_name`（保存ファイル名, 例: UUID_元ファイル名）
  - `original_name`（元ファイル名）
  - `uploader`（ユーザ名）
  - `scheduled_time`（画像が紐づく日時, nullable）
  - `uploaded_at`（タイムスタンプ）

### 6.3 ファイル名の取り扱い
- 日本語や特殊文字を含むファイル名は URL や OS 依存の問題を起こしやすい.
- 保存時に UUID などへ置換し, DB には original_name も保持することを推奨する.

---

## 7. 虹色ハイライト仕様（新機能）
### 7.1 目的
同一画像に対して「過去24時間以内」に2ユーザ以上のリアクションがあった場合, その画像が紐づく日付セルを虹色（`.rainbow-day` 等）でハイライトする.

### 7.2 判定ルール（必須）
1. 画像（`image_id` または `filename`）単位で, 現在時刻から過去24時間以内の `ImageReactionLog` を対象とする.
2. `distinct user_name` を数える.
3. `distinct user count >= 2` の画像を「ハイライト対象画像」とする.
4. 対象画像の `scheduled_time`（日時）から日付を求め, その日付セルをハイライト対象日とする.

### 7.3 実装方針（選択肢）
- 表示時に動的判定（DB 集計クエリ）
  - 長所: 常に最新状態.
  - 短所: 画像やログが多いと負荷.
- リアクション登録時に集計更新（フラグ更新）
  - 長所: 表示負荷を軽減.
  - 短所: 同時更新や整合性の設計が必要.
- 負荷対策案.
  - 短期キャッシュ（Redis 等）
  - バッチで集計し結果をキャッシュ

### 7.4 参考クエリ（概念）
```sql
SELECT image_id, COUNT(DISTINCT user_name) AS uniq_users
FROM image_reaction_log
WHERE created_at >= DATEADD('HOUR', -24, CURRENT_TIMESTAMP)
GROUP BY image_id
HAVING COUNT(DISTINCT user_name) >= 2;
```

---

## 8. 画面側実装ガイド
### 8.1 カレンダー画面（例）
- 日付セルに `.rainbow-day` を付与し, CSS でグラデーションアニメーションを適用する.
- ホバー時にポップアップ等を出す場合は, 体裁崩れ防止のため擬似要素（`::before`）や `box-shadow` を利用する.

### 8.2 日別ページ（例）
- 該当日のヘッダ, もしくは画像サムネイル枠に虹色枠を適用する.

### 8.3 画像リアクション・コメント画面
- フォーム送信時に対象画像を保持するため, `filename` を hidden で送る.
  - `<input type="hidden" name="filename" th:value="${filename}">`

---

## 9. データモデル（要点）
- `Comment`
  - `id`, `filename`, `author`, `text`, `createdAt`
- `ImageLike`（集計）
  - `filename`, `heartCount`, `likeCount`, `laughCount` など
- `ImageReactionLog`（ログ）
  - `id`, `image_id` または `filename`, `user_name`, `reaction_type`, `created_at`
- `PendingUser`
  - 登録申請用. 承認後は実ユーザへ反映する.

---

## 10. テスト方針（DoD）
### 10.1 基本 DoD
- `http://localhost/calendar` にアクセスできる.
- 画像をアップロードできる.
- 日別ページに画像リンクが表示される.
- 日別ページの画像リンクを押すと, その画像のリアクションとコメントができるページに遷移できる.
- リアクションを押すと集計値が更新され, ログが残る.
- コメントを投稿すると一覧に表示される.

### 10.2 新機能 DoD（虹色ハイライト）
- 同一画像に対して, 過去24時間以内に2ユーザ以上がリアクションした場合, その画像の `scheduled_time` に対応する日付が虹色表示になる.

### 10.3 テスト追加案
- 画像アップロードの保存確認（ファイル存在 + DB レコード）.
- 24時間・distinct user 判定ロジックの単体テスト（境界: 24時間ちょうど, 1人, 2人, 同一ユーザ連打）.

---

## 11. 開発運用ルール
- 仕様書更新: `docs/specs.md`（本ファイルを置く場所はリポジトリ規約に従う）
- タスク計画: `docs/tasks.md`
- 完了報告: `docs/reports/done/done_YYYY-MM-DD_実装内容.md`

---

## 12. 参照（代表）
- `schedule/src/main/resources/application.properties`
- `schedule/src/main/resources/schema.sql`
- `schedule/src/main/resources/data.sql`
- `schedule/src/main/java/oit/is/team4/schedule/security/WebConfig.java`
- `schedule/src/main/java/oit/is/team4/schedule/repository/ImageReactionLogRepository.java`
- `schedule/src/main/java/oit/is/team4/schedule/mapper/ImageMapper.java`
