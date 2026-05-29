# MODULE 1 - ĐÁNH GIÁ TRẠNG THÁI HOÀN THIỆN (SCOPE PARTNER-ONLY)

Ngày cập nhật: 2026-05-29
Mục đích: Ghi nhận trạng thái hoàn thiện của Module 1 sau khi thay đổi phạm vi (bỏ luồng Admin approval, chuyển sang luồng Partner-only).

## Phạm vi thay đổi (Scope mới)

- Loại bỏ hoàn toàn mô hình Admin kiểm duyệt hồ sơ.
- Hệ thống hoạt động theo mô hình Partner-Only (Đối tác tự tạo, tự cập nhật hồ sơ kinh doanh và tự quản lý tài sản, hệ thống kích hoạt tự động).
- Yêu cầu bổ sung: Áp dụng Xóa mềm (Soft delete) và Lá chắn an toàn (Booking Guard) để bảo vệ dữ liệu lịch sử hóa đơn/đặt phòng.

## Quy ước trạng thái

- **Đã hoàn thành**: Đã có implementation, chạy đúng theo scope mới và pass build/test thực tế.

---

# GIAI ĐOẠN 1: DATABASE & ENTITIES

## 1.1 Loại bỏ các thành phần Admin
### Đã hoàn thành
- Xóa bỏ bảng và entity `LichSuKiemDuyetHoSo`.
- Xóa bỏ các trường liên quan đến kiểm duyệt trong entity `HoSoKinhDoanh` (`trangThaiKiemDuyet`, `lyDoTuChoiGanNhat`).
- Xóa bỏ các enum phục vụ Admin (`TrangThaiKiemDuyet`, `ApprovalDecisionStatus`).

## 1.2 Áp dụng Soft Delete & Booking Guard
### Đã hoàn thành
- Thêm trường `deleted = false` làm mặc định cho các Entity tài sản (`Phong`, `Ban`, `MonAn`).
- Cập nhật các truy vấn trong Repository (`PhongRepository`, `BanRepository`, `MonAnRepository`, `HoSoKinhDoanhRepository`) để tự động lọc các bản ghi `deleted=false`.
- Khởi tạo các Entity mô phỏng đơn hàng (`DatPhong`, `DonDatCho`, `DonDatMon`, `ChiTietDonDatMon`) làm tham chiếu cho Booking Guard.
- Tạo các Repository tương ứng với hàm đếm đơn hàng trong tương lai (`hasFutureBooking`).

---

# GIAI ĐOẠN 2: BACKEND - PARTNER APIs

## 2.1 Dọn dẹp Controller & Service Admin
### Đã hoàn thành
- Xóa hoàn toàn `AdminApprovalController.java` và `AdminApprovalService.java`.
- Xóa `BusinessApprovalMailService.java` (gửi mail duyệt/từ chối).
- Dọn dẹp hệ thống DTO cũ (`AdminApprovalResponse`, `ApprovalStatusRequest`, `ApprovalStatusUpdateRequest`).

## 2.2 Cập nhật luồng tạo/sửa hồ sơ đối tác
### Đã hoàn thành
- `PartnerBusinessProfileService`: Hồ sơ kinh doanh khi tạo mới mặc định `trangThaiHoatDong = DANG_HOAT_DONG`.
- Loại bỏ logic phân biệt "trường nhạy cảm" gây reset về trạng thái chờ duyệt khi update.
- Áp dụng triệt để kiểm tra quyền sở hữu (ownership check): Đảm bảo chỉ đối tác sở hữu mới được phép cập nhật thông tin tài sản.

## 2.3 Áp dụng Booking Guard & Ràng buộc tài sản
### Đã hoàn thành
- `PartnerHotelService.deleteRoom()`: Kiểm tra `DatPhongRepository`, ném lỗi `BusinessConflictException` nếu có đơn đặt phòng tương lai đang gắn với phòng này. Nếu an toàn, thực hiện soft-delete.
- `PartnerRestaurantService`: Tương tự, `deleteTable()` và `deleteMenuItem()` kiểm tra `DonDatCho` và `DonDatMon` trước khi soft-delete.
- Cập nhật payload `PhongRequest` khớp với ràng buộc `NotNull` và `Check Constraint` của database (trạng thái sử dụng Enum hợp lệ như `SAN_SANG` thay vì `DANG_BAN`).
- Cấu hình backend (`application-dev.properties`) cho phép tải file (multipart) lên tới giới hạn 10MB.

---

# GIAI ĐOẠN 3: FRONTEND - PARTNER DASHBOARD & DỌN DẸP UI

## 3.1 Dọn dẹp tàn dư Admin
### Đã hoàn thành
- Xóa các màn hình và route liên quan: `AdminApprovalsPage.tsx`, `AdminLogin.tsx`, `AdminRoute.tsx`.
- Gỡ bỏ tab chuyển hướng "Quản trị" khỏi giao diện đăng nhập chung (`LoginTemplate.tsx`).
- Loại bỏ role `QUAN_TRI_VIEN` khỏi hệ thống Route Guard.
- Dọn sạch các service API admin (`adminApprovalService.ts`).

## 3.2 Cập nhật Partner Dashboard & Form quản lý
### Đã hoàn thành
- Loại bỏ các nhãn hiển thị "Đang chờ duyệt" hay "Bị từ chối" trong UI tổng quan hồ sơ.
- Thay đổi thông báo sau khi lưu hồ sơ thành: *"Hồ sơ kinh doanh đã được tạo và kích hoạt thành công"*.
- Modal thêm/sửa Phòng:
  - Bổ sung đầy đủ các trường yêu cầu gửi lên API (Tên phòng, Số giường, Giá cơ bản, Số lượng phòng...).
  - Thiết lập giá trị gửi đi cho thuộc tính `trangThai` thành `SAN_SANG` để vượt qua validation của Backend.
- Cải tiến Upload hình ảnh:
  - Nâng cấp UI thành component Upload file thay cho việc nhập URL.
  - Bắt riêng mã lỗi HTTP 413 (File too large) và hiển thị Toast nhắc nhở giới hạn 10MB thay vì gộp chung vào lỗi "Không có quyền".
- Fix triệt để các lỗi Type `any` rủi ro trong hàm cập nhật state form (`updateProfileField`), đảm bảo mã nguồn TypeScript an toàn và pass hoàn toàn khâu kiểm tra CI/Lint.

---

# ĐÁNH GIÁ DEFINITION OF DONE

## Đã hoàn thành
- Đối tác có thể tự đăng ký, tạo hồ sơ và quản lý phòng/món ăn thành công mà không cần Admin duyệt.
- Dữ liệu rác của luồng Admin đã được dọn sạch khỏi Database, Backend và Frontend.
- Mã nguồn Backend biên dịch thành công (`mvn compile`), vượt qua toàn bộ các Unit Tests cho service đối tác.
- Mã nguồn Frontend build thành công không xuất hiện lỗi Type (`tsc -b && vite build`).
- Luồng xóa tài sản được bảo vệ bằng Soft Delete và Booking Guard, không làm mất lịch sử đơn đặt chỗ.

---
