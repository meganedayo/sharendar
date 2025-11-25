/* userやgroupといった名前はSQLでは予約語で使えないため，userNameとしていることに注意 */
CREATE TABLE member (
    id IDENTITY,
    userName VARCHAR NOT NULL,
    email VARCHAR NOT NULL
);

CREATE TABLE schedule (
    id IDENTITY PRIMARY KEY,
    plan_date DATE NOT NULL,
    start_time TIME NOT NULL,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE image(
  id IDENTITY PRIMARY KEY,
  file_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(255) NOT NULL
);
