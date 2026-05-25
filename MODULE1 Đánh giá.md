# MODULE 1 - ĐỐI CHIẾU YÊU CẦU VS HIỆN TRẠNG CODEBASE

Ngày đánh giá: 2026-05-25
Nguồn yêu cầu đối chiếu: `MODULE 1 QUẢN LÝ TÀI SẢN & KIỂM DUYỆT.md`

## Phạm vi đã rà soát

- Backend: `backend/src/main/java/**`, `backend/src/main/resources/db/migration/**`, `backend/src/test/java/**`
- Frontend: `frontend/src/**` (routes, pages, services, types)
- Các file trọng tâm đã đối chiếu trực tiếp:
  - `backend/src/main/java/com/ota/travi/controller/PartnerAssetController.java`
  - `backend/src/main/java/com/ota/travi/controller/AdminApprovalController.java`
  - `backend/src/main/java/com/ota/travi/service/PartnerBusinessProfileService.java`
  - `backend/src/main/java/com/ota/travi/service/PartnerHotelService.java`
  - `backend/src/main/java/com/ota/travi/service/PartnerRestaurantService.java`
  - `backend/src/main/java/com/ota/travi/service/AdminApprovalService.java`
  - `backend/src/main/java/com/ota/travi/entity/*.java` (nhóm module 1)
  - `backend/src/main/java/com/ota/travi/repository/*.java` (nhóm module 1)
  - `frontend/src/pages/partner/PartnerDashboardPage.tsx`
  - `frontend/src/pages/admin/AdminApprovalsPage.tsx`
  - `frontend/src/services/partnerAssetService.ts`
  - `frontend/src/services/adminApprovalService.ts`
  - `frontend/src/types/asset.ts`

## Quy ước trạng thái

- **Làm được**: đã có implementation và chạy theo đúng hướng yêu cầu.
- **Chưa đúng**: có implementation nhưng lệch yêu cầu/thiếu logic bắt buộc.
- **Chưa làm**: chưa có implementation.

---

# GIAI ĐOẠN 1: DATABASE & ENTITIES

## 1.1 Entity cha `HoSoKinhDoanh`

### Làm được

- Có entity hồ sơ kinh doanh: `backend/src/main/java/com/ota/travi/entity/HoSoKinhDoanh.java`
- Có trường lõi: `idHoSo`, `doiTac`, `tenCoSo`, `sdtLienHe`, `maSoThue`, `giayPhepKinhDoanh`, `loaiDichVu`, `trangThaiKiemDuyet`, thời gian tạo/duyệt.
- Có migration bảng tương ứng: `backend/src/main/resources/db/migration/v2__module1_assets_and_approvals.sql`

### Chưa đúng

- Yêu cầu tách 2 trạng thái (`trangThaiKiemDuyet` + `trangThaiHoatDong`) nhưng code chỉ có 1 trạng thái `trangThaiKiemDuyet`.
- Enum trạng thái kiểm duyệt lệch yêu cầu:
  - File: `backend/src/main/java/com/ota/travi/enums/TrangThaiKiemDuyet.java`
  - Hiện có: `BAN_NHAP`, `CHO_DUYET`, `BI_TU_CHOI`, `DANG_HOAT_DONG`, `BI_KHOA_TAM_THOI`
  - Theo module cần: `CHO_DUYET`, `DA_DUYET`, `BI_TU_CHOI` + trạng thái hoạt động riêng.
- Thiếu nhiều field trong yêu cầu: `emailLienHe`, `diaChi`, `thanhPho`, `quanHuyen`, `phuongXa`, `kinhDo`, `viDo`, `updatedAt`, `deleted`, `lyDoTuChoiGanNhat`.
- Không dùng inheritance giữa `HoSoKinhDoanh` với `KhachSan`/`NhaHang` theo mô tả yêu cầu; hiện `KhachSan`/`NhaHang` kế thừa `TaiSan`.

## 1.2 Entity `ChinhSach`

### Làm được

- Có entity `ChinhSach`: `backend/src/main/java/com/ota/travi/entity/ChinhSach.java`
- Có quan hệ 1-1 với hồ sơ kinh doanh.
- Có migration bảng `chinh_sach`.

