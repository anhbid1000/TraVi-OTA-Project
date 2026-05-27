-- Add version cho user va ưu đãi tran race condition

ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS uu_dai
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
