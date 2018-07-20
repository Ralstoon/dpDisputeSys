package com.seu;

import com.seu.service.FrameStartUpService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.seu.repository")
public class DpdisputesysApplication {

	public static void main(String[] args) {
		SpringApplication.run(DpdisputesysApplication.class, args);
	}


	@Bean
	public CommandLineRunner init(final FrameStartUpService frameStartUpService) {
		return new CommandLineRunner() {
			public void run(String... strings) throws Exception {
				frameStartUpService.initActiUserAndGroup();
			}
		};
	}
}
