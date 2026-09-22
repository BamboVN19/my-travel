# 🚀 MYTRAVEL SYSTEM - Tài Liệu Thiết Kế API & Data Models (Khớp Backend DTO)

Tài liệu đồng bộ 100% giữa **Backend (mytravel-api)** và **Mobile App (mytravel-app)**.

---

## 📌 1. Tổng Quan Kiến Trúc & Cấu Hình

- **Base URL (Local Server)**: `http://10.0.2.2:8080/` (Dành cho Android Emulator)
- **Base URL (Production Server)**: `https://api.mytravel.com/`
- **Authentication**: JWT Token truyền qua Header: `Authorization: Bearer <token>`
- **Định dạng Ngày/Giờ**:
  - `LocalDate`: `"dd-MM-yyyy"` (ví dụ: `"01-06-2025"`)
  - `LocalTime`: `"HH:mm:ss"` (ví dụ: `"08:00:00"`)
  - `LocalDateTime`: `"dd-MM-yyyy HH:mm:ss"` (ví dụ: `"01-06-2025 08:00:00"`)

---

## 🔐 2. Module Auth & Người Dùng (User)

### Data Models (DTOs)

#### `LoginRequest` / `LoginResponse`
```json
// POST /api/auth/login
{
  "username": "admin",
  "password": "123"
}

// Response
{
  "accessToken": "mock-token-xyz-123",
  "tokenType": "Bearer",
  "username": "admin"
}
```

#### `RegisterRequest`
```json
// POST /api/auth/register
{
  "username": "user123",
  "password": "password123",
  "email": "user@example.com",
  "fullName": "Nguyễn Văn A"
}
```

#### `UserProfileResponse` (`UserProfile`)
```json
// GET /api/users/me
{
  "id": 1,
  "username": "admin",
  "email": "admin@travel.com",
  "fullName": "Bùi Ngọc Đ",
  "phoneNumber": "0987654321",
  "avatarUrl": "https://i.pravatar.cc/300",
  "createdAt": "01-01-2025 08:00:00"
}
```

### Danh sách API Endpoints

| STT | Chức năng | Method | Endpoint Path | Request Body | Response Success |
| :---: | :--- | :---: | :--- | :--- | :--- |
| 1 | Đăng ký tài khoản | `POST` | `/api/auth/register` | `RegisterRequest` | `200 OK` |
| 2 | Đăng nhập lấy Token | `POST` | `/api/auth/login` | `LoginRequest` | `LoginResponse` (200 OK) |
| 3 | Lấy thông tin cá nhân | `GET` | `/api/users/me` | None (Token Header) | `UserProfile` |

---

## 📅 3. Module Chuyến Đi (Trip)

### Data Models (DTOs)

#### `TripRequest` / `TripResponse` (`Trip`)
```json
// POST /api/trips  hoặc  GET /api/trips
{
  "id": 1,
  "userId": 1,
  "title": "Chuyến đi Đà Nẵng",
  "destination": "Đà Nẵng",
  "startDate": "01-06-2025",
  "endDate": "05-06-2025",
  "totalBudget": 10000000.0,
  "status": "PLANNED",
  "createdAt": "01-01-2025 08:00:00"
}
```

### Danh sách API Endpoints

| STT | Chức năng | Method | Endpoint Path | Request Body | Response Success |
| :---: | :--- | :---: | :--- | :--- | :--- |
| 1 | Tạo chuyến đi mới | `POST` | `/api/trips` | `Trip` (`TripRequest`) | `Trip` (`TripResponse`) |
| 2 | Xem danh sách chuyến đi | `GET` | `/api/trips` | None | `List<Trip>` |
| 3 | Xem chi tiết 1 chuyến đi | `GET` | `/api/trips/{id}` | Path `{id}` | `Trip` |
| 4 | Xóa chuyến đi | `DELETE` | `/api/trips/{id}` | Path `{id}` | `200 OK` |

---

## 📍 4. Module Lịch Trình (Itinerary)

### Data Models (DTOs)

