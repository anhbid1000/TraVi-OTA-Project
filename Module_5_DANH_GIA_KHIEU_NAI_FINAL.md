# ⭐ MODULE 5: ĐÁNH GIÁ & XỬ LÝ KHIẾU NẠI DỊCH VỤ

## 1. Mục tiêu Module

Xây dựng hệ thống phản hồi hai chiều giữa **Khách hàng** và **Đối tác** sau khi khách đã sử dụng dịch vụ, nhằm tăng độ tin cậy, minh bạch chất lượng và hỗ trợ xử lý sự cố trên nền tảng TraVi OTA.

Module này tập trung vào logic thực tế giữa khách hàng và đối tác, **không triển khai flow Admin** để giữ scope gọn và phù hợp với định hướng đồ án.

Điểm nâng cấp quan trọng của bản này:

```text
Complaint không chỉ là chat.
Complaint phải có bằng chứng, trao đổi, phương án xử lý và lịch sử hành động.
```

Nói cách khác, module khiếu nại cần có đủ 4 lớp:

```text
Complaint
├── Evidence / Attachment
├── Conversation / Message
├── Resolution Action
└── Activity Timeline
```

---

## 1.1. Phạm vi chính

```text
- Khách hàng đánh giá dịch vụ sau khi đơn đã hoàn tất.
- Khách hàng tạo khiếu nại liên quan đến đơn đã đặt.
- Review có thể kèm ảnh trải nghiệm thực tế.
- Complaint có thể kèm ảnh/file bằng chứng.
- Đối tác phản hồi đánh giá.
- Đối tác trao đổi khiếu nại qua message.
- Đối tác đề xuất hành động xử lý cụ thể cho khiếu nại.
- Khách hàng xác nhận hoặc từ chối phương án xử lý.
- Đối tác đánh dấu phương án đã thực hiện.
- Hệ thống lưu timeline các sự kiện xử lý.
- Hệ thống tự động lọc từ ngữ không phù hợp ở mức cơ bản.
- Review công khai được hiển thị ở trang chi tiết dịch vụ.
- Complaint/ticket là dữ liệu riêng tư, không public.
```

---

# 2. Nguyên tắc nghiệp vụ

## 2.1. Review chỉ được tạo sau khi hoàn tất đơn

Khách hàng chỉ được đánh giá khi:

```text
- Đã đăng nhập.
- Có booking/reservation thuộc về chính họ.
- Đơn ở trạng thái DA_HOAN_THANH.
- Chưa từng review cho đơn đó.
```

Không cho phép người chưa sử dụng dịch vụ đánh giá để tránh spam, rating ảo và làm giảm độ tin cậy của hệ thống.

---

## 2.2. Review phải gắn với đơn thật

Review không chỉ gắn với khách hàng và cơ sở kinh doanh, mà phải gắn với một đơn sử dụng dịch vụ cụ thể:

```text
- bookingId nếu là đặt phòng khách sạn.
- reservationId nếu là đặt bàn nhà hàng.
```

Lý do:

```text
Một khách hàng có thể sử dụng cùng một cơ sở nhiều lần.
Mỗi lần sử dụng dịch vụ có thể tạo một trải nghiệm khác nhau.
Review cần chứng minh được khách đã thật sự sử dụng dịch vụ.
```

---

## 2.3. Invariant bắt buộc cho bookingId / reservationId

Vì hệ thống có hai loại đơn khác nhau, entity Review/Complaint có thể chứa cả `bookingId` và `reservationId` dạng nullable.

Để tránh dữ liệu bẩn, bắt buộc áp dụng invariant ở cả service layer và database constraint nếu có thể.

### Rule

```text
Nếu loaiDichVu = KHACH_SAN:
    bookingId bắt buộc khác null
    reservationId bắt buộc null

Nếu loaiDichVu = NHA_HANG:
    reservationId bắt buộc khác null
    bookingId bắt buộc null
```

### Không hợp lệ

```text
bookingId = null và reservationId = null
bookingId != null và reservationId != null
loaiDichVu = KHACH_SAN nhưng reservationId != null
loaiDichVu = NHA_HANG nhưng bookingId != null
```

---

## 2.4. Partner chỉ xử lý dữ liệu thuộc cơ sở của mình

Mọi API của đối tác phải kiểm tra:

```text
businessProfile.doiTacId == currentUserId
```

Không tin `doiTacId`, `khachHangId`, `businessProfileId` gửi từ request body nếu có thể suy ra từ SecurityContext hoặc dữ liệu đơn.

---

## 2.5. Complaint không public

Complaint/ticket là kênh trao đổi riêng giữa khách hàng và đối tác.

```text
Customer:
    chỉ thấy complaint của chính mình.

Partner:
    chỉ thấy complaint thuộc cơ sở mình quản lý.

Public:
    không bao giờ thấy complaint.
```

---

## 2.6. Complaint không chỉ là nhắn tin

Nếu complaint chỉ có message thì hệ thống chỉ biết hai bên đã trao đổi, nhưng không biết vấn đề được xử lý thế nào.

Ví dụ flow thiếu:

```text
Customer:
    Phòng không giống ảnh.

Partner:
    Chúng tôi xin lỗi.

Customer:
    Tôi cần được xử lý.

Partner:
    Chúng tôi sẽ kiểm tra.
```

Vấn đề:

```text
- Có đổi phòng không?
- Có hoàn tiền không?
- Có voucher không?
- Có từ chối yêu cầu không?
- Khách có đồng ý không?
- Phương án đã làm xong chưa?
```

Vì vậy cần thêm:

```text
ComplaintResolutionAction
```

---

## 2.7. Không cho Partner tự cập nhật status tùy ý

Không nên để partner gọi API:

```http
PUT /api/v1/partner/complaints/{id}/status
```

và tự set:

```text
DA_GIAI_QUYET
DA_DONG
```

Lý do:

```text
Partner có thể đóng ticket khi chưa có phương án rõ ràng.
Hệ thống mất khả năng audit.
Customer không có quyền xác nhận.
```

Status nên được chuyển theo hành động nghiệp vụ:

```text
Partner gửi message lần đầu
    -> DANG_XU_LY

Partner tạo resolution action
    -> CHO_XAC_NHAN_KHACH

Customer accept action
    -> DANG_THUC_HIEN_PHUONG_AN

Partner complete action
    -> DA_GIAI_QUYET

Customer close complaint
    -> DA_DONG
```

---

# 3. GIAI ĐOẠN 1: BACKEND - DATABASE & ENTITIES

## 3.1. Entity `DanhGia` / `Review`

Đại diện cho đánh giá công khai của khách hàng.

### Fields đề xuất

```text
id: UUID
khachHangId: UUID
hoSoKinhDoanhId: UUID
loaiDichVu: KHACH_SAN | NHA_HANG

bookingId: UUID nullable
reservationId: UUID nullable

soSao: Integer 1-5
noiDung: Text

trangThai: DA_HIEN_THI | BI_AN

createdAt
updatedAt
```

