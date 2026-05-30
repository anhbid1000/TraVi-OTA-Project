# ⭐ MODULE 5: ĐÁNH GIÁ & XỬ LÝ KHIẾU NẠI DỊCH VỤ

## 1. Mục tiêu Module

Xây dựng hệ thống phản hồi hai chiều giữa **Khách hàng** và **Đối tác** sau khi khách đã sử dụng dịch vụ, nhằm tăng độ tin cậy, minh bạch chất lượng và hỗ trợ xử lý sự cố trên nền tảng TraVi OTA.

Module này tập trung vào logic thực tế giữa khách hàng và đối tác, không triển khai flow Admin để giữ scope gọn và phù hợp với định hướng đồ án.

### Phạm vi chính

- Khách hàng đánh giá dịch vụ sau khi đơn đã hoàn tất.
- Khách hàng tạo khiếu nại liên quan đến đơn đã đặt.
- Đối tác phản hồi đánh giá và xử lý khiếu nại.
- Hệ thống tự động lọc từ ngữ không phù hợp ở mức cơ bản.
- Review công khai được hiển thị ở trang chi tiết dịch vụ.
- Complaint/ticket là dữ liệu riêng tư, không public.

---

## 2. Nguyên tắc nghiệp vụ

### 2.1. Review chỉ được tạo sau khi hoàn tất đơn

Khách hàng chỉ được đánh giá khi:

```text
- Đã đăng nhập.
- Có booking/reservation thuộc về chính họ.
- Đơn ở trạng thái DA_HOAN_THANH.
- Chưa từng review cho đơn đó.
```

Không cho phép người chưa sử dụng dịch vụ đánh giá để tránh spam, rating ảo và làm giảm độ tin cậy của hệ thống.

---

### 2.2. Review phải gắn với đơn thật

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

### 2.3. Invariant bắt buộc cho bookingId / reservationId

Vì hệ thống có hai loại đơn khác nhau, entity Review/Complaint có thể chứa cả `bookingId` và `reservationId` dạng nullable. Để tránh dữ liệu bẩn, bắt buộc áp dụng invariant ở cả service layer và database constraint nếu có thể.

#### Rule

```text
Nếu loaiDichVu = KHACH_SAN:
    bookingId bắt buộc khác null
    reservationId bắt buộc null

Nếu loaiDichVu = NHA_HANG:
    reservationId bắt buộc khác null
    bookingId bắt buộc null
```

#### Không hợp lệ

```text
bookingId = null và reservationId = null
bookingId != null và reservationId != null
loaiDichVu = KHACH_SAN nhưng reservationId != null
loaiDichVu = NHA_HANG nhưng bookingId != null
```

---

### 2.4. Partner chỉ xử lý dữ liệu thuộc cơ sở của mình

Mọi API của đối tác phải kiểm tra:

```text
businessProfile.doiTacId == currentUserId
```

Không tin `doiTacId`, `khachHangId`, `businessProfileId` gửi từ request body nếu có thể suy ra từ SecurityContext hoặc dữ liệu đơn.

---

### 2.5. Complaint không public

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

## 3. GIAI ĐOẠN 1: BACKEND - DATABASE & ENTITIES

### 3.1. Entity `DanhGia` / `Review`

Đại diện cho đánh giá công khai của khách hàng.

#### Fields đề xuất

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

#### Constraint đề xuất

```text
UNIQUE(khachHangId, bookingId) WHERE bookingId IS NOT NULL
UNIQUE(khachHangId, reservationId) WHERE reservationId IS NOT NULL
```

#### Check constraint production-minded

Nếu database hỗ trợ, có thể thêm check constraint:

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

#### Ghi chú

```text
- Chỉ review DA_HIEN_THI mới được tính vào rating trung bình.
- Review BI_AN vẫn lưu để audit nhưng không hiển thị public.
- Không hard delete review.
```

---

### 3.2. Entity `PhanHoiDanhGia` / `ReviewReply`

Đại diện cho phản hồi của đối tác dưới một review.

#### Fields đề xuất

