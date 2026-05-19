package com.hospital.ms_urgencia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableDiscoveryClient
// AGREGA ESTO (basePackages):
@EnableFeignClients(basePackages = "com.hospital.ms_urgencia.client")
public class MsUrgenciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsUrgenciaApplication.class, args);
	}

}