### Constraint đề xuất

```text
UNIQUE(khachHangId, bookingId) WHERE bookingId IS NOT NULL
UNIQUE(khachHangId, reservationId) WHERE reservationId IS NOT NULL
```

### Check constraint production-minded

```sql
(
  loai_dich_vu = 'KHACH_SAN'
  AND booking_id IS NOT NULL
  AND reservation_id IS NULL
)
OR
(
  loai_dich_vu = 'NHA_HANG'
  AND reservation_id IS NOT NULL
  AND booking_id IS NULL
)
```

### Ghi chú

```text
- Chỉ review DA_HIEN_THI mới được tính vào rating trung bình.
- Review BI_AN vẫn lưu để audit nhưng không hiển thị public.
- Không hard delete review.
```

---

## 3.2. Entity `DiemThanhPhanDanhGia` / `ReviewAspectScore` - P2

Entity này không bắt buộc cho MVP, nhưng nên thiết kế để mở rộng review chuyên nghiệp hơn.

Ví dụ khách sạn:

```text
CLEANLINESS
SERVICE
LOCATION
VALUE
AMENITIES
```

Ví dụ nhà hàng:

```text
FOOD_QUALITY
SERVICE
CLEANLINESS
AMBIENCE
VALUE
```

### Fields đề xuất

```text
id: UUID
danhGiaId: UUID
aspect: CLEANLINESS | SERVICE | LOCATION | VALUE | FOOD_QUALITY | AMBIENCE | AMENITIES
score: Integer 1-5
createdAt
```

### Rule

```text
- Review tổng vẫn dùng soSao.
- Aspect score dùng để phân tích sâu hơn.
- Nếu không đủ thời gian, để P2.
```

---

## 3.3. Entity `PhanHoiDanhGia` / `ReviewReply`

Đại diện cho phản hồi của đối tác dưới một review.

### Fields đề xuất

```text
id: UUID
danhGiaId: UUID UNIQUE
doiTacId: UUID
noiDung: Text
createdAt
updatedAt
```

### Rule

```text
- Mỗi review chỉ có một phản hồi chính thức.
- Partner được phép cập nhật phản hồi.
- Không hard delete phản hồi.
- Partner reply không cần ảnh/file trong Sprint hiện tại.
```

---

## 3.4. Entity `KhieuNai` / `Complaint`

Đại diện cho ticket khiếu nại giữa khách hàng và đối tác.

### Fields đề xuất

```text
id: UUID
khachHangId: UUID
hoSoKinhDoanhId: UUID
loaiDichVu: KHACH_SAN | NHA_HANG

bookingId: UUID nullable
reservationId: UUID nullable

tieuDe: String
noiDungTomTat: Text

category:
- ROOM_QUALITY
- CLEANLINESS
- SERVICE_ATTITUDE
- BILLING
- FOOD_QUALITY
- BOOKING_PROBLEM
- FACILITY_PROBLEM
- OTHER

mucDo:
- BINH_THUONG
- NGHIEM_TRONG

trangThai:
- CHO_PHAN_HOI
- DANG_XU_LY
- CHO_XAC_NHAN_KHACH
- DANG_THUC_HIEN_PHUONG_AN
- DA_GIAI_QUYET
- DA_DONG

lastCustomerMessageAt
lastPartnerResponseAt

createdAt
updatedAt
```

### Invariant booking/reservation

Complaint áp dụng rule giống Review:

```text
KHACH_SAN -> bookingId required, reservationId null
NHA_HANG  -> reservationId required, bookingId null
```

---

## 3.5. Ý nghĩa trạng thái Complaint

### `CHO_PHAN_HOI`

Ticket mới được khách tạo, đang chờ đối tác phản hồi lần đầu.

---

### `DANG_XU_LY`

Đối tác đã phản hồi và vấn đề đang được trao đổi/xử lý.

Ở trạng thái này:

```text
- Customer có thể gửi thêm tin nhắn/file.
- Partner có thể gửi tin nhắn/file.
- Partner có thể tạo phương án xử lý.
```

---

### `CHO_XAC_NHAN_KHACH`

Đối tác đã đề xuất phương án xử lý và đang chờ khách hàng xác nhận.

Ví dụ:

```text
- Hoàn tiền 20%.
- Đổi phòng.
- Tặng voucher.
- Làm lại món.
- Từ chối yêu cầu kèm lý do.
```

Customer có thể:

```text
ACCEPT -> DANG_THUC_HIEN_PHUONG_AN
REJECT -> DANG_XU_LY
```

---

### `DANG_THUC_HIEN_PHUONG_AN`

Khách hàng đã đồng ý phương án, đối tác đang thực hiện hành động xử lý.

Ví dụ:

```text
- Đang đổi phòng.
- Đang tạo voucher.
- Đang xử lý hoàn tiền nội bộ.
```

Khi partner đánh dấu phương án đã thực hiện xong:

```text
Complaint -> DA_GIAI_QUYET
ResolutionAction -> COMPLETED
```

---

### `DA_GIAI_QUYET`

Phương án xử lý đã được thực hiện xong.

Ở trạng thái này:

```text
- Customer có thể đóng ticket nếu đồng ý.
- Nếu phát sinh vấn đề mới, customer có thể gửi thêm message để mở lại DANG_XU_LY.
```

---

### `DA_DONG`

Ticket chính thức kết thúc.

Rule:

```text
- Không cho gửi thêm tin nhắn.
- Không cho upload thêm file.
- Không cho tạo thêm resolution action.
```

---

## 3.6. Entity `TinNhanKhieuNai` / `ComplaintMessage`

Đại diện cho tin nhắn trong ticket khiếu nại.

### Fields đề xuất

```text
id: UUID
khieuNaiId: UUID
nguoiGuiId: UUID
vaiTroNguoiGui: KHACH_HANG | DOI_TAC | SYSTEM
noiDung: Text
createdAt
```

### Rule

```text
- Không cho edit message sau khi gửi.
- Không hard delete message.
- Không cho gửi message nếu complaint đã DA_DONG.
- SYSTEM message dùng cho thông báo tự động như: action proposed, action accepted, action completed.
```

---

## 3.7. Entity `DinhKemPhanHoi` / `FeedbackAttachment`

Đại diện cho file/ảnh đính kèm trong review, complaint hoặc tin nhắn khiếu nại.

### Vì sao cần?

```text
- Review có ảnh giúp tăng độ tin cậy.
- Complaint cần bằng chứng như ảnh phòng, ảnh món ăn, hóa đơn, screenshot lỗi.
- Complaint message cần cho phép gửi thêm ảnh trong quá trình trao đổi.
```

### Thiết kế dùng chung

Thay vì tạo riêng `ReviewImage`, `ComplaintImage`, `ComplaintMessageImage`, nên dùng một entity tổng quát:

```text
DinhKemPhanHoi / FeedbackAttachment
```

Entity này liên kết đến nhiều loại đối tượng thông qua `ownerType` và `ownerId`.

