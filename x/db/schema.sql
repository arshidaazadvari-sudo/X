
-- reset
DROP TABLE IF EXISTS tweet_hashtags CASCADE;
DROP TABLE IF EXISTS hashtags CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS retweets CASCADE;
DROP TABLE IF EXISTS replies CASCADE;
DROP TABLE IF EXISTS tweets CASCADE;
DROP TABLE IF EXISTS follows CASCADE;
DROP TABLE IF EXISTS sessions CASCADE;
DROP TABLE IF EXISTS users CASCADE;

--user table
CREATE TABLE users(
                      id SERIAL PRIMARY KEY ,
                      username VARCHAR(50) UNIQUE NOT NULL ,
                      email VARCHAR(100) UNIQUE NOT NULL ,
                      password_hash VARCHAR(255) NOT NULL ,
                      display_name VARCHAR(100),
                      bio TEXT,
                      profile_pic VARCHAR(255),
                      banner_pic VARCHAR(255),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      is_verified BOOLEAN DEFAULT FALSE ,
                      is_active BOOLEAN DEFAULT TRUE
);

--session table
CREATE TABLE sessions(
                         id SERIAL PRIMARY KEY ,
                         user_id INTEGER REFERENCES users(id) ON DELETE CASCADE ,
                         token VARCHAR(255) UNIQUE NOT NULL ,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         expires_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '7 days')
);

--tweet table
CREATE TABLE tweets(
                       id SERIAL PRIMARY KEY ,
                       user_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
                       content TEXT NOT NULL,
                       media_urls TEXT[],
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP,
                       is_deleted BOOLEAN   DEFAULT FALSE,
                       reply_to_tweet_id INTEGER REFERENCES tweets (id) ON DELETE CASCADE,
                       retweet_of_tweet_id INTEGER REFERENCES tweets(id) ON DELETE CASCADE
);

--follows table
CREATE TABLE follows(
                        follower_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
                        followee_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
                        created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (follower_id, followee_id)
);

--likes table
CREATE TABLE likes(
                      user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                      tweet_id INTEGER REFERENCES tweets(id) ON DELETE CASCADE,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY (user_id, tweet_id)
);

--replies table
CREATE TABLE replies(
                        id SERIAL PRIMARY KEY,
                        user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                        tweet_id INTEGER REFERENCES tweets(id) ON DELETE CASCADE,
                        content TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--hashtags table
CREATE TABLE hashtags (
                          id SERIAL PRIMARY KEY,
                          tag VARCHAR(100) UNIQUE NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--tweet_hashtag table
CREATE TABLE tweet_hashtags (
                                tweet_id INTEGER REFERENCES tweets(id) ON DELETE CASCADE,
                                hashtag_id INTEGER REFERENCES hashtags(id) ON DELETE CASCADE,
                                PRIMARY KEY (tweet_id, hashtag_id)
);


--media_table
CREATE TABLE media(
                    id SERIAL PRIMARY KEY,
                    tweet_id INTEGER REFERENCES tweets(id) ON DELETE CASCADE ,
                    file_path VARCHAR(255) NOT NULL ,
                    file_type VARCHAR(50) ,
                    file_size BIGINT,
                    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--index
CREATE INDEX idx_tweets_user_id ON tweets(user_id);
CREATE INDEX idx_tweets_created_at ON tweets(created_at DESC);
CREATE INDEX idx_follows_follower_id ON follows(follower_id);
CREATE INDEX idx_follows_followee_id ON follows(followee_id);
CREATE INDEX idx_likes_tweet_id ON likes(tweet_id);
CREATE INDEX idx_likes_user_id ON likes(user_id);
CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_sessions_token ON sessions(token);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

--fake data

-- kimia follows ali and sara
INSERT INTO follows (follower_id, followee_id)
SELECT u1.id, u2.id FROM users u1, users u2
WHERE u1.username = 'kimia' AND u2.username IN ('ali', 'sara');

-- ali follows kimia
INSERT INTO follows (follower_id, followee_id)
SELECT u1.id, u2.id FROM users u1, users u2
WHERE u1.username = 'ali' AND u2.username = 'kimia';

-- Make sure the users exist first (from previous step)

-- Normal tweets
INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'Hello everyone! This is my first tweet. #hello #firstTweet', false
FROM users WHERE username = 'kimia';

INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'Working on my Advanced Programming final project. Java + JavaFX + PostgreSQL 💻', false
FROM users WHERE username = 'kimia';

INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'Just finished the authentication part. Feeling good!', false
FROM users WHERE username = 'ali';

INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'Does anyone know a good resource for learning JavaFX?', false
FROM users WHERE username = 'ali';

INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'UI design is harder than I thought 😅 #JavaFX #design', false
FROM users WHERE username = 'sara';

INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'Backend is done, now fighting with the client side...', false
FROM users WHERE username = 'reza';

INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'Welcome to our Twitter clone project! Feel free to test everything.', false
FROM users WHERE username = 'admin';

INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'This is just a test user. Ignore me.', false
FROM users WHERE username = 'testuser';

INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'Coffee + coding = perfect morning ☕️', false
FROM users WHERE username = 'kimia';

INSERT INTO tweets (user_id, content, is_deleted)
SELECT id, 'Who else is still awake working on the project at 3 AM?', false
FROM users WHERE username = 'sara';

-- Reply example (sara replies to ali's tweet)
INSERT INTO tweets (user_id, content, reply_to_tweet_id, is_deleted)
SELECT u.id, 'I recommend the official OpenJFX documentation!', t.id, false
FROM users u, tweets t
WHERE u.username = 'sara'
  AND t.content LIKE 'Does anyone know a good resource%'
  AND t.user_id = (SELECT id FROM users WHERE username = 'ali');

-- kimia retweets admin's welcome tweet
INSERT INTO tweets (user_id, content, retweet_of_tweet_id, is_deleted)
SELECT u.id, '', t.id, false
FROM users u, tweets t
WHERE u.username = 'kimia'
  AND t.content LIKE 'Welcome to our Twitter clone%'
  AND t.user_id = (SELECT id FROM users WHERE username = 'admin');