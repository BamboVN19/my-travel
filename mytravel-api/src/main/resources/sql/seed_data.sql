-- =============================================================
-- MYTRAVEL SAMPLE SEED DATA SCRIPT (POSTGRESQL)
-- =============================================================

-- 1. SEED USERS (Passwords hashed using BCrypt: Admin123@ & Password123@)
INSERT INTO users (id, username, email, password_hash, full_name, phone_number, avatar_url, status)
VALUES
(1, 'admin', 'admin@mytravel.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.i65bQx2a', 'Bùi Ngọc Đại (Admin)', '0987654321', 'https://i.pravatar.cc/300?img=11', 'ACTIVE'),
(2, 'ngocdai', 'ngocdai@mytravel.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.i65bQx2a', 'Bùi Ngọc Đại', '0912345678', 'https://i.pravatar.cc/300?img=12', 'ACTIVE'),
(3, 'friend', 'friend@mytravel.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.i65bQx2a', 'Trần Việt Anh', '0933445566', 'https://i.pravatar.cc/300?img=13', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Reset sequence for users
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- 2. SEED TRIPS
INSERT INTO trips (id, owner_id, title, destination, start_date, end_date, total_budget, status)
VALUES
(1, 1, 'Chuyến đi Đà Lạt, Lâm Đồng', 'Đà Lạt, Lâm Đồng', '2026-10-18', '2026-10-22', 12000000.00, 'PLANNED'),
(2, 2, 'Khám phá Phố Cổ Hội An & Đà Nẵng', 'Hội An, Quảng Nam', '2026-09-29', '2026-10-03', 15000000.00, 'ONGOING')
ON CONFLICT (id) DO NOTHING;

SELECT setval('trips_id_seq', (SELECT MAX(id) FROM trips));

-- 3. SEED TRIP MEMBERS
INSERT INTO trip_members (id, trip_id, user_id, role)
VALUES
(1, 1, 1, 'OWNER'),
(2, 1, 2, 'EDITOR'),
(3, 1, 3, 'VIEWER'),
(4, 2, 2, 'OWNER'),
(5, 2, 1, 'EDITOR')
ON CONFLICT (id) DO NOTHING;

SELECT setval('trip_members_id_seq', (SELECT MAX(id) FROM trip_members));

-- 4. SEED ITINERARIES
INSERT INTO itineraries (id, trip_id, day_number, order_index, activity_time, activity_name, location_name, latitude, longitude, place_id, note)
VALUES
(101, 1, 1, 1, '08:00:00', 'Bay đến Đà Lạt & Check-in khách sạn', 'Khách sạn Dalat Palace', 11.94040000, 108.43780000, 'ChIJbUa...', 'Nhớ mang theo CCCD khi check-in'),
(102, 1, 1, 2, '14:30:00', 'Dạo chơi Hồ Tuyền Lâm & Đi cáp treo', 'Hồ Tuyền Lâm, Đà Lạt', 11.89670000, 108.44190000, 'ChIJcVa...', 'Thuê thuyền kayak chèo ngắm hoàng hôn'),
(103, 1, 2, 1, '07:30:00', 'Ăn bánh mì xíu mại & Cà phê mây', 'Tiệm cà phê Cheo Veo', NULL, NULL, NULL, 'Đi sớm để bắt trọn mây sương')
ON CONFLICT (id) DO NOTHING;

SELECT setval('itineraries_id_seq', (SELECT MAX(id) FROM itineraries));

-- 5. SEED EXPENSES
INSERT INTO expenses (id, trip_id, paid_by_user_id, amount, category, expense_date, payment_method, description)
VALUES
(201, 1, 1, 3200000.00, 'ACCOMMODATION', '2026-10-18', 'CARD', 'Thanh toán tiền khách sạn 4 đêm'),
(202, 1, 2, 900000.00, 'FOOD', '2026-10-19', 'CASH', 'Ăn tối lẩu gà lá é Tao Ngộ')
ON CONFLICT (id) DO NOTHING;

SELECT setval('expenses_id_seq', (SELECT MAX(id) FROM expenses));

-- 6. SEED EXPENSE SPLITS
INSERT INTO expense_splits (id, expense_id, user_id, split_amount, is_settled)
VALUES
(301, 202, 1, 300000.00, TRUE),
(302, 202, 2, 300000.00, TRUE),
(303, 202, 3, 300000.00, FALSE)
ON CONFLICT (id) DO NOTHING;

SELECT setval('expense_splits_id_seq', (SELECT MAX(id) FROM expense_splits));

-- 7. SEED MEDIA ALBUMS & PHOTOS
INSERT INTO media_albums (id, trip_id, album_title, description)
VALUES
(501, 1, 'Ảnh Hoàng Hôn Hồ Tuyền Lâm', 'Kỷ niệm chiều Ngày 1 Đà Lạt')
ON CONFLICT (id) DO NOTHING;

SELECT setval('media_albums_id_seq', (SELECT MAX(id) FROM media_albums));

INSERT INTO album_photos (id, album_id, photo_url, caption)
VALUES
(601, 501, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', 'Cảnh hồ Tuyền Lâm chiều sương')
ON CONFLICT (id) DO NOTHING;

SELECT setval('album_photos_id_seq', (SELECT MAX(id) FROM album_photos));