### Fields đề xuất

```text
id: UUID

ownerType:
- REVIEW
- COMPLAINT
- COMPLAINT_MESSAGE
- RESOLUTION_ACTION

ownerId: UUID

fileUrl: String
fileName: String
fileType:
- IMAGE
- PDF
- OTHER

mimeType: String
fileSize: Long

moTa: String nullable

uploadedById: UUID
uploadedByRole:
- KHACH_HANG
- DOI_TAC
- SYSTEM

createdAt
```

### Rule nghiệp vụ

```text
Review:
    - Cho phép đính kèm ảnh.
    - Không bắt buộc.
    - Tối đa 5 ảnh.
    - Chỉ cho IMAGE.
    - Ảnh review DA_HIEN_THI được public cùng review.
    - Ảnh review BI_AN không public.

Complaint:
    - Cho phép đính kèm ảnh/file bằng chứng khi tạo ticket.
    - Không bắt buộc nhưng rất khuyến khích.
    - Tối đa 10 file.
    - Cho phép IMAGE và PDF.
    - Không public.

ComplaintMessage:
    - Cho phép gửi thêm ảnh/file trong quá trình trao đổi.
    - Tối đa 5 file mỗi message.
    - Cho phép IMAGE và PDF.
    - Không cho upload nếu complaint đã DA_DONG.

ResolutionAction:
    - Optional.
    - Dùng khi partner cần đính kèm bằng chứng đã xử lý.
    - Ví dụ: ảnh đổi phòng, biên nhận hoàn tiền nội bộ, voucher.
```

### Validation file

```text
Kích thước tối đa:
- IMAGE: 5MB/file
- PDF: 10MB/file

Số lượng:
- Review: tối đa 5 ảnh.
- Complaint tạo mới: tối đa 10 file.
- Complaint message: tối đa 5 file/message.
- Resolution action completion: tối đa 5 file.

Mime type cho phép:
- image/jpeg
- image/png
- image/webp
- application/pdf

Không cho phép:
- .exe
- .bat
- .cmd
- .js
- .html
- file nén nếu không thật sự cần
```

### Bảo mật file upload

```text
- Không tin file extension từ client.
- Kiểm tra mimeType phía backend.
- Có thể kiểm tra magic bytes nếu muốn chắc hơn.
- Rename file khi lưu để tránh trùng tên.
- Không lưu file trực tiếp bằng tên gốc.
- Không cho upload file quá dung lượng.
- Không expose đường dẫn nội bộ server.
- Complaint attachment chỉ user liên quan hoặc partner sở hữu cơ sở mới xem được.
```

### SQL gợi ý

```sql
CREATE TABLE dinh_kem_phan_hoi (
    id UUID PRIMARY KEY,
    owner_type VARCHAR(50) NOT NULL,
    owner_id UUID NOT NULL,

    file_url TEXT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,

    mo_ta TEXT,

    uploaded_by_id UUID NOT NULL,
    uploaded_by_role VARCHAR(50) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_attachment_owner
ON dinh_kem_phan_hoi(owner_type, owner_id);
```

---

## 3.8. Entity `HanhDongXuLyKhieuNai` / `ComplaintResolutionAction`

Đại diện cho phương án/hành động xử lý cụ thể mà đối tác đề xuất hoặc thực hiện cho complaint.

Đây là entity quan trọng để biến complaint từ “chat hỗ trợ” thành “hệ thống xử lý khiếu nại thật”.

### Fields đề xuất

```text
id: UUID
khieuNaiId: UUID

actionType:
- APOLOGY
- ROOM_CHANGE
- TABLE_CHANGE
- SERVICE_REDO
- FULL_REFUND
- PARTIAL_REFUND
- VOUCHER
- DISCOUNT_CODE
- REJECT_REQUEST
- OTHER

tieuDe: String
moTa: Text

amount: Decimal nullable
currency: VND nullable

voucherCode: String nullable
discountPercent: Integer nullable

status:
- PROPOSED
- CUSTOMER_ACCEPTED
- CUSTOMER_REJECTED
- IN_PROGRESS
- COMPLETED
- CANCELLED

proposedByPartnerId: UUID
customerResponseNote: Text nullable
partnerCompletionNote: Text nullable

proposedAt
customerRespondedAt nullable
completedAt nullable
createdAt
updatedAt
```

### Ý nghĩa actionType

```text
APOLOGY:
    Đối tác xin lỗi chính thức, dùng cho lỗi nhẹ.

ROOM_CHANGE:
    Đổi phòng cho khách sạn.

TABLE_CHANGE:
    Đổi bàn/đổi khung giờ cho nhà hàng.

SERVICE_REDO:
    Làm lại dịch vụ, ví dụ chuẩn bị lại món ăn.

FULL_REFUND:
    Hoàn tiền toàn bộ.

PARTIAL_REFUND:
    Hoàn tiền một phần.

VOUCHER:
    Tặng voucher cho lần sử dụng sau.

DISCOUNT_CODE:
    Cấp mã giảm giá.

REJECT_REQUEST:
    Từ chối yêu cầu, bắt buộc có lý do rõ ràng.

OTHER:
    Phương án khác.
```

### Ý nghĩa status

```text
PROPOSED:
    Partner đã đề xuất, chờ khách xác nhận.

CUSTOMER_ACCEPTED:
    Khách đã đồng ý phương án.

CUSTOMER_REJECTED:
    Khách không đồng ý phương án.

IN_PROGRESS:
    Partner đang thực hiện phương án.

COMPLETED:
    Partner đã hoàn tất phương án.

CANCELLED:
    Phương án bị hủy, thường do partner đề xuất lại phương án khác.
```

### Rule nghiệp vụ

```text
- Chỉ partner sở hữu cơ sở mới được tạo ResolutionAction.
- Không cho tạo ResolutionAction nếu complaint DA_DONG.
- Mỗi complaint có thể có nhiều ResolutionAction theo lịch sử xử lý.
- Tại một thời điểm chỉ nên có một action đang PROPOSED hoặc IN_PROGRESS.
- Nếu customer reject action, complaint chuyển về DANG_XU_LY.
- Nếu customer accept action, complaint chuyển sang DANG_THUC_HIEN_PHUONG_AN.
- Nếu partner mark action COMPLETED, complaint chuyển sang DA_GIAI_QUYET.
- Customer chỉ được close ticket khi complaint DA_GIAI_QUYET.
```

### Có cần tích hợp hoàn tiền thật không?

Trong scope đồ án:

```text
Không cần tích hợp payment gateway thật.
```

Nếu action là `FULL_REFUND` hoặc `PARTIAL_REFUND`, hệ thống chỉ cần lưu record:

```text
amount
currency
description
status
```

Như vậy demo vẫn thể hiện được logic xử lý, còn thanh toán thật có thể là future extension.

### SQL gợi ý