```text
id: UUID
danhGiaId: UUID UNIQUE
doiTacId: UUID
noiDung: Text
createdAt
updatedAt
```

#### Rule

```text
- Mỗi review chỉ có một phản hồi chính thức.
- Partner được phép cập nhật phản hồi.
- Không hard delete phản hồi.
```

---

### 3.3. Entity `KhieuNai` / `Complaint`

Đại diện cho ticket khiếu nại giữa khách hàng và đối tác.

#### Fields đề xuất

```text
id: UUID
khachHangId: UUID
hoSoKinhDoanhId: UUID
loaiDichVu: KHACH_SAN | NHA_HANG

bookingId: UUID nullable
reservationId: UUID nullable

tieuDe: String
noiDungTomTat: Text

mucDo: BINH_THUONG | NGHIEM_TRONG

trangThai:
- CHO_PHAN_HOI
- DANG_XU_LY
- DA_GIAI_QUYET
- DA_DONG

lastCustomerMessageAt
lastPartnerResponseAt

createdAt
updatedAt
```

#### Invariant booking/reservation

Complaint cũng áp dụng rule giống Review:

```text
KHACH_SAN -> bookingId required, reservationId null
NHA_HANG  -> reservationId required, bookingId null
```

---

### 3.4. Ý nghĩa trạng thái Complaint

#### `CHO_PHAN_HOI`

Ticket mới được khách tạo, đang chờ đối tác phản hồi.

#### `DANG_XU_LY`

Đối tác đã phản hồi và vấn đề đang được trao đổi/xử lý.

#### `DA_GIAI_QUYET`

Đối tác cho rằng vấn đề đã được xử lý xong. Đây là trạng thái “đề xuất đã giải quyết”, chưa hẳn ticket kết thúc hoàn toàn.

Nếu khách không đồng ý và gửi thêm tin nhắn, hệ thống tự động chuyển trạng thái về:

```text
DANG_XU_LY
```

#### `DA_DONG`

Ticket chính thức kết thúc. Không cho gửi thêm tin nhắn.

---

### 3.5. Entity `TinNhanKhieuNai` / `ComplaintMessage`

Đại diện cho tin nhắn trong ticket khiếu nại.

#### Fields đề xuất

```text
id: UUID
khieuNaiId: UUID
nguoiGuiId: UUID
vaiTroNguoiGui: KHACH_HANG | DOI_TAC
noiDung: Text
createdAt
```

#### Rule

```text
- Không cho edit message sau khi gửi.
- Không hard delete message.
- Không cho gửi message nếu complaint đã DA_DONG.
```


---

### 3.6. Entity `DinhKemPhanHoi` / `FeedbackAttachment`

Đại diện cho file/ảnh đính kèm trong review, complaint hoặc tin nhắn khiếu nại.

Việc bổ sung entity này là cần thiết vì:

```text
- Review có ảnh giúp tăng độ tin cậy.
- Complaint cần bằng chứng như ảnh phòng, ảnh món ăn, hóa đơn, screenshot lỗi.
- Complaint message cần cho phép gửi thêm ảnh trong quá trình trao đổi.
```

#### Thiết kế dùng chung

Thay vì tạo riêng `ReviewImage`, `ComplaintImage`, `ComplaintMessageImage`, nên dùng một entity tổng quát:

```text
DinhKemPhanHoi / FeedbackAttachment
```

Entity này liên kết đến nhiều loại đối tượng thông qua `ownerType` và `ownerId`.

#### Fields đề xuất

```text
id: UUID

ownerType:
- REVIEW
- COMPLAINT
- COMPLAINT_MESSAGE

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

createdAt
```

#### Ý nghĩa field

```text
ownerType:
    Cho biết file đang thuộc về review, complaint hay message trong complaint.

ownerId:
    Id của Review, Complaint hoặc ComplaintMessage.

fileUrl:
    Đường dẫn file sau khi upload. Có thể là local storage, Cloudinary, S3, Firebase Storage.

fileName:
    Tên file gốc hoặc tên file đã chuẩn hóa.

fileType:
    Nhóm file logic để frontend biết cách hiển thị.

mimeType:
    Ví dụ image/png, image/jpeg, application/pdf.

fileSize:
    Dùng để validate giới hạn dung lượng.

uploadedById:
    Người upload file.

uploadedByRole:
    Vai trò người upload, dùng để audit và kiểm tra quyền.
```

