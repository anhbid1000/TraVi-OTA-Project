- =============================================
-- V1__init_schema.sql
-- Script khởi tạo database cho TraVi-OTA
-- =============================================

-- =============================================
-- 1. TABLE: ROLES (VaiTrò)
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
-- 2. TABLE: USERS
-- =============================================
CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       mat_khau VARCHAR(255) NOT NULL, -- Sẽ lưu mã băm BCrypt
                       ho_ten VARCHAR(150),
                       ngay_sinh DATE,
                       gioi_tinh VARCHAR(20), -- Lưu Enum: NAM, NU, KHAC
                       so_dien_thoai VARCHAR(20),
                       trang_thai VARCHAR(50) DEFAULT 'CHUA_XAC_THUC', -- Lưu Enum
                       so_lan_dang_nhap_sai INT DEFAULT 0,
                       lan_cuoi_dang_nhap TIMESTAMP,
                       ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       vai_tro_id VARCHAR(36) REFERENCES vai_tro(id) ON DELETE SET NULL
);

-- =============================================
-- 3. TABLE: Khách hàng (kế thừa users)
-- =============================================
CREATE TABLE khach_hang (
                            id VARCHAR(36) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                            diem_thanh_vien INT DEFAULT 0,
                            hang_thanh_vien VARCHAR(50),
                            tong_chi_tieu DOUBLE PRECISION DEFAULT 0.0
);

CREATE TABLE khach_hang_tu_khoa (
                                    khach_hang_id VARCHAR(36) REFERENCES khach_hang(id) ON DELETE CASCADE,
                                    tu_khoa VARCHAR(255)
);

-- Bảng phụ cho thuộc tính List<String> tuKhoaGanDay của Khách Hàng (@ElementCollection)



-- =============================================
-- 4. TABLE: Đối tác
-- =============================================
CREATE TABLE doi_tac (
                         id VARCHAR(36) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                         ti_le_chiet_khau REAL DEFAULT 0.0
);

-- =============================================
-- 5. TABLE: Đối tác
-- =============================================
CREATE TABLE quan_tri_vien (
                               id VARCHAR(36) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- INDEXES (Tăng tốc độ tìm kiếm)
-- =============================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);