```sql
CREATE TABLE hanh_dong_xu_ly_khieu_nai (
    id UUID PRIMARY KEY,
    khieu_nai_id UUID NOT NULL,

    action_type VARCHAR(50) NOT NULL,
    tieu_de VARCHAR(255) NOT NULL,
    mo_ta TEXT NOT NULL,

    amount NUMERIC(12, 2),
    currency VARCHAR(10),

    voucher_code VARCHAR(100),
    discount_percent INT,

    status VARCHAR(50) NOT NULL,

    proposed_by_partner_id UUID NOT NULL,
    customer_response_note TEXT,
    partner_completion_note TEXT,

    proposed_at TIMESTAMP NOT NULL,
    customer_responded_at TIMESTAMP,
    completed_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_resolution_action_complaint
        FOREIGN KEY (khieu_nai_id) REFERENCES khieu_nai(id)
);

CREATE INDEX idx_resolution_action_complaint
ON hanh_dong_xu_ly_khieu_nai(khieu_nai_id);
```

---

## 3.9. Entity `LichSuHoatDongKhieuNai` / `ComplaintActivity`

Đại diện cho timeline sự kiện của complaint.

Không phải mọi sự kiện đều là message. Ví dụ:

```text
- Complaint created
- Partner replied
- Resolution action proposed
- Customer accepted action
- Partner completed action
- Complaint closed
```

Các sự kiện này nên được lưu riêng để render timeline rõ ràng và audit tốt hơn.

### Fields đề xuất

```text
id: UUID
khieuNaiId: UUID

activityType:
- COMPLAINT_CREATED
- CUSTOMER_MESSAGE_SENT
- PARTNER_MESSAGE_SENT
- ATTACHMENT_UPLOADED
- ACTION_PROPOSED
- ACTION_ACCEPTED
- ACTION_REJECTED
- ACTION_STARTED
- ACTION_COMPLETED
- COMPLAINT_REOPENED
- COMPLAINT_RESOLVED
- COMPLAINT_CLOSED

actorId: UUID nullable
actorRole: KHACH_HANG | DOI_TAC | SYSTEM

summary: String
metadata: JSON/Text nullable

createdAt
```

### Rule

```text
- Mỗi bước quan trọng trong complaint đều tạo activity.
- UI có thể dùng activity để render timeline.
- Message vẫn lưu ở ComplaintMessage.
- Activity lưu sự kiện hệ thống và audit.
```

---

# 4. GIAI ĐOẠN 2: BACKEND - LOGIC & APIs

## 4.1. Customer Review APIs

### Tạo đánh giá

```http
POST /api/v1/user/reviews
Content-Type: multipart/form-data
```

Form data:

```text
request: JSON review request
files: List<MultipartFile> optional
```

### Logic

```text
1. Lấy current user từ SecurityContext.
2. Kiểm tra user là KHACH_HANG.
3. Kiểm tra loaiDichVu hợp lệ.
4. Kiểm tra invariant bookingId/reservationId.
5. Kiểm tra booking/reservation thuộc khách hàng.
6. Kiểm tra đơn đã DA_HOAN_THANH.
7. Kiểm tra chưa review đơn đó.
8. Validate soSao từ 1 đến 5.
9. Validate noiDung không rỗng.
10. Validate ảnh đính kèm nếu có.
11. Chạy ProfanityFilterService.
12. Nếu pass: trangThai = DA_HIEN_THI.
13. Nếu fail: trangThai = BI_AN.
14. Lưu review.
15. Lưu attachment với ownerType = REVIEW.
16. Nếu review DA_HIEN_THI, cập nhật ratingAverage và reviewCount của cơ sở.
```

---

## 4.2. Public Review APIs

```http
GET /api/v1/public/business-profiles/{id}/reviews
GET /api/v1/public/hotels/{id}/reviews
GET /api/v1/public/restaurants/{id}/reviews
```

### Query params

```text
page: default 0
size: default 10, max 20
sort: newest | oldest | rating_desc | rating_asc
```

### Rule

```text
- Chỉ trả review DA_HIEN_THI.
- Trả kèm ảnh review public nếu có.
- Default sort = newest.
- Có pagination bắt buộc.
- Không trả list không giới hạn.
```

---

## 4.3. User Review APIs

```http
GET /api/v1/user/reviews
```

### Query params

```text
page: default 0
size: default 10, max 20
sort: newest | oldest
```

---

## 4.4. Partner Reply APIs

```http
POST /api/v1/partner/reviews/{reviewId}/reply
PUT  /api/v1/partner/reviews/{reviewId}/reply
```

### Logic

```text
1. Lấy current partner từ SecurityContext.
2. Kiểm tra review tồn tại.
3. Kiểm tra review thuộc cơ sở của partner hiện tại.
4. Validate nội dung phản hồi.
5. Chạy ProfanityFilterService.
6. Nếu chưa có reply thì tạo mới.
7. Nếu đã có reply thì cập nhật.
```

---

## 4.5. Complaint APIs

### Customer APIs

```http
POST /api/v1/user/complaints
GET  /api/v1/user/complaints
GET  /api/v1/user/complaints/{id}
POST /api/v1/user/complaints/{id}/messages
PUT  /api/v1/user/complaints/{id}/resolution-actions/{actionId}/accept
PUT  /api/v1/user/complaints/{id}/resolution-actions/{actionId}/reject
PUT  /api/v1/user/complaints/{id}/close
```

### Partner APIs

```http
GET  /api/v1/partner/complaints
GET  /api/v1/partner/complaints/{id}
POST /api/v1/partner/complaints/{id}/messages
POST /api/v1/partner/complaints/{id}/resolution-actions
PUT  /api/v1/partner/complaints/{id}/resolution-actions/{actionId}/start
PUT  /api/v1/partner/complaints/{id}/resolution-actions/{actionId}/complete
```

### Bỏ endpoint này

```http
PUT /api/v1/partner/complaints/{id}/status
```

Lý do:

```text
Status phải được chuyển bởi nghiệp vụ, không để partner tự set tùy ý.
```

---

## 4.6. Complaint upload APIs

Tạo complaint có file bằng chứng:

```http
POST /api/v1/user/complaints
Content-Type: multipart/form-data
```

Form data:

```text
request: JSON complaint request
files: List<MultipartFile>
```

Gửi message có file:

```http
POST /api/v1/user/complaints/{id}/messages
Content-Type: multipart/form-data
```

```http
POST /api/v1/partner/complaints/{id}/messages
Content-Type: multipart/form-data
```

Xem file đính kèm:

```http
GET /api/v1/attachments/{attachmentId}
```

Rule xem attachment:

```text
REVIEW:
    Nếu review DA_HIEN_THI và attachment là IMAGE -> public có thể xem.
    Nếu review BI_AN -> chỉ customer tạo review hoặc partner sở hữu cơ sở xem.

COMPLAINT:
    Chỉ customer tạo complaint hoặc partner sở hữu cơ sở xem.

COMPLAINT_MESSAGE:
    Chỉ customer tạo complaint hoặc partner sở hữu cơ sở xem.

RESOLUTION_ACTION:
    Chỉ customer tạo complaint hoặc partner sở hữu cơ sở xem.
```