#### Rule nghiệp vụ

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

Partner Review Reply:
    - Không cần hỗ trợ ảnh/file trong Sprint hiện tại.
    - Đối tác chỉ phản hồi bằng text.
```

#### Validation file

```text
Kích thước tối đa:
- IMAGE: 5MB/file
- PDF: 10MB/file

Số lượng:
- Review: tối đa 5 ảnh.
- Complaint tạo mới: tối đa 10 file.
- Complaint message: tối đa 5 file/message.

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

#### Bảo mật file upload

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

#### Database constraint đề xuất

Vì `ownerType + ownerId` là polymorphic association, database khó tạo foreign key trực tiếp đến nhiều bảng.

Có 2 cách:

##### Cách 1: Polymorphic association

```text
ownerType
ownerId
```

Ưu điểm:

```text
- Dễ mở rộng.
- Một bảng attachment dùng chung cho nhiều loại đối tượng.
```

Nhược điểm:

```text
- Không có foreign key cứng ở database.
- Phải kiểm tra tính tồn tại owner ở service layer.
```

##### Cách 2: Ba khóa ngoại nullable

```text
reviewId nullable
complaintId nullable
complaintMessageId nullable
```

Ưu điểm:

```text
- Có thể tạo foreign key rõ.
- Dễ query với JPA hơn.
```

Nhược điểm:

```text
- Bảng có nhiều cột nullable.
- Mỗi lần mở rộng loại attachment phải thêm cột.
```

#### Khuyến nghị cho đồ án

Nên dùng cách 1:

```text
ownerType + ownerId
```

Vì module có nhiều loại attachment và cần giữ thiết kế gọn.

#### SQL gợi ý

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

#### Enum đề xuất

```text
AttachmentOwnerType:
- REVIEW
- COMPLAINT
- COMPLAINT_MESSAGE

AttachmentFileType:
- IMAGE
- PDF
- OTHER
```

#### Ghi chú quan trọng

```text
- Review attachment có thể public nếu review DA_HIEN_THI.
- Complaint attachment không public.
- Complaint message attachment không public.
- Nếu review bị chuyển sang BI_AN, ảnh của review cũng không hiển thị public.
```

---

## 4. GIAI ĐOẠN 2: BACKEND - LOGIC & APIs

### 4.1. Customer Review APIs

#### Tạo đánh giá

```http
POST /api/v1/user/reviews
```

