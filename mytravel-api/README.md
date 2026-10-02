# MyTravel Project - Backend API System

> **Hệ thống Backend RESTful API Quản lý Chuyến đi, Lịch trình, Chi tiêu và Thông báo OTP cho MyTravel.**

---

## 🛠️ 1. Công nghệ & Yêu cầu Hệ thống

### 🛠️ Công nghệ sử dụng
- **Java 21 (JDK)** & **Spring Boot 3.4.1**
- **Spring Security** & **JWT (JSON Web Token)** với Refresh Token Rotation
- **Spring Data JPA** & **Hibernate**
- **PostgreSQL 15+**
- **Spring Mail (Gmail SMTP)**
- **Telegram Bot API** (Thông báo mã OTP khôi phục mật khẩu)
- **Vietmap v4 & Google Maps API** (Tích hợp tra cứu địa điểm)

### 📋 Yêu cầu môi trường
- JDK 21 (Java Development Kit)
- Docker & Docker Desktop (Khuyên dùng)
- Gradle 8.x+ (hoặc dùng Wrapper `./gradlew`)

---

## 🚀 2. Hướng dẫn Khởi chạy Dự án

### Bước 1: Khởi tạo Cơ sở Dữ liệu (Database)
Khởi chạy PostgreSQL qua Docker Compose từ thư mục dự án:
```bash
docker-compose up -d
```
> **Thông tin kết nối PostgreSQL (cho DBeaver / DataGrip):**
> - **Host:** `localhost` | **Port:** `5432`
> - **Database:** `mytravel_db`
> - **Username:** `my_travel` | **Password:** `travel`

### Bước 2: Khởi chạy Ứng dụng
Khởi chạy bằng Gradle Wrapper:
```bash
./gradlew bootRun
```
Hoặc mở file `MytravelApiApplication.java` trong IntelliJ IDEA và bấm **Run**.

Ứng dụng sẽ chạy tại địa chỉ: `http://localhost:8080/api/v1`

---

## ⚙️ 3. Cấu hình Hệ thống (`application.properties`)

Ứng dụng hỗ trợ cấu hình qua file [`application.properties`](file:///Users/thanhminh/Documents/GTVT/Java/my-travel/mytravel-api/src/main/resources/application.properties) hoặc ghi đè thông qua các Biến môi trường (Environment Variables):

| Nhóm Cấu hình | Tên Thuộc tính | Biến Môi Trường (`Environment Variable`) | Giá trị Mặc định / Mẫu |
| :--- | :--- | :--- | :--- |
| **Cơ sở dữ liệu** | `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/mytravel_db` |
| **Cơ sở dữ liệu** | `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `my_travel` |
| **Cơ sở dữ liệu** | `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `travel` |
| **Gmail SMTP** | `spring.mail.username` | `SPRING_MAIL_USERNAME` | `ngocdaibui99@gmail.com` |
| **Gmail SMTP** | `spring.mail.password` | `SPRING_MAIL_PASSWORD` | `zpzv wfgq jeng qeiu` *(Mật khẩu ứng dụng 16 ký tự)* |
| **Telegram Bot** | `telegram.bot.token` | `TELEGRAM_BOT_TOKEN` | `8748199413:AAFPREpoQr68Jh7IGyYlrvulhTEQ919GHIU` |
| **Telegram Bot** | `telegram.bot.chat-id` | `TELEGRAM_BOT_CHAT_ID` | `766635464` |
| **Vietmap API** | `vietmap.api-key` | `VIETMAP_API_KEY` | `857d15546a5c48444ba309b31c9faf897d5a5d0ec7a4e141` |
| **Google Maps** | `google.maps.api-key` | `GOOGLE_MAPS_API_KEY` | `""` |

---

## 📧 4. Cấu hình Gửi Mail & Telegram Notification

### 4.1 Cấu hình Gmail SMTP (Gửi OTP qua Email)
Gmail yêu cầu **Mật khẩu ứng dụng (App Password)** 16 ký tự thay vì mật khẩu thông thường:
1. Bật **Xác minh 2 bước** trên tài khoản Google.
2. Truy cập [Google App Passwords](https://myaccount.google.com/apppasswords), tạo mật khẩu cho ứng dụng `MyTravel`.
3. Đặt chuỗi 16 ký tự thu được vào thuộc tính `spring.mail.password` hoặc biến `SPRING_MAIL_PASSWORD`.

### 4.2 Cấu hình Telegram Bot (Gửi OTP qua Telegram Group)
Khi người dùng thực hiện **Quên mật khẩu**, mã OTP sẽ tự động được gửi đồng thời tới Email và Group Telegram:
1. Tạo Bot qua `@BotFather` trên Telegram để lấy **Bot Token**.
2. Thêm Bot vào Group Telegram của bạn và lấy **Chat ID / Group ID**.
3. Đặt thông tin vào `telegram.bot.token` và `telegram.bot.chat-id` (hoặc qua biến môi trường `TELEGRAM_BOT_TOKEN` và `TELEGRAM_BOT_CHAT_ID`).

---

## 📚 5. Tài liệu API (API Documentation)

Chi tiết đầy đủ toàn bộ các RESTful Endpoints (Authentication, Trips, Itineraries, Expenses, Locations, Media) xem tại:
👉 [**Tài liệu Chi tiết API System Documentation (v2.0)**](file:///Users/thanhminh/Documents/GTVT/Java/my-travel/mytravel-api/README.API_v2.md)