---

## 4.7. Complaint List Query Contract

Customer complaint list:

```http
GET /api/v1/user/complaints?page=0&size=10&status=DANG_XU_LY&sort=updated_desc
```

Partner complaint list:

```http
GET /api/v1/partner/complaints?page=0&size=10&status=CHO_PHAN_HOI&mucDo=NGHIEM_TRONG&category=ROOM_QUALITY&sort=updated_desc
```

Query params:

```text
page: default 0
size: default 10, max 20
status: optional
mucDo: optional
category: optional
sort: newest | oldest | updated_desc
```

Rule:

```text
- Complaint list luôn có pagination.
- Default sort = updated_desc.
- Complaint không có public endpoint.
```

---

## 4.8. Logic tạo complaint

```text
1. Customer đăng nhập.
2. Kiểm tra loaiDichVu hợp lệ.
3. Kiểm tra invariant bookingId/reservationId.
4. Kiểm tra booking/reservation thuộc khách.
5. Kiểm tra đơn có tồn tại và liên quan đúng cơ sở.
6. Validate tieuDe, noiDungTomTat.
7. Validate category, mucDo.
8. Validate danh sách file/ảnh bằng chứng nếu có.
9. Chạy ProfanityFilterService.
10. Tạo complaint với trạng thái CHO_PHAN_HOI.
11. Lưu attachment với ownerType = COMPLAINT nếu có.
12. Tạo message đầu tiên từ khách.
13. Set lastCustomerMessageAt = now.
14. Tạo ComplaintActivity: COMPLAINT_CREATED.
15. Tạo ComplaintActivity: CUSTOMER_MESSAGE_SENT.
```

---

## 4.9. Logic customer gửi thêm message

```text
1. Customer đăng nhập.
2. Kiểm tra complaint thuộc chính customer.
3. Kiểm tra complaint chưa DA_DONG.
4. Validate nội dung hoặc file.
5. Validate file/ảnh đính kèm nếu có.
6. Chạy ProfanityFilterService.
7. Tạo ComplaintMessage.
8. Lưu attachment với ownerType = COMPLAINT_MESSAGE nếu có.
9. Set lastCustomerMessageAt = now.
10. Nếu complaint đang DA_GIAI_QUYET, chuyển lại DANG_XU_LY.
11. Tạo ComplaintActivity: CUSTOMER_MESSAGE_SENT.
12. Nếu reopened từ DA_GIAI_QUYET, tạo ComplaintActivity: COMPLAINT_REOPENED.
```

---

## 4.10. Logic partner trả lời complaint

```text
1. Partner đăng nhập.
2. Kiểm tra complaint thuộc cơ sở của partner.
3. Kiểm tra complaint chưa DA_DONG.
4. Validate nội dung hoặc file.
5. Validate file/ảnh đính kèm nếu có.
6. Chạy ProfanityFilterService.
7. Tạo ComplaintMessage.
8. Lưu attachment với ownerType = COMPLAINT_MESSAGE nếu có.
9. Set lastPartnerResponseAt = now.
10. Nếu trạng thái đang CHO_PHAN_HOI thì chuyển sang DANG_XU_LY.
11. Tạo ComplaintActivity: PARTNER_MESSAGE_SENT.
```

---

## 4.11. Logic partner tạo ResolutionAction

```text
1. Partner đăng nhập.
2. Kiểm tra complaint tồn tại.
3. Kiểm tra complaint thuộc cơ sở của partner.
4. Kiểm tra complaint chưa DA_DONG.
5. Kiểm tra không có action khác đang PROPOSED hoặc IN_PROGRESS.
6. Validate actionType.
7. Validate title/description.
8. Nếu actionType là FULL_REFUND/PARTIAL_REFUND:
    - amount bắt buộc > 0.
    - currency bắt buộc.
9. Nếu actionType là VOUCHER/DISCOUNT_CODE:
    - voucherCode hoặc discountPercent cần có nếu áp dụng.
10. Tạo ResolutionAction với status = PROPOSED.
11. Chuyển complaint sang CHO_XAC_NHAN_KHACH.
12. Tạo system message: "Đối tác đã đề xuất phương án xử lý."
13. Tạo ComplaintActivity: ACTION_PROPOSED.
```

---

## 4.12. Logic customer accept ResolutionAction

```text
1. Customer đăng nhập.
2. Kiểm tra complaint thuộc chính customer.
3. Kiểm tra action thuộc complaint.
4. Kiểm tra action status = PROPOSED.
5. Cập nhật action status = CUSTOMER_ACCEPTED.
6. Set customerRespondedAt = now.
7. Chuyển complaint sang DANG_THUC_HIEN_PHUONG_AN.
8. Tạo system message: "Khách hàng đã đồng ý phương án xử lý."
9. Tạo ComplaintActivity: ACTION_ACCEPTED.
```

---

## 4.13. Logic customer reject ResolutionAction

```text
1. Customer đăng nhập.
2. Kiểm tra complaint thuộc chính customer.
3. Kiểm tra action thuộc complaint.
4. Kiểm tra action status = PROPOSED.
5. Cập nhật action status = CUSTOMER_REJECTED.
6. Lưu customerResponseNote.
7. Set customerRespondedAt = now.
8. Chuyển complaint về DANG_XU_LY.
9. Tạo system message: "Khách hàng đã từ chối phương án xử lý."
10. Tạo ComplaintActivity: ACTION_REJECTED.
```

---

## 4.14. Logic partner start ResolutionAction

```text
1. Partner đăng nhập.
2. Kiểm tra complaint thuộc cơ sở của partner.
3. Kiểm tra action thuộc complaint.
4. Kiểm tra action status = CUSTOMER_ACCEPTED.
5. Cập nhật action status = IN_PROGRESS.
6. Chuyển complaint sang DANG_THUC_HIEN_PHUONG_AN.
7. Tạo system message: "Đối tác đang thực hiện phương án xử lý."
8. Tạo ComplaintActivity: ACTION_STARTED.
```

---

## 4.15. Logic partner complete ResolutionAction

```text
1. Partner đăng nhập.
2. Kiểm tra complaint thuộc cơ sở của partner.
3. Kiểm tra action thuộc complaint.
4. Kiểm tra action status = IN_PROGRESS hoặc CUSTOMER_ACCEPTED.
5. Cập nhật action status = COMPLETED.
6. Lưu partnerCompletionNote.
7. Set completedAt = now.
8. Lưu attachment với ownerType = RESOLUTION_ACTION nếu có.
9. Chuyển complaint sang DA_GIAI_QUYET.
10. Tạo system message: "Phương án xử lý đã được hoàn tất."
11. Tạo ComplaintActivity: ACTION_COMPLETED.
12. Tạo ComplaintActivity: COMPLAINT_RESOLVED.
```

---

## 4.16. Logic customer close complaint

