-- Module 5: Thêm rating fields vào bảng tai_san
ALTER TABLE tai_san ADD COLUMN rating_average DOUBLE PRECISION DEFAULT 0.0;
ALTER TABLE tai_san ADD COLUMN review_count INTEGER DEFAULT 0;