### Chưa đúng

- Thiết kế hiện tại là chính sách dạng generic (`loaiChinhSach`, `noiDung`, `ngayApDung`), chưa tách các trường nghiệp vụ module yêu cầu (`gioNhanPhong`, `gioTraPhong`, `gioMoCua`, `gioDongCua`, `chinhSachHuy`, `chinhSachHoanTien`, `quyDinhTreEm`, `quyDinhVatNuoi`, `ghiChuKhac`, `updatedAt`).

## 1.3 Entity con `KhachSan`, `NhaHang`

### Làm được

- Có 2 entity riêng: `KhachSan`, `NhaHang`.
- Có các trường cơ bản: tên, mô tả, giá, một số giờ vận hành, hạng sao/sức chứa.

### Chưa đúng

- Không kế thừa từ `HoSoKinhDoanh` như tài liệu; hiện kế thừa từ `TaiSan`.
- Thiếu nhiều field yêu cầu:
  - `KhachSan`: thiếu `loaiKhachSan`, `gioNhanPhongMacDinh`, `gioTraPhongMacDinh`, `soTang`, `tongSoPhong`.
  - `NhaHang`: thiếu `coDatBanTruoc`, `coDatMonTruoc`.

## 1.4 Entity vệ tinh

### A. Nhóm Khách sạn

#### Làm được

- Có `Phong`, `AnhPhong`, `TienIchKhachSan` và quan hệ chính.

#### Chưa đúng

- `Phong` thiếu các field yêu cầu: `tenPhong`, `soGiuong`, `giaCoBan` (ở phòng), `soLuongPhong`, `deleted`, `createdAt`, `updatedAt`.
- Enum trạng thái phòng lệch yêu cầu (`SAN_SANG`, `DA_DAT_TRUOC`, `DANG_SU_DUNG`, `DANG_BAO_TRI`, `DA_AN`) thay vì `DANG_BAN`, `TAM_DUNG`, `NGUNG_KINH_DOANH`.

### B. Nhóm Nhà hàng

#### Làm được

- Có `Ban`, `ThucDon`, `MonAn`, `Combo`, `AnhNhaHang`, `TienIchNhaHang` và quan hệ chính.

#### Chưa đúng

- `Ban` thiếu `tenBan`, `moTa`, `deleted`, `createdAt`, `updatedAt`.
- `ThucDon` thiếu `tenThucDon`, `trangThai`, `createdAt`, `updatedAt`.
- `MonAn` thiếu `moTa`, `danhMucMon`, `deleted`, `createdAt`, `updatedAt`; trạng thái thiếu `NGUNG_BAN`.
- `ComboItem` chưa được model thành entity riêng như yêu cầu (hiện dùng many-to-many trực tiếp giữa `Combo` và `MonAn`).
- `Combo` thiếu `createdAt`, `updatedAt`.

## 1.5 Entity lịch sử kiểm duyệt

### Chưa làm

- Chưa có entity `LichSuKiemDuyetHoSo`.
- Chưa có bảng migration tương ứng.
- Chưa có logic lưu audit ai duyệt/khi nào/lý do.

## 1.6 Repositories & DTOs

### Làm được

- Có hầu hết repository chính: `HoSoKinhDoanhRepository`, `KhachSanRepository`, `NhaHangRepository`, `PhongRepository`, `BanRepository`, `ThucDonRepository`, `MonAnRepository`, `ComboRepository`, `ChinhSachRepository`.
- Có hệ DTO request/response cho nghiệp vụ Partner/Admin.
- Có tách DTO ra khỏi entity khi expose API.

### Chưa đúng

- Thiếu `LichSuKiemDuyetHoSoRepository`.
- Bộ DTO tên/shape khác tài liệu bắt buộc (không có đúng các DTO như `BusinessProfileCreateRequest`, `ApprovalStatusUpdateRequest`, ... mà dùng `PartnerBusinessProfileRequest`, `ApprovalStatusRequest`, v.v.).

---

# GIAI ĐOẠN 2: BACKEND - PARTNER APIs