```text
1. Customer đăng nhập.
2. Kiểm tra complaint thuộc chính customer.
3. Chỉ cho close nếu complaint đang DA_GIAI_QUYET.
4. Chuyển complaint sang DA_DONG.
5. Tạo ComplaintActivity: COMPLAINT_CLOSED.
6. Không cho gửi thêm message/file/action sau khi DA_DONG.
```

---

# 5. SLA 48h

Partner cần phản hồi tin nhắn khách hàng trong 48 giờ.

## Rule quá hạn

```text
Nếu lastCustomerMessageAt != null
và (
    lastPartnerResponseAt == null
    hoặc lastPartnerResponseAt < lastCustomerMessageAt
)
và now - lastCustomerMessageAt > 48h
=> quaHanXuLy = true
```

## Cách triển khai trong scope hiện tại

Với scope đồ án, `quaHanXuLy` nên là computed field khi query complaint list/detail:

```text
overdue = computeOverdue(lastCustomerMessageAt, lastPartnerResponseAt, now)
```

Không cần persist field nếu chưa có scheduler.

## Production extension

Nếu mở rộng production thật:

```text
- Scheduler đánh dấu overdue định kỳ.
- Notification nhắc partner.
- Báo cáo SLA breach.
- Escalation cho admin/support.
```

---

# 6. GIAI ĐOẠN 3: FRONTEND - UX FLOW

## 6.1. Customer Portal

### Trang chi tiết dịch vụ

Hiển thị:

```text
- Rating summary.
- Điểm trung bình.
- Tổng số review.
- Danh sách review.
- Ảnh review dạng thumbnail/gallery.
- Phản hồi của partner dưới từng review nếu có.
- Pagination review.
- Sort review.
```

### Trang lịch sử đặt chỗ

Hiển thị nút:

```text
- "Viết đánh giá" nếu đơn DA_HOAN_THANH và chưa review.
- "Đã đánh giá" nếu đã review.
- "Khiếu nại" nếu khách muốn báo sự cố.
- Khi viết review cho phép upload ảnh trải nghiệm thực tế.
- Khi tạo khiếu nại cho phép upload ảnh/file bằng chứng.
```

### Trang khiếu nại của tôi

```text
- Danh sách ticket.
- Trạng thái xử lý.
- Category.
- Mức độ.
- Badge quá hạn nếu có.
- Thời gian cập nhật cuối.
- Click vào ticket để xem detail.
```

### Chi tiết ticket

```text
- Thông tin ticket.
- Bằng chứng ban đầu.
- Timeline tin nhắn.
- Activity timeline.
- Ô nhập tin nhắn mới.
- Upload ảnh/file trong tin nhắn.
- Preview file trước khi gửi.
- Resolution Action Card.
- Nút Accept/Reject phương án xử lý.
- Nếu ticket DA_GIAI_QUYET thì hiển thị nút đóng ticket.
- Nếu ticket DA_DONG thì disable ô nhập và upload file.
```

---

## 6.2. Partner Dashboard

### Review Tab

```text
- Danh sách review thuộc cơ sở của partner.
- Filter theo số sao.
- Filter theo trạng thái hiển thị/bị ẩn.
- Hiển thị ảnh review nếu có.
- Nút phản hồi.
- Nếu đã phản hồi thì hiển thị nội dung và nút sửa.
```

### Complaint Tab

```text
- Danh sách ticket.
- Badge quá hạn 48h.
- Filter theo trạng thái.
- Filter theo mức độ.
- Filter theo category.
- Click vào ticket để xem detail.
- Xem ảnh/file bằng chứng.
- Gửi message có thể kèm ảnh/file.
- Tạo phương án xử lý cụ thể.
- Theo dõi khách đã accept/reject phương án.
- Đánh dấu action đang thực hiện.
- Đánh dấu action đã hoàn tất.
```

---

## 6.3. Complaint Resolution Action UI

Customer thấy:

```text
- Thẻ "Phương án xử lý".
- Loại phương án: Hoàn tiền / Đổi phòng / Voucher / Từ chối...
- Mô tả chi tiết.
- Số tiền/voucher nếu có.
- Trạng thái phương án.
- Nút Đồng ý.
- Nút Không đồng ý.
```

Partner thấy:

```text
- Form tạo phương án xử lý.
- Danh sách action đã đề xuất trước đó.
- Badge trạng thái: PROPOSED / ACCEPTED / REJECTED / IN_PROGRESS / COMPLETED.
- Nút Start nếu khách đã accept.
- Nút Complete khi đã thực hiện xong.
```

---

# 7. Profanity Filter

Triển khai service mức cơ bản cho MVP:

```text
ProfanityFilterService
- boolean containsProfanity(String text)
- String normalize(String text)
```

## Logic tối thiểu

```text
1. Lowercase.
2. Trim.
3. Normalize Unicode nếu có thể.
4. Remove repeated spaces.
5. Check contains từ trong blacklist.
```

## Áp dụng cho

```text
- Review content.
- Partner reply.
- Complaint title.
- Complaint summary.
- Complaint message.
- Resolution action description.
```

## Rule xử lý

```text
Review vi phạm:
- Vẫn lưu nhưng trạng thái BI_AN.

Partner reply / complaint message / resolution action vi phạm:
- Từ chối lưu và trả 400.
```

---

# 8. Transaction Consistency

## Khi tạo review

```text
@Transactional
1. Validate booking/reservation.
2. Lưu review.
3. Lưu attachment nếu có.
4. Nếu review DA_HIEN_THI:
   - Recalculate ratingAverage.
   - Update reviewCount.
```

Không nên tự cộng trung bình bằng công thức dễ sai khi có review bị ẩn hoặc sau này có soft delete.

Nên query lại:

```sql
SELECT AVG(so_sao), COUNT(*)
FROM danh_gia
WHERE ho_so_kinh_doanh_id = ?
AND trang_thai = 'DA_HIEN_THI'
```

## Khi xử lý complaint action

```text
@Transactional
1. Validate quyền.
2. Validate trạng thái complaint/action.
3. Update action.
4. Update complaint status.
5. Tạo system message.
6. Tạo activity timeline.
```

---

# 9. Edit/Delete Policy

## 9.1. Review

```text
- Customer chỉ được tạo 1 review cho mỗi booking/reservation.
- Sprint hiện tại: không cho edit review.
- Sprint hiện tại: không cho delete review.
- Nếu cần ẩn review, dùng trangThai = BI_AN.
```

## 9.2. Partner Reply

```text
- Cho phép tạo 1 reply cho mỗi review.
- Cho phép update reply.
- Không hard delete reply.
```

## 9.3. Complaint Message

```text
- Không cho edit sau khi gửi.
- Không cho delete sau khi gửi.
- Không cho gửi message nếu complaint đã DA_DONG.
```

## 9.4. Resolution Action

