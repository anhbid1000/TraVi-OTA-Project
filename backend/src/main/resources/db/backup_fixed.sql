--
-- PostgreSQL database dump
--

\restrict 9uFdfudB46nfoXism21HHfrS0wAqTAZtDIBXf0l0bqhK05RjtscKX9VoCTS5EsA

-- Dumped from database version 16.13
-- Dumped by pg_dump version 16.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: anh_khach_san; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.anh_khach_san (
    id character varying(255) NOT NULL,
    duong_dan_url character varying(255) NOT NULL,
    la_anh_dai_dien boolean,
    mo_ta_anh character varying(255),
    ngay_tai_len date,
    khach_san_id character varying(255) NOT NULL
);


ALTER TABLE public.anh_khach_san OWNER TO postgres;

--
-- Name: anh_nha_hang; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.anh_nha_hang (
    id character varying(255) NOT NULL,
    duong_dan_url character varying(255) NOT NULL,
    la_anh_dai_dien boolean,
    mo_ta_anh character varying(255),
    ngay_tai_len date,
    nha_hang_id character varying(255) NOT NULL
);


ALTER TABLE public.anh_nha_hang OWNER TO postgres;

--
-- Name: anh_phong; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.anh_phong (
    id character varying(255) NOT NULL,
    duong_dan_url character varying(255) NOT NULL,
    la_anh_dai_dien boolean,
    mo_ta_anh character varying(255),
    ngay_tai_len date,
    phong_id character varying(255) NOT NULL
);


ALTER TABLE public.anh_phong OWNER TO postgres;

--
-- Name: ban; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ban (
    id character varying(255) NOT NULL,
    so_cho_ngoi integer,
    trang_thai integer,
    vi_tri_sanh character varying(255),
    nha_hang_id character varying(255) NOT NULL,
    ten_ban character varying(255),
    mo_ta character varying(1000),
    deleted boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE public.ban OWNER TO postgres;

--
-- Name: chinh_sach; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chinh_sach (
    id character varying(255) NOT NULL,
    loai_chinh_sach character varying(255) NOT NULL,
    ngay_ap_dung date,
    noi_dung text,
    ho_so_kinh_doanh_id character varying(255) NOT NULL,
    gio_nhan_phong time without time zone,
    gio_tra_phong time without time zone,
    gio_mo_cua time without time zone,
    gio_dong_cua time without time zone,
    chinh_sach_huy text,
    chinh_sach_hoan_tien text,
    quy_dinh_tre_em text,
    quy_dinh_vat_nuoi text,
    ghi_chu_khac text,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE public.chinh_sach OWNER TO postgres;

--
-- Name: combo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.combo (
    id character varying(255) NOT NULL,
    gia_combo real,
    mo_ta character varying(255),
    ngay_bat_dau date,
    ngay_ket_thuc date,
    ten_combo character varying(255),
    trang_thai integer,
    thuc_don_id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.combo OWNER TO postgres;

--
-- Name: combo_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.combo_item (
    id character varying(255) NOT NULL,
    so_luong integer,
    combo_id character varying(255) NOT NULL,
    mon_an_id character varying(255) NOT NULL
);


ALTER TABLE public.combo_item OWNER TO postgres;

--
-- Name: combo_mon_an; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.combo_mon_an (
    combo_id character varying(255) NOT NULL,
    mon_an_id character varying(255) NOT NULL
);


ALTER TABLE public.combo_mon_an OWNER TO postgres;

--
-- Name: danh_muc_so_thich; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.danh_muc_so_thich (
    id character varying(255) NOT NULL,
    mo_ta character varying(255),
    ten_danh_muc character varying(255)
);


ALTER TABLE public.danh_muc_so_thich OWNER TO postgres;

--
-- Name: doi_tac; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.doi_tac (
    ti_le_chiet_khau real,
    id character varying(255) NOT NULL
);


ALTER TABLE public.doi_tac OWNER TO postgres;

--
-- Name: don_dat_cho; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.don_dat_cho (
    id character varying(255) NOT NULL,
    ma_don character varying(50) NOT NULL,
    khach_hang_id character varying(255) NOT NULL,
    ho_so_kinh_doanh_id character varying(255) NOT NULL,
    ngay_tao timestamp without time zone NOT NULL,
    tong_tien_goc double precision,
    tien_khuyen_mai double precision,
    tong_tien_thanh_toan double precision,
    ten_nguoi_dat character varying(255),
    sdt_nguoi_dat character varying(255),
    email_nguoi_dat character varying(255),
    ghi_chu character varying(1000),
    trang_thai_don character varying(255) NOT NULL,
    hold_expired_at timestamp without time zone,
    payment_expired_at timestamp without time zone,
    cancelled_at timestamp without time zone,
    cancel_reason character varying(500),
    deleted boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE public.don_dat_cho OWNER TO postgres;

--
-- Name: don_khach_san; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.don_khach_san (
    id character varying(255) NOT NULL,
    ngay_check_in date,
    ngay_check_out date,
    so_dem integer,
    so_khach integer,
    gio_nhan_phong_du_kien time without time zone
);


ALTER TABLE public.don_khach_san OWNER TO postgres;

--
-- Name: don_khach_san_chi_tiet; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.don_khach_san_chi_tiet (
    id character varying(255) NOT NULL,
    don_khach_san_id character varying(255) NOT NULL,
    phong_id character varying(255) NOT NULL,
    ten_phong_tai_thoi_diem_dat character varying(255),
    so_luong integer,
    don_gia_tai_thoi_diem_dat double precision,
    so_dem integer,
    thanh_tien double precision
);


ALTER TABLE public.don_khach_san_chi_tiet OWNER TO postgres;

--
-- Name: don_nha_hang; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.don_nha_hang (
    id character varying(255) NOT NULL,
    ngay_gio_bat_dau timestamp without time zone,
    ngay_gio_ket_thuc timestamp without time zone,
    so_nguoi integer,
    tien_coc double precision,
    co_dat_mon_truoc boolean
);


ALTER TABLE public.don_nha_hang OWNER TO postgres;

--
-- Name: don_nha_hang_ban; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.don_nha_hang_ban (
    id character varying(255) NOT NULL,
    don_nha_hang_id character varying(255) NOT NULL,
    ban_id character varying(255) NOT NULL
);


ALTER TABLE public.don_nha_hang_ban OWNER TO postgres;

--
-- Name: ho_so_kinh_doanh; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ho_so_kinh_doanh (
    id_ho_so character varying(255) NOT NULL,
    giay_phep_kinh_doanh character varying(255) NOT NULL,
    loai_dich_vu character varying(255) NOT NULL,
    ma_so_thue character varying(50) NOT NULL,
    old_giay_phep_kinh_doanh character varying(255),
    old_loai_dich_vu character varying(255),
    old_ma_so_thue character varying(255),
    old_sdt_lien_he character varying(255),
    old_ten_co_so character varying(255),
    old_toa_dogps character varying(255),
    sdt_lien_he character varying(20) NOT NULL,
    ten_co_so character varying(255) NOT NULL,
    thoi_gian_dang_ky timestamp(6) without time zone,
    thoi_gian_duyet timestamp(6) without time zone,
    toa_do_gps character varying(255) NOT NULL,
    trang_thai_kiem_duyet character varying(255) NOT NULL,
    doi_tac_id character varying(255) NOT NULL,
    kinh_do double precision,
    vi_do double precision,
    email_lien_he character varying(255),
    dia_chi character varying(255),
    thanh_pho character varying(100),
    quan_huyen character varying(100),
    phuong_xa character varying(100),
    trang_thai_hoat_dong character varying(255) DEFAULT 'CHUA_HOAT_DONG'::character varying NOT NULL,
    ly_do_tu_choi_gan_nhat character varying(1000),
    deleted boolean DEFAULT false NOT NULL,
    thoi_gian_cap_nhat timestamp without time zone,
    CONSTRAINT ho_so_kinh_doanh_loai_dich_vu_check CHECK (((loai_dich_vu)::text = ANY ((ARRAY['KHACH_SAN'::character varying, 'NHA_HANG'::character varying])::text[]))),
    CONSTRAINT ho_so_kinh_doanh_trang_thai_kiem_duyet_check CHECK (((trang_thai_kiem_duyet)::text = ANY ((ARRAY['BAN_NHAP'::character varying, 'CHO_DUYET'::character varying, 'BI_TU_CHOI'::character varying, 'DA_DUYET'::character varying, 'DANG_HOAT_DONG'::character varying, 'BI_KHOA_TAM_THOI'::character varying])::text[])))
);


ALTER TABLE public.ho_so_kinh_doanh OWNER TO postgres;

--
-- Name: khach_hang; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.khach_hang (
    diem_thanh_vien integer,
    hang_thanh_vien character varying(255),
    tong_chi_tieu double precision,
    id character varying(255) NOT NULL,
    CONSTRAINT khach_hang_hang_thanh_vien_check CHECK (((hang_thanh_vien)::text = ANY ((ARRAY['DONG'::character varying, 'BAC'::character varying, 'VANG'::character varying, 'KIM_CUONG'::character varying])::text[])))
);


ALTER TABLE public.khach_hang OWNER TO postgres;

--
-- Name: khach_hang_so_thich; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.khach_hang_so_thich (
    khach_hang_id character varying(255) NOT NULL,
    so_thich_id character varying(255) NOT NULL
);


ALTER TABLE public.khach_hang_so_thich OWNER TO postgres;

--
-- Name: khach_hang_tu_khoa; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.khach_hang_tu_khoa (
    khach_hang_id character varying(255) NOT NULL,
    tu_khoa character varying(255)
);


ALTER TABLE public.khach_hang_tu_khoa OWNER TO postgres;

--
-- Name: khach_san; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.khach_san (
    gio_nhan_phong time(0) without time zone,
    gio_tra_phong time(0) without time zone,
    hang_sao integer,
    ten character varying(255) NOT NULL,
    id_tai_san character varying(255) NOT NULL,
    loai_khach_san character varying(255),
    gio_nhan_phong_mac_dinh time without time zone,
    gio_tra_phong_mac_dinh time without time zone,
    so_tang integer,
    tong_so_phong integer
);


ALTER TABLE public.khach_san OWNER TO postgres;

--
-- Name: khach_san_tien_ich; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.khach_san_tien_ich (
    khach_san_id character varying(255) NOT NULL,
    tien_ich_id character varying(255) NOT NULL
);


ALTER TABLE public.khach_san_tien_ich OWNER TO postgres;

--
-- Name: lich_su_kiem_duyet_ho_so; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lich_su_kiem_duyet_ho_so (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    ghi_chu_noi_bo character varying(1000),
    ly_do character varying(1000),
    trang_thai_cu character varying(255) NOT NULL,
    trang_thai_moi character varying(255) NOT NULL,
    admin_id character varying(255),
    ho_so_kinh_doanh_id character varying(255) NOT NULL,
    CONSTRAINT lich_su_kiem_duyet_ho_so_trang_thai_cu_check CHECK (((trang_thai_cu)::text = ANY ((ARRAY['CHO_DUYET'::character varying, 'DA_DUYET'::character varying, 'BI_TU_CHOI'::character varying, 'BAN_NHAP'::character varying, 'DANG_HOAT_DONG'::character varying, 'BI_KHOA_TAM_THOI'::character varying])::text[]))),
    CONSTRAINT lich_su_kiem_duyet_ho_so_trang_thai_moi_check CHECK (((trang_thai_moi)::text = ANY ((ARRAY['CHO_DUYET'::character varying, 'DA_DUYET'::character varying, 'BI_TU_CHOI'::character varying, 'BAN_NHAP'::character varying, 'DANG_HOAT_DONG'::character varying, 'BI_KHOA_TAM_THOI'::character varying])::text[])))
);


ALTER TABLE public.lich_su_kiem_duyet_ho_so OWNER TO postgres;

--
-- Name: lich_su_thao_tac; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lich_su_thao_tac (
    id_log character varying(255) NOT NULL,
    hanh_dong character varying(255),
    thoi_gian timestamp(6) without time zone,
    id_admin character varying(255)
);


ALTER TABLE public.lich_su_thao_tac OWNER TO postgres;

--
-- Name: mon_an; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mon_an (
    id character varying(255) NOT NULL,
    duong_dan_url character varying(255),
    gia_ban double precision,
    ten_mon character varying(255) NOT NULL,
    trang_thai character varying(255),
    thuc_don_id character varying(255) NOT NULL,
    mo_ta character varying(1000),
    danh_muc_mon character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    CONSTRAINT mon_an_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['DANG_BAN'::character varying, 'CO_SAN'::character varying, 'TAM_HET'::character varying, 'NGUNG_BAN'::character varying])::text[])))
);


