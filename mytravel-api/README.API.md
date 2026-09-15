### BẢNG DANH SÁCH API - HỆ THỐNG MYTRAVEL

| Nhóm | Chức năng | Method | Endpoint (URL) | Quyền truy cập | Body yêu cầu (JSON) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Auth** | Đăng ký tài khoản | `POST` | `/api/auth/register` | **Công khai** | `username, password, email, fullName` |
| **Auth** | Đăng nhập lấy Token | `POST` | `/api/auth/login` | **Công khai** | `username, password` |
| **User** | Lấy thông tin cá nhân | `GET` | `/api/users/me` | Token (Private) | Không có |
| **Trip** | Tạo chuyến đi mới | `POST` | `/api/trips` | Token (Private) | `title, destination, startDate, endDate, totalBudget` |
| **Trip** | Xem danh sách chuyến đi | `GET` | `/api/trips` | Token (Private) | Không có |
| **Trip** | Xem chi tiết 1 chuyến đi | `GET` | `/api/trips/{id}` | Token (Private) | Không có |
| **Trip** | Xóa chuyến đi | `DELETE`| `/api/trips/{id}` | Token (Private) | Không có |
| **Itinerary**| Thêm lịch trình (ngày/giờ)| `POST` | `/api/itineraries` | Token (Private) | `tripId, dayNumber, activityName, activityTime, locationName` |
| **Itinerary**| Xem lịch trình chuyến đi | `GET` | `/api/trips/{tripId}/itineraries` | Token (Private) | Không có |
| **Expense** | Ghi chép chi tiêu | `POST` | `/api/expenses` | Token (Private) | `tripId, amount, category, description, expenseDate` |
| **Expense** | Xem danh sách chi tiêu | `GET` | `/api/trips/{tripId}/expenses` | Token (Private) | Không có |
| **Expense** | Xem tổng tiền đã tiêu | `GET` | `/api/trips/{tripId}/expenses/total` | Token (Private) | Không có |
| **Media** | Upload ảnh lên Drive | `POST` | `/api/media/upload` | Token (Private) | `file, tripId` (Multipart) |

---
