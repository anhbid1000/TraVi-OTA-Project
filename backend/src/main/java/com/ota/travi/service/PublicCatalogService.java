package com.ota.travi.service;

import com.ota.travi.dto.request.HotelSearchRequest;
import com.ota.travi.dto.request.RestaurantSearchRequest;
import com.ota.travi.dto.response.AnhResponse;
import com.ota.travi.dto.response.ChinhSachResponse;
import com.ota.travi.dto.response.FilterOptionResponse;
import com.ota.travi.dto.response.FilterOptionsResponse;
import com.ota.travi.dto.response.HotelCatalogResponse;
import com.ota.travi.dto.response.HotelDetailResponse;
import com.ota.travi.dto.response.MenuItemResponse;
import com.ota.travi.dto.response.MenuResponse;
import com.ota.travi.dto.response.RestaurantCatalogResponse;
import com.ota.travi.dto.response.RestaurantDetailResponse;
import com.ota.travi.dto.response.RoomAvailabilityResponse;
import com.ota.travi.dto.response.TableAvailabilityResponse;
import com.ota.travi.dto.response.TienIchKhachSanResponse;
import com.ota.travi.dto.response.TienIchNhaHangResponse;
import com.ota.travi.entity.AnhKhachSan;
import com.ota.travi.entity.AnhNhaHang;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.ChinhSach;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.MonAn;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.Phong;
import com.ota.travi.entity.ThucDon;
import com.ota.travi.entity.TienIchKhachSan;
import com.ota.travi.entity.TienIchNhaHang;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.enums.TrangThaiHoatDong;
import com.ota.travi.enums.TrangThaiKiemDuyet;
import com.ota.travi.enums.TrangThaiMonAn;
import com.ota.travi.repository.AnhKhachSanRepository;
import com.ota.travi.repository.AnhNhaHangRepository;
import com.ota.travi.repository.AnhPhongRepository;
import com.ota.travi.repository.BanRepository;
import com.ota.travi.repository.ChinhSachRepository;
import com.ota.travi.repository.DonKhachSanChiTietRepository;
import com.ota.travi.repository.DonNhaHangRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.MonAnRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.PhongRepository;
import com.ota.travi.repository.ThucDonRepository;
import com.ota.travi.repository.TienIchKhachSanRepository;
import com.ota.travi.repository.TienIchNhaHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
public class PublicCatalogService {
    private static final int RESTAURANT_SLOT_DURATION_HOURS = 2;
    private static final int DEFAULT_FEATURED_HOTEL_SIZE = 4;
    private static final int DEFAULT_FEATURED_RESTAURANT_SIZE = 3;
    private static final int MAX_FEATURED_SIZE = 12;
    private static final int FEATURED_RECENT_DAYS = 30;

    private static final List<TrangThaiDon> ACTIVE_BOOKING_STATUSES = List.of(
            TrangThaiDon.CHO_THANH_TOAN,
            TrangThaiDon.DA_THANH_TOAN,
            TrangThaiDon.DA_XAC_NHAN,
            TrangThaiDon.DANG_PHUC_VU
    );

    private final CatalogSearchService catalogSearchService;
    private final KhachSanRepository khachSanRepository;
    private final NhaHangRepository nhaHangRepository;
    private final PhongRepository phongRepository;
    private final BanRepository banRepository;
    private final ThucDonRepository thucDonRepository;
    private final MonAnRepository monAnRepository;
    private final DonKhachSanChiTietRepository donKhachSanChiTietRepository;
    private final DonNhaHangRepository donNhaHangRepository;
    private final AnhKhachSanRepository anhKhachSanRepository;
    private final AnhNhaHangRepository anhNhaHangRepository;
    private final AnhPhongRepository anhPhongRepository;
    private final ChinhSachRepository chinhSachRepository;
    private final TienIchKhachSanRepository tienIchKhachSanRepository;
    private final TienIchNhaHangRepository tienIchNhaHangRepository;

