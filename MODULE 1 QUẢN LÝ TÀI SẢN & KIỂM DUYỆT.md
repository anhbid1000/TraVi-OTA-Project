# 🏢 MODULE 1: QUẢN LÝ TÀI SẢN & KIỂM DUYỆT

## Mục tiêu Module

Xây dựng nền tảng dữ liệu dịch vụ cho hệ thống TraVi OTA, cho phép **Đối tác** đăng ký và quản lý hồ sơ kinh doanh khách sạn/nhà hàng, đồng thời cho phép **Admin** kiểm duyệt hồ sơ trước khi dịch vụ được hiển thị công khai trên hệ thống.

Module này đóng vai trò "người gác cổng" cho toàn bộ hệ thống OTA:

- Đối tác chỉ được kinh doanh khi hồ sơ đã được duyệt.
- Khách sạn/Nhà hàng chỉ xuất hiện trên Catalog khi đang hoạt động.
- Admin có quyền phê duyệt, từ chối, yêu cầu sửa đổi và theo dõi lịch sử kiểm duyệt.
- Dữ liệu phòng, bàn, thực đơn, món ăn được quản lý tập trung để phục vụ các module sau: Search, Booking, Payment, Voucher, Feedback.

---

# GIAI ĐOẠN 1: DATABASE & ENTITIES

## 1.1. Khởi tạo Entity cha: `HoSoKinhDoanh`

### Mục tiêu

Tạo lớp cha đại diện cho hồ sơ kinh doanh chung của đối tác. Hai loại hình kinh doanh chính là:

- `KhachSan`
- `NhaHang`

Sử dụng JPA inheritance strategy:

```java
```

### Entity chính: `HoSoKinhDoanh`

Các field đề xuất:

- `id`
- `doiTacId`
- `tenCoSo`
- `moTa`
- `soDienThoai`
- `emailLienHe`
- `diaChi`
- `thanhPho`
- `quanHuyen`
- `phuongXa`
- `kinhDo`
- `viDo`
- `maSoThue`
- `giayPhepKinhDoanhUrl`
- `loaiHinhKinhDoanh`
  - `KHACH_SAN`
  - `NHA_HANG`
- `trangThaiKiemDuyet`
  - `CHO_DUYET`
  - `DA_DUYET`
  - `BI_TU_CHOI`
- `trangThaiHoatDong`
  - `CHUA_HOAT_DONG`
  - `DANG_HOAT_DONG`
  - `TAM_DUNG`
  - `BI_KHOA`
- `lyDoTuChoiGanNhat`
- `createdAt`
- `updatedAt`
- `deleted`

### Ghi chú quan trọng

Không nên chỉ dùng một trạng thái duy nhất. Cần tách:

- `trangThaiKiemDuyet`: phục vụ Admin duyệt hồ sơ
- `trangThaiHoatDong`: phục vụ việc hiển thị/vận hành dịch vụ

Ví dụ:

- Hồ sơ đã duyệt nhưng đối tác tạm đóng cửa → `DA_DUYET` + `TAM_DUNG`
- Hồ sơ bị Admin khóa do vi phạm → `DA_DUYET` + `BI_KHOA`

---

## 1.2. Khởi tạo Entity `ChinhSach`

### Mục tiêu

Lưu các chính sách kinh doanh của từng cơ sở, phục vụ Module Booking và Hủy/Hoàn tiền sau này.

### Entity: `ChinhSach`

Các field đề xuất:

- `id`
- `hoSoKinhDoanhId`
- `gioNhanPhong`
- `gioTraPhong`
- `gioMoCua`
- `gioDongCua`
- `chinhSachHuy`
- `chinhSachHoanTien`
- `quyDinhTreEm`
- `quyDinhVatNuoi`
- `ghiChuKhac`
- `createdAt`
- `updatedAt`

---

## 1.3. Khởi tạo Entity con: `KhachSan` và `NhaHang`

### `KhachSan`

Kế thừa từ `HoSoKinhDoanh`.

Các field riêng:

- `hangSao`
- `loaiKhachSan`
- `gioNhanPhongMacDinh`
- `gioTraPhongMacDinh`
- `soTang`
- `tongSoPhong`

### `NhaHang`

Kế thừa từ `HoSoKinhDoanh`.

Các field riêng:

