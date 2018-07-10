package com.seu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.seu.repository")
public class DpdisputesysApplication {

	public static void main(String[] args) {
		SpringApplication.run(DpdisputesysApplication.class, args);
	}
}
