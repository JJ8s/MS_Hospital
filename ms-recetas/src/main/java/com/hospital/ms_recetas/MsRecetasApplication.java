package com.hospital.ms_recetas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsRecetasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsRecetasApplication.class, args);
	}

}