#### Logic

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
10. Chạy ProfanityFilterService.
11. Validate danh sách ảnh đính kèm nếu có.
12. Nếu pass: trangThai = DA_HIEN_THI.
13. Nếu fail: trangThai = BI_AN.
14. Lưu review.
15. Lưu danh sách attachment với ownerType = REVIEW.
16. Nếu review DA_HIEN_THI, cập nhật ratingAverage và reviewCount của cơ sở.
```

---

### 4.2. Public Review APIs

#### Lấy review công khai của cơ sở

Nếu project có BusinessProfile chung:

```http
GET /api/v1/public/business-profiles/{id}/reviews
```

Nếu project tách hotel/restaurant:

```http
GET /api/v1/public/hotels/{id}/reviews
GET /api/v1/public/restaurants/{id}/reviews
```

#### Query params

```text
page: default 0
size: default 10, max 20
sort: newest | oldest | rating_desc | rating_asc
```

#### Rule

```text
- Chỉ trả review DA_HIEN_THI.
- Default sort = newest.
- Có pagination bắt buộc.
- Không trả list không giới hạn.
```

---

### 4.3. User Review APIs

#### Lấy review của khách hàng

```http
GET /api/v1/user/reviews
```

#### Query params

```text
page: default 0
size: default 10, max 20
sort: newest | oldest
```


---

### 4.3.1. Review Attachment APIs

Có 2 cách xử lý upload ảnh review.

#### Cách A: Upload cùng lúc khi tạo review

```http
POST /api/v1/user/reviews
Content-Type: multipart/form-data
```

Form data:

```text
request: JSON review request
files: List<MultipartFile>
```

Ưu điểm:

```text
- UX đơn giản.
- Tạo review và ảnh trong một flow.
```

Nhược điểm:

```text
- Backend xử lý multipart phức tạp hơn một chút.
```

#### Cách B: Tạo review trước, upload ảnh sau

```http
POST /api/v1/user/reviews
POST /api/v1/user/reviews/{reviewId}/attachments
```

Ưu điểm:

```text
- API tách biệt rõ.
- Dễ retry upload file.
```

Nhược điểm:

```text
- Cần xử lý trường hợp review tạo xong nhưng upload ảnh lỗi.
```

#### Khuyến nghị

Với Sprint hiện tại, dùng Cách A để dễ demo:

```http
POST /api/v1/user/reviews
Content-Type: multipart/form-data
```

Rule:

```text
- Chỉ customer sở hữu booking/reservation mới upload được ảnh review.
- Chỉ upload ảnh khi tạo review.
- Không hỗ trợ thêm/xóa ảnh review sau khi đã tạo trong Sprint hiện tại.
```

---

### 4.4. Partner Reply APIs

#### Tạo phản hồi

```http
POST /api/v1/partner/reviews/{reviewId}/reply
```

#### Cập nhật phản hồi

```http
PUT /api/v1/partner/reviews/{reviewId}/reply
```

#### Logic

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

### 4.5. Complaint APIs

#### Customer APIs

```http
POST /api/v1/user/complaints
GET  /api/v1/user/complaints
GET  /api/v1/user/complaints/{id}
POST /api/v1/user/complaints/{id}/messages
PUT  /api/v1/user/complaints/{id}/close
```

#### Partner APIs

```http
GET  /api/v1/partner/complaints
GET  /api/v1/partner/complaints/{id}
POST /api/v1/partner/complaints/{id}/messages
PUT  /api/v1/partner/complaints/{id}/status
```


#### Complaint upload APIs

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
```

---

### 4.6. Complaint List Query Contract

#### Customer complaint list

```http
GET /api/v1/user/complaints?page=0&size=10&status=DANG_XU_LY&sort=updated_desc
```

#### Partner complaint list

```http
GET /api/v1/partner/complaints?page=0&size=10&status=CHO_PHAN_HOI&mucDo=NGHIEM_TRONG&sort=updated_desc
```

#### Query params

```text
page: default 0
size: default 10, max 20
status: optional
mucDo: optional
sort: newest | oldest | updated_desc
```

#### Rule

```text
- Complaint list luôn có pagination.
- Default sort = updated_desc.
- Complaint không có public endpoint.
```

---

### 4.7. Logic tạo complaint

```text
1. Khách hàng đăng nhập.
2. Kiểm tra loaiDichVu hợp lệ.
3. Kiểm tra invariant bookingId/reservationId.
4. Kiểm tra booking/reservation thuộc khách.
5. Kiểm tra đơn có tồn tại và liên quan đúng cơ sở.
6. Validate tiêu đề/nội dung.
7. Chạy ProfanityFilterService.
8. Validate danh sách file/ảnh bằng chứng nếu có.
9. Tạo complaint với trạng thái CHO_PHAN_HOI.
10. Lưu attachment với ownerType = COMPLAINT nếu có.
11. Tạo message đầu tiên từ khách.
12. Nếu message đầu tiên có file riêng, lưu attachment với ownerType = COMPLAINT_MESSAGE.
13. Set lastCustomerMessageAt = now.
```

---

### 4.8. Logic customer gửi thêm message