```text
- Không cho xóa action.
- Nếu đề xuất sai, dùng status CANCELLED và tạo action mới.
- Không cho sửa action sau khi customer đã accept/reject.
- Chỉ partner sở hữu cơ sở được tạo/start/complete action.
- Chỉ customer tạo complaint được accept/reject action.
```

---

# 10. Security Checklist

```text
- POST /user/reviews:
  Chỉ khách sở hữu booking/reservation mới được review.

- GET /public/.../reviews:
  Chỉ trả review DA_HIEN_THI.

- POST /partner/reviews/{id}/reply:
  Partner chỉ reply review thuộc cơ sở của mình.

- GET /user/complaints/{id}:
  Customer chỉ xem complaint của mình.

- GET /partner/complaints/{id}:
  Partner chỉ xem complaint thuộc cơ sở của mình.

- POST complaint message:
  Chỉ customer của ticket hoặc partner sở hữu cơ sở mới được gửi.

- POST /partner/complaints/{id}/resolution-actions:
  Chỉ partner sở hữu cơ sở mới được tạo phương án xử lý.

- PUT /user/complaints/{id}/resolution-actions/{actionId}/accept:
  Chỉ customer tạo complaint mới được đồng ý phương án.

- PUT /user/complaints/{id}/resolution-actions/{actionId}/reject:
  Chỉ customer tạo complaint mới được từ chối phương án.

- PUT /partner/complaints/{id}/resolution-actions/{actionId}/start:
  Chỉ partner sở hữu cơ sở mới được đánh dấu đang thực hiện.

- PUT /partner/complaints/{id}/resolution-actions/{actionId}/complete:
  Chỉ partner sở hữu cơ sở mới được đánh dấu hoàn tất.

- GET /attachments/{id}:
  Review public chỉ public nếu review DA_HIEN_THI.
  Complaint attachment chỉ customer liên quan hoặc partner sở hữu cơ sở được xem.

- Complaint không public ở bất kỳ endpoint nào.

- Không tin khachHangId/partnerId từ request body.
  Luôn lấy từ SecurityContext.
```

---

# 11. Validation Checklist

## Review

```text
- loaiDichVu hợp lệ.
- bookingId/reservationId đúng invariant.
- soSao từ 1 đến 5.
- noiDung không rỗng.
- noiDung tối đa 1000 ký tự.
- booking/reservation bắt buộc tồn tại.
- Đơn phải DA_HOAN_THANH.
- Không review trùng.
- Ảnh review tối đa 5 file.
- Ảnh review chỉ cho image/jpeg, image/png, image/webp.
- Ảnh review tối đa 5MB/file.
```

## Partner Reply

```text
- noiDung không rỗng.
- noiDung tối đa 1000 ký tự.
- Review phải thuộc cơ sở của partner.
```

## Complaint

```text
- loaiDichVu hợp lệ.
- bookingId/reservationId đúng invariant.
- category hợp lệ.
- tieuDe không rỗng.
- tieuDe tối đa 150 ký tự.
- noiDungTomTat không rỗng.
- mucDo hợp lệ.
- bookingId hoặc reservationId phải tồn tại đúng loại dịch vụ.
- File bằng chứng tối đa 10 file.
- Chỉ cho IMAGE và PDF.
- IMAGE tối đa 5MB/file.
- PDF tối đa 10MB/file.
```

## Complaint Message

```text
- noiDung không rỗng nếu không có file.
- Nếu có file thì nội dung có thể ngắn nhưng vẫn nên validate tối thiểu nếu cần.
- noiDung tối đa 2000 ký tự.
- Không cho gửi nếu ticket đã DA_DONG.
- File đính kèm tối đa 5 file/message.
- Chỉ cho IMAGE và PDF.
```

## Complaint Resolution Action

```text
- actionType hợp lệ.
- title không rỗng, tối đa 255 ký tự.
- description không rỗng.
- Nếu FULL_REFUND/PARTIAL_REFUND thì amount > 0.
- Nếu DISCOUNT_CODE thì voucherCode hoặc discountPercent hợp lệ.
- Không tạo action nếu complaint DA_DONG.
- Không tạo action mới nếu còn action PROPOSED hoặc IN_PROGRESS.
- Customer chỉ accept/reject action đang PROPOSED.
- Partner chỉ start action đã CUSTOMER_ACCEPTED.
- Partner chỉ complete action đã CUSTOMER_ACCEPTED hoặc IN_PROGRESS.
```

## Pagination

```text
- page >= 0.
- size từ 1 đến 20.
- sort nằm trong whitelist.
```

---

# 12. API Response gợi ý

## Review response

```json
{
  "id": "uuid",
  "customerName": "Nguyễn Văn A",
  "businessProfileId": "uuid",
  "serviceType": "KHACH_SAN",
  "rating": 5,
  "content": "Dịch vụ tốt",
  "status": "DA_HIEN_THI",
  "createdAt": "2026-05-28T10:00:00",
  "attachments": [
    {
      "id": "uuid",
      "fileUrl": "https://cdn.travi.vn/reviews/image-1.jpg",
      "fileType": "IMAGE",
      "mimeType": "image/jpeg"
    }
  ],
  "partnerReply": {
    "content": "Cảm ơn quý khách đã sử dụng dịch vụ.",
    "createdAt": "2026-05-28T12:00:00",
    "updatedAt": "2026-05-28T12:30:00"
  }
}
```

---

## Complaint list response

```json
{
  "id": "uuid",
  "title": "Phòng không đúng mô tả",
  "category": "ROOM_QUALITY",
  "serviceType": "KHACH_SAN",
  "severity": "NGHIEM_TRONG",
  "status": "CHO_XAC_NHAN_KHACH",
  "overdue": false,
  "createdAt": "2026-05-28T10:00:00",
  "updatedAt": "2026-05-28T13:00:00",
  "latestAction": {
    "actionType": "PARTIAL_REFUND",
    "status": "PROPOSED",
    "title": "Hoàn tiền một phần"
  }
}
```

---

## Complaint detail response

```json
{
  "id": "uuid",
  "title": "Phòng không đúng mô tả",
  "category": "ROOM_QUALITY",
  "serviceType": "KHACH_SAN",
  "severity": "NGHIEM_TRONG",
  "status": "CHO_XAC_NHAN_KHACH",
  "overdue": false,
  "attachments": [
    {
      "id": "uuid",
      "fileUrl": "https://cdn.travi.vn/complaints/evidence-1.jpg",
      "fileType": "IMAGE",
      "mimeType": "image/jpeg"
    }
  ],
  "resolutionActions": [
    {
      "id": "uuid",
      "actionType": "PARTIAL_REFUND",
      "title": "Hoàn tiền một phần",
      "description": "Hoàn 20% giá trị đặt phòng.",
      "amount": 300000,
      "currency": "VND",
      "status": "PROPOSED",
      "proposedAt": "2026-05-28T13:00:00"
    }
  ],
  "messages": [
    {
      "senderRole": "KHACH_HANG",
      "content": "Phòng nhận được không giống hình.",
      "createdAt": "2026-05-28T10:00:00",
      "attachments": [
        {
          "id": "uuid",
          "fileUrl": "https://cdn.travi.vn/complaints/room-proof.jpg",
          "fileType": "IMAGE",
          "mimeType": "image/jpeg"
        }
      ]
    },
    {
      "senderRole": "DOI_TAC",
      "content": "Chúng tôi đang kiểm tra lại thông tin.",
      "createdAt": "2026-05-28T12:00:00"
    }
  ],
  "activities": [
    {
      "activityType": "COMPLAINT_CREATED",
      "summary": "Khách hàng đã tạo khiếu nại.",
      "createdAt": "2026-05-28T10:00:00"
    },
    {
      "activityType": "ACTION_PROPOSED",
      "summary": "Đối tác đã đề xuất hoàn tiền một phần.",
      "createdAt": "2026-05-28T13:00:00"
    }
  ]
}
```

