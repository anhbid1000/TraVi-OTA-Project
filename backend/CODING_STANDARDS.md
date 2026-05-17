# TraVi-OTA Backend - Coding Standards & Style Guide

Hướng dẫn toàn diện cho việc viết code Java/Spring Boot, đặt tên, tạo file, tổ chức package, và các pattern phát triển trong TraVi-OTA Backend.

---

## 📋 Mục Lục

1. [File & Naming Conventions](#file--naming-conventions)
2. [Package & Folder Structure](#package--folder-structure)
3. [Java Naming Conventions](#java-naming-conventions)
4. [Class Type Patterns](#class-type-patterns)
5. [Entity & JPA Patterns](#entity--jpa-patterns)
6. [DTO (Request/Response) Patterns](#dto-requestresponse-patterns)
7. [Repository Patterns](#repository-patterns)
8. [Service Layer Patterns](#service-layer-patterns)
9. [Controller Patterns](#controller-patterns)
10. [Security Patterns](#security-patterns)
11. [Configuration & Bean Patterns](#configuration--bean-patterns)
12. [Enum & Constants](#enum--constants)
13. [Error Handling](#error-handling)
14. [Validation](#validation)
15. [Comments & Documentation](#comments--documentation)
16. [Best Practices](#best-practices)

---

## 📁 File & Naming Conventions

### File Naming

| Type               | Pattern                                          | Example                                                      |
| ------------------ | ------------------------------------------------ | ------------------------------------------------------------ |
| Class (Controller) | `{Entity}Controller.java`                        | `AuthController.java`, `UserController.java`                 |
| Service            | `{Entity}Service.java`                           | `AuthService.java`, `OrderService.java`                      |
| Repository         | `{Entity}Repository.java`                        | `UserRepository.java`, `OrderRepository.java`                |
| Entity             | `{EntityName}.java` (PascalCase)                 | `User.java`, `Order.java`, `VaiTro.java`                     |
| DTO Request        | `{Action}Request.java`                           | `LoginRequest.java`, `RegisterRequest.java`                  |
| DTO Response       | `{Resource}Response.java`                        | `AuthResponse.java`, `UserResponse.java`                     |
| Config             | `{Feature}Config.java`                           | `SecurityConfig.java`, `AsyncConfig.java`                    |
| Filter             | `{Feature}Filter.java`                           | `JwtAuthenticationFilter.java`, `RateLimitFilter.java`       |
| Enum               | `{Name}.java` (PascalCase)                       | `TrangThaiUser.java`, `GioiTinh.java`                        |
| Util               | `{Feature}Util.java` hoặc `{Feature}Helper.java` | `JwtUtil.java`, `DateUtil.java`                              |
| Constant           | `{Feature}Constants.java`                        | `ApiEndpoints.java`, `ErrorMessages.java`                    |
| Exception          | `{Custom}Exception.java`                         | `ResourceNotFoundException.java`, `ValidationException.java` |

**Quy tắc**:

- Tất cả file sử dụng **PascalCase**
- Naming phải mô tả rõ ràng vai trò (Controller, Service, Entity, etc.)
- Hạn chế dùng tên quá ngắn hoặc quá dài

---

## 📦 Package & Folder Structure

### Standard Package Structure

```
backend/
├── src/main/java/com/ota/travi
│   ├── TraViOtaApplication.java         # Spring Boot main entry point
│   │
│   ├── config/                          # Configuration classes
│   │   ├── AppConfig.java               # General app config (beans, encoder)
│   │   ├── SecurityConfig.java          # Spring Security config
│   │   ├── AsyncConfig.java             # Async executor config
│   │   ├── JdbcConfig.java              # Database config (if needed)
│   │   └── CorsConfig.java              # CORS configuration
│   │
│   ├── constant/                        # Constants & static values
│   │   ├── ApiEndpoints.java            # API endpoint definitions
│   │   ├── ErrorMessages.java           # Error message constants
│   │   └── RegexPatterns.java           # Regex pattern constants
│   │
│   ├── security/                        # Security-related classes
│   │   ├── JwtUtil.java                 # JWT utility
│   │   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   │   ├── CustomUserDetails.java       # Custom UserDetails implementation
│   │   ├── CustomUserDetailsService.java# UserDetailsService implementation
│   │   ├── RateLimitFilter.java         # Rate limiting filter
│   │   └── AuthenticationEntryPoint.java# Exception handler for auth
│   │
│   ├── entity/                          # JPA entities (database models)
│   │   ├── User.java                    # Parent class (abstract or concrete)
│   │   ├── KhachHang.java               # Customer entity
│   │   ├── DoiTac.java                  # Partner entity
│   │   ├── QuanTriVien.java             # Admin entity
│   │   ├── VaiTro.java                  # Role entity
│   │   ├── SoThich.java                 # Preference entity
│   │   ├── DanhMucSoThich.java          # Category entity
│   │   └── LichSuThaoTac.java           # History/Audit log entity
│   │
│   ├── dto/                             # Data Transfer Objects
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── VerifyOtpRequest.java
│   │   │   ├── RefreshTokenRequest.java
│   │   │   └── ResendOtpRequest.java
│   │   │
│   │   └── response/
│   │       ├── AuthResponse.java
│   │       ├── UserResponse.java
│   │       └── ApiResponse.java         # Generic response wrapper
│   │
│   ├── repository/                      # Spring Data JPA repositories
│   │   ├── UserRepository.java
│   │   ├── VaiTroRepository.java
│   │   ├── OrderRepository.java         # (future)
│   │   └── CustomRepository.java        # Custom query repository
│   │
│   ├── service/                         # Business logic layer
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── OTPService.java
│   │   ├── TokenBlacklistService.java
│   │   ├── EmailService.java
│   │   └── OrderService.java            # (future)
│   │
│   ├── controller/                      # REST API endpoints
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── AdminController.java
│   │   └── OrderController.java         # (future)
│   │
│   ├── enum/                            # Java Enumerations
│   │   ├── TrangThaiUser.java           # User status enum
│   │   ├── GioiTinh.java                # Gender enum
│   │   ├── HangThanhVien.java           # Member tier enum
│   │   └── LoaiTaiKhoan.java            # Account type enum
│   │
│   ├── exception/                       # Custom exceptions
│   │   ├── ResourceNotFoundException.java
│   │   ├── ValidationException.java
│   │   ├── AuthenticationException.java
│   │   └── GlobalExceptionHandler.java  # Global exception handler
│   │
│   ├── util/                            # Utility & helper classes
│   │   ├── DateUtil.java
│   │   ├── StringUtil.java
│   │   ├── ValidationUtil.java
│   │   └── EncryptionUtil.java
│   │
│   └── ai/                              # AI integration (future)
│       └── (OpenAI API calls, etc.)
│
├── src/main/resources/
│   ├── application.properties           # Main config
│   ├── application-dev.properties       # Dev environment
│   ├── application-prod.properties      # Production environment
│   ├── application-test.properties      # Test environment
│   └── db/migration/
│       └── V1__init_schema.sql          # Flyway migrations
│
└── src/test/java/com/ota/travi
    ├── service/
    │   ├── AuthServiceTest.java
    │   └── UserServiceTest.java
    ├── controller/
    │   └── AuthControllerTest.java
    └── security/
        └── JwtUtilTest.java
```

### Package Organization Principles

1. **Layer-based Organization**: Package theo layer (controller, service, repository)
2. **Feature-based (Optional)**: Cho project lớn, có thể group package theo feature

   ```
   com.ota.travi.auth.*     (AuthController, AuthService, AuthRequest, etc.)
   com.ota.travi.user.*     (UserController, UserService, UserRequest, etc.)
   com.ota.travi.order.*    (OrderController, OrderService, OrderRequest, etc.)
   com.ota.travi.shared.*   (Exception, Util, Config, Constants)
   ```

3. **Separation of Concerns**: Mỗi package có responsibility riêng
   - `entity`: Database models only
   - `dto`: Transfer objects only
   - `service`: Business logic only
   - `controller`: HTTP endpoints only
   - `repository`: Data access only

---

## 🎯 Java Naming Conventions

### Variables & Fields

```java
// ✅ GOOD - camelCase
private String username;
private Integer diemThanhVien;
private LocalDateTime ngayTao;
private String refreshToken;
private Boolean isActive;

// ✅ GOOD - Prefix for specific types
private Boolean isEnabled;              // boolean - is/has prefix
private String firstName;               // String
private Integer age;                    // Integer/Long
private List<String> emails;           // Collection

// ❌ AVOID
private String UserName;                // PascalCase for variable
private Integer DiemThanhVien;
private String user_name;               // snake_case
private String firstname;               // too short/ambiguous
```

### Constants

```java
// ✅ GOOD - CONSTANT_CASE for final static
private static final String API_BASE_URL = "http://localhost:8080/api";
private static final Integer OTP_EXPIRATION_MINUTES = 5;
private static final String JWT_SECRET_KEY = "your-secret-key";
public static final String ERROR_USER_NOT_FOUND = "User not found";
private static final Long JWT_EXPIRATION_TIME = 3600000L; // 1 hour in ms

// ✅ GOOD - Enum constants (UPPER_CASE)
public enum TrangThaiUser {
    HOAT_DONG,
    BI_KHOA,
    CHUA_XAC_THUC,
    DA_XOA
}

// ❌ AVOID
private static final String apiBaseUrl = "...";     (không CONSTANT_CASE)
private static final String Api_Base_Url = "...";   (mixed case)
```

### Methods

```java
// ✅ GOOD - Action verb + noun (camelCase)
public void sendVerificationEmail(User user) { }
public String generateToken(String username) { }
public Boolean verifyOtp(String email, String otp) { }
public List<User> findAllUsers() { }
public Optional<User> findByEmail(String email) { }
public void updateUserProfile(UserRequest request) { }
public AuthResponse login(LoginRequest request) { }

// ✅ GOOD - Getter/Setter (get/set prefix)
public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
public Boolean isEnabled() { return isEnabled; }
public void setEnabled(Boolean enabled) { this.isEnabled = enabled; }

// ✅ GOOD - Predicate methods (is/has prefix)
public Boolean hasRole(String role) { }
public Boolean isTokenExpired(String token) { }
public Boolean isBlacklisted(String token) { }

// ❌ AVOID
public void Email() { }                 (constructor-like name)
public void userEmail(String email) { } (no action verb)
public String extract_username() { }    (snake_case)
public void SendEmail() { }             (PascalCase for method)
```

### Class Names

```java
// ✅ GOOD - PascalCase
public class AuthController { }
public class UserService { }
public class TokenBlacklistService { }
public class CustomUserDetailsService { }
public class JwtAuthenticationFilter { }
public class ValidationException { }

// ❌ AVOID
public class authController { }         (camelCase)
public class auth_controller { }        (snake_case)
public class AuthControllerService { }  (confusing name - not clear role)
```

### Interface Names

```java
// ✅ GOOD - Can implement Throwable, Serializable directly hoặc can suffix Interface
public interface UserRepository { }
public interface AuthService { }
public interface CustomUserDetailsService { }

// ❌ AVOID
public interface IUserRepository { }    (I prefix - not Java convention)
public interface UserRepositoryInterface { }  (too verbose)
```

---

## 🏗️ Class Type Patterns

### Controller Pattern

```java
// src/main/java/com/ota/travi/controller/AuthController.java

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String message = authService.register(request);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse authResponse = authService.login(authenticationManager, jwtUtil, request);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (DisabledException ex) {
            return new ResponseEntity<>("Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN);
        } catch (BadCredentialsException ex) {
            return new ResponseEntity<>("Sai tài khoản hoặc mật khẩu", HttpStatus.UNAUTHORIZED);
        }
    }
}

/**
 * Quy tắc Controller:
 * 1. Use @RestController (not @Controller for APIs)
 * 2. Use @RequestMapping cho base path
 * 3. Use @PostMapping, @GetMapping, @PutMapping, @DeleteMapping
 * 4. Inject services via @Autowired (constructor injection preferred)
 * 5. Use @Valid @RequestBody for validation
 * 6. Return ResponseEntity<?> để full control response (status, body, headers)
 * 7. Handle exceptions trong controller hoặc dùng @ControllerAdvice/@ExceptionHandler
 * 8. Use @PathVariable, @RequestParam, @RequestHeader khi cần
 */
```

### Service Pattern

```java
// src/main/java/com/ota/travi/service/AuthService.java

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OTPService otpService;

    // ✅ GOOD - Transactional for multi-step operations
    @Transactional
    public String register(RegisterRequest request) {
        // 1. Validation
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // 2. Create entity
        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        newUser.setMatKhau(passwordEncoder.encode(request.matKhau()));

        // 3. Save to DB
        User savedUser = userRepository.save(newUser);

        // 4. Send email (after commit)
        otpService.sendVerificationEmail(savedUser);

        return "Đăng ký thành công!";
    }

    // ✅ GOOD - Separate method cho reusability
    public AuthResponse login(AuthenticationManager manager, JwtUtil jwtUtil, LoginRequest request) {
        // Logic here
    }

    // ✅ GOOD - Use Optional for single entity retrieval
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

/**
 * Quy tắc Service:
 * 1. Mark class as @Service
 * 2. Inject repositories via @Autowired
 * 3. Use @Transactional cho operations thay đổi DB
 * 4. Chia method thành các unit nhỏ (Single Responsibility)
 * 5. Throw meaningful exceptions
 * 6. Use try/catch khi cần, nhưng prefer throwing exceptions
 * 7. Không return null - use Optional<T> hoặc throw exception
 * 8. Keep business logic, không mix với HTTP concerns
 */
```

### Repository Pattern

```java
// src/main/java/com/ota/travi/repository/UserRepository.java

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // ✅ GOOD - Spring Data derives query từ method name
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // ✅ GOOD - For checking existence (use boolean return)
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // ✅ GOOD - Custom queries khi complex
    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.trangThai = 'HOAT_DONG'")
    Optional<User> findActiveUserByEmail(String email);

    // ✅ GOOD - For multiple results
    List<User> findByTrangThai(TrangThaiUser status);
    Page<User> findByVaiTro(VaiTro role, Pageable pageable);
}

/**
 * Quy tắc Repository:
 * 1. Extend JpaRepository<T, ID>
 * 2. Use descriptive method names: findBy*, existsBy*, countBy*, deleteBy*
 * 3. Return Optional<T> cho single entity (never null)
 * 4. Return List<T> or Page<T> cho multiple entities
 * 5. Use @Query cho complex queries (JPQL hoặc SQL)
 * 6. Keep repository interface simple, không add business logic
 * 7. Don't use @Autowired in interface, Spring auto-registers
 */
```

---

## 💾 Entity & JPA Patterns

### Entity Class Template

```java
// src/main/java/com/ota/travi/entity/User.java

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)  // Inheritance strategy
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {

    // ✅ GOOD - UUID primary key
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // ✅ GOOD - Column constraints
    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String matKhau;

    // ✅ GOOD - Enum with @Enumerated
    @Enumerated(EnumType.STRING)
    private TrangThaiUser trangThai = TrangThaiUser.CHUA_XAC_THUC;

    // ✅ GOOD - Timestamps with Hibernate annotations
    @CreationTimestamp
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;

    // ✅ GOOD - Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vai_tro_id")
    private VaiTro vaiTro;

    // ✅ GOOD - OneToMany with cascade
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoThich> soThichs;
}

// ✅ GOOD - Child class using JOINED inheritance
@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@NoArgsConstructor
public class KhachHang extends User {

    @Column(name = "diem_thanh_vien")
    private Integer diemThanhVien = 0;

    @Enumerated(EnumType.STRING)
    private HangThanhVien hangThanhVien = HangThanhVien.DONG;

    @Column(name = "tong_chi_tieu")
    private Double tongChiTieu = 0.0;
}

/**
 * Quy tắc Entity:
 * 1. Use @Entity cho persistent class
 * 2. Use @Table để custom tên table (snake_case_table_names)
 * 3. Use @Id + @GeneratedValue(GenerationType.UUID) cho primary key
 * 4. Use @Column(nullable = false, unique = true) cho constraints
 * 5. Use @Enumerated(EnumType.STRING) cho Enum fields
 * 6. Use @CreationTimestamp/@UpdateTimestamp cho timestamps
 * 7. Use @ManyToOne/@OneToMany/@ManyToMany cho relationships
 * 8. Use Lombok (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor)
 * 9. Use @Inheritance(strategy = InheritanceType.JOINED) để chia bảng
 * 10. Không add business logic logic trong entity - use service
 */
```

### Naming Conventions for Entity Fields

```java
@Entity
public class User {
    // ✅ GOOD - snake_case cho tên column, camelCase cho field
    @Column(name = "email")
    private String email;

    @Column(name = "ho_ten")               // Tiếng Việt field
    private String hoTen;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "trang_thai_user")
    @Enumerated(EnumType.STRING)
    private TrangThaiUser trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vai_tro_id")      // snake_case cho FK column
    private VaiTro vaiTro;
}
```

---

## 📨 DTO (Request/Response) Patterns

### Request DTO Using Records

```java
// src/main/java/com/ota/travi/dto/request/LoginRequest.java

// ✅ GOOD - Use Java Records (Java 16+) for immutable DTOs
public record LoginRequest(
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    String email,

    @NotBlank(message = "Mật khẩu không được để trống")
    String matKhau,

    Boolean nhoMatKhau  // Optional field
) {}

// ✅ GOOD - Register Request
public record RegisterRequest(
    @NotBlank(message = "Username không được để trống")
    String username,

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    String email,

    @NotBlank(message = "Họ tên không được để trống")
    String hoTen,

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)\\d{9}$", message = "Số điện thoại không hợp lệ")
    String soDienThoai,

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 20, message = "Mật khẩu phải từ 8-20 ký tự")
    String matKhau,

    @NotBlank(message = "Loại tài khoản không được để trống")
    String loaiTaiKhoan  // "KHACH_HANG" hoặc "DOI_TAC"
) {}

// ✅ GOOD - Verify OTP Request
public record VerifyOtpRequest(
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    String email,

    @NotBlank(message = "OTP không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "OTP phải gồm 6 chữ số")
    String confirmOTP
) {}
```

### Response DTO Using Records

```java
// src/main/java/com/ota/travi/dto/response/AuthResponse.java

public record AuthResponse(
    String token,           // JWT access token
    String refreshToken,    // Refresh token
    String type,           // Token type (Bearer)
    String message         // Success message
) {}

// ✅ GOOD - Generic API Response
public record ApiResponse<T>(
    Boolean success,
    String message,
    T data,
    Long timestamp
) {}

// ✅ GOOD - User Response DTO
public record UserResponse(
    String id,
    String username,
    String email,
    String hoTen,
    String soDienThoai,
    String vaiTro,
    LocalDateTime ngayTao
) {}
```

### Traditional Class DTO (if not using Records)

```java
// ✅ GOOD - Alternative sử dụng class (für älter Java versions)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String hoTen;
    private String vaiTro;

    // ✅ GOOD - Có thể add helper methods
    public String getFullName() {
        return hoTen;
    }
}
```

**Quy tắc DTO**:

1. Dùng **Records** (immutable, compact, automatic equals/hashCode/toString)
2. Dùng **@Valid @RequestBody** trong controller
3. Validation annotations: @NotBlank, @Email, @Pattern, @Size
4. Không chứa entity directly trong response DTO
5. DTO request/response tách biệt (không dùng entity làm DTO)

---

## 🔍 Repository Patterns

### Basic Repository

```java
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // ✅ GOOD - Method names derived by Spring Data
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // ✅ GOOD - Boolean exists methods
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // ✅ GOOD - Multiple results
    List<User> findByTrangThai(TrangThaiUser status);
    Page<User> findByVaiTro(VaiTro vaiTro, Pageable pageable);
}

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, String> {
    Optional<VaiTro> findByTen(String ten);
}
```

### Custom Repository with @Query

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    // ✅ GOOD - JPQL query
    @Query("SELECT o FROM Order o WHERE o.user.id = ?1 AND o.status = 'COMPLETED'")
    List<Order> findCompletedOrdersByUserId(String userId);

    // ✅ GOOD - Native SQL query
    @Query(value = "SELECT * FROM orders WHERE user_id = ?1 AND status = 'PENDING'",
           nativeQuery = true)
    List<Order> findPendingOrdersByUserId(String userId);

    // ✅ GOOD - With sorting/pagination
    @Query("SELECT o FROM Order o WHERE o.user.id = ?1")
    Page<Order> findOrdersByUserId(String userId, Pageable pageable);
}
```

---

## 🔧 Service Layer Patterns

### Transactional Service Method

```java
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OTPService otpService;

    // ✅ GOOD - @Transactional cho multi-step operations
    @Transactional
    public String register(RegisterRequest request) {
        // 1. Validation
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // 2. Entity creation
        User newUser = switch(request.loaiTaiKhoan().toUpperCase()) {
            case "KHACH_HANG" -> new KhachHang();
            case "DOI_TAC" -> new DoiTac();
            default -> throw new RuntimeException("Invalid account type");
        };

        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        newUser.setMatKhau(passwordEncoder.encode(request.matKhau()));

        // 3. Save (commit to DB)
        User savedUser = userRepository.save(newUser);

        // 4. Side effect (after commit)
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        otpService.sendVerificationEmail(savedUser);
                    }
                }
            );
        }

        return "Đăng ký thành công!";
    }

    // ✅ GOOD - Separate query method (no @Transactional needed)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ✅ GOOD - Exception handling in service
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // logic here

        return new AuthResponse(...);
    }
}

/**
 * Quy tắc Service:
 * 1. @Transactional only khi thay đổi multiple tables
 * 2. Use @Transactional(readOnly = true) cho query-only
 * 3. Throw meaningful exceptions
 * 4. Use Optional<T> - never return null
 * 5. Keep methods focused (Single Responsibility)
 * 6. Register TransactionSynchronization cho async work sau commit
 */
```

---

## 🎯 Controller Patterns

### RESTful Controller Template

```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ GOOD - Endpoint structure
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String message = authService.register(request);
            return new ResponseEntity<>(message, HttpStatus.CREATED);  // 201
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);  // 400
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse authResponse = authService.login(authenticationManager, jwtUtil, request);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);  // 200
        } catch (BadCredentialsException ex) {
            return new ResponseEntity<>("Sai tài khoản hoặc mật khẩu", HttpStatus.UNAUTHORIZED);  // 401
        } catch (DisabledException ex) {
            return new ResponseEntity<>("Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN);  // 403
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            long remainingTime = jwtUtil.getRemainingExpirationTime(token);
            blacklistService.addToBlacklist(token, remainingTime);
        }
        return new ResponseEntity<>("Đăng xuất thành công!", HttpStatus.OK);
    }

    @PutMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyOtpRequest request) {
        try {
            String message = authService.verifyRegisterOtp(request.email(), request.confirmOTP());
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

/**
 * Quy tắc Controller:
 * 1. @RestController + @RequestMapping cho base path
 * 2. Inject services via @Autowired
 * 3. Use @Valid @RequestBody để validate incoming data
 * 4. Use @PathVariable, @RequestParam, @RequestHeader khi cần
 * 5. Return ResponseEntity<> để control status code
 * 6. Use appropriate HTTP status codes: 200, 201, 400, 401, 403, 404, 500
 * 7. Handle exceptions explicitly hoặc dùng @ControllerAdvice
 * 8. Không business logic trong controller - delegate to service
 */
```

### HTTP Status Codes Convention

```java
// ✅ GOOD usage of HTTP status codes
200 OK                  - Successful GET/PUT/DELETE
201 CREATED             - Successful POST (resource created)
204 NO_CONTENT          - Successful DELETE (no response body)
400 BAD_REQUEST         - Validation failed
401 UNAUTHORIZED        - Missing/invalid authentication
403 FORBIDDEN           - Authenticated but no permission
404 NOT_FOUND           - Resource not found
409 CONFLICT            - Resource already exists
500 INTERNAL_SERVER_ERROR - Server error

// ✅ GOOD
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return new ResponseEntity<>(HttpStatus.CREATED);
}

// ✅ GOOD - with message
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    try {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);  // 200
    } catch (BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Sai tài khoản hoặc mật khẩu");
    }
}
```

---

## 🔐 Security Patterns

### JWT Utility Pattern

```java
// src/main/java/com/ota/travi/security/JwtUtil.java

@Component
public class JwtUtil {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Value("${JWT_EXPIRATION}")
    private Long expirationTime;

    private static final Long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 days

    // ✅ GOOD - Generate token with role
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    // ✅ GOOD - Extract username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ GOOD - Extract custom claim (role)
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // ✅ GOOD - Validate token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // ✅ GOOD - Generate refresh token
    public String generateRefreshToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    // ✅ GOOD - Get remaining expiration time (for Redis TTL)
    public long getRemainingExpirationTime(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        long diff = expiration.getTime() - System.currentTimeMillis();
        return Math.max(diff, 0);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return resolver.apply(claims);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### JWT Authentication Filter

```java
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain chain) throws ServletException, IOException {
        // ✅ GOOD - Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // ✅ GOOD - Check blacklist
            if (!blacklistService.isBlacklisted(token)) {
                try {
                    String username = jwtUtil.extractUsername(token);

                    // ✅ GOOD - Load user from DB
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // ✅ GOOD - Validate token
                    if (jwtUtil.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                            );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (JwtException ex) {
                    log.warn("Invalid JWT token: {}", ex.getMessage());
                } catch (Exception ex) {
                    log.error("Error processing JWT: {}", ex.getMessage());
                }
            }
        }

        chain.doFilter(request, response);
    }
}
```

---

## ⚙️ Configuration & Bean Patterns

### Security Configuration

```java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("QUAN_TRI_VIEN")
                .requestMatchers("/api/v1/partner/**").hasRole("DOI_TAC")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### General Configuration

```java
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }
}
```

---

## 📋 Enum & Constants

### Enum Pattern

```java
// src/main/java/com/ota/travi/Enum/TrangThaiUser.java

public enum TrangThaiUser {
    HOAT_DONG("Hoạt động"),
    BI_KHOA("Bị khóa"),
    CHUA_XAC_THUC("Chưa xác thực"),
    DA_XOA("Đã xóa");

    private final String displayName;

    TrangThaiUser(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

// ✅ GOOD - Using in Entity
@Entity
public class User {
    @Enumerated(EnumType.STRING)
    private TrangThaiUser trangThai = TrangThaiUser.CHUA_XAC_THUC;
}
```

### Constants Pattern

```java
// src/main/java/com/ota/travi/constant/ApiEndpoints.java

public final class ApiEndpoints {

    private ApiEndpoints() {  // Private constructor to prevent instantiation
    }

    // Base & Versioning
    public static final String API_PREFIX = "/api";
    public static final String API_VERSION = "/v1";
    public static final String BASE_PREFIX = API_PREFIX + API_VERSION;

    // Authentication endpoints
    public static final String AUTH_PREFIX = BASE_PREFIX + "/auth";
    public static final String AUTH_REGISTER = AUTH_PREFIX + "/register";
    public static final String AUTH_LOGIN = AUTH_PREFIX + "/login";
    public static final String AUTH_LOGOUT = AUTH_PREFIX + "/logout";
    public static final String AUTH_REFRESH = AUTH_PREFIX + "/refresh";
    public static final String AUTH_VERIFY_EMAIL = AUTH_PREFIX + "/verify-email";
}
```

---

## ⚠️ Error Handling

### Custom Exception Pattern

```java
// src/main/java/com/ota/travi/exception/ResourceNotFoundException.java

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

// src/main/java/com/ota/travi/exception/ValidationException.java

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors);
    }
}
```

---

## ✅ Validation

### Validation Annotations

```java
// ✅ GOOD - Use Jakarta Validation annotations
public record RegisterRequest(
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50)
    String username,

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    String email,

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 20, message = "Mật khẩu phải từ 8-20 ký tự")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).*$", message = "Mật khẩu phải có chữ hoa và số")
    String matKhau,

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)\\d{9}$", message = "Số điện thoại không hợp lệ")
    String soDienThoai
) {}

