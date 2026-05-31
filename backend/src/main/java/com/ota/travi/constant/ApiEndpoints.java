package com.ota.travi.constant;

public final class ApiEndpoints {

    private ApiEndpoints() {
    }

    // Base & Versioning
    public static final String API_PREFIX = "/api";
    public static final String API_VERSION = "/v1";
    public static final String BASE_PREFIX = API_PREFIX + API_VERSION; // => /api/v1

    // 1. GUEST - Public routes (no auth required)
    public static final String PUBLIC_PREFIX = BASE_PREFIX + "/public";
    public static final String PUBLIC_HOTELS_SEARCH = PUBLIC_PREFIX + "/hotels/search";
    public static final String PUBLIC_HOTELS_FEATURED = PUBLIC_PREFIX + "/hotels/featured";
    public static final String PUBLIC_HOTELS_FILTER_OPTIONS = PUBLIC_PREFIX + "/hotels/filter-options/data";
    public static final String PUBLIC_HOTEL_AMENITIES = PUBLIC_PREFIX + "/amenities/hotels";
    public static final String PUBLIC_HOTEL_DETAIL = PUBLIC_PREFIX + "/hotels/{id}";
    public static final String PUBLIC_RESTAURANTS_SEARCH = PUBLIC_PREFIX + "/restaurants/search";
    public static final String PUBLIC_RESTAURANTS_FEATURED = PUBLIC_PREFIX + "/restaurants/featured";
    public static final String PUBLIC_RESTAURANTS_FILTER_OPTIONS = PUBLIC_PREFIX + "/restaurants/filter-options/data";
    public static final String PUBLIC_RESTAURANT_DETAIL = PUBLIC_PREFIX + "/restaurants/{id}";
    public static final String PUBLIC_WEATHER_FORECAST = PUBLIC_PREFIX + "/weather/forecast";
    public static final String PUBLIC_WEATHER_FORECAST_RANGE = PUBLIC_PREFIX + "/weather/forecast/range";
    public static final String PUBLIC_CITIES = PUBLIC_PREFIX + "/locations/cities";
    public static final String PUBLIC_ROOMS_SEARCH = PUBLIC_PREFIX + "/rooms/search"; // planned
    public static final String PUBLIC_CULTURE_SPOTS = PUBLIC_PREFIX + "/culture-spots"; // planned
    public static final String PUBLIC_BOOKING_SEARCH = PUBLIC_PREFIX + "/bookings/search";

    // 1.a Authentication (public) - keep under versioned auth prefix
    public static final String AUTH_PREFIX = BASE_PREFIX + "/auth";
    public static final String AUTH_REGISTER = AUTH_PREFIX + "/register";
    public static final String AUTH_VERIFY_EMAIL = AUTH_PREFIX + "/verify-email";
    public static final String AUTH_RESEND_OTP = AUTH_PREFIX + "/resend-otp";
    public static final String AUTH_LOGIN = AUTH_PREFIX + "/login";
    public static final String AUTH_GOOGLE = AUTH_PREFIX + "/google";
    public static final String AUTH_LOGOUT = AUTH_PREFIX + "/logout";
    public static final String AUTH_REFRESH = AUTH_PREFIX + "/refresh";
    public static final String AUTH_FORGOT_PASSWORD_REQUEST_OTP = AUTH_PREFIX + "/forgot-password/request-otp";
    public static final String AUTH_FORGOT_PASSWORD_VERIFY_OTP = AUTH_PREFIX + "/forgot-password/verify-otp";
    public static final String AUTH_FORGOT_PASSWORD_RESET = AUTH_PREFIX + "/forgot-password/reset";

    // 2. USER - Authenticated user endpoints (ROLE: USER)
    public static final String USER_PREFIX = BASE_PREFIX + "/user";
    public static final String USER_ME = USER_PREFIX + "/me";
    public static final String USER_BOOKINGS = USER_PREFIX + "/bookings";
    public static final String USER_BOOKING_HISTORY = USER_BOOKINGS + "/history";
    public static final String USER_REVIEWS = USER_PREFIX + "/reviews";
    public static final String USER_RECOMMENDATIONS = USER_PREFIX + "/recommendations"; // AI hints
    // USER FEEDBACK
    public static final String USER_FEEDBACK_PREFIX = USER_PREFIX + "/feedback";
    public static final String USER_FEEDBACK_REVIEWS = USER_FEEDBACK_PREFIX + "/reviews";
    public static final String USER_FEEDBACK_COMPLAINTS = USER_FEEDBACK_PREFIX + "/complaints";
    public static final String USER_FEEDBACK_REPORTS = USER_FEEDBACK_PREFIX + "/reports";

    // 3. PARTNER - Partner / Vendor endpoints (ROLE: PARTNER)
    public static final String PARTNER_LEGACY_PREFIX = API_PREFIX + "/partner";
    public static final String PARTNER_LEGACY_BUSINESS_PROFILES = PARTNER_LEGACY_PREFIX + "/business-profiles";
    public static final String PARTNER_PREFIX = BASE_PREFIX + "/partner";
    public static final String PARTNER_DASHBOARD = PARTNER_PREFIX + "/dashboard";
    public static final String PARTNER_BUSINESS_PROFILES = PARTNER_PREFIX + "/business-profiles";
    public static final String PARTNER_HOTELS = PARTNER_PREFIX + "/hotels"; // manage partner hotels
    public static final String PARTNER_RESTAURANTS = PARTNER_PREFIX + "/restaurants";
    public static final String PARTNER_PROMOTIONS = PARTNER_PREFIX + "/promotions";
    public static final String PARTNER_ANALYTICS = PARTNER_PREFIX + "/analytics";
    // PARTNER FEEDBACK
    public static final String PARTNER_REVIEWS = PARTNER_PREFIX + "/reviews";
    public static final String PARTNER_REVIEW_REPLY = PARTNER_REVIEWS + "/{id}/reply";
    public static final String PARTNER_COMPLAINT_RESOLVE = PARTNER_PREFIX + "/complaints/{id}/resolve";
    public static final String PARTNER_REPORT_EXPLANATION = PARTNER_PREFIX + "/reports/{id}/explanations";

    // 4. ADMIN - Admin dashboard & management (ROLE: ADMIN)
    public static final String ADMIN_PREFIX = BASE_PREFIX + "/admin";
    public static final String ADMIN_USERS = ADMIN_PREFIX + "/users";
    public static final String ADMIN_APPROVALS = ADMIN_PREFIX + "/approvals";
    public static final String ADMIN_CONFIGS = ADMIN_PREFIX + "/configs";
    public static final String ADMIN_MONITORING = ADMIN_PREFIX + "/activities";
    // MANAGE FEEDBACK
    public static final String ADMIN_REVIEW_MODERATE = ADMIN_PREFIX + "/reviews/{id}/moderate";
    public static final String ADMIN_REPORT_STATUS = ADMIN_PREFIX + "/reports/{id}/status";
    public static final String ADMIN_REPORT_VERDICT = ADMIN_PREFIX + "/reports/{id}/verdict";

    // 5. EXTERNAL SYSTEMS - Integrations (Payment, Map, Notification, etc.)
    public static final String SYSTEMS_PREFIX = BASE_PREFIX + "/systems";
    public static final String SYS_PAYMENT_CALLBACK = SYSTEMS_PREFIX + "/payment-callback";
    public static final String SYS_MAP_INTEGRATION = SYSTEMS_PREFIX + "/map-integration";
    public static final String SYS_NOTIFY = SYSTEMS_PREFIX + "/notification-trigger";
}
