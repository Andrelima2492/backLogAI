package com.nulhart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BacklogTrackerApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context =  SpringApplication.run(BacklogTrackerApplication.class, args);

    }

}