// Common annotations:
// @NotNull, @NotBlank, @NotEmpty
// @Size, @Min, @Max, @DecimalMin, @DecimalMax
// @Email, @Pattern (regex)
// @Positive, @Negative, @PositiveOrZero, @NegativeOrZero
// @Future, @Past, @FutureOrPresent, @PastOrPresent
```

---

## 📝 Comments & Documentation

### Javadoc Pattern

```java
/**
 * Ghi danh tài khoản mới vào hệ thống.
 *
 * Quy trình:
 * 1. Kiểm tra xem email/username có bị trùng không
 * 2. Băm mật khẩu bằng BCrypt
 * 3. Lưu user vào database
 * 4. Gửi OTP xác minh qua email
 *
 * @param request Thông tin đăng ký (username, email, mật khẩu, loại tài khoản)
 * @return Thông báo thành công
 * @throws RuntimeException nếu email hoặc username đã tồn tại
 */
@Transactional
public String register(RegisterRequest request) {
    // Implementation
}

/**
 * Sinh token JWT cho user.
 *
 * @param username Tên đăng nhập
 * @param role Vai trò của user (KHACH_HANG, DOI_TAC, QUAN_TRI_VIEN)
 * @return JWT token hợp lệ 1 giờ
 */
public String generateToken(String username, String role) {
    // Implementation
}
```

### Inline Comments

```java
// ✅ GOOD - Explain WHY, not WHAT
@Transactional
public String register(RegisterRequest request) {
    // Check for duplicates first to fail fast before DB operations
    if (userRepository.existsByEmail(request.email())) {
        throw new RuntimeException("Email already exists");
    }

    // Encode password using BCrypt for security (never store plaintext)
    newUser.setMatKhau(passwordEncoder.encode(request.matKhau()));

    // Send OTP after transaction commits to avoid email if DB rollback
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                otpService.sendVerificationEmail(savedUser);
            }
        }
    );
}