- `loaiAmThuc`
- `sucChuaToiDa`
- `gioMoCua`
- `gioDongCua`
- `coDatBanTruoc`
- `coDatMonTruoc`

---

## 1.4. Khởi tạo Entity vệ tinh

### A. Nhóm Khách sạn

#### `Phong`

Đại diện cho loại phòng hoặc phòng kinh doanh của khách sạn.

Các field đề xuất:

- `id`
- `khachSanId`
- `tenPhong`
- `loaiPhong`
- `moTa`
- `dienTich`
- `soKhachToiDa`
- `soGiuong`
- `giaCoBan`
- `soLuongPhong`
- `trangThai`
  - `DANG_BAN`
  - `TAM_DUNG`
  - `NGUNG_KINH_DOANH`
- `deleted`
- `createdAt`
- `updatedAt`

#### `AnhPhong`

- `id`
- `phongId`
- `url`
- `thuTuHienThi`
- `laAnhDaiDien`

#### `TienIchKhachSan`

- `id`
- `tenTienIch`
- `icon`
- `moTa`

#### Quan hệ

- `KhachSan` 1-n `Phong`
- `Phong` 1-n `AnhPhong`
- `KhachSan` n-n `TienIchKhachSan`
- `Phong` n-n `TienIchKhachSan` hoặc tạo riêng `TienIchPhong` nếu muốn chi tiết hơn

---

### B. Nhóm Nhà hàng

#### `Ban`

Đại diện cho bàn hoặc nhóm bàn trong nhà hàng.

Các field đề xuất:

- `id`
- `nhaHangId`
- `tenBan`
- `soGhe`
- `viTri`
- `moTa`
- `trangThai`
  - `SAN_SANG`
  - `TAM_DUNG`
  - `NGUNG_SU_DUNG`
- `deleted`
- `createdAt`
- `updatedAt`

#### `ThucDon`

- `id`
- `nhaHangId`
- `tenThucDon`
- `moTa`
- `trangThai`
  - `DANG_HIEN_THI`
  - `TAM_AN`
- `createdAt`
- `updatedAt`

#### `MonAn`

- `id`
- `thucDonId`
- `tenMon`
- `moTa`
- `gia`
- `danhMucMon`
- `anhMonUrl`
- `trangThai`
  - `DANG_BAN`
  - `TAM_HET`
  - `NGUNG_BAN`
- `deleted`
- `createdAt`
- `updatedAt`

#### `Combo`

- `id`
- `nhaHangId`
- `tenCombo`
- `moTa`
- `giaCombo`
- `trangThai`
- `createdAt`
- `updatedAt`

#### `ComboItem`

- `id`
- `comboId`
- `monAnId`
- `soLuong`

#### `AnhNhaHang`

- `id`
- `nhaHangId`
- `url`
- `thuTuHienThi`
- `laAnhDaiDien`

#### `TienIchNhaHang`

- `id`
- `tenTienIch`
- `icon`
- `moTa`

#### Quan hệ

- `NhaHang` 1-n `Ban`
- `NhaHang` 1-n `ThucDon`
- `ThucDon` 1-n `MonAn`
- `NhaHang` 1-n `Combo`
- `Combo` 1-n `ComboItem`
- `NhaHang` 1-n `AnhNhaHang`
- `NhaHang` n-n `TienIchNhaHang`

---

## 1.5. Entity lịch sử kiểm duyệt

### Mục tiêu

Lưu lại toàn bộ lịch sử Admin duyệt/từ chối hồ sơ.

### Entity: `LichSuKiemDuyetHoSo`

Các field đề xuất:

- `id`
- `hoSoKinhDoanhId`
- `adminId`
- `trangThaiCu`
- `trangThaiMoi`
- `lyDo`
- `ghiChuNoiBo`
- `createdAt`

### Lý do cần có

- Biết ai đã duyệt hồ sơ.
- Biết thời điểm duyệt.
- Biết lý do từ chối.
- Hỗ trợ audit và xử lý tranh chấp sau này.

---

## 1.6. Repositories & DTOs

### Repository cần tạo

- `HoSoKinhDoanhRepository`
- `KhachSanRepository`
- `NhaHangRepository`
- `PhongRepository`
- `BanRepository`
- `ThucDonRepository`
- `MonAnRepository`
- `ComboRepository`
- `ChinhSachRepository`
- `LichSuKiemDuyetHoSoRepository`

