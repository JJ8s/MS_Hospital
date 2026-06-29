package com.hospital.ms_medicos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsMedicosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsMedicosApplication.class, args);
	}

}
