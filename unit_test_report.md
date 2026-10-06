# BÁO CÁO KẾT QUẢ UNIT TEST & JA CO CO COVERAGE

**Dự án:** `mytravel-api` (Spring Boot 3.4.1, Java 21)  
**Thời gian thực thi:** ~8 giây  
**Trạng thái Build:** ✅ **BUILD SUCCESSFUL** (`jacocoTestCoverageVerification` PASS)

---

## 1. Bảng Tổng Hợp Chỉ Số Kiểm Thử & Độ Phủ

| Chỉ số (Metric)          |                        Kết quả thực tế                        | Mục tiêu |          Trạng thái           |
| :----------------------- | :-----------------------------------------------------------: | :------: | :---------------------------: |
| **Tổng số bài test**     |                         **152 tests**                         |    -     | ✅ **Pass 100% (0 thất bại)** |
| **Số bài test bỏ qua**   | **1 test** (`MytravelApiApplicationTests` - integration test) |    -     | ℹ️ _Đã đánh dấu `@Disabled`_  |
| **Line Coverage**        |                **93.89%** (1,214 / 1,293 dòng)                | > 90.0%  |     🏆 **VƯỢT CHỈ TIÊU**      |
| **Instruction Coverage** |            **90.17%** (4,962 / 5,503 instructions)            | > 90.0%  |     🏆 **VƯỢT CHỈ TIÊU**      |
| **Method Coverage**      |                **93.16%** (177 / 190 methods)                 | > 90.0%  |     🏆 **VƯỢT CHỈ TIÊU**      |
| **Class Coverage**       |                 **100.00%** (28 / 28 classes)                 |  100.0%  |        🏆 **HOÀN HẢO**        |
| **Task Gradle Rule**     |               `jacocoTestCoverageVerification`                | min 0.90 |         ✅ **PASSED**         |

---

## 2. Kết Quả Kiểm Thử Chi Tiết Theo Từng Test Suite (152 Tests)

|       #       | Test Suite                                                                                                                                                           | Số Test |           Pass / Fail           | Thời gian |
| :-----------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :-----: | :-----------------------------: | :-------: |
|       1       | [`AuthServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/AuthServiceTest.java)                          |   18    |             18 / 0              |  0.154s   |
|       2       | [`TripServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/TripServiceTest.java)                          |   18    |             18 / 0              |  0.032s   |
|       3       | [`LocationServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/LocationServiceTest.java)                  |   11    |             11 / 0              |  0.144s   |
|       4       | [`GlobalExceptionHandlerTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/exception/GlobalExceptionHandlerTest.java)  |   11    |             11 / 0              |  0.225s   |
|       5       | [`TripControllerTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/controller/TripControllerTest.java)                 |    9    |              9 / 0              |  0.107s   |
|       6       | [`AuthControllerTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/controller/AuthControllerTest.java)                 |    7    |              7 / 0              |  0.694s   |
|       7       | [`UserServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/UserServiceTest.java)                          |    7    |              7 / 0              |  0.005s   |
|       8       | [`ExpenseServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/ExpenseServiceTest.java)                    |    6    |              6 / 0              |  0.059s   |
|       9       | [`MediaServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/MediaServiceTest.java)                        |    6    |              6 / 0              |  0.047s   |
|      10       | [`VietmapServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/VietmapServiceTest.java)                    |    6    |              6 / 0              |  0.056s   |
|      11       | [`JwtUtilsTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/security/JwtUtilsTest.java)                               |    5    |              5 / 0              |  0.066s   |
|      12       | [`JwtAuthenticationFilterTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/security/JwtAuthenticationFilterTest.java) |    5    |              5 / 0              |  0.060s   |
|      13       | [`ItineraryServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/ItineraryServiceTest.java)                |    5    |              5 / 0              |  0.039s   |
|      14       | [`ExpenseControllerTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/controller/ExpenseControllerTest.java)           |    5    |              5 / 0              |  0.086s   |
|      15       | [`LocationControllerTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/controller/LocationControllerTest.java)         |    5    |              5 / 0              |  0.064s   |
|      16       | [`ItineraryControllerTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/controller/ItineraryControllerTest.java)       |    4    |              4 / 0              |  0.050s   |
|      17       | [`MediaControllerTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/controller/MediaControllerTest.java)               |    4    |              4 / 0              |  0.045s   |
|      18       | [`UserControllerTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/controller/UserControllerTest.java)                 |    3    |              3 / 0              |  0.032s   |
|      19       | [`SecurityConfigTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/config/SecurityConfigTest.java)                     |    3    |              3 / 0              |  1.196s   |
|      20       | [`ExpenseSplitDtoTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/dto/ExpenseSplitDtoTest.java)                      |    3    |              3 / 0              |  0.004s   |
|      21       | [`TripStatusSchedulerTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/scheduler/TripStatusSchedulerTest.java)        |    2    |              2 / 0              |  0.002s   |
|      22       | [`EmailServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/EmailServiceTest.java)                        |    2    |              2 / 0              |  0.047s   |
|      23       | [`GoogleMapsServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/GoogleMapsServiceTest.java)              |    2    |              2 / 0              |  1.005s   |
|      24       | [`TelegramServiceTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/service/TelegramServiceTest.java)                  |    2    |              2 / 0              |  1.506s   |
|      25       | [`WebConfigTest`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/config/WebConfigTest.java)                               |    2    |              2 / 0              |  0.074s   |
|      26       | [`MytravelApiApplicationTests`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/test/java/com/mytravel/api/MytravelApiApplicationTests.java)          |    1    |        0 / 0 (1 skipped)        |  0.001s   |
| **Tổng cộng** | **26 Suites**                                                                                                                                                        | **152** | **151 Pass, 1 Skipped, 0 Fail** | **5.80s** |