## 2.1 Tạo hồ sơ kinh doanh (`POST /api/v1/partner/business-profiles`)

### Làm được

- Endpoint đã có: `PartnerAssetController#createBusinessProfile`.
- Có check role qua `@PreAuthorize("hasRole('DOI_TAC')")` và Security config.
- Có kiểm tra trùng MST (`existsByMaSoThue`).
- Có tạo hotel/restaurant tùy `loaiDichVu`.
- Có lưu ảnh/tiện ích/chính sách từ payload.

### Chưa đúng

- Không set `trangThaiHoatDong = CHUA_HOAT_DONG` (do chưa có field trạng thái hoạt động).
- Response DTO không đúng naming theo tài liệu.

## 2.2 Cập nhật hồ sơ kinh doanh (`PUT /api/v1/partner/business-profiles/{id}`)

### Làm được

- Có endpoint update.
- Có check tồn tại hồ sơ.
- Có check ownership theo partner.
- Có cơ chế detect một phần field nhạy cảm và đẩy lại `CHO_DUYET`.

### Chưa đúng

- Mới coi `maSoThue` và `giayPhepKinhDoanh` là nhạy cảm; thiếu `ten pháp lý cơ sở`, `địa chỉ`, `loại hình kinh doanh`.
- Không có `trangThaiHoatDong` để set `CHUA_HOAT_DONG`/`TAM_DUNG`.
- `notifyAdminProfileNeedsReview` còn TODO, chưa gửi thông báo thật.

## 2.3 Quản lý khách sạn - phòng

### Làm được

- Có endpoint tạo/cập nhật/xóa/lấy danh sách phòng.
- Có check khách sạn thuộc partner hiện tại.
- Có check trùng số phòng trong cùng khách sạn.

### Chưa đúng

- Chưa check điều kiện hồ sơ/hotel đã duyệt trước khi cho tạo phòng.
- Mặc định trạng thái phòng đang dùng `SAN_SANG` thay vì `DANG_BAN`.
- Xóa phòng đang **hard delete** (`phongRepository.delete`) thay vì soft delete theo yêu cầu.
- Check booking tương lai mới là TODO stub (`hasFutureBookingForRoom` luôn `false`).
- Chưa có cơ chế khóa update field ảnh hưởng booking tương lai.

## 2.4 Quản lý nhà hàng - bàn

### Làm được

- Có endpoint tạo/cập nhật/xóa/lấy danh sách bàn.
- Có check nhà hàng thuộc partner hiện tại.

### Chưa đúng

- Đang dùng trạng thái số nguyên `Integer trangThai`, chưa enum nghiệp vụ rõ ràng theo yêu cầu.
- Xóa bàn đang hard delete.
- Chưa có check bàn nằm trong đơn đặt tương lai.
- Chưa soft delete (`deleted`, `NGUNG_SU_DUNG`) theo yêu cầu.

## 2.5 Quản lý nhà hàng - thực đơn và món ăn

### Làm được

- Có endpoint tạo/cập nhật/xóa/lấy món ăn.
- Có endpoint patch trạng thái món.
- Có check ownership nhà hàng.

### Chưa đúng

- Thiếu endpoint tạo thực đơn riêng: `POST /api/v1/partner/restaurants/{restaurantId}/menus`.
- Đang tạo món vào thực đơn mặc định tự sinh (`MAC_DINH`), không theo mô hình quản lý nhiều thực đơn đầy đủ.
- Trạng thái món dùng `CO_SAN|TAM_HET`, lệch spec `DANG_BAN|TAM_HET|NGUNG_BAN`.
- Xóa món đang hard delete, không soft delete.
- Check món có đơn đặt trước chỉ là TODO stub (`hasPreorderedMenuItem` luôn `false`).

---

# GIAI ĐOẠN 3: BACKEND - ADMIN APIs

## 3.1 Danh sách hồ sơ chờ duyệt (`GET /api/v1/admin/approvals/pending`)

### Làm được

- Có endpoint pending approvals.
- Có giới hạn role admin bằng `@PreAuthorize` + Security config.
- Có filter theo `CHO_DUYET` ở service.

