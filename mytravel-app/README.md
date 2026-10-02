# 🧳 MyTravel - Ứng Dụng Quản Lý & Lập Kế Hoạch Du Lịch (Android App v2.0)

**MyTravel App** là ứng dụng di động Android hiện đại dành cho việc lên kế hoạch du lịch thông minh, quản lý lịch trình theo mốc thời gian, theo dõi tài chính & chia tiền nhóm, lưu trữ hình ảnh kỷ niệm và kết nối trực tiếp với hệ thống Backend RESTful API Spring Boot v2.0.

---

## 🌟 1. Các Tính Năng Nổi Bật (Key Features)

### 🔐 1.1 Phân Hệ Xác Thực & Tài Khoản (Authentication & Security)
* **Đăng Nhập / Đăng Ký**: Đăng nhập JWT Security, lưu trữ `accessToken` & `refreshToken` an toàn trong `TokenManager`.
* **Quên Mật Khẩu (Khôi phục OTP)**: Gửi mã OTP 6 chữ số bất đồng bộ qua **Gmail SMTP** và **Group Telegram Notification**.
* **Đổi Mật Khẩu & Đăng Xuất**: Đổi mật khẩu tài khoản và đăng xuất thu hồi token an toàn.

### 👤 1.2 Hồ Sơ Cá Nhân & Avatar Động (User Profile & Dynamic Avatar)
* **Chỉnh Sửa Hồ Sơ**: Cập nhật Họ tên, Số điện thoại và Email.
* **Tải Ảnh Đại Diện (Upload Avatar)**: Chọn tệp từ bộ nhớ và gửi Multipart `POST /users/me/avatar` lên máy chủ.
* **Avatar Bo Tròn Tuyệt Đối**: Sử dụng `CardView` xén ảnh thành hình tròn hoàn hảo (`Perfect Circle`).
* **Hiển Thị Chữ Viết Tắt Tên Tự Động (`getInitialsFromName`)**:
  * Khi người dùng chưa có ảnh đại diện hoặc ảnh gặp sự cố mạng, ứng dụng tự động trích xuất 2 chữ cái in hoa đầu & cuối của tên (Ví dụ: *"Thanh Minh"* $\rightarrow$ **`TM`**, *"Bùi Ngọc Đại"* $\rightarrow$ **`BĐ`**) hiển thị nổi bật trên khung tròn màu xanh.

### 🗺️ 1.3 Quản Lý Chuyến Đi & Lập Kế Hoạch (Trips & Planner)
* **Tạo Chuyến Đi Mới**: Chọn khoảng thời gian với `MaterialDatePicker` (Khởi hành $\rightarrow$ Kết thúc), cài đặt ngân sách và số lượng người đi.
* **Giới Hạn Số Ngày Theo Thời Gian (`maxDurationDays`)**: Tự động đếm khoảng ngày thực tế của chuyến đi và sinh các chip ngày (`[Ngày 1]`, `[Ngày 2]`, `[Ngày 3]`). Tự động **ẩn nút `[+ Ngày]`** khi đã đạt hạn mức thời hạn chuyến đi trừ khi người dùng sửa ngày kết thúc (`Update Trip`).

### 📍 1.4 Tra Cứu Vị Trí & Nhập Tour Mẫu (Locations & Suggested Tour Import)
* **Tìm Kiếm Ưu Tiên DB (Database First)**: Tra cứu nhanh các địa điểm du lịch & tour mẫu nổi tiếng Việt Nam từ CSDL trước khi query dịch vụ bản đồ.
* **Thanh Cuộn Chip Điểm Đến**: Chọn nhanh các thành phố du lịch (*Đà Nẵng, Hà Nội, TP.HCM, Đà Lạt, Vịnh Hạ Long, Hội An, Phú Quốc, Sa Pa, Nha Trang*).
* **Nhập Tour Mẫu Tự Động (`Import Tour`)**: Cho phép chọn 1 Tour gợi ý bất kỳ và nhập ngày khởi hành để tự động chuyển thành chuyến đi cá nhân kèm đầy đủ mốc lịch trình chi tiết.