```text
1. Customer đăng nhập.
2. Kiểm tra complaint thuộc chính customer.
3. Kiểm tra complaint chưa DA_DONG.
4. Validate nội dung.
5. Validate file/ảnh đính kèm nếu có.
6. Chạy ProfanityFilterService.
7. Tạo ComplaintMessage.
8. Lưu attachment với ownerType = COMPLAINT_MESSAGE nếu có.
9. Set lastCustomerMessageAt = now.
10. Nếu complaint đang DA_GIAI_QUYET, chuyển lại DANG_XU_LY.
```

---

### 4.9. Logic partner trả lời complaint

```text
1. Partner đăng nhập.
2. Kiểm tra complaint thuộc cơ sở của partner.
3. Kiểm tra complaint chưa DA_DONG.
4. Validate nội dung.
5. Validate file/ảnh đính kèm nếu có.
6. Chạy ProfanityFilterService.
7. Tạo ComplaintMessage.
8. Lưu attachment với ownerType = COMPLAINT_MESSAGE nếu có.
9. Set lastPartnerResponseAt = now.
10. Nếu trạng thái đang CHO_PHAN_HOI thì chuyển sang DANG_XU_LY.
```

---

## 5. SLA 48h

Partner cần phản hồi tin nhắn khách hàng trong 48 giờ.

### Rule quá hạn

```text
Nếu lastCustomerMessageAt != null
và (
    lastPartnerResponseAt == null
    hoặc lastPartnerResponseAt < lastCustomerMessageAt
)
và now - lastCustomerMessageAt > 48h
=> quaHanXuLy = true
```

### Cách triển khai trong scope hiện tại

Với scope đồ án, `quaHanXuLy` nên là computed field khi query complaint list/detail:

```text
overdue = computeOverdue(lastCustomerMessageAt, lastPartnerResponseAt, now)
```

Không cần persist field nếu chưa có scheduler.

### Production extension

Nếu mở rộng production thật:

```text
- Scheduler đánh dấu overdue định kỳ.
- Notification nhắc partner.
- Báo cáo SLA breach.
- Escalation cho admin/support.
```

---

## 6. GIAI ĐOẠN 3: FRONTEND - UX FLOW

### 6.1. Customer Portal

#### Trang chi tiết dịch vụ

Hiển thị:

```text
- Rating summary.
- Điểm trung bình.
- Tổng số review.
- Danh sách review.
- Phản hồi của partner dưới từng review nếu có.
- Pagination review.
- Sort review.
- Hiển thị ảnh review dạng thumbnail/gallery nếu review có ảnh.
```

#### Trang lịch sử đặt chỗ

Hiển thị nút:

```text
- "Viết đánh giá" nếu đơn DA_HOAN_THANH và chưa review.
- "Đã đánh giá" nếu đã review.
- "Khiếu nại" nếu khách muốn báo sự cố.
- Khi viết review cho phép upload ảnh trải nghiệm thực tế.
- Khi tạo khiếu nại cho phép upload ảnh/file bằng chứng.
```

#### Trang khiếu nại của tôi

```text
- Danh sách ticket.
- Trạng thái xử lý.
- Mức độ.
- Badge quá hạn nếu có.
- Thời gian cập nhật cuối.
- Click vào ticket để xem chat 1-1.
```

#### Chi tiết ticket

```text
- Thông tin ticket.
- Timeline tin nhắn.
- Ô nhập tin nhắn mới.
- Cho phép đính kèm ảnh/file trong tin nhắn.
- Preview file trước khi gửi.
- Hiển thị ảnh/file trong timeline chat.
- Nếu ticket DA_DONG thì disable ô nhập và upload file.
- Nút đóng ticket nếu vấn đề đã xong.
```

---

### 6.2. Partner Dashboard

#### Review Tab

```text
- Danh sách review thuộc cơ sở của partner.
- Filter theo số sao.
- Filter theo trạng thái hiển thị/bị ẩn.
- Hiển thị ảnh review nếu có.
- Nút phản hồi.
- Nếu đã phản hồi thì hiển thị nội dung và nút sửa.
```

#### Complaint Tab