    public PublicCatalogService(
            CatalogSearchService catalogSearchService,
            KhachSanRepository khachSanRepository,
            NhaHangRepository nhaHangRepository,
            PhongRepository phongRepository,
            BanRepository banRepository,
            ThucDonRepository thucDonRepository,
            MonAnRepository monAnRepository,
            DonKhachSanChiTietRepository donKhachSanChiTietRepository,
            DonNhaHangRepository donNhaHangRepository,
            AnhKhachSanRepository anhKhachSanRepository,
            AnhNhaHangRepository anhNhaHangRepository,
            AnhPhongRepository anhPhongRepository,
            ChinhSachRepository chinhSachRepository,
            TienIchKhachSanRepository tienIchKhachSanRepository,
            TienIchNhaHangRepository tienIchNhaHangRepository
    ) {
        this.catalogSearchService = catalogSearchService;
        this.khachSanRepository = khachSanRepository;
        this.nhaHangRepository = nhaHangRepository;
        this.phongRepository = phongRepository;
        this.banRepository = banRepository;
        this.thucDonRepository = thucDonRepository;
        this.monAnRepository = monAnRepository;
        this.donKhachSanChiTietRepository = donKhachSanChiTietRepository;
        this.donNhaHangRepository = donNhaHangRepository;
        this.anhKhachSanRepository = anhKhachSanRepository;
        this.anhNhaHangRepository = anhNhaHangRepository;
        this.anhPhongRepository = anhPhongRepository;
        this.chinhSachRepository = chinhSachRepository;
        this.tienIchKhachSanRepository = tienIchKhachSanRepository;
        this.tienIchNhaHangRepository = tienIchNhaHangRepository;
    }