### DTO bắt buộc

Không nhận Entity trực tiếp từ API.

DTO đề xuất:

- `BusinessProfileCreateRequest`
- `BusinessProfileUpdateRequest`
- `BusinessProfileResponse`
- `BusinessProfileApprovalResponse`
- `ApprovalStatusUpdateRequest`
- `HotelCreateRequest`
- `RestaurantCreateRequest`
- `RoomCreateRequest`
- `RoomUpdateRequest`
- `TableCreateRequest`
- `MenuCreateRequest`
- `MenuItemCreateRequest`
- `MenuItemUpdateRequest`
- `PolicyRequest`
- `PolicyResponse`

### Lý do dùng DTO

- Tránh lộ dữ liệu nhạy cảm.
- Tránh lỗi `LazyInitializationException`.
- Dễ validate input.
- Dễ kiểm soát response trả về frontend.
- Tách API contract khỏi database model.

---

# GIAI ĐOẠN 2: BACKEND - PARTNER APIs

## 2.1. API tạo hồ sơ kinh doanh

### Endpoint

```
POST /api/v1/partner/business-profiles
```

### Mục tiêu

Cho phép đối tác tạo hồ sơ khách sạn hoặc nhà hàng.

### Request

Nhận DTO chứa:

- Thông tin cơ bản
- Loại hình kinh doanh
- Mã số thuế
- Giấy phép kinh doanh
- Ảnh cơ sở
- Tiện ích
- Chính sách cơ bản

### Logic xử lý

- Lấy `currentUser` từ `SecurityContext`.
- Kiểm tra user có role `DOI_TAC`.
- Kiểm tra `maSoThue` có bị trùng không.
- Tạo entity `KhachSan` hoặc `NhaHang` theo `loaiHinhKinhDoanh`.
- Set mặc định:
  - `trangThaiKiemDuyet = CHO_DUYET`
  - `trangThaiHoatDong = CHUA_HOAT_DONG`
  - `deleted = false`
- Lưu hồ sơ.
- Lưu ảnh, tiện ích, chính sách nếu có.
- Trả về `BusinessProfileResponse`.

---

## 2.2. API cập nhật hồ sơ kinh doanh

### Endpoint

```
PUT /api/v1/partner/business-profiles/{id}
```

### Mục tiêu

Cho phép đối tác cập nhật hồ sơ kinh doanh.

### Logic bắt buộc

- Kiểm tra hồ sơ có tồn tại không.
- Kiểm tra hồ sơ có thuộc về partner hiện tại không.
- So sánh DTO mới với Entity cũ.

### Trường không nhạy cảm

Nếu chỉ thay đổi:

- Mô tả
- Ảnh
- Tiện ích
- Số điện thoại
- Email liên hệ
- Chính sách thông thường

Thì lưu ngay, không cần duyệt lại.

### Trường nhạy cảm

Nếu thay đổi:

- Mã số thuế
- Giấy phép kinh doanh
- Tên pháp lý cơ sở
- Địa chỉ kinh doanh chính
- Loại hình kinh doanh

Thì:

- `trangThaiKiemDuyet = CHO_DUYET`
- `trangThaiHoatDong = CHUA_HOAT_DONG` hoặc `TAM_DUNG`

Đồng thời tạo thông báo cho Admin duyệt lại.

---

## 2.3. API quản lý khách sạn - phòng

### Tạo phòng

```
POST /api/v1/partner/hotels/{hotelId}/rooms
```

**Logic:**

- Kiểm tra khách sạn tồn tại.
- Kiểm tra khách sạn thuộc partner hiện tại.
- Kiểm tra khách sạn đã được duyệt hoặc cho phép tạo nháp trước duyệt.
- Tạo phòng kèm ảnh và tiện ích.
- Set trạng thái mặc định:
  - `trangThai = DANG_BAN`
  - `deleted = false`

### Cập nhật phòng

```
PUT /api/v1/partner/hotels/{hotelId}/rooms/{roomId}
```

**Logic:**

- Kiểm tra quyền sở hữu.
- Cập nhật thông tin phòng.
- Không cho sửa các field gây sai lệch đơn đã đặt nếu đang có booking tương lai, trừ khi có chính sách riêng.

### Xóa phòng

```
DELETE /api/v1/partner/hotels/{hotelId}/rooms/{roomId}
```

