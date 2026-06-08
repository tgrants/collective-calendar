package com.collectivecalendar;

import com.collectivecalendar.CollectivecalendarApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootTest(classes = {
	CollectivecalendarApplication.class,
	KopigslaikaplanotajsApplicationTests.TestConfig.class
})
class KopigslaikaplanotajsApplicationTests {

    @Test
    void contextLoads() {
    }

    @Configuration
    static class TestConfig {

        @Bean
        JavaMailSender javaMailSender() {
            return new JavaMailSenderImpl();
        }
    }
}