#### `ItineraryRequest` / `ItineraryResponse` (`ItineraryItem`)
```json
// POST /api/itineraries  hoặc  GET /api/trips/{tripId}/itineraries
{
  "id": 1,
  "tripId": 1,
  "dayNumber": 1,
  "activityTime": "08:00:00",
  "activityName": "Bay đến Đà Nẵng",
  "locationName": "Sân bay Đà Nẵng",
  "latitude": 16.0544,
  "longitude": 108.2022,
  "placeId": "place_123",
  "note": "Đáp sân bay Đà Nẵng"
}
```

### Danh sách API Endpoints

| STT | Chức năng | Method | Endpoint Path | Request Body | Response Success |
| :---: | :--- | :---: | :--- | :--- | :--- |
| 1 | Thêm lịch trình (ngày/giờ) | `POST` | `/api/itineraries` | `ItineraryItem` | `ItineraryItem` |
| 2 | Xem lịch trình chuyến đi | `GET` | `/api/trips/{tripId}/itineraries` | Path `{tripId}` | `List<ItineraryItem>` |

---

## 💸 5. Module Chi Tiêu (Expense)

### Data Models (DTOs)

#### `ExpenseRequest` / `ExpenseResponse` (`Expense`)
```json
// POST /api/expenses  hoặc  GET /api/trips/{tripId}/expenses
{
  "id": 1,
  "tripId": 1,
  "amount": 2000000.0,
  "category": "TICKET",
  "description": "Vé máy bay khứ hồi",
  "expenseDate": "01-06-2025",
  "paymentMethod": "CASH"
}
```

#### `ExpenseTotalResponse`
```json
// GET /api/trips/{tripId}/expenses/total
{
  "tripId": 1,
  "totalExpense": 3500000.0
}
```

### Danh sách API Endpoints

| STT | Chức năng | Method | Endpoint Path | Request Body | Response Success |
| :---: | :--- | :---: | :--- | :--- | :--- |
| 1 | Ghi chép chi tiêu mới | `POST` | `/api/expenses` | `Expense` | `Expense` |
| 2 | Xem danh sách chi tiêu | `GET` | `/api/trips/{tripId}/expenses` | Path `{tripId}` | `List<Expense>` |
| 3 | Xem tổng tiền đã tiêu | `GET` | `/api/trips/{tripId}/expenses/total` | Path `{tripId}` | `ExpenseTotalResponse` |

---

## 🖼️ 6. Module Media (Album & Ảnh)

### Data Models (DTOs)

#### `MediaUploadResponse` (`TripMedia`)
```json
// POST /api/media/upload
{
  "id": 1,
  "tripId": 1,
  "albumId": 10,
  "photoUrl": "https://example.com/photo1.jpg",
  "caption": "Ảnh đẹp Đà Nẵng",
  "uploadedAt": "01-06-2025 10:00:00"
}
```

### Danh sách API Endpoints

| STT | Chức năng | Method | Endpoint Path | Form Data | Response Success |
| :---: | :--- | :---: | :--- | :--- | :--- |
| 1 | Upload ảnh lên Drive / Server | `POST` | `/api/media/upload` | Multipart: `file`, `tripId` | `TripMedia` |

---

## 🛠 7. Cấu Trúc Mã Nguồn Mobile App Đã Đồng Bộ

```text
com.travel.mytravel
├── api
│   ├── ApiClient.java            # Khởi tạo Retrofit
│   ├── ApiService.java           # Khai báo các endpoints khớp 100% BE DTO
│   └── MockApiService.java       # Giả lập trả về đúng định dạng DTO BE
└── model
    ├── Expense.java              # Đồng bộ ExpenseRequest/Response (expenseDate, description, paymentMethod)
    ├── ExpenseTotalResponse.java # Tổng chi tiêu (tripId, totalExpense)
    ├── ItineraryItem.java        # Đồng bộ ItineraryRequest/Response (activityTime, activityName, note)
    ├── LoginRequest.java         # (username, password)
    ├── LoginResponse.java        # (accessToken, tokenType, username)
    ├── RegisterRequest.java      # (username, password, email, fullName)
    ├── Trip.java                 # Đồng bộ TripRequest/Response (startDate, endDate, totalBudget)
    ├── TripMedia.java            # Đồng bộ MediaUploadResponse (photoUrl, albumId)
    └── UserProfile.java          # Đồng bộ UserProfileResponse
```