**Logic bảo vệ:**

Không hard delete nếu phòng đã từng phát sinh đơn.

- Nếu có đơn tương lai liên quan đến phòng: Throw lỗi: `Không thể xóa phòng vì đang có đơn đặt trong tương lai.`
- Nếu không có đơn tương lai:
  - `deleted = true`
  - `trangThai = NGUNG_KINH_DOANH`

---

## 2.4. API quản lý nhà hàng - bàn

### Tạo bàn

```
POST /api/v1/partner/restaurants/{restaurantId}/tables
```

**Logic:**

- Kiểm tra nhà hàng tồn tại.
- Kiểm tra nhà hàng thuộc partner hiện tại.
- Tạo bàn với số ghế, vị trí, mô tả.
- Set trạng thái mặc định:
  - `trangThai = SAN_SANG`
  - `deleted = false`

### Cập nhật bàn

```
PUT /api/v1/partner/restaurants/{restaurantId}/tables/{tableId}
```

### Xóa bàn

```
DELETE /api/v1/partner/restaurants/{restaurantId}/tables/{tableId}
```

**Logic bảo vệ:**

- Không xóa nếu bàn đang nằm trong đơn đặt bàn tương lai.
- Nếu được phép xóa thì soft delete:
  - `deleted = true`
  - `trangThai = NGUNG_SU_DUNG`

---

## 2.5. API quản lý nhà hàng - thực đơn và món ăn

### Tạo thực đơn

```
POST /api/v1/partner/restaurants/{restaurantId}/menus
```

### Tạo món ăn

```
POST /api/v1/partner/restaurants/{restaurantId}/menu-items
```

**Logic:**

- Kiểm tra nhà hàng thuộc partner hiện tại.
- Kiểm tra thực đơn tồn tại.
- Tạo món ăn.
- Set mặc định:
  - `trangThai = DANG_BAN`
  - `deleted = false`

### Cập nhật món ăn

```
PUT /api/v1/partner/restaurants/{restaurantId}/menu-items/{itemId}
```

### Xóa món ăn

```
DELETE /api/v1/partner/restaurants/{restaurantId}/menu-items/{itemId}
```

**Logic bảo vệ:**

- Nếu món ăn đang nằm trong đơn đặt món trước: Throw lỗi: `Không thể xóa vì đang có đơn khách đặt món này.`
- Nếu không còn đơn tương lai:
  - `deleted = true`
  - `trangThai = NGUNG_BAN`

### Cập nhật trạng thái món khẩn cấp

```
PATCH /api/v1/partner/restaurants/{restaurantId}/menu-items/{itemId}/status
```

**Request:**

```json
{
  "status": "DANG_BAN | TAM_HET | NGUNG_BAN"
}
```

**Mục tiêu:** Cho phép partner ẩn món ngay lập tức trên app khách khi món tạm hết.

---

# GIAI ĐOẠN 3: BACKEND - ADMIN APIs

## 3.1. API xem danh sách hồ sơ chờ duyệt

```
GET /api/v1/admin/approvals/pending
```

### Logic

- Chỉ role `QUAN_TRI_VIEN` được truy cập.
- Lấy danh sách `HoSoKinhDoanh` có:
  - `trangThaiKiemDuyet = CHO_DUYET`
  - `deleted = false`
- Hỗ trợ pagination: `?page=0&size=10&sort=createdAt,desc`

---

## 3.2. API xem chi tiết hồ sơ cần duyệt

```
GET /api/v1/admin/approvals/{id}
```

### Response cần có

- Thông tin cơ bản
- Thông tin đối tác sở hữu
- Địa chỉ
- Tọa độ
- Mã số thuế
- Ảnh giấy phép kinh doanh
- Ảnh cơ sở
- Tiện ích
- Chính sách
- Trạng thái hiện tại
- Lý do từ chối gần nhất
- Lịch sử kiểm duyệt

---

## 3.3. API phê duyệt / từ chối hồ sơ

```
PUT /api/v1/admin/approvals/{id}/status
```

### Request

```json
{
  "status": "APPROVED | REJECTED",
  "reason": "Ảnh giấy phép bị mờ, vui lòng tải lại ảnh rõ hơn."
}
```

### Logic APPROVED

Khi `status = APPROVED`:

