package com.fdt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fdt.mapper")
public class UserCenterFdtApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserCenterFdtApplication.class, args);
	}

}