---

# 13. Checklist triển khai

## Backend

```text
- [ ] Tạo migration cho review/reply/complaint/message.
- [ ] Tạo migration cho attachment.
- [ ] Tạo migration cho resolution action.
- [ ] Tạo migration cho complaint activity.
- [ ] Thêm constraint bookingId/reservationId theo loaiDichVu.
- [ ] Tạo entity Review.
- [ ] Tạo entity ReviewReply.
- [ ] Tạo entity Complaint.
- [ ] Tạo entity ComplaintMessage.
- [ ] Tạo entity FeedbackAttachment / DinhKemPhanHoi.
- [ ] Tạo entity ComplaintResolutionAction / HanhDongXuLyKhieuNai.
- [ ] Tạo entity ComplaintActivity / LichSuHoatDongKhieuNai.
- [ ] Tạo repository.
- [ ] Tạo DTO request/response.
- [ ] Tạo AttachmentService / FileStorageService.
- [ ] Tạo ProfanityFilterService.
- [ ] Tạo ReviewService.
- [ ] Tạo ComplaintService.
- [ ] Tạo ComplaintResolutionActionService.
- [ ] Tạo ComplaintActivityService.
- [ ] Tạo UserReviewController.
- [ ] Tạo PartnerReviewController.
- [ ] Tạo PublicReviewController.
- [ ] Tạo UserComplaintController.
- [ ] Tạo PartnerComplaintController.
- [ ] Tạo UserComplaintResolutionActionController.
- [ ] Tạo PartnerComplaintResolutionActionController.
- [ ] Thêm security check theo owner/partner.
- [ ] Thêm transaction khi tạo review và cập nhật rating.
- [ ] Thêm transaction cho action accept/reject/start/complete.
- [ ] Thêm pagination/sort/filter cho review và complaint list.
- [ ] Thêm multipart upload cho review/complaint/message/action completion.
- [ ] Validate file size, mime type, số lượng file.
- [ ] Kiểm tra quyền xem attachment.
- [ ] Bỏ API partner update status thủ công.
```

## Frontend

```text
- [ ] Hiển thị rating summary ở detail page.
- [ ] Hiển thị review list ở detail page.
- [ ] Thêm sort/pagination review.
- [ ] Thêm ảnh review dạng gallery.
- [ ] Thêm nút viết đánh giá ở booking history.
- [ ] Tạo form review có upload ảnh.
- [ ] Preview ảnh review trước khi gửi.
- [ ] Tạo trang My Complaints.
- [ ] Tạo trang Complaint Detail.
- [ ] Hiển thị complaint evidence.
- [ ] Hiển thị message timeline.
- [ ] Hiển thị activity timeline.
- [ ] Cho phép gửi ảnh/file trong complaint message.
- [ ] Hiển thị Resolution Action Card.
- [ ] Customer accept/reject resolution action.
- [ ] Partner tạo/start/complete resolution action.
- [ ] Disable input/upload/action nếu complaint DA_DONG.
- [ ] Partner dashboard Review Tab.
- [ ] Partner xem ảnh review nếu có.
- [ ] Partner dashboard Complaint Tab.
- [ ] Partner xem/gửi attachment trong complaint.
- [ ] Partner tạo phương án xử lý cụ thể.
- [ ] Validate form phía frontend.
```

---

# 14. Ưu tiên triển khai nếu thời gian gấp

## P0

```text
- Review sau đơn hoàn tất.
- Review có ảnh optional.
- Public review list có pagination.
- Partner reply review.
- Update ratingAverage/reviewCount.
- Invariant bookingId/reservationId.
```

## P1

```text
- Customer tạo complaint.
- Complaint có ảnh/file bằng chứng.
- Partner xem và trả lời complaint.
- Complaint message dạng chat đơn giản.
- Complaint message có thể kèm ảnh/file.
- Partner tạo phương án xử lý cụ thể.
- Customer accept/reject phương án.
- Partner đánh dấu đã hoàn tất phương án.
- Complaint visibility/security.
```

## P2

```text
- Activity timeline.
- SLA 48h computed field.
- Filter trạng thái/mức độ/category.
- Review aspect score.
- UI polish.
- Production extension cho moderation/scheduler.
```

---

# 15. Flow demo cuối cùng

## Customer flow

```text
1. Customer đăng nhập.
2. Customer xem đơn đã hoàn tất.
3. Customer viết review + upload ảnh.
4. Customer tạo complaint + upload bằng chứng.
5. Customer gửi thêm message nếu cần.
6. Customer xem phương án xử lý do partner đề xuất.
7. Customer accept hoặc reject phương án.
8. Nếu partner hoàn tất, customer đóng ticket.
```

## Partner flow

```text
1. Partner đăng nhập.
2. Partner xem review.
3. Partner reply review.
4. Partner xem complaint thuộc cơ sở.
5. Partner xem bằng chứng.
6. Partner trao đổi qua message.
7. Partner tạo ResolutionAction.
8. Partner chờ customer xác nhận.
9. Partner start action.
10. Partner complete action.
```

---

# 16. Kết luận

Module 5 sau khi hoàn thiện không còn là một hệ thống chat khiếu nại đơn giản.

Nó trở thành một hệ thống xử lý phản hồi dịch vụ gồm:

```text
Review công khai
+
Review attachment
+
Partner reply
+
Complaint riêng tư
+
Complaint evidence
+
Complaint message
+
Resolution action
+
Activity timeline
```

Thiết kế này tốt hơn vì:

```text
- Không cho review ảo.
- Gắn review với đơn thật.
- Có invariant rõ cho booking/reservation.
- Hỗ trợ cả khách sạn và nhà hàng.
- Review có ảnh trải nghiệm thực tế.
- Complaint có bằng chứng.
- Complaint không public.
- Complaint không chỉ chat mà có hành động xử lý cụ thể.
- Customer có quyền accept/reject phương án xử lý.
- Partner không được tự đóng ticket tùy ý.
- Có activity timeline để audit.
- Có pagination/sort/filter rõ.
- Không cần Admin vẫn đủ flow nghiệp vụ.
- Dễ demo và phù hợp tiến độ đồ án.
