🚀 TraVi-OTA: Smart Hotel & Restaurant Management System
TraVi-OTA là nền tảng quản lý tích hợp "Tất-cả-trong-một" dành cho Khách sạn và Nhà hàng, được tối ưu hóa bằng Trí tuệ nhân tạo (AI). Dự án hướng tới việc cung cấp trải nghiệm du lịch thông minh, từ đặt phòng, quản lý thực đơn đến lịch trình cá nhân hóa cho khách hàng.

🛠 Tech Stack

1. Backend
   Language: Java 21 (LTS)
   Framework: Spring Boot 3.3+
   Security: Spring Security & JWT
   AI Integration: Spring AI (OpenAI/Ollama)
   API Doc: Springdoc OpenAPI (Swagger)

2. Frontend
   Library: React 18
   Language: TypeScript
   Build Tool: Vite
   Styling: Tailwind CSS

3. Database & Infrastructure
   Database: PostgreSQL 16

4. Caching: Redis 7 (Alpine)

5. Containerization: Docker & Docker Compose

6. CI/CD: GitHub Actions

📂 Cấu trúc dự án (Monorepo)
Plaintext
Project-TraVi-OTA/
├── backend/ # Mã nguồn Spring Boot
├── frontend/ # Mã nguồn React + Vite
├── docker-compose.yml # File cấu hình chạy toàn bộ hệ thống
├── .gitignore/ #ngó lơ một số đường dẫn để bảo mật và tránh các thư mục không cần thiết
├── .env.example # File mẫu biến môi trường
├── .github/ # Cấu hình GitHub Actions (CI Pipeline)
└── README.md # Tài liệu hướng dẫn (Bạn đang ở đây)

⚙️ Hướng dẫn cài đặt & Chạy dự án

1. Yêu cầu hệ thống
   Đã cài đặt Docker và Docker Compose.

(Tùy chọn) Java 21 và Node.js 20+ nếu muốn chạy local không qua Docker.

2. Thiết lập biến môi trường
   Sao chép file mẫu (.env.example thành .env) và điền các thông tin cần thiết (như API Key của OpenAI):
   cp .env.example .env

3. Cách chạy bằng Docker (Khuyến nghị)

- Chỉ với một câu lệnh, toàn bộ hệ thống (Postgres, Redis, Backend, Frontend) sẽ tự động được build và khởi chạy:
  docker compose up -d --build # Với lệnh này ta tạo ra các container cho các image được build từ các ứng dụng. | Chỉ sử dụng tùy chọn --build khi mà cần build lại 1 image mới. Còn để chạy thôi thì không cần
  - Để dừng các container. ==> docker compose down (dừng và xóa các container đó).

- Các dịch vụ sau khi khởi chạy:
  Frontend: http://localhost:5173 (Hot-reload enabled)
  Backend API: http://localhost:8080
  Swagger UI: http://localhost:8080/swagger-ui.html
  Postgres: localhost:5432

4. Cách chạy Local
   Chạy Backend:
   cd backend
   ./mvnw clean compile -DskipTests // clean + compile bỏ test | Dành cho dev khi chỉ cần compile nhanh để chạy | Nếu ko có thể dùng mvnw clean install ==> clean+ compile + test+ đóng gói JAR ==> để đi deploy luôn
   ./mvnw clean spring-boot:run "-Dspring-boot.run.profiles=dev"
   Chạy Frontend:
   cd frontend
   npm install
   npm run dev

🛡️ Quy trình CI/CD
Dự án đã được thiết lập GitHub Actions tự động: (Mỗi khi push code sẽ có 1 quy trình CI tự động test các phần ta push lên để kiểm tra trước luôn)

- Lint & Build Check: Mỗi khi có Pull Request hoặc Push vào nhánh main/develop.
- Docker Build Check: Đảm bảo mã nguồn luôn có thể đóng gói thành Image hoàn chỉnh.
- Test Report: Tự động chạy Unit Test và báo cáo nếu có lỗi xảy ra.