### ⏱️ 1.5 Lịch Trình Chi Tiết Theo Ngày (Timeline Itineraries)
* **Quản Lý Mốc Lịch Trình (CRUD)**: Thêm, sửa, xóa các hoạt động trong ngày với giờ (`TimePickerDialog`), vị trí và ghi chú.
* **Chuẩn Hóa Định Dạng Giờ (`HH:mm:ss`)**: Tự động định dạng thời gian chuẩn ISO gửi lên Spring Boot backend để tránh lỗi `DateTimeParseException 500`.
* **Đính Kèm Ảnh Timeline**: Tải tệp ảnh trực tiếp từ thiết bị lên album chuyến đi trên server và lưu vĩnh viễn URL ảnh vào mốc lịch trình.
* **Cố Định Nút Thao Tác Đáy (`Fixed Bottom Bar`)**: Nút **`🗑 Xóa chuyến đi này`** được ghim cố định ở đáy màn hình với hiệu ứng đổ bóng `elevation="8dp"` giúp thao tác thuận tiện.

### 💰 1.6 Quản Lý Tài Chính & Chia Tiền Nhóm (Budget & Expense Splitting)
* **Theo Dõi Chi Tiêu Thực Tế**: Phân loại khoản chi (*FOOD, TRANSPORT, ACCOMMODATION, TICKET, SHOPPING, OTHER*) và biểu đồ tiến độ ngân sách.
* **Chia Tiền Nhóm (Expense Splitting)**:
  * Tùy chọn `"👥 Chia tiền cho các thành viên nhóm"`.
  * Nhập số người chia tiền và hiển thị dòng tính toán tự động: `"💡 Mỗi người trả: XXX.XXXđ (X người)"`.
  * Trạng thái quyết toán `"Đã quyết toán / Hoàn tất trả tiền"`.
  * Thẻ khoản chi tự động hiển thị chi tiết: `FOOD • 30-09-2026 • 👥 Chia 2 người: 450.000đ/người`.

### 📸 1.7 Lưu Trữ Album Ảnh Kỷ Niệm (Media Albums)
* **Tải Ảnh Kỷ Niệm**: Lấy danh sách ảnh lưu trữ thật từ server (`GET /trips/{id}/albums`). Tự động **ẩn danh sách ảnh** khi chuyến đi chưa có ảnh nào tải lên (không chèn ảnh mẫu ảo).
* **Upload Ảnh Lên Máy Chủ**: Đẩy nhiều tệp ảnh thực lên máy chủ Spring Boot thông qua API Multipart `POST /albums/{albumId}/photos`.

### ⚡ 1.8 Bộ Nhớ Đệm Toàn Cục (Global Memory Data Cache)
* **Sử Dụng Repository Pattern (`GlobalDataCache`)**: Lưu trữ `UserProfile` và `List<Trip>` trực tiếp trong bộ nhớ RAM ứng dụng.
* **Tải Nhanh Tức Thì (0ms Latency)**: Khi chuyển đổi qua lại giữa các tab (*Trang chủ $\leftrightarrow$ Kế hoạch $\leftrightarrow$ Tài chính $\leftrightarrow$ Cá nhân*), ứng dụng phục vụ dữ liệu từ RAM ngay lập tức, triệt tiêu độ trễ mạng và loại bỏ hoàn toàn các lượt gọi API thừa.
* **Tự Động Làm Mới (Auto Invalidation)**: Tự động hủy cache và đồng bộ lại từ máy chủ ngay khi có thao tác Thêm / Sửa / Xóa dữ liệu hoặc Đăng xuất.

---

## 🛠 2. Công Nghệ & Thư Viện Sử Dụng (Tech Stack)

| Thành Phần | Công Nghệ / Thư Viện |
| :--- | :--- |
| **Ngôn Ngữ** | Java (Android Native) |
| **Kiến Trúc** | MVC / Repository Pattern / Singleton Global Cache |
| **UI Component** | Material Components 3, CardView, RecyclerView, ChipGroup, BottomSheetDialog, TimePickerDialog |
| **Networking** | Retrofit 2, Gson, OkHttp 4 (HttpLoggingInterceptor), OkHttp Multipart |
| **Xử Lý Hình Ảnh** | Glide 4 (Circular Crop, RequestListener Error Handling) |
| **Security & Auth** | Bearer Token JWT, SharedPreferences (`TokenManager`) |
| **Icons & Style** | Custom Vector Drawables Material Icons (📍 Location, 🗺️ Map, 🔍 Search, 👥 Group, 👛 Wallet) |

---

## 📂 3. Cấu Trúc Thư Mục Dự Án (Project Structure)

