package swyp_8th.bungmakase_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@PropertySource("classpath:application.yml")
@EnableScheduling
public class BungmakaseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BungmakaseBackendApplication.class, args);
    }

}
