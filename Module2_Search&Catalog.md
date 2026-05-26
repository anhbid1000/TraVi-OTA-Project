# 🔍 MODULE 2: TÌM KIẾM & TRẢI NGHIỆM CATALOG

## Mục tiêu Module

Xây dựng hệ thống tìm kiếm và hiển thị dịch vụ công khai cho TraVi OTA, cho phép khách vãng lai và khách hàng đã đăng nhập có thể:

- Tìm kiếm khách sạn theo địa điểm, ngày nhận/trả phòng, số khách, khoảng giá, hạng sao, tiện ích.
- Tìm kiếm nhà hàng theo địa điểm, ngày giờ dùng bữa, số khách, loại ẩm thực.
- Xem danh sách kết quả dạng catalog.
- Xem chi tiết khách sạn/nhà hàng.
- Xem phòng/bàn/món ăn khả dụng theo thời gian đã chọn.
- Chuyển tiếp sang Module 3 để đặt phòng/đặt bàn/thanh toán.

Module này là “mặt tiền” của hệ thống OTA, nên cần tối ưu cả **trải nghiệm người dùng**, **payload API**, **hiệu năng truy vấn**, và **độ chính xác của availability**.

---

# GIAI ĐOẠN 1: BACKEND - THIẾT KẾ DTOs

## 1.1. Nguyên tắc thiết kế DTO

Tuyệt đối không trả trực tiếp Entity ra ngoài API.

Lý do:

- Tránh lộ dữ liệu nhạy cảm.
- Tránh vòng lặp vô hạn khi serialize quan hệ JPA.
- Tránh lỗi `LazyInitializationException`.
- Giảm payload trả về.
- Tách API contract khỏi database model.
- Frontend nhận đúng dữ liệu cần cho từng màn hình.

---

## 1.2. DTO cho màn hình Catalog/List

## `HotelCatalogDTO`

Dùng cho danh sách khách sạn.

Field đề xuất:

- `id`
- `tenKhachSan`
- `diaChi`
- `thanhPho`
- `hangSao`
- `thumbnailUrl`
- `diemDanhGiaTrungBinh`
- `soLuongDanhGia`
- `giaThapNhat`
- `soPhongConTrong`
- `tienIchNoiBat`
- `trangThaiHoatDong`

### Mục tiêu

DTO này phải nhẹ, chỉ đủ để render card khách sạn.

Không trả:

- Danh sách phòng đầy đủ.
- Danh sách ảnh đầy đủ.
- Chính sách chi tiết.
- Entity partner.
- Thông tin pháp lý.

---

## `RestaurantCatalogDTO`

Dùng cho danh sách nhà hàng.

Field đề xuất:

- `id`
- `tenNhaHang`
- `diaChi`
- `thanhPho`
- `loaiAmThuc`
- `thumbnailUrl`
- `diemDanhGiaTrungBinh`
- `soLuongDanhGia`
- `khoangGia`
- `soGheConTrong`
- `tienIchNoiBat`
- `trangThaiHoatDong`

### Mục tiêu

DTO này chỉ phục vụ card nhà hàng trong catalog.

Không trả:

- Toàn bộ menu.
- Toàn bộ món ăn.
- Thông tin pháp lý.
- Thông tin kiểm duyệt.

---

## 1.3. DTO cho màn hình Detail

## `HotelDetailDTO`

Dùng cho trang chi tiết khách sạn.

Field đề xuất:

- `id`
- `tenKhachSan`
- `moTa`
- `diaChi`
- `thanhPho`
- `quanHuyen`
- `phuongXa`
- `kinhDo`
- `viDo`
- `hangSao`
- `gioNhanPhong`
- `gioTraPhong`
- `chinhSach`
- `diemDanhGiaTrungBinh`
- `soLuongDanhGia`
- `images`
- `amenities`
- `availableRooms`

Trong đó:

### `RoomAvailabilityDTO`

- `roomId`
- `tenPhong`
- `loaiPhong`
- `moTa`
- `dienTich`
- `soKhachToiDa`
- `soGiuong`
- `giaCoBan`
- `soLuongConTrong`
- `images`
- `amenities`

---

## `RestaurantDetailDTO`

Dùng cho trang chi tiết nhà hàng.

Field đề xuất:

