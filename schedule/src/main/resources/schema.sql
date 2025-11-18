/* userやgroupといった名前はSQLでは予約語で使えないため，userNameとしていることに注意 */
CREATE TABLE member (
    id IDENTITY,
    userName VARCHAR NOT NULL,
    email VARCHAR NOT NULL
);
