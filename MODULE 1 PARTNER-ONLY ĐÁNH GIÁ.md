# MODULE 1 - QUẢN LÝ TÀI SẢN ĐỐI TÁC (PARTNER-ONLY)

Ngày cập nhật: 2026-05-29
Mục đích: Ghi nhận trạng thái hoàn thành và thay đổi phạm vi (scope) của Module 1.

## 1. MỤC TIÊU VÀ PHẠM VI (SCOPE MỚI)

Dự án TraVi-OTA đã loại bỏ hoàn toàn mô hình Admin kiểm duyệt hồ sơ đối tác. Module 1 giờ đây tập trung vào luồng **Partner-Only** (Đối tác tự quản) với các tiêu chí:
- Đối tác tự tạo, cập nhật hồ sơ kinh doanh (khách sạn/nhà hàng) và được kích hoạt ngay lập tức (auto-active).
- Đối tác quản lý danh mục tài sản: Phòng (với Khách sạn), Bàn/Thực đơn/Món ăn/Combo (với Nhà hàng).
- Ngăn chặn hard-delete dữ liệu để bảo vệ lịch sử hóa đơn/đặt phòng.
- Áp dụng Booking Guard: Không cho phép xóa (kể cả soft-delete) nếu tài sản đang gắn với một đơn đặt chỗ trong tương lai chưa hoàn tất.

---

## 2. GIAI ĐOẠN 1: DATABASE & ENTITIES (ĐÃ HOÀN THÀNH)

### 2.1. Loại bỏ các bảng & entity liên quan Admin
- Xóa bỏ hoàn toàn Entity `LichSuKiemDuyetHoSo` (Lịch sử kiểm duyệt).
- Xóa bỏ các trường liên quan đến kiểm duyệt trong Entity `HoSoKinhDoanh` (`trangThaiKiemDuyet`, `lyDoTuChoiGanNhat`).
- Xóa bỏ các Enum liên quan đến Admin (`TrangThaiKiemDuyet`, `ApprovalDecisionStatus`).

### 2.2. Áp dụng Soft Delete
- Thiết lập trường `deleted = false` mặc định cho các Entity tài sản: `Phong`, `Ban`, `MonAn`.
- Cập nhật các truy vấn trong Repository (`PhongRepository`, `BanRepository`, `MonAnRepository`, `HoSoKinhDoanhRepository`) để tự động filter các bản ghi có `deleted=false`.

### 2.3. Khởi tạo các bảng hỗ trợ Booking Guard
- Khởi tạo Entity mô phỏng đơn hàng để làm tham chiếu cho Booking Guard: `DatPhong`, `DonDatCho`, `DonDatMon`, `ChiTietDonDatMon`.
- Khởi tạo các Repository tương ứng để thực hiện câu truy vấn đếm số lượng đơn tương lai (`hasFutureBooking`).

---

## 3. GIAI ĐOẠN 2: BACKEND - PARTNER APIs (ĐÃ HOÀN THÀNH)

### 3.1. Dọn dẹp Controller & Service cũ
- Xóa hoàn toàn `AdminApprovalController.java` và `AdminApprovalService.java`.
- Xóa `BusinessApprovalMailService.java` (gửi mail khi duyệt/từ chối).
- Dọn dẹp hệ thống DTO cũ (`AdminApprovalResponse`, `ApprovalStatusRequest`, `ApprovalStatusUpdateRequest`).

### 3.2. Cập nhật luồng tạo/sửa hồ sơ (PartnerBusinessProfileService)
- Hồ sơ kinh doanh khi được tạo mới mặc định `trangThaiHoatDong = DANG_HOAT_DONG` (thay vì chờ duyệt).
- Loại bỏ logic phân biệt "trường nhạy cảm" gây reset trạng thái chờ duyệt khi update.
- Áp dụng triệt để check ownership: Đảm bảo chỉ đối tác sở hữu mới được cập nhật thông tin.

### 3.3. Áp dụng Booking Guard & Ràng buộc toàn vẹn vào tài sản
- **Phòng (Hotel):** `PartnerHotelService.deleteRoom()` sẽ ném ra `BusinessConflictException` nếu `DatPhongRepository` báo có đơn đặt phòng tương lai đang gắn với phòng này. Nếu an toàn, áp dụng soft-delete.
- **Bàn & Món ăn (Restaurant):** Tương tự, `deleteTable()` và `deleteMenuItem()` kiểm tra `DonDatCho` và `DonDatMon` trước khi soft-delete.
- Cập nhật payload `PhongRequest` khớp với ràng buộc `NotNull` và `Check Constraint` của database (ví dụ: `trangThai` phải thuộc tập `[SAN_SANG, DANG_SU_DUNG...]` thay vì `DANG_BAN`).
- Bổ sung cấu hình multipart giới hạn tải ảnh lên 10MB (`application-dev.properties`).

---

## 4. GIAI ĐOẠN 3: FRONTEND - DỌN DẸP & NÂNG CẤP UI (ĐÃ HOÀN THÀNH)

### 4.1. Dọn dẹp tàn dư Admin
- Xóa các màn hình và route liên quan: `AdminApprovalsPage.tsx`, `AdminLogin.tsx`, `AdminRoute.tsx`.
- Gỡ bỏ tab chuyển hướng "Quản trị" khỏi giao diện đăng nhập chung (`LoginTemplate.tsx`).
- Loại bỏ role `QUAN_TRI_VIEN` khỏi hệ thống Route Guard.
- Dọn sạch các service call API admin (`adminApprovalService.ts`).

### 4.2. Cập nhật Partner Dashboard
- Loại bỏ các hiển thị "Đang chờ duyệt" hay "Bị từ chối" trong UI tổng quan hồ sơ.
- Thay đổi thông báo sau khi lưu hồ sơ thành: *"Hồ sơ kinh doanh đã được tạo và kích hoạt thành công"*.
- Modal thêm/sửa Phòng:
  - Bổ sung đầy đủ các trường yêu cầu gửi lên API (Tên phòng, Số giường, Giá cơ bản, Số lượng phòng...).
  - Thiết lập giá trị gửi đi cho thuộc tính `trangThai` thành `SAN_SANG` để pass qua validator của Backend.
- Cải tiến Upload hình ảnh: 
  - Form trực quan dùng component Upload file thay cho text URL.
  - Xử lý mượt mà mã lỗi HTTP 413 (File too large) và hiển thị Toast nhắc nhở giới hạn 10MB rõ ràng.

---

## 5. TỔNG KẾT ĐÁNH GIÁ (DEFINITION OF DONE)

Toàn bộ yêu cầu theo scope Partner-Only đã được đáp ứng thành công. Các công đoạn đã pass qua khâu build & compile thực tế:
- Backend: `mvn compile` thành công không lỗi, pass toàn bộ unit tests.
- Frontend: `tsc -b && vite build` thành công, không còn lỗi TypeScript.
- Dữ liệu rác đã được clear, codebase gọn gàng, luồng nghiệp vụ API và Database đã đồng nhất.

Module 1 đã sẵn sàng để tích hợp với các module liên quan tới Booking và Payment tiếp theo.
