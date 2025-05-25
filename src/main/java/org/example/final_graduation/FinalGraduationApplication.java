package org.example.final_graduation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Kích hoạt Scheduled Jobs

public class FinalGraduationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalGraduationApplication.class, args);
    }

}