```text
app/src/main/java/com/travel/mytravel/
├── adapter/                   # Cầu nối hiển thị dữ liệu RecyclerView
│   ├── ExpenseAdapter.java        # Danh sách chi tiêu & Chia tiền nhóm
│   ├── ItineraryAdapter.java      # Mốc lịch trình Timeline
│   ├── LocationAdapter.java       # Danh sách vị trí & Tour gợi ý
│   ├── MediaAdapter.java          # Bộ sưu tập ảnh kỷ niệm
│   ├── TripAdapter.java           # Danh sách chuyến đi
│   └── TripMemberAdapter.java     # Danh sách thành viên nhóm
├── api/                       # Tầng giao tiếp Mạng & REST API
│   ├── ApiClient.java             # Singleton Retrofit Client, Bắt lỗi tập trung & Format Avatar
│   ├── ApiService.java            # Khai báo tất cả các Endpoint v2.0
│   ├── MockApiService.java        # Dữ liệu giả lập offline
│   └── TokenManager.java          # Quản lý JWT Token trong SharedPreferences
├── controller/                # Màn hình Activity & Fragment
│   ├── ChangePasswordActivity.java   # Đổi mật khẩu
│   ├── EditProfileActivity.java      # Chỉnh sửa hồ sơ & Upload Avatar
│   ├── ForgotPasswordActivity.java  # Yêu cầu gửi OTP khôi phục MK
│   ├── LoginActivity.java           # Đăng nhập tài khoản
│   ├── MainActivity.java            # Điều hướng BottomNavigation 5 Tab
│   ├── MediaAlbumActivity.java      # Quản lý Album ảnh chuyến đi
│   ├── RegisterActivity.java         # Đăng ký tài khoản
│   ├── ResetPasswordActivity.java   # Đặt lại MK với OTP
│   ├── SearchLocationActivity.java  # Tra cứu vị trí & Import Tour gợi ý
│   ├── TripDetailActivity.java      # Chi tiết chuyến đi, Timeline & Fixed Delete Bar
│   └── fragment/
│       ├── BudgetFragment.java      # Tab Tài chính & Chia tiền
│       ├── CreateTripFragment.java  # Tab Danh sách Kế hoạch chuyến đi
│       ├── HomeFragment.java        # Tab Trang chủ & Chuyến đi hiện tại
│       ├── PlanFragment.java        # Tab Tạo chuyến đi mới & Gợi ý lịch trình
│       └── ProfileFragment.java     # Tab Hồ sơ cá nhân & Thống kê
├── model/                     # Data Models & DTOs
│   ├── AlbumRequest.java          # Request tạo Album mới
│   ├── Expense.java / ExpenseSplit.java # Chi tiêu & Chia tiền
│   ├── ImportTourRequest.java     # Request Import Tour gợi ý
│   ├── ItineraryItem.java         # Mốc lịch trình Timeline
│   ├── SuggestedTour.java         # Tour mẫu gợi ý
│   ├── Trip.java / TripRequest.java # Chuyến đi & Request khởi tạo v2.0
│   └── UserProfile.java           # Thông tin tài khoản
└── repository/
    └── GlobalDataCache.java       # Bộ nhớ đệm RAM toàn cục (Global Cache)
```

---

## 🚀 4. Hướng Dẫn Chạy & Cấu Hình Ứng Dụng

### 4.1 Yêu Cầu Môi Trường
* **Android Studio**: Version Ladybug (2024.2.1) trở lên.
* **Java Development Kit (JDK)**: JDK 11 hoặc JDK 17.
* **Android SDK**: Target SDK 34 / Minimum SDK 24 (Android 7.0+).

### 4.2 Cấu Hình Kết Nối Server Backend (Spring Boot)
1. Khởi động server Spring Boot **`mytravel-api`** chạy tại địa chỉ local: `http://localhost:8080/api/v1`.
2. Mở file [ApiClient.java](file:///Users/thanhminh/Documents/GTVT/Java/my-travel/mytravel-app/app/src/main/java/com/travel/mytravel/api/ApiClient.java):
   ```java
   // Đặt BASE_URL chỉ tới IP Android Emulator (10.0.2.2 tương đương localhost của máy host)
   public static final String BASE_URL = "http://10.0.2.2:8080/api/v1/";
   private static final boolean IS_MOCK = false; // Đặt false để kết nối Backend thật
   ```

### 4.3 Biên Dịch & Chạy App
1. Mở dự án `mytravel-app` trong **Android Studio**.
2. Chọn `Sync Project with Gradle Files` (`Shift + Ctrl + O`).
3. Chọn thiết bị giả lập Android Emulator hoặc kết nối điện thoại Android qua USB Debugging.
4. Bấm **Run (`Shift + F10`)** để biên dịch và trải nghiệm ứng dụng.