ALTER TABLE public.mon_an OWNER TO postgres;

--
-- Name: mon_an_the_ngu_canh; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mon_an_the_ngu_canh (
    mon_an_id character varying(255) NOT NULL,
    the_ngu_canh character varying(255)
);


ALTER TABLE public.mon_an_the_ngu_canh OWNER TO postgres;

--
-- Name: nha_hang; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nha_hang (
    gio_dong_cua time(0) without time zone,
    gio_mo_cua time(0) without time zone,
    loai_am_thuc character varying(255),
    suc_chua integer,
    ten character varying(255) NOT NULL,
    id_tai_san character varying(255) NOT NULL,
    co_dat_ban_truoc boolean DEFAULT true NOT NULL,
    co_dat_mon_truoc boolean DEFAULT true NOT NULL
);


ALTER TABLE public.nha_hang OWNER TO postgres;

--
-- Name: phong; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.phong (
    id character varying(255) NOT NULL,
    dien_tich real,
    loai_phong character varying(255) NOT NULL,
    phan_tram_giam_gia real,
    so_phong character varying(255) NOT NULL,
    suc_chua_toi_da integer,
    trang_thai character varying(255),
    khach_san_id character varying(255) NOT NULL,
    ten_phong character varying(255),
    mo_ta character varying(1000),
    so_giuong integer,
    so_luong_phong integer,
    gia_co_ban double precision,
    deleted boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    CONSTRAINT phong_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['SAN_SANG'::character varying, 'DA_DAT_TRUOC'::character varying, 'DANG_SU_DUNG'::character varying, 'DANG_BAO_TRI'::character varying, 'DA_AN'::character varying])::text[])))
);


ALTER TABLE public.phong OWNER TO postgres;

--
-- Name: phong_tien_ich; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.phong_tien_ich (
    phong_id character varying(255) NOT NULL,
    tien_ich character varying(255),
    CONSTRAINT phong_tien_ich_tien_ich_check CHECK (((tien_ich)::text = ANY ((ARRAY['WIFI'::character varying, 'DIEU_HOA'::character varying, 'TV'::character varying, 'TU_LANH'::character varying, 'BAN_CONG'::character varying, 'BON_TAM'::character varying, 'VIEW_DEP'::character varying])::text[])))
);


ALTER TABLE public.phong_tien_ich OWNER TO postgres;

--
-- Name: quan_tri_vien; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.quan_tri_vien (
    cap_do_quyen integer,
    id character varying(255) NOT NULL
);


ALTER TABLE public.quan_tri_vien OWNER TO postgres;

--
-- Name: so_thich; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.so_thich (
    id character varying(255) NOT NULL,
    ten_so_thich character varying(255),
    danh_muc_id character varying(255)
);


ALTER TABLE public.so_thich OWNER TO postgres;

--
-- Name: tai_san; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tai_san (
    id_tai_san character varying(255) NOT NULL,
    gia_co_ban double precision,
    is_dynamic_pricing boolean NOT NULL,
    mo_ta character varying(255),
    trang_thai character varying(255),
    ho_so_kinh_doanh_id character varying(255) NOT NULL,
    CONSTRAINT tai_san_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['SAN_SANG'::character varying, 'DA_DAT_TRUOC'::character varying, 'DANG_SU_DUNG'::character varying, 'DANG_BAO_TRI'::character varying, 'DA_AN'::character varying])::text[])))
);


ALTER TABLE public.tai_san OWNER TO postgres;

