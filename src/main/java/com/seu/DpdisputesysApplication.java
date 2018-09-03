package com.seu;

import com.seu.service.FrameStartUpService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.cors.CorsConfiguration;

@SpringBootApplication
@MapperScan("com.seu.repository")
//@ComponentScan(basePackages = {"com.seu.common","com.seu.config","com.seu.common"})
@EnableCaching
public class DpdisputesysApplication {

	public static void main(String[] args) {
		SpringApplication.run(DpdisputesysApplication.class, args);
	}


//	private CorsConfiguration buildConfig(){
//		CorsConfiguration corsConfiguration=new CorsConfiguration(());
//
//	}

//	@Bean
//	public CommandLineRunner init(final FrameStartUpService frameStartUpService) {
//		return new CommandLineRunner() {
//			public void run(String... strings) throws Exception {
//				frameStartUpService.initActiUserAndGroup();
//			}
//		};
//	}
}