### Chưa đúng

- Chưa có pagination/sort theo query (`page`, `size`, `sort`).
- Chưa filter `deleted=false` (vì chưa có soft delete profile).

## 3.2 Chi tiết hồ sơ cần duyệt (`GET /api/v1/admin/approvals/{id}`)

### Làm được

- Có endpoint chi tiết.
- Có trả dữ liệu đối tác, hồ sơ cơ bản, chính sách, tài sản, comparison old/new.

### Chưa đúng

- Thiếu nhiều thông tin yêu cầu: địa chỉ cấu trúc, ảnh cơ sở chi tiết theo nghiệp vụ duyệt, `lyDoTuChoiGanNhat`, `lịch sử kiểm duyệt`.

## 3.3 Phê duyệt / từ chối (`PUT /api/v1/admin/approvals/{id}/status`)

### Làm được

- Có endpoint approve/reject.
- Có bắt buộc reason khi reject.
- Có gửi email async cho cả approve/reject (`BusinessApprovalMailService` dùng `@Async`).

### Chưa đúng

- Logic approve set sai trạng thái: `trangThaiKiemDuyet = DANG_HOAT_DONG` thay vì `DA_DUYET`.
- Không có `trangThaiHoatDong = DANG_HOAT_DONG` riêng.
- Không có lưu `lyDoTuChoiGanNhat`.
- Không có ghi `LichSuKiemDuyetHoSo`.

---

# GIAI ĐOẠN 4: FRONTEND - PARTNER DASHBOARD

## 4.1 Form tạo/cập nhật hồ sơ kinh doanh (multi-step)

### Làm được

- Có màn hình Partner Dashboard lớn, có luồng tạo/cập nhật profile.
- Có 3 bước đầu:
  - Bước 1: thông tin cơ bản.
  - Bước 2: giấy tờ pháp lý + upload.
  - Bước 3: bản đồ Leaflet chọn tọa độ.
- Có validate client cho số điện thoại, mã số thuế, mô tả, tọa độ, file upload.

### Chưa đúng

- Thiếu Bước 4 (Ảnh & tiện ích) theo thiết kế module.
- Thiếu Bước 5 (Chính sách chi tiết) theo thiết kế module.
- Thiếu các field địa chỉ chi tiết (thành phố/quận/phường/địa chỉ).

## 4.2 Giao diện quản lý khách sạn (Room Management)

### Làm được

- Có trang quản lý phòng trong `PartnerDashboardPage`.
- Có tìm kiếm, thêm/sửa, upload ảnh phòng.
- Có xóa phòng từ UI.

### Chưa đúng

- Chưa có lọc trạng thái thật sự (button Filters chưa nối logic).
- Trạng thái UI/backend chưa theo enum yêu cầu module.
- Chưa có cảnh báo đúng case booking tương lai (backend chưa implement check thật).
- Chưa soft delete đúng chuẩn module (backend hard delete).

## 4.3 Giao diện quản lý nhà hàng

### Làm được

- Có khu quản lý bàn.
- Có khu quản lý món ăn.
- Có toggle trạng thái món gọi API patch.
- Có quản lý combo.

### Chưa đúng

- Chưa có quản lý thực đơn đầy đủ theo nhiều menu.
- Trạng thái món dùng `CO_SAN/TAM_HET`, không khớp chuẩn module.
- Xóa bàn/món vẫn phụ thuộc backend hard delete.

---

# GIAI ĐOẠN 5: FRONTEND - ADMIN DASHBOARD

## 5.1 Bảng danh sách chờ duyệt

### Làm được

- Có trang admin duyệt hồ sơ dùng Ant Design Table.
- Có search/filter loại dịch vụ/loại yêu cầu.
- Có pagination phía client table.
- Có action xem chi tiết.

### Chưa đúng

- Route đang mount ở `/admin` thay vì `/admin/approvals`.
- Chưa gọi API phân trang server-side đúng spec.
- Cột dữ liệu chưa đầy đủ theo tài liệu (ví dụ thành phố chưa có do backend chưa có field).