--
-- Name: thuc_don; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.thuc_don (
    id character varying(255) NOT NULL,
    phan_loai character varying(255),
    nha_hang_id character varying(255) NOT NULL,
    ten_thuc_don character varying(255),
    trang_thai character varying(255) DEFAULT 'DANG_HIEN_THI'::character varying NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE public.thuc_don OWNER TO postgres;

--
-- Name: tien_ich_khach_san; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tien_ich_khach_san (
    id character varying(255) NOT NULL,
    loai_tien_ich character varying(255),
    mo_ta character varying(255),
    ten_tien_ich character varying(255) NOT NULL
);


ALTER TABLE public.tien_ich_khach_san OWNER TO postgres;

--
-- Name: tien_ich_nha_hang; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tien_ich_nha_hang (
    id character varying(255) NOT NULL,
    co_thu_phi boolean,
    loai_tien_ich character varying(255),
    mo_ta character varying(255),
    phi_su_dung real,
    ten_tien_ich character varying(255) NOT NULL,
    nha_hang_id character varying(255) NOT NULL
);


ALTER TABLE public.tien_ich_nha_hang OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    gioi_tinh character varying(255),
    ho_ten character varying(255) NOT NULL,
    lan_cuoi_dang_nhap timestamp(6) without time zone,
    mat_khau character varying(255) NOT NULL,
    ngay_cap_nhat timestamp(6) without time zone,
    ngay_sinh date,
    ngay_tao timestamp(6) without time zone,
    so_dien_thoai character varying(255),
    so_lan_dang_nhap_sai integer,
    trang_thai character varying(255),
    username character varying(255) NOT NULL,
    vai_tro_id character varying(255),
    CONSTRAINT users_gioi_tinh_check CHECK (((gioi_tinh)::text = ANY ((ARRAY['NAM'::character varying, 'NU'::character varying, 'KHAC'::character varying])::text[]))),
    CONSTRAINT users_trang_thai_check CHECK (((trang_thai)::text = ANY ((ARRAY['CHUA_XAC_THUC'::character varying, 'HOAT_DONG'::character varying, 'TAM_VO_HIEU_HOA'::character varying, 'BI_KHOA_TAM_THOI'::character varying, 'BI_CAM_VINH_VIEN'::character varying, 'DA_XOA'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: vai_tro; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vai_tro (
    id character varying(255) NOT NULL,
    mo_ta character varying(255),
    ngay_tao timestamp(6) without time zone,
    ten character varying(255) NOT NULL
);


ALTER TABLE public.vai_tro OWNER TO postgres;

--
-- Data for Name: anh_khach_san; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.anh_khach_san (id, duong_dan_url, la_anh_dai_dien, mo_ta_anh, ngay_tai_len, khach_san_id) FROM stdin;
IMG-HOTEL-DN-001-1	https://images.travi.local/hotel-dn-1-main.jpg	t	Mat tien va ho boi vo cuc	2026-05-20	ASSET-HOTEL-DN-001
IMG-HOTEL-DN-001-2	https://images.travi.local/hotel-dn-1-room.jpg	f	Phong Deluxe huong bien	2026-05-20	ASSET-HOTEL-DN-001
IMG-HOTEL-VT-002-1	https://images.travi.local/hotel-vt-1-main.jpg	t	Canh bien va khu vuon	2026-05-20	ASSET-HOTEL-VT-002
IMG-HOTEL-VT-002-2	https://images.travi.local/hotel-vt-1-lobby.jpg	f	Sanh le tan sang trong	2026-05-20	ASSET-HOTEL-VT-002
IMG-HOTEL-HIDDEN-003-1	https://images.travi.local/hotel-hidden-main.jpg	t	Anh khach san dang tam an	2026-05-20	ASSET-HOTEL-HIDDEN-003
\.


--
-- Data for Name: anh_nha_hang; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.anh_nha_hang (id, duong_dan_url, la_anh_dai_dien, mo_ta_anh, ngay_tai_len, nha_hang_id) FROM stdin;
IMG-REST-DN-004-1	https://images.travi.local/rest-dn-004-main.jpg	t	Mat tien nha hang hai san	2026-05-20	ASSET-REST-DN-004
IMG-REST-DN-004-2	https://images.travi.local/rest-dn-004-dining.jpg	f	Khong gian trong nha	2026-05-20	ASSET-REST-DN-004
IMG-REST-VT-005-1	https://images.travi.local/rest-vt-005-main.jpg	t	San thuong nhin bien	2026-05-20	ASSET-REST-VT-005
IMG-REST-HIDDEN-006-1	https://images.travi.local/rest-hidden-main.jpg	t	Anh nha hang dang cho duyet	2026-05-20	ASSET-REST-HIDDEN-006
\.


--
-- Data for Name: anh_phong; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.anh_phong (id, duong_dan_url, la_anh_dai_dien, mo_ta_anh, ngay_tai_len, phong_id) FROM stdin;
IMG-PH-DN-101-1	https://images.travi.local/room-dn-101-main.jpg	t	Phong Deluxe chinh	2026-05-20	PH-DN-101
IMG-PH-DN-101-2	https://images.travi.local/room-dn-101-bath.jpg	f	Phong tam	2026-05-20	PH-DN-101
IMG-PH-DN-102-1	https://images.travi.local/room-dn-102-main.jpg	t	Phong gia dinh chinh	2026-05-20	PH-DN-102
IMG-PH-VT-201-1	https://images.travi.local/room-vt-201-main.jpg	t	Phong Superior	2026-05-20	PH-VT-201
IMG-PH-VT-202-1	https://images.travi.local/room-vt-202-main.jpg	t	Phong Suite view bien	2026-05-20	PH-VT-202
\.


--
-- Data for Name: ban; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ban (id, so_cho_ngoi, trang_thai, vi_tri_sanh, nha_hang_id, ten_ban, mo_ta, deleted, created_at, updated_at) FROM stdin;
BAN-DN-01	2	1	Tang tret	ASSET-REST-DN-004	Ban 01	Ban gan cua so	f	2026-05-20 09:30:00	2026-05-20 09:30:00
BAN-DN-02	4	1	Tang tret	ASSET-REST-DN-004	Ban 02	Ban 4 cho trong khu yên tinh	f	2026-05-20 09:31:00	2026-05-20 09:31:00
BAN-DN-03	6	1	Tang 2	ASSET-REST-DN-004	Ban 03	Ban 6 cho phu hop nhom	f	2026-05-20 09:32:00	2026-05-20 09:32:00
BAN-DN-04	10	2	Phong rieng	ASSET-REST-DN-004	Ban VIP	Phong rieng 10 cho	f	2026-05-20 09:33:00	2026-05-20 09:33:00
BAN-VT-01	2	1	San thuong	ASSET-REST-VT-005	Ban 01	Ban ngoai troi 2 cho	f	2026-05-20 09:34:00	2026-05-20 09:34:00
BAN-VT-02	4	1	San thuong	ASSET-REST-VT-005	Ban 02	Ban 4 cho huong bien	f	2026-05-20 09:35:00	2026-05-20 09:35:00
BAN-HIDDEN-01	4	0	Tang tret	ASSET-REST-HIDDEN-006	Ban an	Ban test an	t	2026-05-20 09:36:00	2026-05-20 09:36:00
\.


--
-- Data for Name: chinh_sach; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.chinh_sach (id, loai_chinh_sach, ngay_ap_dung, noi_dung, ho_so_kinh_doanh_id, gio_nhan_phong, gio_tra_phong, gio_mo_cua, gio_dong_cua, chinh_sach_huy, chinh_sach_hoan_tien, quy_dinh_tre_em, quy_dinh_vat_nuoi, ghi_chu_khac, created_at, updated_at) FROM stdin;
3bff27a9-f896-47f2-b985-fcedf075eaf5	CHINH_SACH_CHUNG	2026-05-24	Doi tac tuan thu quy dinh niem yet, hoan huy va chat luong dich vu cua TraVi OTA.	c8dfc955-9766-41ad-9c24-23723e321100	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
POL-HOTEL-DN-001	Chinh sach khach san	2026-01-01	Gia phong da bao gom buffet sang va dich vu don phong co ban.	HS-HOTEL-DN-001	14:00:00	12:00:00	\N	\N	Huy trong 7 ngay truoc nhan phong: hoan 100%. Huy trong 3 ngay: tinh 50%.	Hoan tien trong 5-7 ngay lam viec.	Tre em duoi 6 tuoi mien phi neu ngu cung nguoi lon.	Khong cho phep vat nuoi.	Co dich vu dua don san bay theo yeu cau.	2026-05-20 10:00:00	2026-05-20 10:00:00
POL-HOTEL-VT-002	Chinh sach khach san	2026-01-01	Khach san phu hop luu tru gia dinh va cong tac.	HS-HOTEL-VT-002	14:00:00	12:00:00	\N	\N	Huy trong 5 ngay truoc nhan phong: hoan 80%.	Hoan tien trong 3-5 ngay lam viec.	Tre em duoi 12 tuoi co the su dung giuong phu.	Cho phep vat nuoi nho co thu phi.	Co bai do xe mien phi.	2026-05-20 10:05:00	2026-05-20 10:05:00
POL-REST-DN-004	Chinh sach nha hang	2026-01-01	Uu tien dat ban truoc vao buoi toi va cuoi tuan.	HS-REST-DN-004	\N	\N	06:30:00	22:30:00	Huy truoc 2 gio khong tinh phi.	Hoan tien coc neu huy dung han.	Tre em co ghe rieng.	Khong cho phep vat nuoi trong khu trong nha.	Dat mon truoc tu 10 phut truoc gio den.	2026-05-20 10:10:00	2026-05-20 10:10:00
POL-REST-VT-005	Chinh sach nha hang	2026-01-01	Phu hop tiec nho va dat ban nhom.	HS-REST-VT-005	\N	\N	07:00:00	23:00:00	Huy truoc 3 gio khong tinh phi.	Hoan tien coc trong 24 gio.	Tre em duoi 5 tuoi mien phi.	Cho phep vat nuoi ngoai khu trong nha.	Co khu rooftop phu hop chup anh.	2026-05-20 10:15:00	2026-05-20 10:15:00
\.


--
-- Data for Name: combo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.combo (id, gia_combo, mo_ta, ngay_bat_dau, ngay_ket_thuc, ten_combo, trang_thai, thuc_don_id, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: combo_item; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.combo_item (id, so_luong, combo_id, mon_an_id) FROM stdin;
\.


--
-- Data for Name: combo_mon_an; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.combo_mon_an (combo_id, mon_an_id) FROM stdin;
\.


--
-- Data for Name: danh_muc_so_thich; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.danh_muc_so_thich (id, mo_ta, ten_danh_muc) FROM stdin;
DM-TRAVEL	Phong cach du lich va trai nghiem luu tru	Du lich
DM-FOOD	Mon an, trai nghiem an uong	Am thuc
DM-UTILITY	Tien ich va dich vu ho tro	Tien nghi
\.


--
-- Data for Name: doi_tac; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.doi_tac (ti_le_chiet_khau, id) FROM stdin;
0	131470c1-341d-418d-81f1-cccb82cd2c83
0.12	PAR-001
0.08	PAR-002
\.


--
-- Data for Name: don_dat_cho; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, hold_expired_at, payment_expired_at, cancelled_at, cancel_reason, deleted, created_at, updated_at) FROM stdin;
DR-HOTEL-001	HD-20260610-01	CUS-001	HS-HOTEL-DN-001	2026-05-20 09:45:00	15000000	1500000	13500000	Hoa Le	0907000001	hoa.le@example.com	Uu tien phong view bien	DA_XAC_NHAN	\N	\N	\N	\N	f	2026-05-20 09:45:00	2026-05-20 09:45:00
DR-HOTEL-002	HD-20260612-02	CUS-002	HS-HOTEL-DN-001	2026-05-20 09:46:00	3600000	0	3600000	Minh Nguyen	0907000002	minh.nguyen@example.com	Dat 1 phong single	DA_HOAN_THANH	\N	\N	2026-05-25 12:00:00	Khach da nhan phong va tra phong	f	2026-05-20 09:46:00	2026-05-20 09:46:00
DR-HOTEL-003	HD-20260614-03	CUS-003	HS-HOTEL-VT-002	2026-05-20 09:47:00	3800000	200000	3600000	Thao Pham	0907000003	thao.pham@example.com	Nho giu phong ban cong	CHO_THANH_TOAN	2026-05-25 18:00:00	2026-05-25 23:59:00	\N	\N	f	2026-05-20 09:47:00	2026-05-20 09:47:00
DR-REST-001	NH-20260601-01	CUS-003	HS-REST-DN-004	2026-05-20 10:00:00	980000	0	980000	Thao Pham	0907000003	thao.pham@example.com	Ban gan san thuong	DA_THANH_TOAN	\N	\N	\N	\N	f	2026-05-20 10:00:00	2026-05-20 10:00:00
DR-REST-002	NH-20260601-02	CUS-001	HS-REST-DN-004	2026-05-20 10:01:00	420000	0	420000	Hoa Le	0907000001	hoa.le@example.com	Ban bo sung cho tiec nho	DA_HOAN_THANH	\N	\N	\N	\N	f	2026-05-20 10:01:00	2026-05-20 10:01:00
DR-REST-003	NH-20260601-03	CUS-002	HS-REST-VT-005	2026-05-20 10:02:00	760000	50000	710000	Minh Nguyen	0907000002	minh.nguyen@example.com	Ban san thuong huong bien	DANG_PHUC_VU	\N	\N	\N	\N	f	2026-05-20 10:02:00	2026-05-20 10:02:00
\.


--
-- Data for Name: don_khach_san; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien) FROM stdin;
DR-HOTEL-001	2026-06-10	2026-06-13	3	4	15:30:00
DR-HOTEL-002	2026-06-20	2026-06-21	1	2	14:00:00
DR-HOTEL-003	2026-06-14	2026-06-16	2	3	14:30:00
\.


--
-- Data for Name: don_khach_san_chi_tiet; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien) FROM stdin;
DR-HOTEL-001-CT1	DR-HOTEL-001	PH-DN-101	Deluxe Ocean View	1	1800000	3	5400000
DR-HOTEL-001-CT2	DR-HOTEL-001	PH-DN-102	Family Suite	1	3200000	3	9600000
DR-HOTEL-002-CT1	DR-HOTEL-002	PH-DN-103	Standard Twin	1	1200000	1	1200000
DR-HOTEL-003-CT1	DR-HOTEL-003	PH-VT-201	Superior Garden	2	950000	2	3800000
\.


--
-- Data for Name: don_nha_hang; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc) FROM stdin;
DR-REST-001	2026-06-01 19:00:00	2026-06-01 21:00:00	4	200000	t
DR-REST-002	2026-06-01 18:30:00	2026-06-01 20:30:00	2	100000	f
DR-REST-003	2026-06-01 20:00:00	2026-06-01 22:00:00	6	300000	t
\.


--
-- Data for Name: don_nha_hang_ban; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.don_nha_hang_ban (id, don_nha_hang_id, ban_id) FROM stdin;
DR-REST-001-B1	DR-REST-001	BAN-DN-01
DR-REST-002-B1	DR-REST-002	BAN-DN-02
DR-REST-003-B1	DR-REST-003	BAN-VT-01
DR-REST-003-B2	DR-REST-003	BAN-VT-02
\.


--
-- Data for Name: ho_so_kinh_doanh; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ho_so_kinh_doanh (id_ho_so, giay_phep_kinh_doanh, loai_dich_vu, ma_so_thue, old_giay_phep_kinh_doanh, old_loai_dich_vu, old_ma_so_thue, old_sdt_lien_he, old_ten_co_so, old_toa_dogps, sdt_lien_he, ten_co_so, thoi_gian_dang_ky, thoi_gian_duyet, toa_do_gps, trang_thai_kiem_duyet, doi_tac_id, kinh_do, vi_do, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, trang_thai_hoat_dong, ly_do_tu_choi_gan_nhat, deleted, thoi_gian_cap_nhat) FROM stdin;
c8dfc955-9766-41ad-9c24-23723e321100	http://127.0.0.1:8080/uploads/partner-assets/b2565c98-4956-46fb-8357-2cf03fbbf76a-avatar_snake.jpg	KHACH_SAN	1234567890123	\N	\N	\N	\N	\N	\N	0972496201	Danny Lover	2026-05-24 05:42:06.526956	\N	10.77690,106.70090	CHO_DUYET	131470c1-341d-418d-81f1-cccb82cd2c83	\N	\N	\N	\N	\N	\N	\N	CHUA_HOAT_DONG	\N	f	\N
HS-HOTEL-DN-001	GP-DN-HOTEL-001	KHACH_SAN	0401234567	\N	\N	\N	\N	\N	\N	02363880001	Sun Bay Hotel & Spa	2026-04-10 08:00:00	2026-05-20 09:00:00	16.0678,108.2240	DA_DUYET	PAR-001	108.224	16.0678	contact@sunbayda-nang.vn	120 Vo Nguyen Giap, Phuong Phước Mỹ	Đà Nẵng	Sơn Trà	Phước Mỹ	DANG_HOAT_DONG	\N	f	2026-05-20 09:00:00
HS-REST-DN-004	GP-DN-REST-004	NHA_HANG	0407654321	\N	\N	\N	\N	\N	\N	02363880004	Ngon Garden Seafood	2026-04-11 07:45:00	2026-05-20 09:20:00	16.0760,108.2212	DA_DUYET	PAR-001	108.2212	16.076	booking@ngongarden.vn	68 Bach Dang, Hải Châu	Đà Nẵng	Hải Châu	Hải Châu 1	DANG_HOAT_DONG	\N	f	2026-05-20 09:20:00
HS-REST-HIDDEN-006	GP-DN-REST-006	NHA_HANG	0409998887	\N	\N	\N	\N	\N	\N	02363880006	Night Market Kitchen	2026-04-18 08:15:00	\N	16.0600,108.2100	CHO_DUYET	PAR-001	108.21	16.06	hidden@nightmarket.vn	09 Nguyen Van Linh, Hải Châu	Đà Nẵng	Hải Châu	Hải Châu 2	CHUA_HOAT_DONG	Dang cho bo sung giay to	f	2026-05-20 09:30:00
HS-HOTEL-VT-002	GP-VT-HOTEL-002	KHACH_SAN	0649876543	\N	\N	\N	\N	\N	\N	02543880002	Lotus Bay Hotel	2026-04-12 08:30:00	2026-05-20 09:15:00	10.3453,107.1019	DA_DUYET	PAR-002	107.1019	10.3453	hello@lotusbayvt.vn	45 Le Hong Phong, Phuong Thắng Tam	Vũng Tàu	TP Vũng Tàu	Thắng Tam	DANG_HOAT_DONG	\N	f	2026-05-20 09:15:00
HS-REST-VT-005	GP-VT-REST-005	NHA_HANG	0645558888	\N	\N	\N	\N	\N	\N	02543880005	Spice Street Bistro	2026-04-13 07:30:00	2026-05-20 09:25:00	10.3478,107.0850	DA_DUYET	PAR-002	107.085	10.3478	contact@spicestreetvt.vn	22 Tran Phu, Phường 1	Vũng Tàu	TP Vũng Tàu	Phường 1	DANG_HOAT_DONG	\N	f	2026-05-20 09:25:00
HS-HOTEL-HIDDEN-003	GP-HN-HOTEL-003	KHACH_SAN	0101112223	\N	\N	\N	\N	\N	\N	02437770003	Old Quarter Inn	2026-04-15 10:00:00	2026-05-10 11:00:00	21.0333,105.8519	BI_TU_CHOI	PAR-001	105.8519	21.0333	hidden@oldquarter.vn	12 Hang Bac, Hoan Kiem	Hà Nội	Hoan Kiem	Hang Bac	TAM_DUNG	Ho so thieu anh giay phep kinh doanh	f	2026-05-10 11:00:00
\.


--
-- Data for Name: khach_hang; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.khach_hang (diem_thanh_vien, hang_thanh_vien, tong_chi_tieu, id) FROM stdin;
0	DONG	0	9d97f628-85a9-4e8a-99ae-292d308de07c
0	DONG	0	7f3a8628-0777-4c8b-b0a9-50ab3d848c84
1200	VANG	18500000	CUS-001
450	BAC	6200000	CUS-002
2100	KIM_CUONG	34800000	CUS-003
90	DONG	950000	CUS-004
\.


--
-- Data for Name: khach_hang_so_thich; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.khach_hang_so_thich (khach_hang_id, so_thich_id) FROM stdin;
CUS-001	ST-BEACH
CUS-001	ST-SPA
CUS-002	ST-FAMILY
CUS-002	ST-BUDGET
CUS-003	ST-SEAFOOD
CUS-003	ST-FINE-DINING
CUS-003	ST-BEACH
CUS-004	ST-WELLNESS
\.


--
-- Data for Name: khach_hang_tu_khoa; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.khach_hang_tu_khoa (khach_hang_id, tu_khoa) FROM stdin;
CUS-001	resort bien da nang
CUS-001	spa view dep
CUS-002	hotel gia tot
CUS-002	phong gia dinh 4 nguoi
CUS-003	nha hang hai san ngon
CUS-003	bo sung buffet sang
CUS-004	ks gan trung tam
\.


--
-- Data for Name: khach_san; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.khach_san (gio_nhan_phong, gio_tra_phong, hang_sao, ten, id_tai_san, loai_khach_san, gio_nhan_phong_mac_dinh, gio_tra_phong_mac_dinh, so_tang, tong_so_phong) FROM stdin;
14:00:00	12:00:00	5	Danny Lover	8d21860b-4d65-4df0-91d3-d4b13ebbd81f	\N	\N	\N	\N	\N
14:00:00	12:00:00	5	Sun Bay Hotel & Spa	ASSET-HOTEL-DN-001	Resort biển	15:00:00	11:00:00	12	10
14:00:00	12:00:00	4	Lotus Bay Hotel	ASSET-HOTEL-VT-002	Khách sạn boutique	14:30:00	11:30:00	8	7
14:00:00	12:00:00	3	Old Quarter Inn	ASSET-HOTEL-HIDDEN-003	Khách sạn thành phố	14:00:00	12:00:00	5	4
\.


--
-- Data for Name: khach_san_tien_ich; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.khach_san_tien_ich (khach_san_id, tien_ich_id) FROM stdin;
8d21860b-4d65-4df0-91d3-d4b13ebbd81f	2c169444-df39-4757-b768-003b9f180e11
8d21860b-4d65-4df0-91d3-d4b13ebbd81f	6681e39c-53e5-4e38-8f02-5119e9af2fed
ASSET-HOTEL-DN-001	KS-AM-POOL
ASSET-HOTEL-DN-001	KS-AM-SPA
ASSET-HOTEL-DN-001	KS-AM-GYM
ASSET-HOTEL-DN-001	KS-AM-PARKING
ASSET-HOTEL-DN-001	KS-AM-BREAKFAST
ASSET-HOTEL-DN-001	KS-AM-SHUTTLE
ASSET-HOTEL-VT-002	KS-AM-PARKING
ASSET-HOTEL-VT-002	KS-AM-BREAKFAST
ASSET-HOTEL-VT-002	KS-AM-GYM
ASSET-HOTEL-VT-002	KS-AM-COWORK
ASSET-HOTEL-HIDDEN-003	KS-AM-PARKING
\.


--
-- Data for Name: lich_su_kiem_duyet_ho_so; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lich_su_kiem_duyet_ho_so (id, created_at, ghi_chu_noi_bo, ly_do, trang_thai_cu, trang_thai_moi, admin_id, ho_so_kinh_doanh_id) FROM stdin;
\.


--
-- Data for Name: lich_su_thao_tac; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lich_su_thao_tac (id_log, hanh_dong, thoi_gian, id_admin) FROM stdin;
LOG-001	Duyet ho so HS-HOTEL-DN-001	2026-05-20 11:00:00	ADMIN-001
LOG-002	Duyet ho so HS-REST-DN-004	2026-05-20 11:05:00	ADMIN-002
LOG-003	Tu choi ho so HS-HOTEL-HIDDEN-003	2026-05-20 11:10:00	ADMIN-003
\.


--
-- Data for Name: mon_an; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mon_an (id, duong_dan_url, gia_ban, ten_mon, trang_thai, thuc_don_id, mo_ta, danh_muc_mon, deleted, created_at, updated_at) FROM stdin;
MA-DN-001	https://images.travi.local/mq-tom.jpg	65000	Mi Quang tom	DANG_BAN	TD-DN-BRUNCH	Mi Quang dac biet voi tom tuoi va dau phung rang	Bua sang	f	2026-05-20 09:20:00	2026-05-20 09:20:00
MA-DN-002	https://images.travi.local/banh-mi-op-la.jpg	45000	Banh mi op la	DANG_BAN	TD-DN-BRUNCH	Banh mi nong gion voi trung op la	Bua sang	f	2026-05-20 09:21:00	2026-05-20 09:21:00
MA-DN-003	https://images.travi.local/che-sua-chua.jpg	39000	Che sua chua	TAM_HET	TD-DN-BRUNCH	Mon trang mieng mat lanh	Trang mieng	f	2026-05-20 09:22:00	2026-05-20 09:22:00
MA-DN-004	https://images.travi.local/tom-hum.jpg	420000	Tom hum nuong mo toi	DANG_BAN	TD-DN-SEAFOOD	Tom hum tuoi nuong voi mo toi	Hai san	f	2026-05-20 09:23:00	2026-05-20 09:23:00
MA-VT-001	https://images.travi.local/steak-wagu.jpg	380000	Steak bo Wagu	DANG_BAN	TD-VT-MAIN	Bo nuong phuc vu kem sot tieu den	Mon chinh	f	2026-05-20 09:24:00	2026-05-20 09:24:00
MA-VT-002	https://images.travi.local/muc-nuong.jpg	220000	Muc mot nang nuong	CO_SAN	TD-VT-MAIN	Dac san bien nuong muoi ot	Hai san	f	2026-05-20 09:25:00	2026-05-20 09:25:00
MA-HIDDEN-001	https://images.travi.local/test-hidden.jpg	99000	Mon an test an	NGUNG_BAN	TD-HIDDEN-001	Mon an chi dung cho ho so tam an	Test	t	2026-05-20 09:26:00	2026-05-20 09:26:00
\.


--
-- Data for Name: mon_an_the_ngu_canh; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mon_an_the_ngu_canh (mon_an_id, the_ngu_canh) FROM stdin;
MA-DN-001	dac san
MA-DN-001	buoi sang
MA-DN-002	nhanh
MA-DN-002	chat luong
MA-DN-004	hai san
MA-DN-004	dac trung
MA-VT-001	fine dining
MA-VT-001	bo
MA-VT-002	bien
MA-HIDDEN-001	an
\.


--
-- Data for Name: nha_hang; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nha_hang (gio_dong_cua, gio_mo_cua, loai_am_thuc, suc_chua, ten, id_tai_san, co_dat_ban_truoc, co_dat_mon_truoc) FROM stdin;
22:30:00	06:30:00	Việt - Hải sản	120	Ngon Garden Seafood	ASSET-REST-DN-004	t	t
23:00:00	07:00:00	Âu - Fusion	80	Spice Street Bistro	ASSET-REST-VT-005	t	t
22:00:00	16:00:00	Ẩm thực đường phố	60	Night Market Kitchen	ASSET-REST-HIDDEN-006	f	f
\.


--
-- Data for Name: phong; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.phong (id, dien_tich, loai_phong, phan_tram_giam_gia, so_phong, suc_chua_toi_da, trang_thai, khach_san_id, ten_phong, mo_ta, so_giuong, so_luong_phong, gia_co_ban, deleted, created_at, updated_at) FROM stdin;
PH-DN-101	28	DELUXE	0.1	101	2	SAN_SANG	ASSET-HOTEL-DN-001	Deluxe Ocean View	Phong Deluxe huong bien, rong thoang	1	3	1800000	f	\N	\N
PH-DN-102	45	SUITE	0.15	102	4	SAN_SANG	ASSET-HOTEL-DN-001	Family Suite	Phong gia dinh 2 phong ngu	2	2	3200000	f	\N	\N
PH-DN-103	22	STANDARD	0	103	2	DANG_BAO_TRI	ASSET-HOTEL-DN-001	Standard Twin	Phong tieu chuan, phu hop doan nho	2	4	1200000	f	\N	\N
PH-DN-104	20	STANDARD	0.2	104	2	DA_AN	ASSET-HOTEL-DN-001	Hidden Promo Room	Phong an de test ẩn	1	1	900000	t	\N	\N
PH-VT-201	26	SUPERIOR	0.05	201	2	SAN_SANG	ASSET-HOTEL-VT-002	Superior Garden	Phong view vuon trang nhã	1	5	950000	f	\N	\N
PH-VT-202	34	SUITE	0.08	202	3	DA_DAT_TRUOC	ASSET-HOTEL-VT-002	Sea Breeze Suite	Can suite co ban cong rieng	1	1	1450000	f	\N	\N
PH-HI-301	18	ECONOMY	0	301	2	DA_AN	ASSET-HOTEL-HIDDEN-003	Hidden Economy	Phong kinh te cho test	1	2	650000	t	\N	\N
\.


--
-- Data for Name: phong_tien_ich; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.phong_tien_ich (phong_id, tien_ich) FROM stdin;
PH-DN-101	WIFI
PH-DN-101	DIEU_HOA
PH-DN-101	VIEW_DEP
PH-DN-102	WIFI
PH-DN-102	DIEU_HOA
PH-DN-102	BAN_CONG
PH-DN-102	BON_TAM
PH-DN-103	WIFI
PH-DN-103	TV
PH-VT-201	WIFI
PH-VT-201	DIEU_HOA
PH-VT-201	TV
PH-VT-202	WIFI
PH-VT-202	TU_LANH
PH-VT-202	BAN_CONG
\.


--
-- Data for Name: quan_tri_vien; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.quan_tri_vien (cap_do_quyen, id) FROM stdin;
3	ADMIN-001
2	ADMIN-002
1	ADMIN-003
\.


--
-- Data for Name: so_thich; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.so_thich (id, ten_so_thich, danh_muc_id) FROM stdin;
ST-BEACH	Bien va nghi duong	DM-TRAVEL
ST-SPA	Spa va cham soc suc khoe	DM-UTILITY
ST-FAMILY	Du lich gia dinh	DM-TRAVEL
ST-SEAFOOD	Hai san tuoi song	DM-FOOD
ST-FINE-DINING	An uong sang trong	DM-FOOD
ST-BUDGET	Tiet kiem chi phi	DM-TRAVEL
ST-WELLNESS	Cham soc suc khoe	DM-UTILITY
\.


--
-- Data for Name: tai_san; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tai_san (id_tai_san, gia_co_ban, is_dynamic_pricing, mo_ta, trang_thai, ho_so_kinh_doanh_id) FROM stdin;
8d21860b-4d65-4df0-91d3-d4b13ebbd81f	1199970	f	Khách sạn 5 sao với view biển siêu xịn.	SAN_SANG	c8dfc955-9766-41ad-9c24-23723e321100
ASSET-HOTEL-DN-001	1800000	f	Resort ven bien 5 sao co ho boi vo cuc va spa	SAN_SANG	HS-HOTEL-DN-001
ASSET-HOTEL-VT-002	950000	f	Khach san 4 sao gan bien phu hop nghi duong gia dinh	SAN_SANG	HS-HOTEL-VT-002
ASSET-HOTEL-HIDDEN-003	650000	f	Khach san co quan tam can cap nhat ho so	DA_AN	HS-HOTEL-HIDDEN-003
ASSET-REST-DN-004	120000	f	Nha hang hai san sang trong tai trung tam Đà Nẵng	SAN_SANG	HS-REST-DN-004
ASSET-REST-VT-005	150000	f	Bistro phong cách Âu - Fusion có sân thượng	SAN_SANG	HS-REST-VT-005
ASSET-REST-HIDDEN-006	90000	f	Nhà hàng đang tạm dừng để bảo trì	DA_AN	HS-REST-HIDDEN-006
\.


--
-- Data for Name: thuc_don; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.thuc_don (id, phan_loai, nha_hang_id, ten_thuc_don, trang_thai, created_at, updated_at) FROM stdin;
TD-DN-BRUNCH	BRUNCH	ASSET-REST-DN-004	Buoi sang va brunch	DANG_HIEN_THI	2026-05-20 09:00:00	2026-05-20 09:00:00
TD-DN-SEAFOOD	DINNER	ASSET-REST-DN-004	Hai san dac trung	DANG_HIEN_THI	2026-05-20 09:05:00	2026-05-20 09:05:00
TD-VT-MAIN	MAIN	ASSET-REST-VT-005	A La Carte	DANG_HIEN_THI	2026-05-20 09:10:00	2026-05-20 09:10:00
TD-HIDDEN-001	HIDDEN	ASSET-REST-HIDDEN-006	Menu an	TAM_AN	2026-05-20 09:15:00	2026-05-20 09:15:00
\.


--
-- Data for Name: tien_ich_khach_san; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tien_ich_khach_san (id, loai_tien_ich, mo_ta, ten_tien_ich) FROM stdin;
6681e39c-53e5-4e38-8f02-5119e9af2fed	CO_BAN	Phu song trong khuon vien	Wifi mien phi
2c169444-df39-4757-b768-003b9f180e11	DICH_VU	Ho tro khach moi luc	Le tan 24/7
KS-AM-POOL	Tien ich nghi duong	Ho boi ngoai troi huong bien	Ho boi vo cuc
KS-AM-SPA	Suc khoe	Dich vu massage va spa cao cap	Spa va massage
KS-AM-GYM	The thao	Phong tap co day du may moc	Phong gym
KS-AM-PARKING	Tien ich co ban	Bai do xe an toan, rong rai	Bai do xe
KS-AM-BREAKFAST	An uong	Buffet sang phong phu	Buffet sang
KS-AM-SHUTTLE	Di chuyen	Xe dua don san bay theo yeu cau	Xe dua don
KS-AM-COWORK	Cong viec	Khong gian lam viec thong thoang	Khu lam viec chung
\.


--
-- Data for Name: tien_ich_nha_hang; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tien_ich_nha_hang (id, co_thu_phi, loai_tien_ich, mo_ta, phi_su_dung, ten_tien_ich, nha_hang_id) FROM stdin;
RH-AM-PARKING	f	Tien ich co ban	Bai do xe o to va xe may	0	Bai do xe	ASSET-REST-DN-004
RH-AM-PRIVATE	t	Khong gian	Phu hop tiec gia dinh va hop mat	300000	Phong rieng	ASSET-REST-DN-004
RH-AM-CHILD	f	Gia dinh	Ghe an an toan cho tre nho	0	Ghe tre em	ASSET-REST-DN-004
RH-AM-LIVE	f	Giai tri	Trinh dien buoi toi	0	Nhac acoustic	ASSET-REST-DN-004
RH-AM-ROOFTOP	f	Khong gian	View bien va hoang hon	0	San thuong	ASSET-REST-VT-005
RH-AM-WIFI	f	Tien ich co ban	Internet toc do cao	0	Wi-Fi mien phi	ASSET-REST-VT-005
RH-AM-PARKING-VT	f	Tien ich co ban	Do xe rong va tien loi	0	Bai do xe	ASSET-REST-VT-005
RH-AM-PRIVATE-HIDDEN	f	Khong gian	Dich vu test cho nha hang an	0	Phong rieng	ASSET-REST-HIDDEN-006
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, email, gioi_tinh, ho_ten, lan_cuoi_dang_nhap, mat_khau, ngay_cap_nhat, ngay_sinh, ngay_tao, so_dien_thoai, so_lan_dang_nhap_sai, trang_thai, username, vai_tro_id) FROM stdin;
131470c1-341d-418d-81f1-cccb82cd2c83	anhbid2000@gmail.com	\N	Lê Chí Bảo	\N	$2a$10$3m8oC/77mlKB7I6E4YQ2Meobb1YURPzoHV/fM2OxQrR.HyQ1XKd7C	2026-05-24 05:37:18.86293	\N	2026-05-24 05:36:46.426443	0972496201	0	HOAT_DONG	John	34afd646-5ccc-445c-a95d-4c5e79192cba
ADMIN-001	admin1@travi.vn	\N	Nguyen Quan Tri 1	\N	$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK	\N	\N	\N	\N	\N	HOAT_DONG	admin.root	d1be1d92-87a5-4763-bf48-f0ba30917b58
ADMIN-002	admin2@travi.vn	\N	Tran Quan Tri 2	\N	$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK	\N	\N	\N	\N	\N	HOAT_DONG	admin.ops	d1be1d92-87a5-4763-bf48-f0ba30917b58
ADMIN-003	admin3@travi.vn	\N	Le Quan Tri 3	\N	$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK	\N	\N	\N	\N	\N	HOAT_DONG	admin.audit	d1be1d92-87a5-4763-bf48-f0ba30917b58
9d97f628-85a9-4e8a-99ae-292d308de07c	anhbid1000@gmail.com	\N	Lê Chí Bảo	\N	$2a$10$tXgF2ty9g2hr7tAU8/qnte3Q6RBbQIFlNDk65PpH33zcfgEgGPZvq	2026-05-24 09:37:58.654118	\N	2026-05-24 07:41:28.833412	0972496201	0	HOAT_DONG	JohnDanny	abccf5ee-7467-42b0-b6a5-c6a8f6d1fcb3
7f3a8628-0777-4c8b-b0a9-50ab3d848c84	23520107@gm.uit.edu.vn	\N	Chí Bảo	\N	$2a$10$ns4zVRlVm9gMj2CJb0qhYukkDRhTTnzJOCVUwh5P2dJrmLkEj60dG	2026-05-24 10:13:05.591571	\N	2026-05-24 10:12:18.653588	0972496201	0	HOAT_DONG	Kailu	abccf5ee-7467-42b0-b6a5-c6a8f6d1fcb3
PAR-001	partner.da.nang@example.com	NAM	Nguyen Minh Quan	2026-05-25 09:15:00	$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK	2026-05-25 09:15:00	1990-03-12	2026-05-25 09:15:00	0905000001	0	HOAT_DONG	partner.danang	34afd646-5ccc-445c-a95d-4c5e79192cba
PAR-002	partner.vungtau@example.com	NU	Tran Thu Ha	2026-05-25 09:30:00	$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK	2026-05-25 09:30:00	1988-09-22	2026-05-25 09:30:00	0905000002	0	HOAT_DONG	partner.vungtau	34afd646-5ccc-445c-a95d-4c5e79192cba
CUS-001	hoa.le@example.com	NU	Hoa Le	2026-05-25 10:00:00	$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK	2026-05-25 10:00:00	1994-08-21	2026-05-25 10:00:00	0907000001	0	HOAT_DONG	hoa.le	abccf5ee-7467-42b0-b6a5-c6a8f6d1fcb3
CUS-002	minh.nguyen@example.com	NAM	Minh Nguyen	2026-05-25 10:05:00	$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK	2026-05-25 10:05:00	1992-01-15	2026-05-25 10:05:00	0907000002	0	HOAT_DONG	minh.nguyen	abccf5ee-7467-42b0-b6a5-c6a8f6d1fcb3
CUS-003	thao.pham@example.com	KHAC	Thao Pham	2026-05-25 10:10:00	$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK	2026-05-25 10:10:00	1996-11-03	2026-05-25 10:10:00	0907000003	0	HOAT_DONG	thao.pham	abccf5ee-7467-42b0-b6a5-c6a8f6d1fcb3
CUS-004	an.tran@example.com	NAM	An Tran	2026-05-25 10:15:00	$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK	2026-05-25 10:15:00	1989-05-08	2026-05-25 10:15:00	0907000004	0	HOAT_DONG	an.tran	abccf5ee-7467-42b0-b6a5-c6a8f6d1fcb3
\.


--
-- Data for Name: vai_tro; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vai_tro (id, mo_ta, ngay_tao, ten) FROM stdin;
abccf5ee-7467-42b0-b6a5-c6a8f6d1fcb3	Nguoi dung tim kiem va dat dich vu	2026-05-24 05:32:16.394358	KHACH_HANG
34afd646-5ccc-445c-a95d-4c5e79192cba	Chu khach san/nha hang dang ban dich vu	2026-05-24 05:32:16.409154	DOI_TAC
d1be1d92-87a5-4763-bf48-f0ba30917b58	Quan tri vien he thong OTA	2026-05-24 05:32:16.413314	QUAN_TRI_VIEN
\.


--
-- Name: anh_khach_san anh_khach_san_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anh_khach_san
    ADD CONSTRAINT anh_khach_san_pkey PRIMARY KEY (id);


--
-- Name: anh_nha_hang anh_nha_hang_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anh_nha_hang
    ADD CONSTRAINT anh_nha_hang_pkey PRIMARY KEY (id);


--
-- Name: anh_phong anh_phong_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anh_phong
    ADD CONSTRAINT anh_phong_pkey PRIMARY KEY (id);


--
-- Name: ban ban_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ban
    ADD CONSTRAINT ban_pkey PRIMARY KEY (id);


--
-- Name: chinh_sach chinh_sach_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chinh_sach
    ADD CONSTRAINT chinh_sach_pkey PRIMARY KEY (id);


--
-- Name: combo_item combo_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.combo_item
    ADD CONSTRAINT combo_item_pkey PRIMARY KEY (id);


--
-- Name: combo_mon_an combo_mon_an_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.combo_mon_an
    ADD CONSTRAINT combo_mon_an_pkey PRIMARY KEY (combo_id, mon_an_id);


--
-- Name: combo combo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.combo
    ADD CONSTRAINT combo_pkey PRIMARY KEY (id);


--
-- Name: danh_muc_so_thich danh_muc_so_thich_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.danh_muc_so_thich
    ADD CONSTRAINT danh_muc_so_thich_pkey PRIMARY KEY (id);


--
-- Name: doi_tac doi_tac_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doi_tac
    ADD CONSTRAINT doi_tac_pkey PRIMARY KEY (id);


--
-- Name: don_dat_cho don_dat_cho_ma_don_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_dat_cho
    ADD CONSTRAINT don_dat_cho_ma_don_key UNIQUE (ma_don);


--
-- Name: don_dat_cho don_dat_cho_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_dat_cho
    ADD CONSTRAINT don_dat_cho_pkey PRIMARY KEY (id);


--
-- Name: don_khach_san_chi_tiet don_khach_san_chi_tiet_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_khach_san_chi_tiet
    ADD CONSTRAINT don_khach_san_chi_tiet_pkey PRIMARY KEY (id);


--
-- Name: don_khach_san don_khach_san_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_khach_san
    ADD CONSTRAINT don_khach_san_pkey PRIMARY KEY (id);


--
-- Name: don_nha_hang_ban don_nha_hang_ban_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_nha_hang_ban
    ADD CONSTRAINT don_nha_hang_ban_pkey PRIMARY KEY (id);


--
-- Name: don_nha_hang don_nha_hang_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_nha_hang
    ADD CONSTRAINT don_nha_hang_pkey PRIMARY KEY (id);


--
-- Name: ho_so_kinh_doanh ho_so_kinh_doanh_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ho_so_kinh_doanh
    ADD CONSTRAINT ho_so_kinh_doanh_pkey PRIMARY KEY (id_ho_so);


--
-- Name: khach_hang khach_hang_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_hang
    ADD CONSTRAINT khach_hang_pkey PRIMARY KEY (id);


--
-- Name: khach_hang_so_thich khach_hang_so_thich_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_hang_so_thich
    ADD CONSTRAINT khach_hang_so_thich_pkey PRIMARY KEY (khach_hang_id, so_thich_id);


--
-- Name: khach_san khach_san_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_san
    ADD CONSTRAINT khach_san_pkey PRIMARY KEY (id_tai_san);


--
-- Name: khach_san_tien_ich khach_san_tien_ich_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_san_tien_ich
    ADD CONSTRAINT khach_san_tien_ich_pkey PRIMARY KEY (khach_san_id, tien_ich_id);


--
-- Name: lich_su_kiem_duyet_ho_so lich_su_kiem_duyet_ho_so_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lich_su_kiem_duyet_ho_so
    ADD CONSTRAINT lich_su_kiem_duyet_ho_so_pkey PRIMARY KEY (id);


--
-- Name: lich_su_thao_tac lich_su_thao_tac_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lich_su_thao_tac
    ADD CONSTRAINT lich_su_thao_tac_pkey PRIMARY KEY (id_log);


--
-- Name: mon_an mon_an_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mon_an
    ADD CONSTRAINT mon_an_pkey PRIMARY KEY (id);


--
-- Name: nha_hang nha_hang_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nha_hang
    ADD CONSTRAINT nha_hang_pkey PRIMARY KEY (id_tai_san);


--
-- Name: phong phong_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phong
    ADD CONSTRAINT phong_pkey PRIMARY KEY (id);


--
-- Name: quan_tri_vien quan_tri_vien_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.quan_tri_vien
    ADD CONSTRAINT quan_tri_vien_pkey PRIMARY KEY (id);


--
-- Name: so_thich so_thich_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.so_thich
    ADD CONSTRAINT so_thich_pkey PRIMARY KEY (id);


--
-- Name: tai_san tai_san_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tai_san
    ADD CONSTRAINT tai_san_pkey PRIMARY KEY (id_tai_san);


--
-- Name: thuc_don thuc_don_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.thuc_don
    ADD CONSTRAINT thuc_don_pkey PRIMARY KEY (id);


--
-- Name: tien_ich_khach_san tien_ich_khach_san_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tien_ich_khach_san
    ADD CONSTRAINT tien_ich_khach_san_pkey PRIMARY KEY (id);


--
-- Name: tien_ich_nha_hang tien_ich_nha_hang_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tien_ich_nha_hang
    ADD CONSTRAINT tien_ich_nha_hang_pkey PRIMARY KEY (id);


--
-- Name: vai_tro uk19fdfmoa7i3s54reitajsmonw; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vai_tro
    ADD CONSTRAINT uk19fdfmoa7i3s54reitajsmonw UNIQUE (ten);


--
-- Name: users uk6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: tien_ich_khach_san ukgnhmd6jb1m61a66dbk25jmc35; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tien_ich_khach_san
    ADD CONSTRAINT ukgnhmd6jb1m61a66dbk25jmc35 UNIQUE (ten_tien_ich);


--
-- Name: chinh_sach ukkv8ljimoo6g7grjua607noqqt; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chinh_sach
    ADD CONSTRAINT ukkv8ljimoo6g7grjua607noqqt UNIQUE (ho_so_kinh_doanh_id);


--
-- Name: users ukr43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT ukr43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- Name: ho_so_kinh_doanh ukrngq6kueg9d6awsup6pdyq7gi; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ho_so_kinh_doanh
    ADD CONSTRAINT ukrngq6kueg9d6awsup6pdyq7gi UNIQUE (ma_so_thue);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: vai_tro vai_tro_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vai_tro
    ADD CONSTRAINT vai_tro_pkey PRIMARY KEY (id);


--
-- Name: idx_don_khach_san_ct_phong; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_don_khach_san_ct_phong ON public.don_khach_san_chi_tiet USING btree (phong_id, don_khach_san_id);


--
-- Name: idx_don_khach_san_overlap; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_don_khach_san_overlap ON public.don_khach_san USING btree (ngay_check_in, ngay_check_out);


--
-- Name: idx_don_nha_hang_ban_lookup; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_don_nha_hang_ban_lookup ON public.don_nha_hang_ban USING btree (ban_id, don_nha_hang_id);


--
-- Name: idx_don_nha_hang_overlap; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_don_nha_hang_overlap ON public.don_nha_hang USING btree (ngay_gio_bat_dau, ngay_gio_ket_thuc);


--
-- Name: phong fk16oi6kmyp6ne7m7q2k957otma; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phong
    ADD CONSTRAINT fk16oi6kmyp6ne7m7q2k957otma FOREIGN KEY (khach_san_id) REFERENCES public.khach_san(id_tai_san);


--
-- Name: khach_hang fk49nllm1rhw35kroq642elxhxu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_hang
    ADD CONSTRAINT fk49nllm1rhw35kroq642elxhxu FOREIGN KEY (id) REFERENCES public.users(id);


--
-- Name: anh_khach_san fk4yi8ddl505e1l3nqq6mpvpxs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anh_khach_san
    ADD CONSTRAINT fk4yi8ddl505e1l3nqq6mpvpxs FOREIGN KEY (khach_san_id) REFERENCES public.khach_san(id_tai_san);


--
-- Name: tien_ich_nha_hang fk5qoaance51u2rmam68fm89njg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tien_ich_nha_hang
    ADD CONSTRAINT fk5qoaance51u2rmam68fm89njg FOREIGN KEY (nha_hang_id) REFERENCES public.nha_hang(id_tai_san);


--
-- Name: mon_an fk5s8t5b2em5a3976xwmgpn4a26; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mon_an
    ADD CONSTRAINT fk5s8t5b2em5a3976xwmgpn4a26 FOREIGN KEY (thuc_don_id) REFERENCES public.thuc_don(id);


--
-- Name: ban fk6rfckib2if6siqo85lil036gk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ban
    ADD CONSTRAINT fk6rfckib2if6siqo85lil036gk FOREIGN KEY (nha_hang_id) REFERENCES public.nha_hang(id_tai_san);


--
-- Name: lich_su_kiem_duyet_ho_so fk6t7aec055p50i392hsrf7j6fw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lich_su_kiem_duyet_ho_so
    ADD CONSTRAINT fk6t7aec055p50i392hsrf7j6fw FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES public.ho_so_kinh_doanh(id_ho_so);


--
-- Name: anh_nha_hang fk6v8hhg1ie5761qbwoug3p7se9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anh_nha_hang
    ADD CONSTRAINT fk6v8hhg1ie5761qbwoug3p7se9 FOREIGN KEY (nha_hang_id) REFERENCES public.nha_hang(id_tai_san);


--
-- Name: doi_tac fk7yr34rtxxlbwtf1k2bbv002sb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doi_tac
    ADD CONSTRAINT fk7yr34rtxxlbwtf1k2bbv002sb FOREIGN KEY (id) REFERENCES public.users(id);


--
-- Name: combo_item fk8163lac8tty6571u6ensdpwr8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.combo_item
    ADD CONSTRAINT fk8163lac8tty6571u6ensdpwr8 FOREIGN KEY (combo_id) REFERENCES public.combo(id);


--
-- Name: khach_hang_so_thich fk8kwck46fhw2klungy9cdhcryb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_hang_so_thich
    ADD CONSTRAINT fk8kwck46fhw2klungy9cdhcryb FOREIGN KEY (khach_hang_id) REFERENCES public.khach_hang(id);


--
-- Name: don_dat_cho fk_don_dat_cho_ho_so; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_dat_cho
    ADD CONSTRAINT fk_don_dat_cho_ho_so FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES public.ho_so_kinh_doanh(id_ho_so);


--
-- Name: don_dat_cho fk_don_dat_cho_khach_hang; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_dat_cho
    ADD CONSTRAINT fk_don_dat_cho_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES public.khach_hang(id);


--
-- Name: don_khach_san_chi_tiet fk_don_khach_san_ct_don; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_khach_san_chi_tiet
    ADD CONSTRAINT fk_don_khach_san_ct_don FOREIGN KEY (don_khach_san_id) REFERENCES public.don_khach_san(id);


--
-- Name: don_khach_san_chi_tiet fk_don_khach_san_ct_phong; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_khach_san_chi_tiet
    ADD CONSTRAINT fk_don_khach_san_ct_phong FOREIGN KEY (phong_id) REFERENCES public.phong(id);


--
-- Name: don_khach_san fk_don_khach_san_don_dat_cho; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_khach_san
    ADD CONSTRAINT fk_don_khach_san_don_dat_cho FOREIGN KEY (id) REFERENCES public.don_dat_cho(id);


--
-- Name: don_nha_hang_ban fk_don_nha_hang_ban_ban; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_nha_hang_ban
    ADD CONSTRAINT fk_don_nha_hang_ban_ban FOREIGN KEY (ban_id) REFERENCES public.ban(id);


--
-- Name: don_nha_hang_ban fk_don_nha_hang_ban_don; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_nha_hang_ban
    ADD CONSTRAINT fk_don_nha_hang_ban_don FOREIGN KEY (don_nha_hang_id) REFERENCES public.don_nha_hang(id);


--
-- Name: don_nha_hang fk_don_nha_hang_don_dat_cho; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.don_nha_hang
    ADD CONSTRAINT fk_don_nha_hang_don_dat_cho FOREIGN KEY (id) REFERENCES public.don_dat_cho(id);


--
-- Name: combo_mon_an fkax6lqktnnmxt0t73aicbf3qch; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.combo_mon_an
    ADD CONSTRAINT fkax6lqktnnmxt0t73aicbf3qch FOREIGN KEY (combo_id) REFERENCES public.combo(id);


--
-- Name: ho_so_kinh_doanh fkb3prg0yr1hyacbr1veyyu7bse; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ho_so_kinh_doanh
    ADD CONSTRAINT fkb3prg0yr1hyacbr1veyyu7bse FOREIGN KEY (doi_tac_id) REFERENCES public.doi_tac(id);


--
-- Name: users fkb753wf97aoi9bimfusk61td68; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkb753wf97aoi9bimfusk61td68 FOREIGN KEY (vai_tro_id) REFERENCES public.vai_tro(id);


--
-- Name: mon_an_the_ngu_canh fkbpjogw608qojak6ccwuja42ug; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mon_an_the_ngu_canh
    ADD CONSTRAINT fkbpjogw608qojak6ccwuja42ug FOREIGN KEY (mon_an_id) REFERENCES public.mon_an(id);


--
-- Name: khach_san fkc79pk1bg389flyyyxvbkl0g9x; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_san
    ADD CONSTRAINT fkc79pk1bg389flyyyxvbkl0g9x FOREIGN KEY (id_tai_san) REFERENCES public.tai_san(id_tai_san);


--
-- Name: anh_phong fkd71nsr106g30018j8tjl7orgt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anh_phong
    ADD CONSTRAINT fkd71nsr106g30018j8tjl7orgt FOREIGN KEY (phong_id) REFERENCES public.phong(id);


--
-- Name: combo_item fkdlm953v5gi3wsktj0dlvutnmv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.combo_item
    ADD CONSTRAINT fkdlm953v5gi3wsktj0dlvutnmv FOREIGN KEY (mon_an_id) REFERENCES public.mon_an(id);


--
-- Name: combo fkfsvwkor039d41iaodi7afg7pk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.combo
    ADD CONSTRAINT fkfsvwkor039d41iaodi7afg7pk FOREIGN KEY (thuc_don_id) REFERENCES public.thuc_don(id);


--
-- Name: combo_mon_an fkgcob3vuwb8se367oc2wtk4t3p; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.combo_mon_an
    ADD CONSTRAINT fkgcob3vuwb8se367oc2wtk4t3p FOREIGN KEY (mon_an_id) REFERENCES public.mon_an(id);


--
-- Name: nha_hang fkh70bkb4iubfyrnbu0vjrc6e5a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nha_hang
    ADD CONSTRAINT fkh70bkb4iubfyrnbu0vjrc6e5a FOREIGN KEY (id_tai_san) REFERENCES public.tai_san(id_tai_san);


--
-- Name: khach_hang_so_thich fkj2af5bxwov86ki4dx5wh3cqme; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_hang_so_thich
    ADD CONSTRAINT fkj2af5bxwov86ki4dx5wh3cqme FOREIGN KEY (so_thich_id) REFERENCES public.so_thich(id);


--
-- Name: quan_tri_vien fkjra8y9vp0sovuhlegq4yowph6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.quan_tri_vien
    ADD CONSTRAINT fkjra8y9vp0sovuhlegq4yowph6 FOREIGN KEY (id) REFERENCES public.users(id);


--
-- Name: thuc_don fkkahka4blccdxw2xrecvupda9b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.thuc_don
    ADD CONSTRAINT fkkahka4blccdxw2xrecvupda9b FOREIGN KEY (nha_hang_id) REFERENCES public.nha_hang(id_tai_san);


--
-- Name: chinh_sach fklc0jaxv4u2pq1iidrtrmtcq2e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chinh_sach
    ADD CONSTRAINT fklc0jaxv4u2pq1iidrtrmtcq2e FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES public.ho_so_kinh_doanh(id_ho_so);


--
-- Name: so_thich fklfmaxqveraumbjnyc2nrc4w8s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.so_thich
    ADD CONSTRAINT fklfmaxqveraumbjnyc2nrc4w8s FOREIGN KEY (danh_muc_id) REFERENCES public.danh_muc_so_thich(id);


--
-- Name: khach_hang_tu_khoa fkli41kd5kvxxeoylmeab3ec304; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_hang_tu_khoa
    ADD CONSTRAINT fkli41kd5kvxxeoylmeab3ec304 FOREIGN KEY (khach_hang_id) REFERENCES public.khach_hang(id);


--
-- Name: tai_san fkmm8f4kny8pm47d77r306hqnuq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tai_san
    ADD CONSTRAINT fkmm8f4kny8pm47d77r306hqnuq FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES public.ho_so_kinh_doanh(id_ho_so);


--
-- Name: khach_san_tien_ich fknpjd04nvga50gbp6cnmitdo9c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_san_tien_ich
    ADD CONSTRAINT fknpjd04nvga50gbp6cnmitdo9c FOREIGN KEY (tien_ich_id) REFERENCES public.tien_ich_khach_san(id);


--
-- Name: lich_su_thao_tac fkqj8n1r9oq0yg62evxpxd49o1s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lich_su_thao_tac
    ADD CONSTRAINT fkqj8n1r9oq0yg62evxpxd49o1s FOREIGN KEY (id_admin) REFERENCES public.quan_tri_vien(id);


--
-- Name: lich_su_kiem_duyet_ho_so fkqwi8r65kiob1bhtijkb2t1rxg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lich_su_kiem_duyet_ho_so
    ADD CONSTRAINT fkqwi8r65kiob1bhtijkb2t1rxg FOREIGN KEY (admin_id) REFERENCES public.quan_tri_vien(id);


--
-- Name: phong_tien_ich fkr94a74g9glbbjref56ysmfk48; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phong_tien_ich
    ADD CONSTRAINT fkr94a74g9glbbjref56ysmfk48 FOREIGN KEY (phong_id) REFERENCES public.phong(id);


--
-- Name: khach_san_tien_ich fkyn7okjhx1337ojkxh9d4mdsl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.khach_san_tien_ich
    ADD CONSTRAINT fkyn7okjhx1337ojkxh9d4mdsl FOREIGN KEY (khach_san_id) REFERENCES public.khach_san(id_tai_san);


--
-- PostgreSQL database dump complete
--

\unrestrict 9uFdfudB46nfoXism21HHfrS0wAqTAZtDIBXf0l0bqhK05RjtscKX9VoCTS5EsA