- `id`
- `tenNhaHang`
- `moTa`
- `diaChi`
- `thanhPho`
- `quanHuyen`
- `phuongXa`
- `kinhDo`
- `viDo`
- `loaiAmThuc`
- `gioMoCua`
- `gioDongCua`
- `coDatBanTruoc`
- `coDatMonTruoc`
- `diemDanhGiaTrungBinh`
- `soLuongDanhGia`
- `images`
- `amenities`
- `availableTables`
- `menus`

### `TableAvailabilityDTO`

- `tableId`
- `tenBan`
- `soGhe`
- `viTri`
- `trangThai`
- `available`

### `MenuDTO`

- `id`
- `tenThucDon`
- `moTa`
- `items`

### `MenuItemDTO`

- `id`
- `tenMon`
- `moTa`
- `gia`
- `danhMucMon`
- `anhMonUrl`
- `trangThai`

Chỉ trả món có:

```text
trangThai = DANG_BAN
deleted = false
________________________________________
1.4. DTO dùng chung
AmenityDTO
•	id
•	tenTienIch
•	icon
•	moTa
ImageDTO
•	id
•	url
•	thuTuHienThi
•	laAnhDaiDien
PolicyDTO
•	gioNhanPhong
•	gioTraPhong
•	gioMoCua
•	gioDongCua
•	chinhSachHuy
•	chinhSachHoanTien
•	ghiChuKhac
WeatherForecastDTO
•	date
•	temperature
•	condition
•	icon
•	warningMessage
________________________________________
GIAI ĐOẠN 2: BACKEND - CORE SEARCH LOGIC
2.1. Điều kiện bắt buộc khi search
Tất cả API public chỉ được trả về hồ sơ có:
trangThaiKiemDuyet = DA_DUYET
trangThaiHoatDong = DANG_HOAT_DONG
deleted = false
Không được hiển thị:
•	Hồ sơ chưa duyệt.
•	Hồ sơ bị từ chối.
•	Hồ sơ bị khóa.
•	Hồ sơ tạm dừng.
•	Hồ sơ đã xóa mềm.
________________________________________
2.2. Cấu hình Repository
Cập nhật repository:
public interface KhachSanRepository extends JpaRepository<KhachSan, Long>, JpaSpecificationExecutor<KhachSan> {
}
public interface NhaHangRepository extends JpaRepository<NhaHang, Long>, JpaSpecificationExecutor<NhaHang> {
}
________________________________________
2.3. Hotel Specification
Tạo class:
HotelSpecification
Các điều kiện lọc động:
•	isPublicVisible()
•	hasCity(String city)
•	priceBetween(BigDecimal minPrice, BigDecimal maxPrice)
•	hasStarRatings(List<Integer> stars)
•	hasAmenities(List<Long> amenityIds)
•	keywordContains(String keyword)
•	hasGuestCapacity(Integer guests)
Filter đề xuất
Query params:
•	city
•	keyword
•	checkIn
•	checkOut
•	guests
•	minPrice
•	maxPrice
•	stars
•	amenities
•	sort
•	page
•	size
________________________________________
2.4. Restaurant Specification
Tạo class:
RestaurantSpecification
Các điều kiện lọc động:
•	isPublicVisible()
•	hasCity(String city)
•	hasCuisineType(String cuisineType)
•	hasAmenities(List<Long> amenityIds)
•	keywordContains(String keyword)
•	hasGuestCapacity(Integer guests)
•	priceRangeBetween(...)
Filter đề xuất
Query params:
•	city
•	keyword
•	date
•	time
•	guests
•	cuisineType
•	amenities
•	minPrice
•	maxPrice
•	sort
•	page
•	size
________________________________________
GIAI ĐOẠN 3: BACKEND - AVAILABILITY LOGIC
3.1. Availability khách sạn
Vì Phong trong Module 1 đại diện cho loại phòng và có field soLuongPhong, không nên chỉ loại trừ roomId.
Cần tính:
soLuongConTrong = soLuongPhong - soLuongDaDuocDatTrongKhoangNgay
Chỉ trả phòng nếu:
soLuongConTrong > 0
________________________________________
3.2. Công thức overlap ngày khách sạn
Một đơn đặt phòng bị overlap với khoảng khách đang tìm nếu:
existing.checkIn < requested.checkOut
AND existing.checkOut > requested.checkIn
Ví dụ:
•	Khách tìm: 10/06 → 12/06
•	Đơn cũ: 11/06 → 13/06
•	Có overlap.
Các trạng thái đơn cần tính là đang giữ chỗ/thành công:
CHO_THANH_TOAN
DA_THANH_TOAN
DA_XAC_NHAN
DANG_SU_DUNG
Không tính:
DA_HUY
THANH_TOAN_THAT_BAI
DA_HOAN_TIEN
________________________________________
3.3. Query tính số phòng đã đặt
Repository cần có query kiểu:
SUM(soLuongPhongDat)
WHERE roomId = :roomId
AND status IN (...)
AND checkIn < :requestedCheckOut
AND checkOut > :requestedCheckIn
Sau đó:
availableQuantity = room.soLuongPhong - bookedQuantity
________________________________________
3.4. Availability nhà hàng
Nhà hàng cần tính theo khung giờ, không chỉ một thời điểm.
Ví dụ:
reservationTime = 18:00
duration = 2 giờ
requestedStart = 18:00
requestedEnd = 20:00
Một đơn đặt bàn bị overlap nếu:
existing.startTime < requestedEnd
AND existing.endTime > requestedStart
________________________________________
3.5. Cách tính ghế trống nhà hàng
Cách đơn giản cho Sprint 2:
soGheConTrong = tongSoGheDangHoatDong - soGheDaDatTrongKhungGio
Chỉ trả nhà hàng nếu:
soGheConTrong >= guests
Các trạng thái đơn cần tính:
CHO_THANH_TOAN
DA_THANH_TOAN
DA_XAC_NHAN
DANG_PHUC_VU
Không tính:
DA_HUY
KHACH_KHONG_DEN
THANH_TOAN_THAT_BAI
________________________________________
3.6. Lưu ý production
Cách tính tổng ghế là đủ cho Sprint 2.
Nếu muốn chính xác hơn ở giai đoạn sau, cần booking theo từng Ban cụ thể và tối ưu thuật toán gán bàn.
Ví dụ:
•	Khách 6 người nên ưu tiên bàn 6 ghế.
•	Không nên ghép 3 bàn 2 ghế nếu còn bàn 6 ghế.
•	Có thể cần thuật toán table allocation.
________________________________________
GIAI ĐOẠN 4: BACKEND - PUBLIC APIs
4.1. Quy ước endpoint
Module này là public API, dành cho cả khách vãng lai và khách đã đăng nhập.
Thống nhất prefix:
/api/v1/public
Cần mở permitAll() trong SecurityConfig cho:
/api/v1/public/**
________________________________________
4.2. API tìm kiếm khách sạn
GET /api/v1/public/hotels/search
Query params bắt buộc
•	city
•	checkIn
•	checkOut
•	guests
Query params tùy chọn
•	keyword
•	minPrice
•	maxPrice
•	stars
•	amenities
•	sort
•	page
•	size
Response
Trả về:
Page<HotelCatalogDTO>
Sort hỗ trợ
•	price_asc
•	price_desc
•	rating_desc
•	popular_desc
________________________________________
4.3. API xem chi tiết khách sạn
GET /api/v1/public/hotels/{id}
Query params bắt buộc
•	checkIn
•	checkOut
•	guests
Response
Trả về:
HotelDetailDTO
Trong đó availableRooms chỉ gồm phòng còn trống theo khoảng ngày khách chọn.
________________________________________
4.4. API tìm kiếm nhà hàng
GET /api/v1/public/restaurants/search
Query params bắt buộc
•	city
•	date
•	time
•	guests
Query params tùy chọn
•	keyword
•	cuisineType
•	amenities
•	minPrice
•	maxPrice
•	sort
•	page
•	size
Response
Trả về:
Page<RestaurantCatalogDTO>
________________________________________
4.5. API xem chi tiết nhà hàng
GET /api/v1/public/restaurants/{id}
Query params khuyến nghị
•	date
•	time
•	guests
Response
Trả về:
RestaurantDetailDTO
Trong đó:
•	availableTables phản ánh khả dụng theo ngày giờ khách chọn.
•	menus chỉ trả món đang bán.
________________________________________
4.6. API dự báo thời tiết
GET /api/v1/public/weather/forecast
Query params
•	lat
•	lng
•	date
Logic
•	Nhận tọa độ GPS của khách sạn/nhà hàng.
•	Gọi OpenWeatherMap API hoặc provider tương đương.
•	Trả về WeatherForecastDTO.
Ghi chú
Đây là tính năng phụ, không phải core bắt buộc của Module 2.
Nếu deadline quá gấp, có thể mock response trước hoặc đẩy sang cuối module.
________________________________________
GIAI ĐOẠN 5: FRONTEND - HOME SEARCH
5.1. Home Page
Thành phần chính
•	Hero Banner.
•	Search Bar lớn.
•	Tabs:
  o Lưu trú
  o Ẩm thực
•	Section khách sạn nổi bật.
•	Section nhà hàng nổi bật.
________________________________________
5.2. Search Bar khách sạn
Field:
•	Địa điểm
•	Ngày nhận phòng
•	Ngày trả phòng
•	Số khách
Validation:
•	Không được chọn ngày trong quá khứ.
•	checkOut phải lớn hơn checkIn.
•	guests >= 1.
•	city không được rỗng.
Khi submit, điều hướng:
/hotels?city=DaLat&checkIn=2026-06-01&checkOut=2026-06-03&guests=2
________________________________________
5.3. Search Bar nhà hàng
Field:
•	Địa điểm
•	Ngày dùng bữa
•	Giờ dùng bữa
•	Số khách
•	Loại ẩm thực nếu có
Validation:
•	Không được chọn ngày giờ trong quá khứ.
•	guests >= 1.
•	city không được rỗng.
Khi submit, điều hướng:
/restaurants?city=DaLat&date=2026-06-01&time=18:00&guests=4
________________________________________
GIAI ĐOẠN 6: FRONTEND - CATALOG/LIST PAGE
6.1. Nguyên tắc URL state
Toàn bộ filter/search phải được sync lên URL query params.
Lý do:
•	Reload không mất filter.
•	Copy link chia sẻ được.
•	Back/forward browser hoạt động đúng.
•	Dễ debug API.
Ví dụ:
/hotels?city=DaLat&checkIn=2026-06-01&checkOut=2026-06-03&guests=2&minPrice=500000&stars=4,5
________________________________________
6.2. Hotel Catalog Page
Layout
Bên trái:
•	Sidebar filter.
Bên phải:
•	Header kết quả.
•	Sort dropdown.
•	Danh sách hotel cards.
•	Pagination.
Sidebar filter
•	Khoảng giá.
•	Hạng sao.
•	Tiện ích.
•	Điểm đánh giá.
•	Loại khách sạn nếu có.
Sort
•	Giá tăng dần.
•	Giá giảm dần.
•	Đánh giá cao nhất.
•	Phổ biến nhất.
Hotel Card
Hiển thị:
•	Ảnh thumbnail.
•	Tên khách sạn.
•	Địa chỉ.
•	Hạng sao.
•	Điểm đánh giá.
•	Giá thấp nhất.
•	Số phòng còn trống.
•	Nút xem chi tiết.
________________________________________
6.3. Restaurant Catalog Page
Sidebar filter
•	Loại ẩm thực.
•	Khoảng giá.
•	Tiện ích.
•	Điểm đánh giá.
Restaurant Card
Hiển thị:
•	Ảnh thumbnail.
•	Tên nhà hàng.
•	Địa chỉ.
•	Loại ẩm thực.
•	Điểm đánh giá.
•	Khoảng giá.
•	Số ghế còn trống.
•	Nút xem chi tiết.
________________________________________
6.4. Loading, Empty, Error State
Skeleton Loading
Hiển thị skeleton card khi đang gọi API.
Empty State
Nếu không có kết quả:
Rất tiếc, không tìm thấy kết quả phù hợp.
Gợi ý người dùng:
•	Thử đổi ngày.
•	Tăng khoảng giá.
•	Chọn khu vực khác.
•	Giảm số lượng khách.
Error State
Nếu API lỗi:
Không thể tải dữ liệu. Vui lòng thử lại sau.
Có nút:
Thử lại
________________________________________
GIAI ĐOẠN 7: FRONTEND - DETAIL PAGE
7.1. Hotel Detail Page
Route
/hotels/:id?checkIn=...&checkOut=...&guests=...
Layout
•	Gallery ảnh.
•	Tên khách sạn, hạng sao, điểm đánh giá.
•	Địa chỉ.
•	Mô tả.
•	Tiện ích.
•	Chính sách.
•	Bản đồ.
•	Danh sách phòng còn trống.
•	Sticky Booking Bar.
________________________________________
7.2. Danh sách phòng khách sạn
Mỗi dòng/card phòng hiển thị:
•	Ảnh phòng.
•	Tên phòng.
•	Diện tích.
•	Sức chứa.
•	Số giường.
•	Tiện ích nổi bật.
•	Giá.
•	Số lượng còn trống.
•	Nút chọn phòng.
Khi chọn phòng:
•	Cập nhật sticky booking bar.
•	Cho phép chọn số lượng phòng.
•	Không cho chọn vượt quá soLuongConTrong.
________________________________________
7.3. Restaurant Detail Page
Route
/restaurants/:id?date=...&time=...&guests=...
Layout
•	Gallery ảnh.
•	Tên nhà hàng, điểm đánh giá.
•	Địa chỉ.
•	Loại ẩm thực.
•	Mô tả.
•	Tiện ích.
•	Bản đồ.
•	Danh sách món ăn.
•	Trạng thái bàn/ghế còn trống.
•	Sticky Booking Bar.
________________________________________
7.4. Danh sách món ăn
Mỗi món hiển thị:
•	Ảnh món.
•	Tên món.
•	Mô tả.
•	Giá.
•	Trạng thái.
•	Nút thêm vào đơn đặt trước nếu nhà hàng cho phép đặt món trước.
Không hiển thị món:
TAM_HET
NGUNG_BAN
deleted = true
________________________________________
7.5. Sticky Booking Bar
Với khách sạn
Hiển thị:
•	Ngày nhận phòng.
•	Ngày trả phòng.
•	Số khách.
•	Phòng đã chọn.
•	Số lượng phòng.
•	Tổng tiền tạm tính.
•	Nút Đặt ngay.
Với nhà hàng
Hiển thị:
•	Ngày dùng bữa.
•	Giờ dùng bữa.
•	Số khách.
•	Món đã chọn trước nếu có.
•	Tổng tiền tạm tính nếu có món.
•	Nút Đặt bàn.
Khi click:
•	Điều hướng sang Module 3 Checkout.
•	Truyền dữ liệu qua URL/state/store tùy kiến trúc frontend.
________________________________________
7.6. Bản đồ
Có thể dùng:
•	Leaflet.
•	Google Maps iframe.
•	Google Maps API nếu có key.
Yêu cầu:
•	Hiển thị marker theo kinhDo, viDo.
•	Nếu thiếu tọa độ, fallback hiển thị địa chỉ text.
________________________________________
7.7. Cảnh báo thời tiết thông minh
Nếu có API weather:
•	Gọi theo tọa độ và ngày khách chọn.
•	Hiển thị icon nắng/mưa/bão.
•	Hiển thị badge cảnh báo.
Ví dụ:
Trời dự báo có mưa, bạn nên ưu tiên dịch vụ trong nhà.
Nếu API lỗi:
•	Không block trang detail.
•	Chỉ ẩn weather widget.
________________________________________
GIAI ĐOẠN 8: FRONTEND SERVICES & STATE
8.1. API services
Tạo service:
•	hotelService.ts
•	restaurantService.ts
•	weatherService.ts
Method đề xuất:
searchHotels(params)
getHotelDetail(id, params)
searchRestaurants(params)
getRestaurantDetail(id, params)
getWeatherForecast(params)
________________________________________
8.2. Types
Tạo type tương ứng:
•	HotelCatalog
•	HotelDetail
•	RoomAvailability
•	RestaurantCatalog
•	RestaurantDetail
•	TableAvailability
•	Menu
•	MenuItem
•	Amenity
•	Image
•	WeatherForecast
________________________________________
8.3. Custom hooks
Có thể tạo:
•	useHotelSearch
•	useRestaurantSearch
•	useHotelDetail
•	useRestaurantDetail
•	useDebounce
•	useQueryParams
________________________________________
GIAI ĐOẠN 9: TỐI ƯU HIỆU SUẤT
9.1. Chống N+1 Query
Vấn đề:
Khi list 20 khách sạn, JPA có thể bắn thêm nhiều query phụ để lấy ảnh, tiện ích, giá phòng.
Cách xử lý:
•	Dùng DTO projection.
•	Dùng @EntityGraph.
•	Dùng JOIN FETCH có kiểm soát.
•	Không fetch toàn bộ collection lớn khi chỉ cần thumbnail.
•	Query riêng giaThapNhat.
•	Query riêng reviewAverage.
________________________________________
9.2. Pagination bắt buộc
Tất cả API list/search phải dùng:
Pageable
Không trả list không giới hạn.
Default đề xuất:
page = 0
size = 10
Max size:
size <= 50
________________________________________
9.3. Redis Cache
Cache các dữ liệu ít thay đổi:
•	Khách sạn nổi bật theo thành phố.
•	Nhà hàng nổi bật theo thành phố.
•	Danh sách tiện ích.
•	Danh sách loại ẩm thực.
•	Danh sách hạng sao/filter options.
Không nên cache mạnh các API phụ thuộc ngày booking như:
search?checkIn=...&checkOut=...
Vì availability thay đổi liên tục.
Nếu cache thì TTL ngắn.
________________________________________
9.4. Debounce frontend
Dùng useDebounce cho:
•	Slider khoảng giá.
•	Search keyword.
•	Checkbox filter nếu gọi API tự động.
Delay đề xuất:
500ms
________________________________________
9.5. Lazy loading ảnh
Frontend cần:
•	Lazy load ảnh card.
•	Dùng thumbnail thay vì ảnh gốc.
•	Placeholder khi ảnh chưa load.
•	Fallback image nếu thiếu ảnh.
________________________________________
GIAI ĐOẠN 10: VALIDATION & TESTING
10.1. Backend validation
Cần validate:
Hotel search
•	city không rỗng.
•	checkIn không ở quá khứ.
•	checkOut > checkIn.
•	guests >= 1.
•	minPrice <= maxPrice.
•	page >= 0.
•	size <= 50.
Restaurant search
•	city không rỗng.
•	date/time không ở quá khứ.
•	guests >= 1.
•	minPrice <= maxPrice.
•	page >= 0.
•	size <= 50.
________________________________________
10.2. Backend unit test
Cần test:
•	Search chỉ trả hồ sơ DA_DUYET + DANG_HOAT_DONG.
•	Search không trả hồ sơ CHO_DUYET.
•	Search không trả hồ sơ BI_TU_CHOI.
•	Search khách sạn filter theo city.
•	Search khách sạn filter theo khoảng giá.
•	Search khách sạn filter theo hạng sao.
•	Tính availability khách sạn đúng khi có booking overlap.
•	Tính availability khách sạn đúng khi booking không overlap.
•	Tính availability nhà hàng đúng theo time slot.
•	Detail khách sạn chỉ trả phòng còn trống.
•	Detail nhà hàng chỉ trả món đang bán.
________________________________________
10.3. Frontend test
Cần test flow:
•	User nhập search khách sạn từ Home.
•	Redirect sang Catalog Page với query params đúng.
•	Filter thay đổi thì URL update.
•	API loading thì hiển thị skeleton.
•	API rỗng thì hiển thị empty state.
•	Click hotel card sang detail giữ nguyên checkIn/checkOut/guests.
•	Chọn phòng không vượt quá số lượng còn trống.
•	Click Đặt ngay chuyển sang checkout Module 3.
________________________________________
Definition of Done
Module 2 được xem là hoàn thành khi:
•	Public API search khách sạn hoạt động.
•	Public API search nhà hàng hoạt động.
•	API chỉ trả dữ liệu đã được duyệt và đang hoạt động.
•	Search khách sạn hỗ trợ city, ngày, số khách, giá, sao, tiện ích.
•	Search nhà hàng hỗ trợ city, ngày giờ, số khách, loại ẩm thực.
•	API có pagination.
•	Detail khách sạn trả đúng danh sách phòng còn trống.
•	Detail nhà hàng trả đúng menu và khả dụng bàn/ghế.
•	Availability khách sạn tính theo số lượng phòng còn lại.
•	Availability nhà hàng tính theo time slot.
•	Frontend có Home Search với tab khách sạn/nhà hàng.
•	Frontend có Catalog Page với filter, sort, pagination.
•	Frontend có Detail Page với gallery, tiện ích, map, phòng/menu, sticky booking bar.
•	Filter/search được sync lên URL query params.
•	Có skeleton loading, empty state, error state.
•	Có debounce khi filter.
•	Có xử lý N+1 query ở backend.
•	Có unit test cho search và availability logic.

```