// ❌ AVOID - Obvious comments
// Set email to user
newUser.setEmail(request.email());

// ❌ AVOID - Outdated comments
// Last modified 2024 - this is deprecated
```

---

## ✨ Best Practices

### Dependency Injection

```java
// ✅ GOOD - Constructor injection (Spring 4.3+)
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
}

// ✅ ACCEPTABLE - @Autowired field injection (less preferred)
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
}

// ❌ AVOID - Setter injection for required dependencies
```

### Exception Handling

```java
// ✅ GOOD - Throw meaningful exceptions
User user = userRepository.findByEmail(email)
    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

// ✅ GOOD - Use try/catch only when necessary
try {
    String token = jwtUtil.extractUsername(invalidToken);
} catch (JwtException ex) {
    log.warn("Invalid JWT: {}", ex.getMessage());
    throw new ValidationException("Token validation failed");
}

// ❌ AVOID - Swallowing exceptions
try {
    authService.login(request);
} catch (Exception ex) {
    // Silently ignore
}

// ❌ AVOID - Generic exceptions
throw new Exception("Something went wrong");
```

### Null Handling

```java
// ✅ GOOD - Use Optional
Optional<User> user = userRepository.findByEmail(email);
user.ifPresent(u -> {
    // Process user
});

// ✅ GOOD - Use orElseThrow
User user = userRepository.findByEmail(email)
    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

