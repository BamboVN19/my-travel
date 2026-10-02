# 🗺️ MyTravel RESTful API System Documentation
> **Version:** 1.0.0  
> **Base URL:** `https://api.mytravel.com/api/v1` (Hoặc Local Server: `http://localhost:8080/api/v1`)  
> **Format:** JSON (`Content-Type: application/json`)  
> **Authentication:** Bearer Token JWT (`Authorization: Bearer <token>`)  

---

## 📋 Danh Sách Phân Hệ API (API Modules Overview)

| Phân Hệ API | Base Path | Mô Tả Chức Năng |
| :--- | :--- | :--- |
| **Authentication** | `/auth` | Đăng nhập, Đăng ký, Quên mật khẩu, Đặt lại mật khẩu OTP, Đổi mật khẩu |
| **User Profile** | `/users` | Lấy thông tin cá nhân người dùng, Cập nhật hồ sơ |
| **Trips (Kế Hoạch)** | `/trips` | Tạo chuyến đi mới, Lấy danh sách chuyến đi, Chi tiết chuyến đi, Xóa chuyến đi |
| **Itineraries (Lịch Trình)** | `/itineraries` | Lấy lịch trình theo ngày, Thêm mốc lịch trình, Cập nhật & Xóa mốc lịch trình |
| **Expenses (Tài Chính)** | `/expenses` | Lấy danh sách chi tiêu theo chuyến đi/Total, Thêm khoản chi, Tổng ngân sách |
| **Media & Storage** | `/media` | Tải lên album ảnh/video kỷ niệm chuyến đi |

---

## 1. 🔐 Phân Hệ Xác Thực & Tài Khoản (`/auth`)

### 1.1 Đăng Nhập Người Dùng
* **HTTP Method:** `POST`
* **Endpoint:** `/auth/login`
* **Request Body:**
```json
{
  "username": "admin",
  "password": "123"
}
```
* **Response `200 OK`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNjIzOTAyMn0...",
  "tokenType": "Bearer",
  "username": "admin"
}
```

### 1.2 Đăng Ký Tài Khoản Mới
* **HTTP Method:** `POST`
* **Endpoint:** `/auth/register`
* **Request Body:**
```json
{
  "username": "ngocdai",
  "email": "ngocdai@gmail.com",
  "password": "Password123@",
  "fullName": "Bùi Ngọc Đại",
  "phone": "0987654321"
}
```
* **Response `201 Created`:**
```json
{
  "message": "Đăng ký tài khoản thành công!",
  "status": 201
}
```

### 1.3 Gửi Yêu Cầu Quên Mật Khẩu (Gửi OTP)
* **HTTP Method:** `POST`
* **Endpoint:** `/auth/forgot-password`
* **Request Body:**
```json
{
  "email": "ngocdai@gmail.com"
}
```

### 1.4 Xác Nhận OTP & Đặt Lại Mật Khẩu
* **HTTP Method:** `POST`
* **Endpoint:** `/auth/reset-password`
* **Request Body:**
```json
{
  "email": "ngocdai@gmail.com",
  "otpCode": "123456",
  "newPassword": "NewPassword123@"
}
```

### 1.5 Thay Đổi Mật Khẩu Khỏi Phiên Đăng Nhập
* **HTTP Method:** `POST`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/auth/change-password`
* **Request Body:**
```json
{
  "oldPassword": "123",
  "newPassword": "NewPassword123@"
}
```

---

## 2. 👤 Phân Hệ Hồ Sơ Cá Nhân (`/users`)

### 2.1 Lấy Thông Tin Người Dùng
* **HTTP Method:** `GET`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/users/profile`
* **Response `200 OK`:**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@travel.com",
  "fullName": "Bùi Ngọc Đ",
  "phone": "0987654321",
  "avatarUrl": "https://i.pravatar.cc/300",
  "createdAt": "01-01-2025 08:00:00"
}
```

---

## 3. 🧳 Phân Hệ Chuyến Đi (`/trips`)

### 3.1 Khởi Tạo Chuyến Đi Mới
* **HTTP Method:** `POST`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/trips`
* **Request Body:**
```json
{
  "userId": 1,
  "title": "Chuyến đi Đà Lạt, Lâm Đồng",
  "destination": "Đà Lạt, Lâm Đồng",
  "startDate": "18-10-2026",
  "endDate": "22-10-2026",
  "totalBudget": 12000000.0,
  "status": "PLANNED"
}
```
* **Response `201 Created`:**
```json
{
  "id": 1728000000000,
  "userId": 1,
  "title": "Chuyến đi Đà Lạt, Lâm Đồng",
  "destination": "Đà Lạt, Lâm Đồng",
  "startDate": "18-10-2026",
  "endDate": "22-10-2026",
  "totalBudget": 12000000.0,
  "status": "PLANNED"
}
```

### 3.2 Lấy Danh Sách Tất Cả Chuyến Đi
* **HTTP Method:** `GET`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/trips`
* **Response `200 OK`:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "title": "Chuyến đi Phố Cổ Hội An, Quảng Nam",
    "destination": "Hội An, Quảng Nam",
    "startDate": "29-09-2026",
    "endDate": "01-10-2026",
    "totalBudget": 12000000.0,
    "status": "PLANNED"
  },
  {
    "id": 2,
    "userId": 1,
    "title": "Chuyến đi Đà Nẵng",
    "destination": "Đà Nẵng",
    "startDate": "01-06-2025",
    "endDate": "05-06-2025",
    "totalBudget": 10000000.0,
    "status": "ONGOING"
  },
  {
    "id": 3,
    "userId": 1,
    "title": "Khám phá Đà Lạt",
    "destination": "Đà Lạt",
    "startDate": "10-07-2025",
    "endDate": "14-07-2025",
    "totalBudget": 8000000.0,
    "status": "COMPLETED"
  }
]
```

### 3.3 Lấy Chi Tiết Một Chuyến Đi theo ID
* **HTTP Method:** `GET`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/trips/{id}`

