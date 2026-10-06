package com.mytravel.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Integration test đòi hỏi kết nối PostgreSQL thật; Pure unit tests độc lập được triển khai riêng biệt.")
@SpringBootTest
class MytravelApiApplicationTests {

    @Test
    void contextLoads() {
    }

}

