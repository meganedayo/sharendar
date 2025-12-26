/* userやgroupといった名前はSQLでは予約語で使えないため，userNameとしていることに注意 */
CREATE TABLE member (
    id IDENTITY,
    userName VARCHAR NOT NULL
);

CREATE TABLE schedule (
    id IDENTITY PRIMARY KEY,
    plan_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    title VARCHAR(255) NOT NULL,
    user_name VARCHAR(100) -- 【追加】投稿者
);

CREATE TABLE image(
  id IDENTITY PRIMARY KEY,
  image_name VARCHAR(255) NOT NULL,
  scheduled_time DATETIME,
  user_name VARCHAR(100)
);

CREATE TABLE reaction (
  id IDENTITY PRIMARY KEY,
  schedule_id INT NOT NULL,
  user_name VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (schedule_id) REFERENCES schedule(id)
);

CREATE TABLE reactionlog (
  id IDENTITY PRIMARY KEY,
  user_name VARCHAR(100) NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  reaction_type VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