    // --- 1. TÌMM KIẾM KHÁCH SẠN (SEARCH HOTELS) ---
    @Transactional(readOnly = true)
    public Page<HotelCatalogResponse> searchHotels(HotelSearchRequest request) {
        // 1. Tìm kiếm khách sạn từ cơ sở dữ liệu dựa trên các tiêu chí lọc
        Page<KhachSan> hotelPage = catalogSearchService.searchHotels(request);

        // 2. Lập bản đồ kết quả, lọc những khách sạn không công khai hoặc không có phòng còn trống
        List<HotelCatalogResponse> mapped = hotelPage.getContent().stream()
                .filter(this::isPublicVisible)
                .map(hotel -> mapHotelCatalog(hotel, request.checkIn(), request.checkOut(), request.guests()))
                .filter(response -> response.soPhongConTrong() > 0)
                .toList();

        return new PageImpl<>(mapped, hotelPage.getPageable(), hotelPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<HotelCatalogResponse> getFeaturedHotels(LocalDate checkIn, LocalDate checkOut, Integer guests, Integer size) {
        LocalDate resolvedCheckIn = checkIn == null ? LocalDate.now().plusDays(1) : checkIn;
        LocalDate resolvedCheckOut = checkOut == null ? resolvedCheckIn.plusDays(1) : checkOut;
        int resolvedGuests = guests == null || guests < 1 ? 2 : guests;
        int resolvedSize = normalizeFeaturedSize(size, DEFAULT_FEATURED_HOTEL_SIZE);
        LocalDateTime recentFrom = LocalDateTime.now().minusDays(FEATURED_RECENT_DAYS);

        return khachSanRepository.findAll().stream()
                .filter(this::isPublicVisible)
                .map(hotel -> {
                    HotelCatalogResponse mapped = mapHotelCatalog(hotel, resolvedCheckIn, resolvedCheckOut, resolvedGuests);
                    int recentBookings = normalizeCount(donKhachSanChiTietRepository.sumRecentBookedQuantityByHotel(
                            hotel.getIdTaiSan(),
                            recentFrom,
                            ACTIVE_BOOKING_STATUSES
                    ));
                    double score = scoreFeaturedHotel(hotel, mapped, recentBookings);
                    return new ScoredHotel(mapped, score);
                })
                .filter(item -> item.response().soPhongConTrong() > 0)
                .sorted(Comparator.comparingDouble(ScoredHotel::score).reversed())
                .limit(resolvedSize)
                .map(ScoredHotel::response)
                .toList();
    }

    // --- 2. LẤY CHI TIẾT KHÁCH SẠN (GET HOTEL DETAIL) ---
    @Transactional(readOnly = true)
    public HotelDetailResponse getHotelDetail(String hotelId, LocalDate checkIn, LocalDate checkOut, Integer guests) {
        // 1. Lấy khách sạn từ cơ sở dữ liệu
        KhachSan hotel = khachSanRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay khach san"));

        // 2. Kiểm tra khách sạn có công khai trên kênh công cộng không
        if (!isPublicVisible(hotel)) {
            throw new RuntimeException("Khach san khong kha dung tren kenh public");
        }

        // 3. Lấy danh sách phòng còn trống theo ngày nhập và thông tin khách
        List<RoomAvailabilityResponse> availableRooms = phongRepository.findByKhachSan_IdTaiSan(hotelId).stream()
                .filter(room -> Boolean.FALSE.equals(room.getDeleted()))
                .filter(room -> guests == null || room.getSucChuaToiDa() == null || room.getSucChuaToiDa() >= guests)
                .map(room -> mapRoomAvailability(room, checkIn, checkOut))
                .filter(room -> room.soLuongConTrong() > 0)
                .toList();

        List<AnhResponse> images = mapHotelImages(hotelId);
        ChinhSachResponse policy = mapPolicy(hotel.getHoSoKinhDoanh().getIdHoSo());

        // 4. Xây dựng và trả về phản hồi chi tiết khách sạn đầy đủ

        return new HotelDetailResponse(
                hotel.getIdTaiSan(),
                hotel.getTen(),
                hotel.getMoTa(),
                hotel.getHoSoKinhDoanh().getDiaChi(),
                hotel.getHoSoKinhDoanh().getThanhPho(),
                hotel.getHoSoKinhDoanh().getQuanHuyen(),
                hotel.getHoSoKinhDoanh().getPhuongXa(),
                hotel.getHoSoKinhDoanh().getKinhDo(),
                hotel.getHoSoKinhDoanh().getViDo(),
                hotel.getHangSao(),
                hotel.getGioNhanPhong(),
                hotel.getGioTraPhong(),
                policy,
                0.0,
                0,
                images,
                mapHotelAmenities(hotel.getTienIch()),
                availableRooms
        );
    }

    // --- 3. TÌM KIẾM NHÀ HÀNG (SEARCH RESTAURANTS) ---
    @Transactional(readOnly = true)
    public Page<RestaurantCatalogResponse> searchRestaurants(RestaurantSearchRequest request) {
        // 1. Tìm kiếm nhà hàng từ cơ sở dữ liệu dựa trên các tiêu chí lọc
        Page<NhaHang> restaurantPage = catalogSearchService.searchRestaurants(request);
        LocalDateTime requestedStart = LocalDateTime.of(request.date(), request.time());
        LocalDateTime requestedEnd = requestedStart.plusHours(RESTAURANT_SLOT_DURATION_HOURS);

        // 2. Lập bản đồ kết quả, lọc những nhà hàng không công khai hoặc không có bàn trống
        List<RestaurantCatalogResponse> mapped = restaurantPage.getContent().stream()
                .filter(this::isPublicVisible)
                .map(restaurant -> mapRestaurantCatalog(restaurant, requestedStart, requestedEnd))
                .filter(response -> response.soGheConTrong() >= request.guests())
                .toList();

        return new PageImpl<>(mapped, restaurantPage.getPageable(), restaurantPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<RestaurantCatalogResponse> getFeaturedRestaurants(LocalDate date, LocalTime time, Integer guests, Integer size) {
        LocalDate resolvedDate = date == null ? LocalDate.now().plusDays(1) : date;
        LocalTime resolvedTime = time == null ? LocalTime.of(19, 0) : time;
        int resolvedGuests = guests == null || guests < 1 ? 2 : guests;
        int resolvedSize = normalizeFeaturedSize(size, DEFAULT_FEATURED_RESTAURANT_SIZE);
        LocalDateTime requestedStart = LocalDateTime.of(resolvedDate, resolvedTime);
        LocalDateTime requestedEnd = requestedStart.plusHours(RESTAURANT_SLOT_DURATION_HOURS);
        LocalDateTime recentFrom = LocalDateTime.now().minusDays(FEATURED_RECENT_DAYS);

        return nhaHangRepository.findAll().stream()
                .filter(this::isPublicVisible)
                .map(restaurant -> {
                    RestaurantCatalogResponse mapped = mapRestaurantCatalog(restaurant, requestedStart, requestedEnd);
                    int recentReservations = normalizeCount(donNhaHangRepository.countRecentReservationsByRestaurant(
                            restaurant.getIdTaiSan(),
                            recentFrom,
                            ACTIVE_BOOKING_STATUSES
                    ));
                    double score = scoreFeaturedRestaurant(restaurant, mapped, recentReservations);
                    return new ScoredRestaurant(mapped, score);
                })
                .filter(item -> item.response().soGheConTrong() >= resolvedGuests)
                .sorted(Comparator.comparingDouble(ScoredRestaurant::score).reversed())
                .limit(resolvedSize)
                .map(ScoredRestaurant::response)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getPublicCities() {
        return java.util.stream.Stream.concat(
                        khachSanRepository.findAll().stream()
                                .filter(this::isPublicVisible)
                                .map(hotel -> hotel.getHoSoKinhDoanh().getThanhPho()),
                        nhaHangRepository.findAll().stream()
                                .filter(this::isPublicVisible)
                                .map(restaurant -> restaurant.getHoSoKinhDoanh().getThanhPho())
                )
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(city -> !city.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Transactional(readOnly = true)
    public FilterOptionsResponse getHotelFilterOptions() {
        List<FilterOptionResponse> amenities = tienIchKhachSanRepository.findAll().stream()
                .sorted(Comparator.comparing(TienIchKhachSan::getTenTienIch, String.CASE_INSENSITIVE_ORDER))
                .map(amenity -> new FilterOptionResponse(
                        amenity.getTenTienIch(),
                        amenity.getTenTienIch(),
                        amenity.getMoTa()
                ))
                .toList();

        List<FilterOptionResponse> types = khachSanRepository.findAll().stream()
                .filter(this::isPublicVisible)
                .map(KhachSan::getLoaiKhachSan)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .map(type -> new FilterOptionResponse(
                        type,
                        type,
                        null
                ))
                .toList();

        return new FilterOptionsResponse(amenities, types, java.util.Collections.emptyList());
    }

    @Transactional(readOnly = true)
    public FilterOptionsResponse getRestaurantFilterOptions() {
        List<FilterOptionResponse> amenities = tienIchNhaHangRepository.findAll().stream()
                .map(TienIchNhaHang::getTenTienIch)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .map(amenity -> new FilterOptionResponse(
                        amenity,
                        amenity,
                        null
                ))
                .toList();

        // Get distinct cuisines from restaurants
        List<FilterOptionResponse> cuisines = nhaHangRepository.findAll().stream()
                .filter(this::isPublicVisible)
                .map(restaurant -> restaurant.getLoaiAmThuc())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(cuisine -> !cuisine.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .map(cuisine -> new FilterOptionResponse(
                        cuisine,
                        cuisine,
                        null
                ))
                .toList();

        return new FilterOptionsResponse(amenities, java.util.Collections.emptyList(), cuisines);
    }

    // --- 4. LẤY CHI TIẾT NHÀ HÀNG (GET RESTAURANT DETAIL) ---
    @Transactional(readOnly = true)
    public RestaurantDetailResponse getRestaurantDetail(String restaurantId, LocalDate date, LocalTime time, Integer guests) {
        // 1. Lấy nhà hàng từ cơ sở dữ liệu
        NhaHang restaurant = nhaHangRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay nha hang"));

        // 2. Kiểm tra nhà hàng có công khai trên kênh công cộng không
        if (!isPublicVisible(restaurant)) {
            throw new RuntimeException("Nha hang khong kha dung tren kenh public");
        }

        // 3. Lấy danh sách bàn còn trống theo thời gian và số khách
        List<TableAvailabilityResponse> tables = mapTableAvailability(
                restaurantId,
                date,
                time,
                guests
        );

        // 4. Xây dựng và trả về phản hồi chi tiết nhà hàng đầy đủ
        return new RestaurantDetailResponse(
                restaurant.getIdTaiSan(),
                restaurant.getTen(),
                restaurant.getMoTa(),
                restaurant.getHoSoKinhDoanh().getDiaChi(),
                restaurant.getHoSoKinhDoanh().getThanhPho(),
                restaurant.getHoSoKinhDoanh().getQuanHuyen(),
                restaurant.getHoSoKinhDoanh().getPhuongXa(),
                restaurant.getHoSoKinhDoanh().getKinhDo(),
                restaurant.getHoSoKinhDoanh().getViDo(),
                restaurant.getLoaiAmThuc(),
                restaurant.getGioMoCua(),
                restaurant.getGioDongCua(),
                restaurant.getCoDatBanTruoc(),
                restaurant.getCoDatMonTruoc(),
                0.0,
                0,
                mapRestaurantImages(restaurantId),
                mapRestaurantAmenities(restaurant.getTienIch()),
                tables,
                mapMenus(restaurantId)
        );
    }

    // --- HELPER METHODS - MAPPING & UTILITY ---
    // Lập bản đồ đối tượng KhachSan sang HotelCatalogResponse
    private HotelCatalogResponse mapHotelCatalog(KhachSan hotel, LocalDate checkIn, LocalDate checkOut, Integer guests) {
        String hotelId = hotel.getIdTaiSan();
        Integer availableRooms = calculateHotelAvailableRoomCount(hotelId, checkIn, checkOut, guests);

        return new HotelCatalogResponse(
                hotelId,
                hotel.getTen(),
                hotel.getHoSoKinhDoanh().getDiaChi(),
                hotel.getHoSoKinhDoanh().getThanhPho(),
                hotel.getHangSao(),
                resolveHotelThumbnail(hotelId),
                0.0,
                0,
                hotel.getGiaCoBan(),
                availableRooms,
                mapHotelAmenities(hotel.getTienIch()).stream().limit(5).toList(),
                hotel.getHoSoKinhDoanh().getTrangThaiHoatDong()
        );
    }

    // Lập bản đồ đối tượng NhaHang sang RestaurantCatalogResponse
    private RestaurantCatalogResponse mapRestaurantCatalog(
            NhaHang restaurant,
            LocalDateTime requestedStart,
            LocalDateTime requestedEnd
    ) {
        int availableSeats = calculateRestaurantAvailableSeats(
                restaurant.getHoSoKinhDoanh().getIdHoSo(),
                restaurant.getIdTaiSan(),
                requestedStart,
                requestedEnd
        );

        return new RestaurantCatalogResponse(
                restaurant.getIdTaiSan(),
                restaurant.getTen(),
                restaurant.getHoSoKinhDoanh().getDiaChi(),
                restaurant.getHoSoKinhDoanh().getThanhPho(),
                restaurant.getLoaiAmThuc(),
                resolveRestaurantThumbnail(restaurant.getIdTaiSan()),
                0.0,
                0,
                resolveRestaurantPriceRange(restaurant.getIdTaiSan(), restaurant.getGiaCoBan()),
                availableSeats,
                mapRestaurantAmenities(restaurant.getTienIch()).stream().limit(5).toList(),
                restaurant.getHoSoKinhDoanh().getTrangThaiHoatDong()
        );
    }

    // Tính toán số phòng khách sạn còn trống
    private Integer calculateHotelAvailableRoomCount(String hotelId, LocalDate checkIn, LocalDate checkOut, Integer guests) {
        return phongRepository.findByKhachSan_IdTaiSan(hotelId).stream()
                .filter(room -> Boolean.FALSE.equals(room.getDeleted()))
                .filter(room -> guests == null || room.getSucChuaToiDa() == null || room.getSucChuaToiDa() >= guests)
                .map(room -> calculateRoomAvailableQuantity(room, checkIn, checkOut))
                .reduce(0, Integer::sum);
    }

    // Lập bản đồ đối tượng Phong sang RoomAvailabilityResponse
    private RoomAvailabilityResponse mapRoomAvailability(Phong room, LocalDate checkIn, LocalDate checkOut) {
        int availableQuantity = calculateRoomAvailableQuantity(room, checkIn, checkOut);

        return new RoomAvailabilityResponse(
                room.getId(),
                room.getTenPhong(),
                room.getLoaiPhong(),
                room.getMoTa(),
                room.getDienTich(),
                room.getSucChuaToiDa(),
                room.getSoGiuong(),
                room.getGiaCoBan(),
                availableQuantity,
                mapRoomImages(room.getId()),
                room.getTienIch()
        );
    }

    // Tính toán số lượng phòng còn trống trong khoảng thời gian
    private int calculateRoomAvailableQuantity(Phong room, LocalDate checkIn, LocalDate checkOut) {
        int totalQuantity = room.getSoLuongPhong() == null ? 0 : room.getSoLuongPhong();
        if (checkIn == null || checkOut == null) {
            return Math.max(totalQuantity, 0);
        }

        Integer bookedQuantity = donKhachSanChiTietRepository.sumBookedQuantityByRoomAndDateRange(
                room.getId(),
                checkIn,
                checkOut,
                ACTIVE_BOOKING_STATUSES
        );

        int booked = bookedQuantity == null ? 0 : bookedQuantity;
        return Math.max(totalQuantity - booked, 0);
    }

    // Tính toán số chỗ ngồi nhà hàng còn trống
    private int calculateRestaurantAvailableSeats(String businessProfileId, String restaurantId, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        int totalSeats = banRepository.findByNhaHang_IdTaiSan(restaurantId).stream()
                .filter(table -> Boolean.FALSE.equals(table.getDeleted()))
                .map(Ban::getSoChoNgoi)
                .filter(value -> value != null && value > 0)
                .reduce(0, Integer::sum);

        if (requestedStart == null || requestedEnd == null) {
            return totalSeats;
        }

        Integer bookedSeats = donNhaHangRepository.sumBookedGuestsByBusinessProfileAndTimeSlot(
                businessProfileId,
                requestedStart,
                requestedEnd,
                ACTIVE_BOOKING_STATUSES
        );

        int booked = bookedSeats == null ? 0 : bookedSeats;
        return Math.max(totalSeats - booked, 0);
    }

    // Lập bản đồ danh sách bàn theo tính khả dụng
    private List<TableAvailabilityResponse> mapTableAvailability(
            String restaurantId,
            LocalDate date,
            LocalTime time,
            Integer guests
    ) {
        LocalDateTime requestedStart = date == null || time == null ? null : LocalDateTime.of(date, time);
        LocalDateTime requestedEnd = requestedStart == null ? null : requestedStart.plusHours(RESTAURANT_SLOT_DURATION_HOURS);

        return banRepository.findByNhaHang_IdTaiSan(restaurantId).stream()
                .filter(table -> Boolean.FALSE.equals(table.getDeleted()))
                .map(table -> {
                    boolean overlap = false;
                    if (requestedStart != null && requestedEnd != null) {
                        overlap = donNhaHangRepository.existsOverlappingReservationByTableAndTimeSlot(
                                table.getId(),
                                requestedStart,
                                requestedEnd,
                                ACTIVE_BOOKING_STATUSES
                        );
                    }

                    boolean enoughSeats = guests == null || table.getSoChoNgoi() == null || table.getSoChoNgoi() >= guests;
                    return new TableAvailabilityResponse(
                            table.getId(),
                            table.getTenBan(),
                            table.getSoChoNgoi(),
                            table.getViTriSanh(),
                            table.getTrangThai(),
                            !overlap && enoughSeats
                    );
                })
                .toList();
    }

    // Lập bản đồ danh sách thực đơn
    private List<MenuResponse> mapMenus(String restaurantId) {
        return thucDonRepository.findByNhaHang_IdTaiSan(restaurantId).stream()
                .map(menu -> new MenuResponse(
                        menu.getId(),
                        menu.getTenThucDon(),
                        menu.getPhanLoai(),
                        mapMenuItems(menu)
                ))
                .toList();
    }

    // Lập bản đồ danh sách các mục thực đơn
    private List<MenuItemResponse> mapMenuItems(ThucDon menu) {
        return monAnRepository.findByThucDon_Id(menu.getId()).stream()
                .filter(item -> item.getTrangThai() == TrangThaiMonAn.DANG_BAN)
                .filter(item -> Boolean.FALSE.equals(item.getDeleted()))
                .map(this::mapMenuItem)
                .toList();
    }

    // Lập bản đồ một mục thực đơn
    private MenuItemResponse mapMenuItem(MonAn item) {
        return new MenuItemResponse(
                item.getId(),
                item.getTenMon(),
                item.getMoTa(),
                item.getGiaBan(),
                item.getDanhMucMon(),
                item.getDuongDanUrl(),
                item.getTrangThai()
        );
    }

    private List<AnhResponse> mapHotelImages(String hotelId) {
        return anhKhachSanRepository.findByKhachSan_IdTaiSan(hotelId).stream()
                .map(image -> new AnhResponse(
                        image.getId(),
                        hotelId,
                        image.getDuongDanUrl(),
                        image.getMoTaAnh(),
                        image.getLaAnhDaiDien(),
                        image.getNgayTaiLen()
                ))
                .sorted(Comparator.comparing(AnhResponse::laAnhDaiDien).reversed())
                .toList();
    }

    private List<AnhResponse> mapRestaurantImages(String restaurantId) {
        return anhNhaHangRepository.findByNhaHang_IdTaiSan(restaurantId).stream()
                .map(image -> new AnhResponse(
                        image.getId(),
                        restaurantId,
                        image.getDuongDanUrl(),
                        image.getMoTaAnh(),
                        image.getLaAnhDaiDien(),
                        image.getNgayTaiLen()
                ))
                .sorted(Comparator.comparing(AnhResponse::laAnhDaiDien).reversed())
                .toList();
    }

    private List<AnhResponse> mapRoomImages(String roomId) {
        return anhPhongRepository.findByPhong_Id(roomId).stream()
                .map(image -> new AnhResponse(
                        image.getId(),
                        roomId,
                        image.getDuongDanUrl(),
                        image.getMoTaAnh(),
                        image.getLaAnhDaiDien(),
                        image.getNgayTaiLen()
                ))
                .sorted(Comparator.comparing(AnhResponse::laAnhDaiDien).reversed())
                .toList();
    }

    private Set<TienIchKhachSanResponse> mapHotelAmenities(Set<TienIchKhachSan> amenities) {
        return amenities.stream()
                .map(amenity -> new TienIchKhachSanResponse(
                        amenity.getId(),
                        amenity.getTenTienIch(),
                        amenity.getLoaiTienIch(),
                        amenity.getMoTa()
                ))
                .collect(java.util.stream.Collectors.toSet());
    }

    private List<TienIchNhaHangResponse> mapRestaurantAmenities(List<TienIchNhaHang> amenities) {
        return amenities.stream()
                .map(amenity -> new TienIchNhaHangResponse(
                        amenity.getId(),
                        amenity.getNhaHang().getIdTaiSan(),
                        amenity.getTenTienIch(),
                        amenity.getLoaiTienIch(),
                        amenity.getMoTa(),
                        amenity.getCoThuPhi(),
                        amenity.getPhiSuDung()
                ))
                .toList();
    }

    private ChinhSachResponse mapPolicy(String businessProfileId) {
        return chinhSachRepository.findByHoSoKinhDoanh_IdHoSo(businessProfileId)
                .map(this::toChinhSachResponse)
                .orElse(null);
    }

    private ChinhSachResponse toChinhSachResponse(ChinhSach policy) {
        return new ChinhSachResponse(
                policy.getId(),
                policy.getHoSoKinhDoanh().getIdHoSo(),
                policy.getLoaiChinhSach(),
                policy.getNoiDung(),
                policy.getNgayApDung(),
                policy.getGioNhanPhong(),
                policy.getGioTraPhong(),
                policy.getGioMoCua(),
                policy.getGioDongCua(),
                policy.getChinhSachHuy(),
                policy.getChinhSachHoanTien(),
                policy.getQuyDinhTreEm(),
                policy.getQuyDinhVatNuoi(),
                policy.getGhiChuKhac(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
        );
    }

    private String resolveHotelThumbnail(String hotelId) {
        return anhKhachSanRepository.findFirstByKhachSan_IdTaiSanAndLaAnhDaiDienTrue(hotelId)
                .map(AnhKhachSan::getDuongDanUrl)
                .orElseGet(() -> anhKhachSanRepository.findByKhachSan_IdTaiSan(hotelId).stream()
                        .findFirst()
                        .map(AnhKhachSan::getDuongDanUrl)
                        .orElse(null));
    }

    private String resolveRestaurantThumbnail(String restaurantId) {
        return anhNhaHangRepository.findFirstByNhaHang_IdTaiSanAndLaAnhDaiDienTrue(restaurantId)
                .map(AnhNhaHang::getDuongDanUrl)
                .orElseGet(() -> anhNhaHangRepository.findByNhaHang_IdTaiSan(restaurantId).stream()
                        .findFirst()
                        .map(AnhNhaHang::getDuongDanUrl)
                        .orElse(null));
    }

    private String resolveRestaurantPriceRange(String restaurantId, Double basePrice) {
        List<Double> activePrices = monAnRepository.findByThucDon_NhaHang_IdTaiSan(restaurantId).stream()
                .filter(item -> item.getTrangThai() == TrangThaiMonAn.DANG_BAN)
                .filter(item -> Boolean.FALSE.equals(item.getDeleted()))
                .map(MonAn::getGiaBan)
                .filter(price -> price != null && price > 0)
                .toList();

        if (activePrices.isEmpty()) {
            if (basePrice == null || basePrice <= 0) {
                return "Dang cap nhat";
            }
            return String.format(Locale.ROOT, "%.0f", basePrice);
        }

        double minPrice = activePrices.stream().min(Double::compareTo).orElse(basePrice == null ? 0 : basePrice);
        double maxPrice = activePrices.stream().max(Double::compareTo).orElse(minPrice);
        return String.format(Locale.ROOT, "%.0f - %.0f", minPrice, maxPrice);
    }

    private int normalizeFeaturedSize(Integer size, int defaultSize) {
        if (size == null || size < 1) {
            return defaultSize;
        }
        return Math.min(size, MAX_FEATURED_SIZE);
    }

    private int normalizeCount(Integer value) {
        return value == null ? 0 : Math.max(value, 0);
    }

    private double scoreFeaturedHotel(KhachSan hotel, HotelCatalogResponse response, int recentBookings) {
        int stars = hotel.getHangSao() == null ? 0 : hotel.getHangSao();
        int availableRooms = response.soPhongConTrong() == null ? 0 : response.soPhongConTrong();
        int amenities = response.tienIchNoiBat() == null ? 0 : response.tienIchNoiBat().size();
        boolean hasThumbnail = response.thumbnailUrl() != null && !response.thumbnailUrl().isBlank();
        double price = response.giaThapNhat() == null ? 0.0 : response.giaThapNhat();

        double roomDiscountScore = phongRepository.findByKhachSan_IdTaiSan(hotel.getIdTaiSan()).stream()
                .filter(room -> Boolean.FALSE.equals(room.getDeleted()))
                .map(Phong::getPhanTramGiamGia)
                .filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        double priceScore = price > 0 ? Math.min(5.0, 2_000_000.0 / price) : 0.0;

        return (stars * 4.0)
                + (recentBookings * 1.5)
                + (availableRooms * 0.8)
                + (amenities * 0.5)
                + roomDiscountScore
                + priceScore
                + (hasThumbnail ? 2.0 : -1.0);
    }

    private double scoreFeaturedRestaurant(NhaHang restaurant, RestaurantCatalogResponse response, int recentReservations) {
        int availableSeats = response.soGheConTrong() == null ? 0 : response.soGheConTrong();
        int amenities = response.tienIchNoiBat() == null ? 0 : response.tienIchNoiBat().size();
        boolean hasThumbnail = response.thumbnailUrl() != null && !response.thumbnailUrl().isBlank();
        boolean canPreOrder = Boolean.TRUE.equals(restaurant.getCoDatMonTruoc());

        long activeDishCount = monAnRepository.findByThucDon_NhaHang_IdTaiSan(restaurant.getIdTaiSan()).stream()
                .filter(item -> item.getTrangThai() == TrangThaiMonAn.DANG_BAN)
                .filter(item -> Boolean.FALSE.equals(item.getDeleted()))
                .count();

        return (recentReservations * 2.0)
                + (availableSeats * 0.4)
                + (amenities * 0.5)
                + (activeDishCount * 0.2)
                + (canPreOrder ? 1.5 : 0.0)
                + (hasThumbnail ? 2.0 : -1.0);
    }

    private record ScoredHotel(HotelCatalogResponse response, double score) {
    }

    private record ScoredRestaurant(RestaurantCatalogResponse response, double score) {
    }

    private boolean isPublicVisible(KhachSan hotel) {
        return hotel.getHoSoKinhDoanh() != null
                && hotel.getHoSoKinhDoanh().getTrangThaiKiemDuyet() == TrangThaiKiemDuyet.DA_DUYET
                && hotel.getHoSoKinhDoanh().getTrangThaiHoatDong() == TrangThaiHoatDong.DANG_HOAT_DONG
                && Boolean.FALSE.equals(hotel.getHoSoKinhDoanh().getDeleted());
    }

    private boolean isPublicVisible(NhaHang restaurant) {
        return restaurant.getHoSoKinhDoanh() != null
                && restaurant.getHoSoKinhDoanh().getTrangThaiKiemDuyet() == TrangThaiKiemDuyet.DA_DUYET
                && restaurant.getHoSoKinhDoanh().getTrangThaiHoatDong() == TrangThaiHoatDong.DANG_HOAT_DONG
                && Boolean.FALSE.equals(restaurant.getHoSoKinhDoanh().getDeleted());
    }
}



