# 就活管理ツール

企業への応募・選考進捗・メモをまとめて管理する Web アプリケーションです。

## 機能

- **企業マスタ** — 企業情報（業種・URL・メモ）の登録・編集・削除
- **応募管理** — 応募先企業・職種・ステータスの一覧管理
- **選考ステージ管理** — 面接・書類選考などの日程・結果を記録
- **メモ** — 応募ごとに自由なメモを追記・編集
- **リマインダー** — 選考日時前に通知（バックグラウンドで1分ごとに実行）
- **ダッシュボード** — 総応募数・今月の応募数・選考中件数・内定数を集計表示

## 技術スタック

| レイヤー | 技術 |
|---|---|
| バックエンド | Java 21 / Spring Boot 3.3.0 / Spring Data JPA / Hibernate |
| データベース | PostgreSQL |
| フロントエンド | React 18 / TypeScript / Vite / Tailwind CSS v3 |
| HTTP クライアント | Axios |
| データフェッチ | TanStack React Query v5 |
| ルーティング | React Router v6 |

## 必要な環境

- Java 21
- Node.js 18 以上
- PostgreSQL 15 以上

## セットアップ

### 1. データベース作成

PostgreSQL に接続して、データベースを作成します。

```sql
CREATE DATABASE job_hunting_db;
```

### 2. バックエンド設定

`backend/src/main/resources/application.yml` のデータベース接続情報を確認します。

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/job_hunting_db
    username: postgres
    password: postgres   # 実際のパスワードに変更
```

### 3. 依存ライブラリのダウンロード（初回のみ）

```bash
cd backend
./mvnw.cmd package -DskipTests   # Windows
./mvnw package -DskipTests       # Mac / Linux
```

### 4. フロントエンドの依存ライブラリインストール（初回のみ）

```bash
cd frontend
npm install
```

## 起動方法

バックエンドとフロントエンドをそれぞれ別のターミナルで起動します。

### バックエンド（ターミナル 1）

```bash
cd backend
java -jar target/job-hunting-manager-0.0.1-SNAPSHOT.jar
```

起動後 → `http://localhost:8080`

> **注意（Windows）**: パスに日本語が含まれる場合、`mvnw.cmd spring-boot:run` ではなく `java -jar` で起動してください。

### フロントエンド（ターミナル 2）

```bash
cd frontend
npm run dev
```

起動後 → `http://localhost:3000` をブラウザで開く

## プロジェクト構成

```
job-hunting-manager/
├── backend/
│   ├── src/main/java/com/example/jobhunting/
│   │   ├── entity/          # JPA エンティティ
│   │   ├── repository/      # Spring Data JPA リポジトリ
│   │   ├── service/         # ビジネスロジック
│   │   ├── controller/      # REST API コントローラー
│   │   ├── dto/             # リクエスト / レスポンス DTO
│   │   ├── exception/       # 例外ハンドリング
│   │   └── config/          # CORS 設定
│   └── src/main/resources/
│       └── application.yml
└── frontend/
    └── src/
        ├── api/             # Axios API クライアント
        ├── components/      # 共通コンポーネント
        ├── pages/           # 各ページ
        └── types/           # TypeScript 型定義
```

## API エンドポイント

| メソッド | パス | 説明 |
|---|---|---|
| GET | `/api/dashboard` | ダッシュボード集計 |
| GET/POST | `/api/companies` | 企業一覧・登録 |
| GET/PUT/DELETE | `/api/companies/{id}` | 企業詳細・更新・削除 |
| GET/POST | `/api/applications` | 応募一覧・登録 |
| GET/PUT/DELETE | `/api/applications/{id}` | 応募詳細・更新・削除 |
| PATCH | `/api/applications/{id}/status` | ステータス更新 |
| GET/POST | `/api/applications/{id}/stages` | 選考ステージ一覧・登録 |
| PATCH | `/api/stages/{id}/result` | 選考結果更新 |
| GET/POST | `/api/applications/{id}/memos` | メモ一覧・登録 |
| PUT/DELETE | `/api/memos/{id}` | メモ更新・削除 |
