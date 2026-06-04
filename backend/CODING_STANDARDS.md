# TraVi-OTA Backend Coding Standards
Tai lieu nay quy dinh quy uoc code cho backend Spring Boot cua TraVi-OTA.
Muc tieu: de doc, de review, de test, de mo rong.
---
## 1) Nguyen tac chung
- Uu tien code ro rang hon code ngan.
- Khong de business logic trong controller.
- Mot method mot trach nhiem chinh.
- Uu tien constructor injection.
- Uu tien Java record cho DTO.
- Khong hardcode endpoint, role, regex, message quan trong.
- Moi thay doi schema DB phai qua Flyway migration.
---
## 2) Cau truc package (theo hien trang project)
```text
backend/src/main/java/com/ota/travi/
  config/
  constant/
  controller/
  dto/request/
  dto/response/
  entity/
  Enum/
  repository/
  security/
  service/
```
Luu y:
- Project dang dung package `Enum` (chu E hoa). Giu nguyen de tranh vo import hien tai.
- DTO phai tach `dto/request` va `dto/response`.
---
## 3) Naming conventions
### File/Class
| Loai | Pattern | Vi du |
|---|---|---|
| Controller | `{Domain}Controller.java` | `AuthController.java` |
| Service | `{Domain}Service.java` | `AuthService.java` |
| Repository | `{Domain}Repository.java` | `UserRepository.java` |
| Entity | `PascalCase.java` | `KhachHang.java` |
| DTO Request | `{Action}Request.java` | `GoogleAuthRequest.java` |
| DTO Response | `{Resource}Response.java` | `AuthResponse.java` |
| Config | `{Feature}Config.java` | `SecurityConfig.java` |
| Filter | `{Feature}Filter.java` | `JwtAuthenticationFilter.java` |
### Method/Field
- Method: camelCase, bat dau bang dong tu (`login`, `verifyRegisterOtp`).
- Field: camelCase (`soDienThoai`, `trangThai`).
- Constant: UPPER_SNAKE_CASE.
- Boolean: `is*`, `has*`, `can*`, `should*`.
---
## 4) Endpoint va versioning
- Tat ca endpoint phai khai bao trong `constant/ApiEndpoints.java`.
- Controller khong hardcode route string.
- Prefix hien tai: `/api/v1`.
Vi du:
```java
public static final String AUTH_PREFIX = BASE_PREFIX + "/auth";
public static final String AUTH_LOGIN = AUTH_PREFIX + "/login";
public static final String AUTH_GOOGLE = AUTH_PREFIX + "/google";
public static final String AUTH_FORGOT_PASSWORD_RESET = AUTH_PREFIX + "/forgot-password/reset";
```
---
## 5) Controller standards
- Dung `@RestController`.
- Dung `@PostMapping/@PutMapping/...` voi constant endpoint.
- Validate input bang `@Valid @RequestBody`.
- Controller chi lam: parse input, goi service, map HTTP status.
- Khong de business logic trong controller.
Mau:
```java
@PostMapping(AUTH_LOGIN)
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    try {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    } catch (BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Sai tai khoan hoac mat khau");
    }
}
```
---
## 6) Service standards
- Service chua business logic.
- Uu tien constructor injection.
- Dung `@Transactional` cho luong ghi DB nhieu buoc.
- Side effect sau commit (gui email OTP) phai chay sau transaction commit.
- Khong return `null`; dung `Optional` hoac throw exception.
`AuthService` can tach method ro:
- `register(...)`
- `login(...)`
- `loginWithGoogle(...)`
- `verifyRegisterOtp(...)`
- `resendRegisterOtp(...)`
- `requestPasswordResetOtp(...)`
- `verifyPasswordResetOtp(...)`
- `resetPassword(...)`
---
## 7) DTO standards
- Uu tien Java `record` cho request/response.
- Request DTO phai gan validation annotations.
- Khong dung entity lam response auth API.
Mau:
```java
public record VerifyOtpRequest(
    @NotBlank @Email String email,
    @NotBlank @Pattern(regexp = "^\\d{6}$") String confirmOTP
) {}
```
`AuthResponse` phai thong nhat field:
- `accessToken`
- `refreshToken`
- `tokenType`
- `message`
---
## 8) Entity va JPA standards
- Dung `@Entity`, `@Table` ro rang.
- Enum field phai `@Enumerated(EnumType.STRING)`.
- FK column dat ten snake_case (`vai_tro_id`).
- Inheritance strategy hien tai: `JOINED`.
- Khong viet business flow phuc tap trong entity.
---
## 9) Security standards
### JWT
- Tach ro access token va refresh token.
- Khong log full token.
- `JwtUtil` can co: generate access/refresh token, extract username, validate token, get remaining expiration.
### JwtAuthenticationFilter
- Extract token tu `Authorization: Bearer ...`.
- Check blacklist truoc khi set SecurityContext.
- Bat loi parse JWT, khong lam crash request chain.
### Rate limiting
- Login endpoint duoc gioi han bang Bucket4j.
- Vuot nguong tra HTTP 429.
---
## 10) Redis standards (OTP + blacklist)
- OTP luu Redis phai co TTL.
- OTP key phai tach purpose bang `OtpPurpose`: `REGISTER`, `RESET_PASSWORD`.
- Blacklist token luu Redis voi TTL bang thoi gian con lai cua token.
---
## 11) Google OAuth2 standards
- Backend nhan `idToken` tu frontend.
- Bat buoc verify bang `GoogleTokenVerifierService`.
- Khong tin payload Google neu chua verify.
- Trong `loginWithGoogle`, xu ly du 3 case:
  - email moi: tao account,
  - account `CHUA_XAC_THUC`: kich hoat,
  - account `HOAT_DONG`: login binh thuong.
