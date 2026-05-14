package com.ota.travi.constant;

public class ApiEndpoints {
    // 1. GUEST - KHÁCH VÃNG LAI (permitAll)
    public static final String PUBLIC_PREFIX = "/api/v1/public";

    public static final String AUTH_LOGIN = PUBLIC_PREFIX + "/auth/login";
    public static final String AUTH_REGISTER = PUBLIC_PREFIX + "/auth/register";
    public static final String SEARCH_ROOMS = PUBLIC_PREFIX + "/rooms/search";
    public static final String VIEW_CULTURE = PUBLIC_PREFIX + "/culture-spots";

    // 2. USER - KHÁCH THÀNH VIÊN (Authenticated & Role: USER)
    public static final String USER_PREFIX = "/api/v1/user";

    public static final String USER_PROFILE = USER_PREFIX + "/me";
    public static final String USER_BOOKING = USER_PREFIX + "/bookings"; // Đặt phòng
    public static final String USER_REVIEW = USER_PREFIX + "/reviews";   // Đánh giá
    public static final String USER_RECOMMENDATIONS = USER_PREFIX + "/recommendations"; // Nhận gợi ý AI

    // 3. PARTNER - ĐỐI TÁC ĐỊA PHƯƠNG (Role: PARTNER)
    public static final String PARTNER_PREFIX = "/api/v1/partner";

    public static final String PARTNER_MANAGE_INFO = PARTNER_PREFIX + "/hotels"; // Quản lý thông tin KS
    public static final String PARTNER_PROMOTIONS = PARTNER_PREFIX + "/promotions"; // Quảng bá văn hóa
    public static final String PARTNER_ANALYTICS = PARTNER_PREFIX + "/analytics"; // Theo dõi đề xuất

    // 4. ADMIN - QUẢN TRỊ VIÊN (Role: ADMIN)
    public static final String ADMIN_PREFIX = "/api/v1/admin";

    public static final String ADMIN_USERS = ADMIN_PREFIX + "/users"; // Kiểm soát dữ liệu người dùng
    public static final String ADMIN_ALGO_CONFIG = ADMIN_PREFIX + "/configs/algorithms"; // Cấu hình thuật toán
    public static final String ADMIN_MONITORING = ADMIN_PREFIX + "/activities"; // Giám sát hoạt động

    // 5. EXTERNAL SYSTEMS - HỆ THỐNG LIÊN KẾT (Weather, Map, Payment, Notification)
    public static final String SYSTEM_PREFIX = "/api/v1/systems";

    public static final String SYS_WEATHER = SYSTEM_PREFIX + "/weather-sync";
    public static final String SYS_MAP = SYSTEM_PREFIX + "/map-integration";
    public static final String SYS_PAYMENT = SYSTEM_PREFIX + "/payment-callback";
    public static final String SYS_NOTIFY = SYSTEM_PREFIX + "/notification-trigger";
}