```text
- Danh sách ticket.
- Badge quá hạn 48h.
- Filter theo trạng thái.
- Filter theo mức độ.
- Click vào ticket để chat với khách.
- Xem ảnh/file bằng chứng của complaint.
- Gửi message có thể kèm ảnh/file nếu cần.
- Cập nhật trạng thái: DANG_XU_LY, DA_GIAI_QUYET, DA_DONG.
```

---

## 7. Profanity Filter

Triển khai service mức cơ bản cho MVP:

```text
ProfanityFilterService
- boolean containsProfanity(String text)
- String normalize(String text)
```

### Logic tối thiểu

```text
1. Lowercase.
2. Trim.
3. Normalize Unicode nếu có thể.
4. Remove repeated spaces.
5. Check contains từ trong blacklist.
```

### Áp dụng cho

```text
- Review content.
- Partner reply.
- Complaint title.
- Complaint message.
```

### Rule xử lý

```text
Review vi phạm:
- Vẫn lưu nhưng trạng thái BI_AN.

Partner reply / complaint message vi phạm:
- Từ chối lưu và trả 400.
```

### Ghi chú production

Đây không phải moderation hoàn chỉnh. Production thật có thể cần:

```text
- Moderation API.
- Dictionary quản trị được.
- Xử lý teencode/biến thể chính tả.
- Audit log.
```

---

## 8. Transaction Consistency

Khi tạo review:

```text
@Transactional
1. Validate booking/reservation.
2. Lưu review.
3. Nếu review DA_HIEN_THI:
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

---

## 9. Edit/Delete Policy

### 9.1. Review

```text
- Customer chỉ được tạo 1 review cho mỗi booking/reservation.
- Sprint hiện tại: không cho edit review.
- Sprint hiện tại: không cho delete review.
- Nếu cần ẩn review, dùng trangThai = BI_AN.
```

Lý do:

```text
Review là dữ liệu liên quan đến trải nghiệm thật và ảnh hưởng rating.
Không nên cho sửa/xóa tùy ý trong scope ban đầu.
```

### 9.2. Partner Reply

```text
- Cho phép tạo 1 reply cho mỗi review.
- Cho phép update reply.
- Không hard delete reply.
```

### 9.3. Complaint Message

```text
- Không cho edit sau khi gửi.
- Không cho delete sau khi gửi.
- Không cho gửi message nếu complaint đã DA_DONG.
```

---

## 10. Security Checklist

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

- Complaint không public ở bất kỳ endpoint nào.

- Không tin khachHangId/partnerId từ request body.
  Luôn lấy từ SecurityContext.
```

---

## 11. Validation Checklist

### Review

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

### Partner Reply

```text
- noiDung không rỗng.
- noiDung tối đa 1000 ký tự.
- Review phải thuộc cơ sở của partner.
```

### Complaint

