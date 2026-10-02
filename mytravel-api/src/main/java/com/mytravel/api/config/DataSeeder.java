package com.mytravel.api.config;

import com.mytravel.api.entity.*;
import com.mytravel.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final TripRepository tripRepository;
  private final TripMemberRepository tripMemberRepository;
  private final ItineraryRepository itineraryRepository;
  private final ExpenseRepository expenseRepository;
  private final ExpenseSplitRepository expenseSplitRepository;
  private final MediaAlbumRepository mediaAlbumRepository;
  private final AlbumPhotoRepository albumPhotoRepository;
  private final SuggestedTourRepository suggestedTourRepository;
  private final SuggestedTourStopRepository suggestedTourStopRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) {
    if (suggestedTourRepository.count() >= 5 && tripRepository.count() >= 5) {
      log.info("Dữ liệu hệ thống đã đầy đủ 5 Tour gợi ý & 5 Chuyến đi mẫu. Thôi không seed data.");
      return;
    }

    log.info("Bắt đầu khởi tạo dữ liệu mẫu Tour gợi ý & Chuyến đi cá nhân Việt Nam (Sample Data Seeding)...");

    // ==========================================
    // 1. SEED BẢNG SUGGESTED_TOURS & SUGGESTED_TOUR_STOPS
    // ==========================================
    if (suggestedTourRepository.count() < 5) {
      suggestedTourStopRepository.deleteAll();
      suggestedTourRepository.deleteAll();

      // TOUR 1: HÀ GIANG
      SuggestedTour tourHagiang = SuggestedTour.builder()
          .title("Hành trình Chinh phục Hà Giang Loop & Sông Nho Quế")
          .destination("Hà Giang")
          .description("Khám phá cao nguyên đá Đồng Văn, đèo Mã Pí Lèng, chèo thuyền sông Nho Quế và trải nghiệm văn hóa bản địa độc đáo.")
          .coverImageUrl("https://images.unsplash.com/photo-1528127269322-539801943592")
          .durationDays(4)
          .estimatedBudget(new BigDecimal("9500000.00"))
          .category("MOUNTAIN")
          .rating(new BigDecimal("4.95"))
          .reviewCount(142)
          .isActive(true)
          .build();

      suggestedTourRepository.save(tourHagiang);

      List<SuggestedTourStop> hagiangStops = List.of(
          SuggestedTourStop.builder().suggestedTour(tourHagiang).dayNumber(1).orderIndex(1).activityTime(LocalTime.of(7, 0)).activityName("Check-in Km0 TP. Hà Giang & Khởi hành đi Quản Bạ").locationName("Cột Mốc Km0 Hà Giang").latitude(new BigDecimal("22.82330000")).longitude(new BigDecimal("104.98390000")).note("Chụp ảnh kỷ niệm khởi đầu chuyến phiêu lưu Hà Giang Loop.").build(),
          SuggestedTourStop.builder().suggestedTour(tourHagiang).dayNumber(1).orderIndex(2).activityTime(LocalTime.of(11, 30)).activityName("Ngắm Núi Đôi Quản Bạ & Vượt Dốc Thẩm Mã").locationName("Dốc Thẩm Mã, Đồng Văn").latitude(new BigDecimal("23.16700000")).longitude(new BigDecimal("105.18300000")).note("Cung đường đèo uốn lượn huyền thoại của dân phượt.").build(),
          SuggestedTourStop.builder().suggestedTour(tourHagiang).dayNumber(1).orderIndex(3).activityTime(LocalTime.of(16, 0)).activityName("Thăm Dinh Thự Vua Mèo (Nhà Vương)").locationName("Dinh Thự Vua Mèo, Sà Phìn").latitude(new BigDecimal("23.26440000")).longitude(new BigDecimal("105.25750000")).note("Kiến trúc độc đáo kết hợp giữa nghệ thuật H'Mông, Pháp và Trung Hoa.").build(),
          SuggestedTourStop.builder().suggestedTour(tourHagiang).dayNumber(2).orderIndex(1).activityTime(LocalTime.of(7, 30)).activityName("Chinh phục Cột cờ Lũng Cú - Điểm cực Bắc Tổ Quốc").locationName("Cột Cờ Lũng Cú").latitude(new BigDecimal("23.36270000")).longitude(new BigDecimal("105.31560000")).note("Đứng dưới lá cờ 54m2 rộng lớn thiêng liêng.").build(),
          SuggestedTourStop.builder().suggestedTour(tourHagiang).dayNumber(2).orderIndex(2).activityTime(LocalTime.of(13, 30)).activityName("Chinh phục Đèo Mã Pí Lèng & Đi du thuyền Sông Nho Quế").locationName("Hẻm Tu Sản - Sông Nho Quế").latitude(new BigDecimal("23.18300000")).longitude(new BigDecimal("105.41700000")).note("Đi thuyền trên làn nước xanh ngọc bích chẻ đôi hẻm vực sâu nhất Đông Nam Á.").build()
      );
      suggestedTourStopRepository.saveAll(hagiangStops);

      // TOUR 2: ĐÀ LẠT
      SuggestedTour tourDalat = SuggestedTour.builder()
          .title("Chuyến đi Đà Lạt - Mộng Mơ & Ngàn Hoa")
          .destination("Đà Lạt, Lâm Đồng")
          .description("Trải nghiệm không khí se lạnh, săn mây đồi chè Cầu Đất, dạo chợ đêm và ngắm hoàng hôn Hồ Tuyền Lâm.")
          .coverImageUrl("https://images.unsplash.com/photo-1583417319070-4a69db38a482")
          .durationDays(3)
          .estimatedBudget(new BigDecimal("12000000.00"))
          .category("RELAX")
          .rating(new BigDecimal("4.90"))
          .reviewCount(98)
          .isActive(true)
          .build();

      suggestedTourRepository.save(tourDalat);

      List<SuggestedTourStop> dalatStops = List.of(
          SuggestedTourStop.builder().suggestedTour(tourDalat).dayNumber(1).orderIndex(1).activityTime(LocalTime.of(8, 30)).activityName("Bay đến Sân bay Liên Khương & Nhận phòng khách sạn").locationName("Khách sạn Dalat Palace").latitude(new BigDecimal("11.94040000")).longitude(new BigDecimal("108.43780000")).note("Di chuyển bằng taxi sân bay về trung tâm, làm thủ tục check-in.").build(),
          SuggestedTourStop.builder().suggestedTour(tourDalat).dayNumber(1).orderIndex(2).activityTime(LocalTime.of(12, 0)).activityName("Ăn trưa lẩu gà lá é Tao Ngộ").locationName("Lẩu Gà Lá É Tao Ngộ - 3/4 Đà Lạt").latitude(new BigDecimal("11.93280000")).longitude(new BigDecimal("108.44420000")).note("Món ăn đặc sản cực ngon cho ngày đầu đến Đà Lạt.").build(),
          SuggestedTourStop.builder().suggestedTour(tourDalat).dayNumber(1).orderIndex(3).activityTime(LocalTime.of(14, 30)).activityName("Dạo chơi Hồ Tuyền Lâm & Đi cáp treo Đồi Robin").locationName("Hồ Tuyền Lâm, Đà Lạt").latitude(new BigDecimal("11.89670000")).longitude(new BigDecimal("108.44190000")).note("Thuê thuyền kayak chèo ngắm hoàng hôn rực rỡ.").build(),
          SuggestedTourStop.builder().suggestedTour(tourDalat).dayNumber(2).orderIndex(1).activityTime(LocalTime.of(7, 0)).activityName("Săn mây & Uống cà phê Cheo Veo").locationName("Tiệm Cà Phê Cheo Veo").latitude(new BigDecimal("11.93510000")).longitude(new BigDecimal("108.46100000")).note("Đi sớm để đón hừng đông và biển mây cực đẹp.").build()
      );
      suggestedTourStopRepository.saveAll(dalatStops);

      // TOUR 3: HỘI AN - ĐÀ NẴNG
      SuggestedTour tourDanang = SuggestedTour.builder()
          .title("Khám phá Phố Cổ Hội An & Đà Nẵng")
          .destination("Đà Nẵng & Hội An, Quảng Nam")
          .description("Tận hưởng thiên đường du lịch miền Trung với Cầu Rồng, Bà Nà Hills, Phố cổ Hội An lung linh đèn hoa đăng.")
          .coverImageUrl("https://images.unsplash.com/photo-1559592413-7cec4d0cae2b")
          .durationDays(4)
          .estimatedBudget(new BigDecimal("15000000.00"))
          .category("CULTURE")
          .rating(new BigDecimal("4.88"))
          .reviewCount(175)
          .isActive(true)
          .build();

      suggestedTourRepository.save(tourDanang);

      List<SuggestedTourStop> danangStops = List.of(
          SuggestedTourStop.builder().suggestedTour(tourDanang).dayNumber(1).orderIndex(1).activityTime(LocalTime.of(9, 0)).activityName("Đến Sân bay Đà Nẵng & Thưởng thức Bánh tráng thịt heo Trần").locationName("Đặc sản Trần - Đà Nẵng").latitude(new BigDecimal("16.06800000")).longitude(new BigDecimal("108.21200000")).note("Đặc sản bánh tráng thịt heo hai đầu da trứ danh.").build(),
          SuggestedTourStop.builder().suggestedTour(tourDanang).dayNumber(1).orderIndex(2).activityTime(LocalTime.of(14, 0)).activityName("Check-in Bán đảo Sơn Trà & Chùa Linh Ứng").locationName("Chùa Linh Ứng - Bán Đảo Sơn Trà").latitude(new BigDecimal("16.10040000")).longitude(new BigDecimal("108.27780000")).note("Chiêm bái tượng Phật Bà Quan Âm cao 67m hướng ra biển.").build(),
          SuggestedTourStop.builder().suggestedTour(tourDanang).dayNumber(2).orderIndex(1).activityTime(LocalTime.of(8, 0)).activityName("Vui chơi trọn ngày tại Sun World Bà Nà Hills").locationName("Bà Nà Hills & Cầu Vàng").latitude(new BigDecimal("15.99840000")).longitude(new BigDecimal("107.98800000")).note("Check-in Cầu Vàng nổi tiếng thế giới.").build()
      );
      suggestedTourStopRepository.saveAll(danangStops);

      // TOUR 4: PHÚ QUỐC
      SuggestedTour tourPhuquoc = SuggestedTour.builder()
          .title("Nghỉ dưỡng Đảo Ngọc Phú Quốc & Hòn Thơm")
          .destination("Phú Quốc, Kiên Giang")
          .description("Đắm mình trong làn nước biển trong xanh, trải nghiệm cáp treo Hòn Thơm vượt biển và thành phố không ngủ Grand World.")
          .coverImageUrl("https://images.unsplash.com/photo-1540555700478-4be289fbecef")
          .durationDays(4)
          .estimatedBudget(new BigDecimal("20000000.00"))
          .category("BEACH")
          .rating(new BigDecimal("4.92"))
          .reviewCount(85)
          .isActive(true)
          .build();

      suggestedTourRepository.save(tourPhuquoc);

      List<SuggestedTourStop> phuquocStops = List.of(
          SuggestedTourStop.builder().suggestedTour(tourPhuquoc).dayNumber(1).orderIndex(1).activityTime(LocalTime.of(14, 0)).activityName("Check-in Resort Bãi Trường & Thưởng thức bún quậy Kiến Xây").locationName("Bún Quậy Kiến Xây Phú Quốc").latitude(new BigDecimal("10.21700000")).longitude(new BigDecimal("103.96000000")).note("Tự tay pha nước chấm bún quậy độc đáo.").build(),
          SuggestedTourStop.builder().suggestedTour(tourPhuquoc).dayNumber(2).orderIndex(1).activityTime(LocalTime.of(8, 30)).activityName("Trải nghiệm Cáp treo Hòn Thơm vượt biển dài nhất thế giới").locationName("Sun World Hon Thom Nature Park").latitude(new BigDecimal("10.02700000")).longitude(new BigDecimal("104.01500000")).note("Tắm biển Bãi Trào và chơi công viên nước Aquatopia.").build()
      );
      suggestedTourStopRepository.saveAll(phuquocStops);

      // TOUR 5: HẠ LONG & NINH BÌNH
      SuggestedTour tourHalong = SuggestedTour.builder()
          .title("Kỳ quan thiên nhiên Hạ Long & Tràng An Ninh Bình")
          .destination("Quảng Ninh & Ninh Bình")
          .description("Khám phá vinh danh UNESCO Tràng An, đỉnh Hang Múa và trải nghiệm du thuyền 5 sao ngắm hàng ngàn hòn đảo Vịnh Hạ Long.")
          .coverImageUrl("https://images.unsplash.com/photo-1507525428034-b723cf961d3e")
          .durationDays(4)
          .estimatedBudget(new BigDecimal("18000000.00"))
          .category("EXPLORE")
          .rating(new BigDecimal("4.97"))
          .reviewCount(210)
          .isActive(true)
          .build();

      suggestedTourRepository.save(tourHalong);

      List<SuggestedTourStop> halongStops = List.of(
          SuggestedTourStop.builder().suggestedTour(tourHalong).dayNumber(1).orderIndex(1).activityTime(LocalTime.of(8, 0)).activityName("Khởi hành đi Ninh Bình - Thăm Quần thể danh thắng Tràng An").locationName("Khu Du Lịch Sinh Thái Tràng An").latitude(new BigDecimal("20.25200000")).longitude(new BigDecimal("105.91400000")).note("Ngồi thuyền đò tham quan các hang động tự nhiên.").build(),
          SuggestedTourStop.builder().suggestedTour(tourHalong).dayNumber(2).orderIndex(1).activityTime(LocalTime.of(11, 30)).activityName("Check-in Du thuyền 5 sao Vịnh Hạ Long").locationName("Cảng Tàu Khách Quốc Tế Hạ Long").latitude(new BigDecimal("20.94900000")).longitude(new BigDecimal("107.05600000")).note("Thưởng thức buffet hải sản và tham quan Hang Sửng Sốt.").build()
      );
      suggestedTourStopRepository.saveAll(halongStops);
    }

    // ==========================================
    // 2. SEED USERS, TRIPS & ITINERARIES CÁ NHÂN
    // ==========================================
    if (userRepository.count() == 0) {
      User admin = User.builder().username("admin").email("admin@mytravel.com").passwordHash(passwordEncoder.encode("Admin123@")).fullName("Bùi Ngọc Đại (Admin)").phoneNumber("0987654321").status("ACTIVE").build();
      User ngocdai = User.builder().username("ngocdai").email("ngocdai@mytravel.com").passwordHash(passwordEncoder.encode("Password123@")).fullName("Bùi Ngọc Đại").phoneNumber("0912345678").status("ACTIVE").build();
      User friend = User.builder().username("friend").email("friend@mytravel.com").passwordHash(passwordEncoder.encode("Password123@")).fullName("Trần Việt Anh").phoneNumber("0933445566").status("ACTIVE").build();

      userRepository.saveAll(List.of(admin, ngocdai, friend));

      Trip tripDalat = Trip.builder().owner(admin).title("Chuyến đi Đà Lạt - Mộng Mơ & Ngàn Hoa").destination("Đà Lạt, Lâm Đồng").startDate(LocalDate.of(2026, 10, 18)).endDate(LocalDate.of(2026, 10, 21)).totalBudget(new BigDecimal("12000000.00")).status("PLANNED").build();
      Trip tripDanang = Trip.builder().owner(ngocdai).title("Khám phá Phố Cổ Hội An & Đà Nẵng").destination("Đà Nẵng & Hội An, Quảng Nam").startDate(LocalDate.of(2026, 9, 29)).endDate(LocalDate.of(2026, 10, 3)).totalBudget(new BigDecimal("15000000.00")).status("ONGOING").build();
      Trip tripHagiang = Trip.builder().owner(admin).title("Hành trình Chinh phục Hà Giang Loop & Sông Nho Quế").destination("Hà Giang").startDate(LocalDate.of(2026, 11, 5)).endDate(LocalDate.of(2026, 11, 8)).totalBudget(new BigDecimal("9500000.00")).status("PLANNED").build();

      tripRepository.saveAll(List.of(tripDalat, tripDanang, tripHagiang));

      tripMemberRepository.saveAll(List.of(
          TripMember.builder().trip(tripDalat).user(admin).role("OWNER").build(),
          TripMember.builder().trip(tripDalat).user(ngocdai).role("EDITOR").build(),
          TripMember.builder().trip(tripDanang).user(ngocdai).role("OWNER").build(),
          TripMember.builder().trip(tripHagiang).user(admin).role("OWNER").build()
      ));

      List<Itinerary> itineraries = List.of(
          Itinerary.builder().trip(tripDalat).dayNumber(1).orderIndex(1).activityTime(LocalTime.of(8, 30)).activityName("Bay đến Sân bay Liên Khương & Nhận phòng khách sạn").locationName("Khách sạn Dalat Palace").latitude(new BigDecimal("11.94040000")).longitude(new BigDecimal("108.43780000")).note("Check-in nhận phòng.").build(),
          Itinerary.builder().trip(tripDalat).dayNumber(1).orderIndex(2).activityTime(LocalTime.of(12, 0)).activityName("Ăn trưa lẩu gà lá é Tao Ngộ").locationName("Lẩu Gà Lá É Tao Ngộ - 3/4 Đà Lạt").latitude(new BigDecimal("11.93280000")).longitude(new BigDecimal("108.44420000")).build(),
          Itinerary.builder().trip(tripDanang).dayNumber(1).orderIndex(1).activityTime(LocalTime.of(9, 0)).activityName("Đến Sân bay Đà Nẵng & Thưởng thức Bánh tráng thịt heo Trần").locationName("Đặc sản Trần - Đà Nẵng").latitude(new BigDecimal("16.06800000")).longitude(new BigDecimal("108.21200000")).build()
      );
      itineraryRepository.saveAll(itineraries);
    }

    log.info("Khởi tạo dữ liệu mẫu Tour gợi ý & Chuyến đi cá nhân hoàn tất thành công!");
  }
}