- `trangThaiKiemDuyet = DA_DUYET`
- `trangThaiHoatDong = DANG_HOAT_DONG`
- `lyDoTuChoiGanNhat = null`

Sau đó:

- Ghi `LichSuKiemDuyetHoSo`.
- Gửi email chúc mừng partner bằng Spring Mail `@Async`.
- Trả về response thành công.

### Logic REJECTED

Khi `status = REJECTED`:

- Bắt buộc có `reason`.
- Nếu `reason` rỗng thì trả lỗi validation.
- Cập nhật:
  - `trangThaiKiemDuyet = BI_TU_CHOI`
  - `trangThaiHoatDong = CHUA_HOAT_DONG`
  - `lyDoTuChoiGanNhat = reason`

Sau đó:

- Ghi `LichSuKiemDuyetHoSo`.
- Gửi email thông báo lý do từ chối cho partner.
- Trả về response thành công.

---

# GIAI ĐOẠN 4: FRONTEND - PARTNER DASHBOARD

## 4.1. Form tạo/cập nhật hồ sơ kinh doanh

### Dạng UI

Multi-step form.

### Bước 1: Thông tin cơ bản

Field:

- Tên cơ sở
- Loại hình kinh doanh: Khách sạn/Nhà hàng
- Số điện thoại
- Email liên hệ
- Mô tả
- Thành phố
- Quận/Huyện
- Phường/Xã
- Địa chỉ chi tiết

### Bước 2: Giấy tờ pháp lý

Field:

- Mã số thuế
- Upload giấy phép kinh doanh
- Preview ảnh giấy phép
- Validate file ảnh/PDF nếu có

### Bước 3: Định vị bản đồ

Tích hợp:

- Leaflet hoặc Google Maps
- Partner click chọn vị trí
- Lưu latitude, longitude
- Có ô tìm kiếm địa chỉ nếu kịp làm

### Bước 4: Ảnh & tiện ích

Field:

- Upload ảnh cơ sở
- Chọn tiện ích bằng checkbox
- Preview ảnh
- Sắp xếp ảnh đại diện nếu có

### Bước 5: Chính sách

Field:

- Chính sách hủy
- Chính sách hoàn tiền
- Giờ nhận/trả phòng nếu là khách sạn
- Giờ mở/đóng cửa nếu là nhà hàng
- Ghi chú khác

---

## 4.2. Giao diện quản lý khách sạn

### Room Management Page

Chức năng:

- Hiển thị danh sách phòng bằng table.
- Tìm kiếm phòng theo tên/loại.
- Lọc theo trạng thái.
- Modal thêm phòng mới.
- Modal sửa phòng.
- Upload ảnh phòng.
- Chọn tiện ích phòng bằng checkbox.
- Soft delete phòng.
- Hiển thị cảnh báo nếu phòng không thể xóa do có booking tương lai.

### Field thêm/sửa phòng

- Tên phòng
- Loại phòng
- Mô tả
- Diện tích
- Số khách tối đa
- Số giường
- Giá cơ bản
- Số lượng phòng
- Ảnh phòng
- Tiện ích
- Trạng thái

---

## 4.3. Giao diện quản lý nhà hàng

### Table Management Page

Chức năng:

- Danh sách bàn.
- Thêm bàn.
- Sửa bàn.
- Xóa mềm bàn.
- Nhập số ghế.
- Nhập vị trí bàn.

### Menu Management Page

Chức năng:

- Danh sách thực đơn.
- Danh sách món ăn theo thực đơn.
- Thêm món.
- Sửa món.
- Xóa mềm món.
- Upload ảnh món.
- Tạo combo nếu kịp.

### Nút bật/tắt trạng thái món

Có switch: **Còn phục vụ / Tạm hết món**

Khi bật/tắt, gọi API:

```
PATCH /api/v1/partner/restaurants/{restaurantId}/menu-items/{itemId}/status
```

Mục tiêu:

- Ẩn món ngay trên app khách.
- Không cần vào form sửa món đầy đủ.

---

# GIAI ĐOẠN 5: FRONTEND - ADMIN DASHBOARD

## 5.1. Bảng danh sách chờ duyệt

### Page

`/admin/approvals`

### UI

Dùng Ant Design Table, Material Data Grid hoặc table tự custom.

### Cột đề xuất

- Tên cơ sở
- Loại hình
- Tên đối tác
- Mã số thuế
- Thành phố
- Ngày gửi
- Trạng thái
- Action: Xem chi tiết