```text
- loaiDichVu hợp lệ.
- bookingId/reservationId đúng invariant.
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

### Complaint Message

```text
- noiDung không rỗng nếu không có file.
- Nếu có file thì nội dung có thể ngắn nhưng vẫn nên validate tối thiểu nếu cần.
- noiDung tối đa 2000 ký tự.
- Không cho gửi nếu ticket đã DA_DONG.
- File đính kèm tối đa 5 file/message.
- Chỉ cho IMAGE và PDF.
```

### Pagination

```text
- page >= 0.
- size từ 1 đến 20.
- sort nằm trong whitelist.
```

---

## 12. API Response gợi ý

### Review response

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

### Complaint response

```json
{
  "id": "uuid",
  "title": "Phòng không đúng mô tả",
  "serviceType": "KHACH_SAN",
  "severity": "NGHIEM_TRONG",
  "status": "DANG_XU_LY",
  "overdue": false,
  "createdAt": "2026-05-28T10:00:00",
  "updatedAt": "2026-05-28T12:00:00",
  "attachments": [
    {
      "id": "uuid",
      "fileUrl": "https://cdn.travi.vn/complaints/evidence-1.jpg",
      "fileType": "IMAGE",
      "mimeType": "image/jpeg"
    }
  ]
}
```

### Complaint detail response

```json
{
  "id": "uuid",
  "title": "Phòng không đúng mô tả",
  "serviceType": "KHACH_SAN",
  "severity": "NGHIEM_TRONG",
  "status": "DANG_XU_LY",
  "overdue": false,
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
  ]
}
```

---

## 13. Checklist triển khai

### Backend

```text
- [ ] Tạo migration cho review/reply/complaint/message.
- [ ] Thêm constraint bookingId/reservationId theo loaiDichVu.
- [ ] Tạo entity Review.
- [ ] Tạo entity ReviewReply.
- [ ] Tạo entity Complaint.
- [ ] Tạo entity ComplaintMessage.
- [ ] Tạo entity FeedbackAttachment / DinhKemPhanHoi.
- [ ] Tạo repository.
- [ ] Tạo DTO request/response.
- [ ] Tạo AttachmentService / FileStorageService.
- [ ] Tạo ProfanityFilterService.
- [ ] Tạo ReviewService.
- [ ] Tạo ComplaintService.
- [ ] Tạo UserReviewController.
- [ ] Tạo PartnerReviewController.
- [ ] Tạo PublicReviewController.
- [ ] Tạo UserComplaintController.
- [ ] Tạo PartnerComplaintController.
- [ ] Thêm security check theo owner/partner.
- [ ] Thêm transaction khi tạo review và cập nhật rating.
- [ ] Thêm pagination/sort cho review và complaint list.
- [ ] Thêm multipart upload cho review/complaint/message.
- [ ] Validate file size, mime type, số lượng file.
- [ ] Kiểm tra quyền xem attachment.
```

### Frontend

```text
- [ ] Hiển thị rating summary ở detail page.
- [ ] Hiển thị review list ở detail page.
- [ ] Thêm sort/pagination review.
- [ ] Thêm nút viết đánh giá ở booking history.
- [ ] Tạo form review có upload ảnh.
- [ ] Preview ảnh review trước khi gửi.
- [ ] Tạo trang My Complaints.
- [ ] Tạo trang Complaint Detail dạng chat.
- [ ] Cho phép gửi ảnh/file trong complaint message.
- [ ] Hiển thị file/ảnh bằng chứng trong complaint detail.
- [ ] Disable input/upload nếu complaint DA_DONG.
- [ ] Partner dashboard Review Tab.
- [ ] Partner xem ảnh review nếu có.
- [ ] Partner dashboard Complaint Tab.
- [ ] Partner xem/gửi attachment trong complaint.
- [ ] Validate form phía frontend.
```

---

## 14. Ưu tiên triển khai nếu thời gian gấp

### P0

```text
- Review sau đơn hoàn tất.
- Review có ảnh optional.
- Public review list có pagination.
- Partner reply review.
- Update ratingAverage/reviewCount.
- Invariant bookingId/reservationId.
```

### P1

```text
- Customer tạo complaint.
- Complaint có ảnh/file bằng chứng.
- Partner xem và trả lời complaint.
- Complaint message dạng chat đơn giản.
- Complaint message có thể kèm ảnh/file.
- Complaint visibility/security.
```

### P2

```text
- SLA 48h computed field.
- Filter trạng thái/mức độ.
- UI polish.
- Production extension cho moderation/scheduler.
```

---

## 15. Kết luận

Module 5 nên tập trung vào giá trị chính:

```text
Khách đã sử dụng dịch vụ -> được đánh giá/khiếu nại.
Đối tác -> được phản hồi và xử lý.
Hệ thống -> kiểm soát nội dung và đảm bảo quyền truy cập.
```

Thiết kế sau khi cập nhật tốt hơn vì:

```text
- Không cho review ảo.
- Gắn review với đơn thật.
- Có invariant rõ cho booking/reservation.
- Hỗ trợ cả khách sạn và nhà hàng.
- Complaint không public.
- Trạng thái complaint rõ nghĩa hơn.
- Có chính sách edit/delete đơn giản.
- Có pagination/sort contract rõ.
- Có ảnh/file đính kèm cho review và complaint.
- Không cần Admin vẫn đủ flow nghiệp vụ.
- Dễ demo và phù hợp tiến độ đồ án.
```
