# Partner onboarding và hồ sơ kinh doanh

## Mục tiêu
Xác định lại mô hình dữ liệu và luồng onboarding cho partner trong TraVi-OTA-Project.

## Quy tắc dữ liệu
- 1 Partner có thể sở hữu nhiều Hồ sơ kinh doanh.
- 1 Hồ sơ kinh doanh tương ứng đúng 1 Tài sản (Asset).
- 1 Tài sản có thể là:
  - 1 Khách sạn
  - hoặc 1 Nhà hàng
- Vì vậy, 1 Partner có thể quản lý nhiều khách sạn, nhiều nhà hàng, hoặc cả hai.

## Luồng đăng ký / đăng nhập lần đầu
### Trường hợp chưa có hồ sơ kinh doanh nào
- Sau khi partner tạo tài khoản hoặc đăng nhập lần đầu:
  - nếu chưa có hồ sơ kinh doanh,
  - hệ thống cần yêu cầu/điều hướng người dùng tạo hồ sơ kinh doanh đầu tiên.

### Trường hợp đã có ít nhất 1 hồ sơ kinh doanh
- Nếu partner đã có hồ sơ kinh doanh:
  - cho phép đi thẳng vào dashboard hoặc khu vực quản lý tài sản.

## Luồng sau khi tạo hồ sơ kinh doanh thành công
- Sau khi tạo thành công bất kỳ hồ sơ kinh doanh nào:
  - nếu hồ sơ đó là KHACH_SAN -> điều hướng sang trang khách sạn để tiếp tục cài đặt / cấu hình.
  - nếu hồ sơ đó là NHA_HANG -> điều hướng sang trang nhà hàng để tiếp tục cài đặt / cấu hình.

## Ý nghĩa UX
- Wizard tạo hồ sơ chỉ dùng để khởi tạo hồ sơ.
- Sau khi hồ sơ đã tồn tại:
  - người dùng cần được đưa sang trang quản lý asset tương ứng,
  - thay vì bị giữ lại quá lâu trong luồng onboarding.

## Hướng triển khai route gợi ý
- `/partner/dashboard`: trang tổng quan
- `/partner/business-profile`: tạo/chỉnh sửa hồ sơ kinh doanh
- `/partner/hotels`: cấu hình / quản lý khách sạn
- `/partner/restaurants`: cấu hình / quản lý nhà hàng
- `/partner/asset-management`: route container cho nhóm quản lý tài sản

## Điều cần đảm bảo khi implement
- Không giới hạn partner chỉ có 1 business profile.
- Khi tạo hồ sơ mới, backend và frontend phải hiểu rõ:
  - profile là thực thể cha
  - asset là thực thể con 1-1 của từng profile
- Sau create phải redirect theo loại tài sản tương ứng.