### Tính năng

- Pagination.
- Search theo tên cơ sở/mã số thuế.
- Filter theo loại hình: Khách sạn/Nhà hàng.
- Sort theo ngày gửi mới nhất.

---

## 5.2. Trang/Modal kiểm duyệt chi tiết

### Page

`/admin/approvals/{id}`

### Layout

**Bên trái:**

- Thông tin cơ bản.
- Địa chỉ.
- Thông tin liên hệ.
- Tiện ích.
- Chính sách.

**Bên phải:**

- Ảnh giấy phép kinh doanh.
- Ảnh cơ sở.
- Preview lớn.
- Zoom/Pan để Admin soi rõ chữ.

### Nếu là hồ sơ cập nhật lại

Hiển thị dạng so sánh: **Old Data vs New Data**

Mục tiêu:

- Admin thấy trường nào đã thay đổi.
- Trường nhạy cảm được highlight.

### Action

**Nút phê duyệt:**

- Màu xanh.
- Confirm trước khi gửi.
- Gọi API approve.

**Nút từ chối:**

- Màu đỏ.
- Khi click mở popup textarea.
- Bắt buộc nhập lý do.
- Gọi API reject.

---

# GIAI ĐOẠN 6: VALIDATION, SECURITY & TESTING

## 6.1. Validation Backend

Cần validate:

- Tên cơ sở không rỗng.
- Mã số thuế không rỗng và không trùng.
- Loại hình kinh doanh hợp lệ.
- Số điện thoại hợp lệ.
- Email liên hệ hợp lệ.
- Tọa độ hợp lệ.
- Giá phòng/món ăn không âm.
- Số ghế/số khách tối đa lớn hơn 0.
- Reason bắt buộc khi từ chối hồ sơ.

---

## 6.2. Security

Tất cả Partner API phải kiểm tra:

```
currentUser.role == DOI_TAC
resource.doiTacId == currentUser.id
```

Tất cả Admin API phải kiểm tra:

```
currentUser.role == QUAN_TRI_VIEN
```

Không được để partner sửa/xóa tài nguyên của partner khác.

---

## 6.3. Testing Backend

### Unit Test

Cần test:

- Tạo hồ sơ thành công.
- Tạo hồ sơ bị trùng mã số thuế.
- Cập nhật field thường không cần duyệt lại.
- Cập nhật field nhạy cảm thì chuyển về `CHO_DUYET`.
- Admin approve thành công.
- Admin reject không có reason thì lỗi.
- Partner không thể sửa hồ sơ của partner khác.
- Không thể xóa phòng nếu có booking tương lai.
- Không thể xóa món nếu có đơn đặt món trước.

### Integration Test

Cần test:

- `POST /api/v1/partner/business-profiles`
- `PUT /api/v1/partner/business-profiles/{id}`
- `GET /api/v1/admin/approvals/pending`
- `GET /api/v1/admin/approvals/{id}`
- `PUT /api/v1/admin/approvals/{id}/status`
- `POST /api/v1/partner/hotels/{hotelId}/rooms`
- `POST /api/v1/partner/restaurants/{restaurantId}/menu-items`

---

# Definition of Done

Module 1 được xem là hoàn thành khi:

- Đối tác tạo được hồ sơ khách sạn/nhà hàng.
- Hồ sơ mới mặc định ở trạng thái `CHO_DUYET`.
- Admin xem được danh sách hồ sơ chờ duyệt.
- Admin xem được chi tiết hồ sơ.
- Admin phê duyệt được hồ sơ.
- Admin từ chối được hồ sơ kèm lý do.
- Partner cập nhật hồ sơ nhạy cảm thì hồ sơ quay lại trạng thái chờ duyệt.
- Partner quản lý được phòng khách sạn.
- Partner quản lý được bàn, thực đơn, món ăn nhà hàng.
- Không thể xóa phòng/món/bàn nếu đang có đơn tương lai liên quan.
- Các API partner đều kiểm tra quyền sở hữu tài nguyên.
- Frontend Partner có form tạo/cập nhật hồ sơ.
- Frontend Admin có màn hình duyệt hồ sơ.
- Có unit test cho nghiệp vụ kiểm duyệt và quyền sở hữu.
- Có integration test cho các API chính.
