# 🧳 MyTravel - Ứng Dụng Quản Lý & Lập Kế Hoạch Du Lịch

**MyTravel** là ứng dụng Android dành cho di động hỗ trợ người dùng lên kế hoạch chuyến đi, quản lý lịch trình chi tiết, theo dõi tài chính/chi tiêu thực tế và lưu trữ hình ảnh, nhật ký hành trình.

---

## 📐 Kiến Trúc Hệ Thống (System Architecture)

Hệ thống được chia thành 4 module chính:
1. **Module Người dùng (User Module)**: Đăng ký, Đăng nhập (JWT Security), Quản lý hồ sơ cá nhân.
2. **Module Lập kế hoạch (Planning Module)**: Tạo chuyến đi, Lên lịch trình chi tiết theo mốc thời gian, Tích hợp bản đồ GPS (Google Maps API).
3. **Module Quản lý tài chính (Budget Module)**: Theo dõi chi phí thực tế, Phân loại chi tiêu (Ăn uống, Di chuyển, Vé, Khách sạn...), Biểu đồ so sánh Ngân sách dự kiến vs Thực tế.
4. **Module Lưu trữ (Media & Notes Module)**: Nhật ký hành trình, Ghi chú chuyến đi, Album lưu trữ ảnh/video.

---

## 📚 Tài Liệu API & Data Models Detail

Chi tiết về thiết kế API RESTful, cấu trúc các mô hình dữ liệu (Data Models) và các Endpoints đã được tổng hợp tại:
👉 **[MYTRAVEL_SYSTEM_API.md](MYTRAVEL_SYSTEM_API.md)**

---

## 🛠 Hướng Dẫn Chạy & Phát Triển App (Android Studio)

### Yêu Cầu
- Android Studio Ladybug trở lên / Gradle 8.x
- JDK 11
- Android SDK 24 (Android 7.0) trở lên

### Cách Build & Run
1. Mở dự án trong **Android Studio**.
2. Đồng bộ Gradle project (`Sync Project with Gradle Files`).
3. Chọn thiết bị giả lập (Emulator) hoặc máy thật và bấm **Run (`Shift + F10`)**.

### Chế độ API (Mock vs Real Server)
Chuyển đổi trong file `com.travel.mytravel.api.ApiClient`:
```java
// Đặt IS_MOCK = true để chạy offline với dữ liệu giả lập sẵn
// Đặt IS_MOCK = false để kết nối Backend Java Spring Boot / Node.js thật
private static final boolean IS_MOCK = true;
```
