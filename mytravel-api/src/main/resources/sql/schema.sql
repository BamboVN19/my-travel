-- 1. Bảng Users
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100),
    phone_number VARCHAR(20),
    avatar_url TEXT,
    refresh_token TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 2. Bảng Trips
CREATE TABLE IF NOT EXISTS trips (
                                     id BIGSERIAL PRIMARY KEY,
                                     user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    destination VARCHAR(255),
    start_date DATE,
    end_date DATE,
    total_budget DECIMAL(15, 2),
    status VARCHAR(20) DEFAULT 'PLANNED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 3. Bảng Itineraries
CREATE TABLE IF NOT EXISTS itineraries (
                                           id BIGSERIAL PRIMARY KEY,
                                           trip_id BIGINT REFERENCES trips(id) ON DELETE CASCADE,
    day_number INT,
    activity_time TIME,
    activity_name VARCHAR(255),
    location_name VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    place_id VARCHAR(255),
    note TEXT
    );

-- 4. Bảng Expenses
CREATE TABLE IF NOT EXISTS expenses (
                                        id BIGSERIAL PRIMARY KEY,
                                        trip_id BIGINT REFERENCES trips(id) ON DELETE CASCADE,
    amount DECIMAL(15, 2) NOT NULL,
    category VARCHAR(50),
    expense_date DATE,
    description TEXT,
    payment_method VARCHAR(50)
    );

-- 5. Bảng Trip Notes
CREATE TABLE IF NOT EXISTS trip_notes (
                                          id BIGSERIAL PRIMARY KEY,
                                          trip_id BIGINT REFERENCES trips(id) ON DELETE CASCADE,
    title VARCHAR(255),
    content TEXT,
    entry_date DATE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 6. Bảng Media Albums
CREATE TABLE IF NOT EXISTS media_albums (
                                            id BIGSERIAL PRIMARY KEY,
                                            trip_id BIGINT REFERENCES trips(id) ON DELETE CASCADE,
    album_title VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 7. Bảng Album Photos
CREATE TABLE IF NOT EXISTS album_photos (
                                            id BIGSERIAL PRIMARY KEY,
                                            album_id BIGINT REFERENCES media_albums(id) ON DELETE CASCADE,
    photo_url TEXT NOT NULL,
    caption VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