// ✅ GOOD - Use orElse
User user = userRepository.findByEmail(email)
    .orElse(null);

// ❌ AVOID - Null checks
User user = userRepository.findByEmail(email).get();  // May throw NoSuchElementException
if (user != null) {
    // Process
}
```

### Logging Best Practices

```java
private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
// hoặc với Lombok
@Slf4j

// ✅ GOOD - Use appropriate log levels
@Service
@Slf4j
public class AuthService {

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.email());

        try {
            // Process login
            log.debug("Generated JWT token for user: {}", request.email());
            return new AuthResponse(...);
        } catch (BadCredentialsException ex) {
            log.warn("Failed login attempt for user: {}", request.email());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during login", ex);
            throw new RuntimeException("Login failed");
        }
    }
}

// Log Levels:
// DEBUG - Development info
// INFO - Important business events
// WARN - Potential problems
// ERROR - Errors that need attention
```

---

## 🚀 Checklist trước khi Push Code

- [ ] Package structure theo conventions
- [ ] Class/Method names rõ ràng + mô tả
- [ ] Annotations đầy đủ (@Service, @Repository, @Entity, @Transactional)
- [ ] DTO validation @Valid + validation annotations
- [ ] Error handling + meaningful exceptions
- [ ] ResponseEntity với đúng HTTP status codes
- [ ] No null pointer exceptions - use Optional
- [ ] Logging ở các điểm quan trọng
- [ ] Comments cho non-obvious logic
- [ ] No hardcoded values - use constants
- [ ] No println - use logger
- [ ] @Transactional cho multi-step operations
- [ ] Flyway migrations cho schema changes
- [ ] Dependencies via constructor injection
- [ ] Records dùng cho immutable DTOs
- [ ] Enums cho fixed set values
- [ ] Build passes: `mvn clean build`
- [ ] Tests written (Unit + Integration)

---

## 📚 References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT Documentation](https://tools.ietf.org/html/rfc7519)
- [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/)
- [Lombok](https://projectlombok.org/)

---

**Last Updated**: May 2026
**Version**: 1.0.0
