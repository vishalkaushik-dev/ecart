package com.JVM.eCart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ECartApplication {

	public static void main(String[] args) {

        SpringApplication.run(ECartApplication.class, args);
        System.out.println("ECart Application Started Successfully!");
	}
}
