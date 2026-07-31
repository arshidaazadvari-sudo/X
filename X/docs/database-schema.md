# 📊 Database Documentation – XClone

## 🧾 Overview

The database for this project is designed using **PostgreSQL**.  
It consists of 8 main tables that manage users, tweets, social interactions, hashtags, and media.

>**Note:** Replies are stored in the 'tweet' table using the 'reply_to_tweet_id' field.
> The separate 'replies' table has been removed to simplify the architecture.

---

## 📋 Table List

| # | Table | Description |
|---|-------|-------------|
| 1 | `users` | User account information |
| 2 | `sessions` | Login session management |
| 3 | `tweets` | Tweets, replies, and retweets |
| 4 | `follows` | Follower/following relationships |
| 5 | `likes` | User likes on tweets |
| 6 | `hashtags` | Hashtags used in tweets |
| 7 | `tweet_hashtags` | Many-to-many relationship between tweets and hashtags |
| 8 | `media` | Multimedia files (images, videos) |

---

## 🧩 Table Details

### 1. `users`

**Purpose:** Store user account information

| Column | Type | Description |
|--------|------|-------------|
| `id` | SERIAL PRIMARY KEY | Unique user ID |
| `username` | VARCHAR(50) UNIQUE NOT NULL | Username |
| `email` | VARCHAR(100) UNIQUE NOT NULL | Email address |
| `password_hash` | VARCHAR(255) NOT NULL | BCrypt hashed password |
| `display_name` | VARCHAR(100) | Display name |
| `bio` | TEXT | User biography |
| `profile_pic` | VARCHAR(255) | Profile picture path |
| `banner_pic` | VARCHAR(255) | Banner image path |
| `created_at` | TIMESTAMP DEFAULT NOW() | Registration timestamp |
| `is_verified` | BOOLEAN DEFAULT FALSE | Verification badge |
| `is_active` | BOOLEAN DEFAULT TRUE | Account active status |

---

### 2. `sessions`

**Purpose:** Manage user login sessions

| Column | Type | Description |
|--------|------|-------------|
| `id` | SERIAL PRIMARY KEY | Session ID |
| `user_id` | INTEGER REFERENCES users(id) | Associated user |
| `token` | VARCHAR(255) UNIQUE NOT NULL | Unique session token |
| `created_at` | TIMESTAMP DEFAULT NOW() | Creation timestamp |
| `expires_at` | TIMESTAMP | Expiration time (default 7 days) |

---

### 3. `tweets`

**Purpose:** Store tweets, replies, and retweets

| Column | Type | Description |
|--------|------|-------------|
| `id` | SERIAL PRIMARY KEY | Tweet ID |
| `user_id` | INTEGER REFERENCES users(id) | Author ID |
| `content` | TEXT NOT NULL | Tweet content |
| `media_urls` | TEXT[] | Array of image URLs |
| `created_at` | TIMESTAMP DEFAULT NOW() | Creation timestamp |
| `updated_at` | TIMESTAMP | Last edit timestamp |
| `is_deleted` | BOOLEAN DEFAULT FALSE | Soft delete flag |
| `reply_to_tweet_id` | INTEGER REFERENCES tweets(id) | Parent tweet ID (if reply) |
| `retweet_of_tweet_id` | INTEGER REFERENCES tweets(id) | Original tweet ID (if retweet) |

---

### 4. `follows`

**Purpose:** Manage follower/following relationships

| Column | Type | Description |
|--------|------|-------------|
| `follower_id` | INTEGER REFERENCES users(id) | User who follows |
| `followee_id` | INTEGER REFERENCES users(id) | User being followed |
| `created_at` | TIMESTAMP DEFAULT NOW() | Follow timestamp |

**Primary Key:** (`follower_id`, `followee_id`)

---

### 5. `likes`

**Purpose:** Store user likes on tweets

| Column | Type | Description |
|--------|------|-------------|
| `user_id` | INTEGER REFERENCES users(id) | User who liked |
| `tweet_id` | INTEGER REFERENCES tweets(id) | Liked tweet |
| `created_at` | TIMESTAMP DEFAULT NOW() | Like timestamp |

**Primary Key:** (`user_id`, `tweet_id`)

---

### 7. `hashtags`

**Purpose:** Store hashtags used in tweets

| Column | Type | Description |
|--------|------|-------------|
| `id` | SERIAL PRIMARY KEY | Hashtag ID |
| `tag` | VARCHAR(100) UNIQUE NOT NULL | Hashtag text (without #) |
| `created_at` | TIMESTAMP DEFAULT NOW() | First use timestamp |

---

### 8. `tweet_hashtags`

**Purpose:** Many-to-many relationship between tweets and hashtags

| Column | Type | Description |
|--------|------|-------------|
| `tweet_id` | INTEGER REFERENCES tweets(id) | Tweet ID |
| `hashtag_id` | INTEGER REFERENCES hashtags(id) | Hashtag ID |

**Primary Key:** (`tweet_id`, `hashtag_id`)

---

### 9. `media`

**Purpose:** Store multimedia files (images, videos, GIFs)

| Column | Type | Description |
|--------|------|-------------|
| `id` | SERIAL PRIMARY KEY | Media ID |
| `tweet_id` | INTEGER REFERENCES tweets(id) | Associated tweet |
| `file_path` | VARCHAR(255) NOT NULL | File path on server |
| `file_type` | VARCHAR(50) | File type (image, video, gif) |
| `file_size` | BIGINT | File size in KB |
| `uploaded_at` | TIMESTAMP DEFAULT NOW() | Upload timestamp |

---

## 🔗 Entity Relationship Diagram (ERD)
```

users (1) ----< (M) tweets
│                  │
│                  │
└----< (M) follows
│                  │
│                  │
└----< (M) likes ──┘
│                  │
│                  │
└----< (M) sessions
│
└----< (M) tweet_hashtags >---- (1) hashtags
│
└----< (M) media

```

---

## 🧪 Example Queries

### 1. Get user feed
```sql
SELECT t.*, u.username, u.display_name
FROM tweets t
JOIN users u ON t.user_id = u.id
WHERE t.user_id = ? 
   OR t.user_id IN (SELECT followee_id FROM follows WHERE follower_id = ?)
ORDER BY t.created_at DESC
LIMIT 50;
```

2. Get like count for a tweet

```sql
SELECT COUNT(*) FROM likes WHERE tweet_id = ?;
```

3. Get followers of a user

```sql
SELECT u.* FROM users u
JOIN follows f ON u.id = f.follower_id
WHERE f.followee_id = ?;
```

4. Search tweets by hashtag

```sql
SELECT t.* FROM tweets t
JOIN tweet_hashtags th ON t.id = th.tweet_id
JOIN hashtags h ON th.hashtag_id = h.id
WHERE h.tag = ?;
```

---

🛠️ Database Setup

To run the database script:

```bash
psql -U ap_user -d x_clone -f docs/database-schema.sql
```

Or using pgAdmin:
Query Tool → Open file → Execute

---

Author: Zahra 

```