### 3.4 Xóa Chuyến Đi
* **HTTP Method:** `DELETE`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/trips/{id}`

---

## 4. 📅 Phân Hệ Lịch Trình Chi Tiết Từng Ngày (`/itineraries`)

### 4.1 Lấy Danh Sách Lịch Trình Theo Trip ID
* **HTTP Method:** `GET`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/itineraries?tripId={tripId}`
* **Response `200 OK`:**
```json
[
  {
    "id": 101,
    "tripId": 1,
    "dayNumber": 1,
    "activityTime": "08:00:00",
    "activityName": "Bay đến Đà Nẵng & Di chuyển Hội An",
    "locationName": "Sân bay Đà Nẵng ➔ Phố cổ",
    "latitude": 15.8801,
    "longitude": 108.3380,
    "placeId": "p1",
    "note": "Khởi hành chuyến đi"
  },
  {
    "id": 105,
    "tripId": 1,
    "dayNumber": 2,
    "activityTime": "07:30:00",
    "activityName": "Ăn bánh mì Phượng & Cà phê Mắt Đèn",
    "locationName": "Đường Phan Chu Trinh",
    "latitude": 15.8780,
    "longitude": 108.3290,
    "placeId": "p5",
    "note": "Ăn sáng điểm tâm"
  }
]
```

### 4.2 Thêm Mốc Lịch Trình Mới
* **HTTP Method:** `POST`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/itineraries`
* **Request Body:**
```json
{
  "tripId": 1,
  "dayNumber": 2,
  "activityTime": "14:30:00",
  "activityName": "Trải nghiệm làm gốm Thanh Hà",
  "locationName": "Làng gốm Thanh Hà",
  "latitude": 15.8820,
  "longitude": 108.3050,
  "placeId": "p7",
  "note": "Tự tay nặn sản phẩm gốm"
}
```

---

## 5. 💳 Phân Hệ Quản Lý Tài Chính & Chi Tiêu (`/expenses`)

### 5.1 Lấy Chi Tiết Chi Tiêu Theo Chuyến Đi
* **HTTP Method:** `GET`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/expenses?tripId={tripId}`
* **Response `200 OK`:**
```json
[
  {
    "id": 201,
    "tripId": 1,
    "amount": 2500000.0,
    "category": "TICKET",
    "description": "Vé máy bay khứ hồi",
    "expenseDate": "29-09-2026",
    "paymentMethod": "CASH"
  },
  {
    "id": 202,
    "tripId": 1,
    "amount": 3200000.0,
    "category": "ACCOMMODATION",
    "description": "Resort 3 đêm",
    "expenseDate": "29-09-2026",
    "paymentMethod": "CARD"
  }
]
```

### 5.2 Thêm Khoản Chi Tiêu Mới
* **HTTP Method:** `POST`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/expenses`
* **Request Body:**
```json
{
  "tripId": 1,
  "amount": 350000.0,
  "category": "FOOD",
  "description": "Ăn tối Cơm gà Bà Buổi",
  "expenseDate": "29-09-2026",
  "paymentMethod": "CASH"
}
```

### 5.3 Lấy Tổng Chi Tiêu Của Chuyến Đi
* **HTTP Method:** `GET`
* **Headers:** `Authorization: Bearer <token>`
* **Endpoint:** `/expenses/total?tripId={tripId}`
* **Response `200 OK`:**
```json
{
  "tripId": 1,
  "totalExpense": 7500000.0
}
```

---

## 🖼️ 6. Phân Hệ Lưu Trữ Media & Ảnh (`/media`)

### 6.1 Tải Lên Ảnh/Video Kỷ Niệm
* **HTTP Method:** `POST`
* **Headers:** `Content-Type: multipart/form-data`
* **Endpoint:** `/media/upload`
* **Form Data:**
  - `file`: (Binary image/video)
  - `tripId`: `1`
* **Response `200 OK`:**
```json
{
  "id": 501,
  "tripId": 1,
  "mediaUrl": "https://cdn.mytravel.com/media/2026/09/photo_hoian.jpg",
  "caption": "Ảnh thả đèn lồng sông Hoài",
  "uploadedAt": "29-09-2026 20:15:00"
}
```

---

## 🚨 Mã Lỗi Chuẩn HTTP (HTTP Error Codes)

| Status Code | Tên Lỗi | Nguyên Nhân & Giải Pháp |
| :--- | :--- | :--- |
| `200 OK` | Success | Yêu cầu xử lý thành công. |
| `201 Created` | Created | Tạo mới dữ liệu (Trip, Itinerary, Expense) thành công. |
| `400 Bad Request` | Bad Request | Dữ liệu gửi lên sai định dạng hoặc thiếu trường bắt buộc. |
| `401 Unauthorized` | Unauthorized | Token JWT hết hạn hoặc không hợp lệ. |
| `404 Not Found` | Not Found | Không tìm thấy chuyến đi, tài khoản hoặc mốc lịch trình. |
| `500 Internal Error` | Server Error | Lỗi xử lý phía máy chủ Backend. |