---
## 12) Error handling standards
- Khong nuot exception (`catch {}` rong).
- Message loi phai ro nghiep vu.
- Uu tien custom exception cho case dung lai.
- Dung dung HTTP status code: 200, 201, 400, 401, 403, 404, 409, 429.
---
## 13) Validation standards
- Dung `jakarta.validation` tren DTO request.
- Rule password frontend va backend phai dong bo.
- Rule email/phone phai co regex ro va message ro.
---
## 14) Logging standards
- Dung `@Slf4j` (hoac LoggerFactory).
- Khong dung `System.out.println`.
- Khong log password, OTP, token day du.
Log level:
- `debug`: thong tin ky thuat
- `info`: su kien nghiep vu quan trong
- `warn`: tinh huong bat thuong
- `error`: exception can dieu tra
---
## 15) Test standards
- Test file dat ten `*Test.java`.
- Unit test bat buoc cho auth luong quan trong: login, loginWithGoogle, verify OTP, reset password, refresh token.
- Mock external dependency (Google API, Redis, SMTP) trong unit test.
---
## 16) Build va quality gate
Truoc khi push:
```powershell
cd backend
mvn test
mvn clean package -DskipTests
```
Checklist:
- [ ] Dung endpoint constants trong controller
- [ ] Khong hardcode secret/password/token
- [ ] DTO request co validation
- [ ] Khong business logic trong controller
- [ ] Redis key co purpose va TTL dung
- [ ] Flyway migration da them neu doi schema
- [ ] Unit test lien quan da cap nhat
---
## 17) Anti-patterns cam tranh
- Hardcode endpoint trong controller.
- Goi external side effect truoc khi transaction commit.
- Return entity thang ra API auth.
- Dung `Optional.get()` khong check.
- Dung generic exception khong context.
---
## 18) Update policy
Moi khi them endpoint auth moi, phai cap nhat dong thoi:
1. `ApiEndpoints.java`
2. Controller lien quan
3. Service lien quan
4. DTO request/response
5. Unit tests
6. `README-SPRINT1.md` va file coding standards neu co rule moi
---
**Last Updated:** May 2026  
**Version:** 1.1.0
