-- =============================================
-- V1__init_schema.sql
-- Script khởi tạo database cho TraVi-OTA
-- =============================================
-- =============================================
-- 1. TABLE: ROLES (Vai Trò)
-- =============================================
CREATE TABLE vai_tro (
                         id VARCHAR(36) PRIMARY KEY,
                         ten VARCHAR(50) NOT NULL UNIQUE,
                         mo_ta TEXT,
                         ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert các role mặc định
INSERT INTO vai_tro (id, ten, mo_ta) VALUES
                                         ('ROLE-GUEST', 'KHACH_HANG', 'Người dùng tìm kiếm và đặt dịch vụ'),
                                         ('ROLE-PARTNER', 'DOI_TAC', 'Chủ khách sạn/nhà hàng đăng bán dịch vụ'),
                                         ('ROLE-ADMIN', 'QUAN_TRI_VIEN', 'Quản trị viên hệ thống OTA');

-- =============================================
-- 2. TABLE: DANH MỤC & SỞ THÍCH (Mới thêm theo UML)
-- =============================================
CREATE TABLE danh_muc_so_thich (
                                   id VARCHAR(36) PRIMARY KEY,
                                   ten_danh_muc VARCHAR(100) NOT NULL,
                                   mo_ta TEXT
);

CREATE TABLE so_thich (
                          id VARCHAR(36) PRIMARY KEY,
                          danh_muc_id VARCHAR(36) REFERENCES danh_muc_so_thich(id) ON DELETE CASCADE,
                          ten_so_thich VARCHAR(100) NOT NULL
);

-- =============================================
-- 3. TABLE: USERS (Lớp trừu tượng cha)
-- =============================================
CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       username Varchar(100) NOT NULL UNIQUE,
                       mat_khau VARCHAR(255) NOT NULL, -- Lưu mã băm BCrypt
                       ho_ten VARCHAR(150) NOT NULL,
                       ngay_sinh DATE,
                       gioi_tinh VARCHAR(20), -- Enum: NAM, NU, KHAC
                       so_dien_thoai VARCHAR(20),
                       trang_thai VARCHAR(50) DEFAULT 'CHUA_XAC_THUC', -- Enum: CHUA_XAC_THUC, HOAT_DONG, BI_KHOA...
                       so_lan_dang_nhap_sai INT DEFAULT 0,
                       lan_cuoi_dang_nhap TIMESTAMP,
                       ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       vai_tro_id VARCHAR(36) REFERENCES vai_tro(id) ON DELETE SET NULL
);

-- =============================================
-- 4. TABLE: KHÁCH HÀNG (Kế thừa Users)
-- =============================================
CREATE TABLE khach_hang (
                            id VARCHAR(36) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                            diem_thanh_vien INT DEFAULT 0,
                            hang_thanh_vien VARCHAR(50) DEFAULT 'Dong', -- Enum: Dong, Bac, Vang, Kim Cuong
                            tong_chi_tieu DOUBLE PRECISION DEFAULT 0.0
);

-- Bảng phụ lưu danh sách từ khóa gần đây (@ElementCollection)
CREATE TABLE khach_hang_tu_khoa (
                                    khach_hang_id VARCHAR(36) REFERENCES khach_hang(id) ON DELETE CASCADE,
                                    tu_khoa VARCHAR(255)
);

-- Bảng trung gian cho quan hệ Many-to-Many giữa Khách hàng và Sở thích
CREATE TABLE khach_hang_so_thich (
                                     khach_hang_id VARCHAR(36) REFERENCES khach_hang(id) ON DELETE CASCADE,
                                     so_thich_id VARCHAR(36) REFERENCES so_thich(id) ON DELETE CASCADE,
                                     PRIMARY KEY (khach_hang_id, so_thich_id)
);

-- =============================================
-- 5. TABLE: ĐỐI TÁC (Abstract - Kế thừa Users)
-- =============================================
CREATE TABLE doi_tac (
                         id VARCHAR(36) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                         ti_le_chiet_khau REAL DEFAULT 0.0
);


-- =============================================
-- 6. TABLE: QUẢN TRỊ VIÊN & LỊCH SỬ THAO TÁC (Kế thừa Users)
-- =============================================
CREATE TABLE quan_tri_vien (
                               id VARCHAR(36) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                               cap_do_quyen INT DEFAULT 1
);

CREATE TABLE lich_su_thao_tac (
                                  id_log VARCHAR(36) PRIMARY KEY,
                                  id_admin VARCHAR(36) REFERENCES quan_tri_vien(id) ON DELETE CASCADE,
                                  hanh_dong TEXT NOT NULL,
                                  thoi_gian TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed dữ liệu quản trị viên mặc định
INSERT INTO users (id, email, username, mat_khau, ho_ten, trang_thai, vai_tro_id) VALUES
                                                                                      ('ADMIN-001', 'admin1@travi.vn', 'admin.root', '$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK', 'Nguyen Quan Tri 1', 'HOAT_DONG', 'ROLE-ADMIN'),
                                                                                      ('ADMIN-002', 'admin2@travi.vn', 'admin.ops', '$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK', 'Tran Quan Tri 2', 'HOAT_DONG', 'ROLE-ADMIN'),
                                                                                      ('ADMIN-003', 'admin3@travi.vn', 'admin.audit', '$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK', 'Le Quan Tri 3', 'HOAT_DONG', 'ROLE-ADMIN');

INSERT INTO quan_tri_vien (id, cap_do_quyen) VALUES
                                                 ('ADMIN-001', 3),
                                                 ('ADMIN-002', 2),
                                                 ('ADMIN-003', 1);


-- =============================================
-- INDEXES (Tăng tốc độ tìm kiếm)
-- =============================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);