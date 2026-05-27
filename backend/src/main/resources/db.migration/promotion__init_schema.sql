-- =========================================================================
-- 1. TẠO CÁC KIỂU ENUM
-- =========================================================================
CREATE TYPE created_by_role AS ENUM ('DOI_TAC', 'QUAN_TRI_VIEN');
CREATE TYPE loai_giam_gia AS ENUM ('PHAN_TRAM', 'SO_TIEN_CO_DINH');
CREATE TYPE trang_thai_uu_dai AS ENUM ('DA_LEN_LICH', 'DANG_CO_HIEU_LUC', 'TAM_DUNG', 'DA_HET_HAN', 'DA_XOA');
CREATE TYPE pham_vi_ap_dung AS ENUM ('TOAN_SAN', 'DOI_TAC', 'CO_SO_CU_THE', 'DICH_VU_CU_THE');
CREATE TYPE target_type AS ENUM ('HOTEL', 'ROOM', 'RESTAURANT', 'MENU_ITEM', 'ALL_PLATFORM');
CREATE TYPE trang_thai_customer_voucher AS ENUM ('CHUA_DUNG', 'DA_DUNG', 'HET_HAN', 'BI_THU_HOI');
CREATE TYPE loai_giao_dich_diem AS ENUM ('TICH_DIEM', 'DOI_VOUCHER', 'HOAN_DIEM', 'DIEU_CHINH_ADMIN');

-- =========================================================================
-- 2. TẠO BẢNG CẤU HÌNH LOYALTY (LoyaltyRule)
-- =========================================================================
CREATE TABLE loyalty_rule (
                              id BIGSERIAL PRIMARY KEY,
                              money_per_point NUMERIC(12,2) NOT NULL,
                              silver_threshold NUMERIC(12,2) NOT NULL,
                              gold_threshold NUMERIC(12,2) NOT NULL,
                              diamond_threshold NUMERIC(12,2) NOT NULL,
                              is_active BOOLEAN DEFAULT TRUE,
                              created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);



-- =========================================================================
-- 4. TẠO BẢNG CHA: UU_DAI (Lưu thông tin chung cho mọi loại ưu đãi)
-- =========================================================================
CREATE TABLE uu_dai (
                        id BIGSERIAL PRIMARY KEY,
                        ten_uu_dai VARCHAR(255) NOT NULL,
                        mo_ta TEXT,
                        created_by_user_id VARCHAR(255) NOT NULL,
                        created_by_role created_by_role NOT NULL,
                        business_profile_id VARCHAR(255), -- Null nếu là ưu đãi toàn sàn do Admin tạo
                        muc_giam NUMERIC(12,2) NOT NULL,
                        loai_giam_gia loai_giam_gia NOT NULL,
                        gia_tri_giam_toi_da NUMERIC(12,2),
                        ngay_bat_dau TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                        ngay_ket_thuc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                        trang_thai_uu_dai trang_thai_uu_dai DEFAULT 'DA_LEN_LICH',
                        created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        deleted BOOLEAN DEFAULT FALSE,
                        version BIGINT NOT NULL DEFAULT 0,

    -- Ràng buộc: Ngày kết thúc phải lớn hơn ngày bắt đầu, mức giảm phải lớn hơn 0
                        CONSTRAINT chk_ngay_hop_le CHECK (ngay_ket_thuc > ngay_bat_dau),
                        CONSTRAINT chk_muc_giam_hop_le CHECK (muc_giam > 0)
);

-- =========================================================================
-- 5. TẠO BẢNG CON 1: VOUCHER (Kế thừa JOINED từ uu_dai)
-- =========================================================================
CREATE TABLE voucher (
                         uu_dai_id BIGINT PRIMARY KEY REFERENCES uu_dai(id) ON DELETE CASCADE, -- Khóa chính vừa là khóa ngoại trỏ sang bảng cha
                         ma_voucher VARCHAR(50) UNIQUE NOT NULL,
                         so_luong_phat_hanh INT NOT NULL,
                         so_luong_da_dung INT DEFAULT 0,
                         don_hang_toi_thieu NUMERIC(12,2) DEFAULT 0.00,
                         usage_limit_per_user INT DEFAULT 1,
                         diem_can_doi INT DEFAULT 0,
                         cho_phep_doi_bang_diem BOOLEAN DEFAULT FALSE,
                         pham_vi_ap_dung pham_vi_ap_dung NOT NULL,

    -- Ràng buộc: Số lượng đã dùng không được vượt quá số lượng phát hành
                         CONSTRAINT chk_so_luong_hop_le CHECK (so_luong_da_dung <= so_luong_phat_hanh)
);

-- =========================================================================
-- 6. TẠO BẢNG CON 2: KHUYEN_MAI_TRUC_TIEP (Kế thừa JOINED từ uu_dai)
-- =========================================================================
CREATE TABLE khuyen_mai_truc_tiep (
                                      uu_dai_id BIGINT PRIMARY KEY REFERENCES uu_dai(id) ON DELETE CASCADE, -- Khóa chính vừa là khóa ngoại trỏ sang bảng cha
                                      target_type target_type NOT NULL,
                                      target_id VARCHAR(255) NOT NULL -- ID linh hoạt lưu hotelId, roomId, v.v.
);

-- =========================================================================
-- 7. TẠO BẢNG VOUCHER CỦA KHÁCH: CUSTOMER_VOUCHER
-- =========================================================================
CREATE TABLE customer_voucher (
                                  id BIGSERIAL PRIMARY KEY,
                                  voucher_id BIGINT NOT NULL REFERENCES uu_dai(id) ON DELETE RESTRICT, -- Trỏ về ID ưu đãi (bảng voucher)
                                  khach_hang_id VARCHAR(255) NOT NULL REFERENCES khach_hang(id) ON DELETE RESTRICT,
                                  ma_voucher_ca_nhan VARCHAR(100),
                                  trang_thai trang_thai_customer_voucher DEFAULT 'CHUA_DUNG',
                                  issued_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  used_at TIMESTAMP WITHOUT TIME ZONE,
                                  expired_at TIMESTAMP WITHOUT TIME ZONE,
                                  booking_id BIGINT -- ID của đơn đặt (khi nào dùng thì update vào)
);

-- =========================================================================
-- 8. TẠO BẢNG NHẬT KÝ ĐIỂM: LICH_SU_DIEM
-- =========================================================================
CREATE TABLE lich_su_diem (
                              id BIGSERIAL PRIMARY KEY,
                              khach_hang_id VARCHAR(255) NOT NULL REFERENCES khach_hang(id) ON DELETE CASCADE,
                              so_diem_thay_doi INT NOT NULL,
                              loai_giao_dich_diem loai_giao_dich_diem NOT NULL,
                              diem_truoc_giao_dich INT NOT NULL,
                              diem_sau_giao_dich INT NOT NULL,
                              booking_id BIGINT,
                              voucher_id BIGINT,
                              ghi_chu TEXT,
                              created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