---

## 3. Bảng Chi Tiết Độ Phủ JaCoCo Theo Từng Class

### Tầng Service (`com.mytravel.api.service`)

| Class                                                                                                                                           |    Line Coverage    | Branch Coverage | Instruction Coverage  |
| :---------------------------------------------------------------------------------------------------------------------------------------------- | :-----------------: | :-------------: | :-------------------: |
| [`AuthService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/AuthService.java)             | **100.0%** (98/98)  |  88.9% (16/18)  |  **98.3%** (410/417)  |
| [`VietmapService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/VietmapService.java)       | **100.0%** (87/87)  |  71.7% (33/46)  |  **97.1%** (371/382)  |
| [`ItineraryService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/ItineraryService.java)   | **100.0%** (63/63)  |  50.0% (5/10)   |  **96.6%** (252/261)  |
| [`EmailService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/EmailService.java)           | **100.0%** (14/14)  |        -        |  **100.0%** (61/61)   |
| [`ExpenseService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/ExpenseService.java)       | **99.1%** (109/110) |  55.6% (10/18)  |  **94.4%** (423/448)  |
| [`TripService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/TripService.java)             | **98.8%** (166/168) |  71.4% (60/84)  |  **96.9%** (778/803)  |
| [`MediaService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/MediaService.java)           |  **96.0%** (72/75)  |  75.0% (9/12)   |  **93.7%** (296/316)  |
| [`LocationService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/LocationService.java)     | **94.3%** (281/298) | 58.9% (86/146)  | **90.4%** (1151/1273) |
| [`UserService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/UserService.java)             |  **93.2%** (41/44)  |  72.2% (13/18)  |  **93.9%** (186/198)  |
| [`TelegramService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/TelegramService.java)     |  **89.3%** (25/28)  |  62.5% (10/16)  |  **93.7%** (119/127)  |
| [`GoogleMapsService`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/service/GoogleMapsService.java) |  **39.0%** (32/82)  |  24.2% (15/62)  |  **27.9%** (117/419)  |

---

### Tầng Controller (`com.mytravel.api.controller`)

| Class                                                                                                                                                  |   Line Coverage    | Branch Coverage  | Instruction Coverage |
| :----------------------------------------------------------------------------------------------------------------------------------------------------- | :----------------: | :--------------: | :------------------: |
| [`AuthController`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/controller/AuthController.java)           | **100.0%** (14/14) |        -         |  **100.0%** (62/62)  |
| [`TripController`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/controller/TripController.java)           | **100.0%** (20/20) | **100.0%** (2/2) |  **100.0%** (87/87)  |
| [`ExpenseController`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/controller/ExpenseController.java)     | **100.0%** (10/10) |        -         |  **100.0%** (45/45)  |
| [`ItineraryController`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/controller/ItineraryController.java) |  **100.0%** (8/8)  |        -         |  **100.0%** (37/37)  |
| [`LocationController`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/controller/LocationController.java)   |  **100.0%** (7/7)  |        -         |  **100.0%** (39/39)  |
| [`MediaController`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/controller/MediaController.java)         |  **100.0%** (8/8)  |        -         |  **100.0%** (40/40)  |
| [`UserController`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/controller/UserController.java)           |  **100.0%** (6/6)  |        -         |  **100.0%** (23/23)  |

---

### Tầng Security, Scheduler & Exception

| Class                                                                                                                                                        |   Line Coverage    | Branch Coverage  | Instruction Coverage |
| :----------------------------------------------------------------------------------------------------------------------------------------------------------- | :----------------: | :--------------: | :------------------: |
| [`JwtUtils`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/security/JwtUtils.java)                               | **100.0%** (27/27) |        -         |  **100.0%** (70/70)  |
| [`JwtAuthenticationFilter`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/security/JwtAuthenticationFilter.java) | **100.0%** (16/16) | **100.0%** (8/8) |  **100.0%** (62/62)  |
| [`TripStatusScheduler`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/scheduler/TripStatusScheduler.java)        |  **100.0%** (7/7)  |        -         |  **100.0%** (18/18)  |
| [`GlobalExceptionHandler`](file:///Users/dev/Projects/personal/my-travel/mytravel-api/src/main/java/com/mytravel/api/exception/GlobalExceptionHandler.java)  | **100.0%** (90/90) |   66.7% (4/6)    | **100.0%** (283/283) |
| _Tất cả 6 Custom Exceptions_                                                                                                                                 | **100.0%** (13/13) |        -         |  **100.0%** (32/32)  |

---

## 4. Lệnh Kiểm Tra Trực Tiếp

```bash
cd mytravel-api
./gradlew test jacocoTestReport jacocoTestCoverageVerification
open build/reports/jacoco/test/html/index.html
```
