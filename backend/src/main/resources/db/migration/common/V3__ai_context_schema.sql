-- Schema for AI_Context classes in the class diagram: HoSoAI and ThongBaoNguCanh.

CREATE TABLE IF NOT EXISTS ho_so_ai (
    id_ho_so_ai VARCHAR(36) PRIMARY KEY,
    khach_hang_id VARCHAR(36) NOT NULL UNIQUE,
    so_thich_tong_hop TEXT,
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'KHOI_DONG_NGUOI_DUNG',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ho_so_ai_khach_hang') THEN
        ALTER TABLE ho_so_ai
            ADD CONSTRAINT fk_ho_so_ai_khach_hang
            FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS ho_so_ai_lich_su_click (
    ho_so_ai_id VARCHAR(36) NOT NULL,
    id_tai_san VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ho_so_ai_tu_khoa_tim_kiem (
    ho_so_ai_id VARCHAR(36) NOT NULL,
    tu_khoa VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ho_so_ai_lich_su_tim_kiem (
    id VARCHAR(36) PRIMARY KEY,
    ho_so_ai_id VARCHAR(36) NOT NULL,
    tu_khoa VARCHAR(255),
    thanh_pho VARCHAR(255),
    loai_dich_vu VARCHAR(50),
    nguon VARCHAR(100),
    searched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ho_so_ai_hanh_vi (
    id VARCHAR(36) PRIMARY KEY,
    ho_so_ai_id VARCHAR(36) NOT NULL,
    loai_su_kien VARCHAR(50) NOT NULL,
    loai_tai_san VARCHAR(50),
    id_tai_san VARCHAR(255),
    diem_phan_hoi INTEGER,
    ghi_chu VARCHAR(1000),
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ho_so_ai_lich_su_click') THEN
        ALTER TABLE ho_so_ai_lich_su_click
            ADD CONSTRAINT fk_ho_so_ai_lich_su_click
            FOREIGN KEY (ho_so_ai_id) REFERENCES ho_so_ai(id_ho_so_ai) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ho_so_ai_tu_khoa_tim_kiem') THEN
        ALTER TABLE ho_so_ai_tu_khoa_tim_kiem
            ADD CONSTRAINT fk_ho_so_ai_tu_khoa_tim_kiem
            FOREIGN KEY (ho_so_ai_id) REFERENCES ho_so_ai(id_ho_so_ai) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ho_so_ai_lich_su_tim_kiem') THEN
        ALTER TABLE ho_so_ai_lich_su_tim_kiem
            ADD CONSTRAINT fk_ho_so_ai_lich_su_tim_kiem
            FOREIGN KEY (ho_so_ai_id) REFERENCES ho_so_ai(id_ho_so_ai) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ho_so_ai_hanh_vi') THEN
        ALTER TABLE ho_so_ai_hanh_vi
            ADD CONSTRAINT fk_ho_so_ai_hanh_vi
            FOREIGN KEY (ho_so_ai_id) REFERENCES ho_so_ai(id_ho_so_ai) ON DELETE CASCADE;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS thong_bao_ngu_canh (
    id_thong_bao VARCHAR(36) PRIMARY KEY,
    loai_ngu_canh VARCHAR(50) NOT NULL,
    noi_dung VARCHAR(1000) NOT NULL,
    muc_do VARCHAR(50) NOT NULL,
    thoi_gian_hieu_luc TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ho_so_ai_khach_hang ON ho_so_ai(khach_hang_id);
CREATE INDEX IF NOT EXISTS idx_ho_so_ai_search_time ON ho_so_ai_lich_su_tim_kiem(ho_so_ai_id, searched_at);
CREATE INDEX IF NOT EXISTS idx_ho_so_ai_event_type ON ho_so_ai_hanh_vi(ho_so_ai_id, loai_su_kien);
CREATE INDEX IF NOT EXISTS idx_thong_bao_ngu_canh_time ON thong_bao_ngu_canh(thoi_gian_hieu_luc);