## 5.2 Trang/Modal kiểm duyệt chi tiết

### Làm được

- Có modal chi tiết kiểm duyệt.
- Có preview giấy phép + zoom/pan/rotate.
- Có so sánh Old vs New.
- Có nút phê duyệt/từ chối, popup nhập lý do từ chối.

### Chưa đúng

- Không phải route riêng `/admin/approvals/{id}` mà là modal trong cùng page.
- Thiếu hiển thị lịch sử kiểm duyệt thực tế (backend chưa có).

---

# GIAI ĐOẠN 6: VALIDATION, SECURITY & TESTING

## 6.1 Validation Backend

### Làm được

- Có nhiều validate bằng Bean Validation DTO:
  - Tên cơ sở.
  - Mã số thuế không rỗng.
  - Loại hình dịch vụ.
  - SĐT.
  - Giá phòng/món ăn > 0.
  - Sức chứa/số ghế >= 1.
  - Reason bắt buộc khi reject.

### Chưa đúng

- Chưa validate format tọa độ GPS ở backend (chỉ check not blank/size).
- Một số enum trạng thái không theo chuẩn module nên validate nghiệp vụ chưa sát tài liệu.

## 6.2 Security

### Làm được

- Có phân quyền role ở Security config (`DOI_TAC`, `QUAN_TRI_VIEN`).
- Có check ownership tài nguyên ở các service partner (hotel/restaurant/profile).

### Chưa đúng

- Chưa có cơ chế soft delete + filter deleted thống nhất nên còn hở case truy xuất dữ liệu đã xóa (vì đang hard delete).

## 6.3 Testing Backend

### Làm được

- Có `contextLoads` test.
- Có `AuthServiceTest` cho auth/otp/google.

### Chưa làm

- Chưa có unit test cho các nghiệp vụ module 1 yêu cầu:
  - create profile thành công/trùng MST.
  - update thường vs update nhạy cảm.
  - approve/reject.
  - ownership.
  - delete room/menu item có booking tương lai.
- Chưa có integration test cho các API module 1 đã liệt kê.

---

# ĐÁNH GIÁ DEFINITION OF DONE

## Làm được

- Đối tác tạo được hồ sơ khách sạn/nhà hàng.
- Hồ sơ mới mặc định `CHO_DUYET`.
- Admin xem được danh sách hồ sơ chờ duyệt.
- Admin xem được chi tiết hồ sơ.
- Admin phê duyệt được hồ sơ.
- Admin từ chối được hồ sơ kèm lý do.
- Partner quản lý được phòng khách sạn (mức cơ bản).
- Partner quản lý được bàn, món ăn, combo nhà hàng (mức cơ bản).
- Frontend Partner có form tạo/cập nhật hồ sơ (nhưng mới 3 bước).
- Frontend Admin có màn hình duyệt hồ sơ.

## Chưa đúng

- Partner cập nhật hồ sơ nhạy cảm quay lại chờ duyệt: **mới đúng một phần** (chỉ bắt 2 field nhạy cảm).
- Không thể xóa phòng/món/bàn nếu có đơn tương lai: **chưa đúng** (logic đang TODO/hard delete).
- Các API partner kiểm tra quyền sở hữu: **đúng phần lớn**, nhưng nghiệp vụ soft delete/audit chưa hoàn chỉnh.

## Chưa làm

- Unit test cho nghiệp vụ kiểm duyệt và quyền sở hữu.
- Integration test cho các API chính module 1.

---

# Kết luận tổng quan

- **Mức độ hoàn thành Module 1 (ước lượng): ~55-65%**.
- **Đã có khung khá tốt**: entity/repository/service/controller, dashboard partner/admin, upload file, approval flow cơ bản.
- **Các khoảng trống lớn cần xử lý để "đúng spec"**:
  - Chuẩn hóa domain model trạng thái (duyệt vs hoạt động).
  - Bổ sung soft delete + booking guard thực tế.
  - Bổ sung lịch sử kiểm duyệt/audit.
  - Hoàn thiện frontend form 5 bước đúng tài liệu.
  - Bổ sung test module 1 (unit + integration).

