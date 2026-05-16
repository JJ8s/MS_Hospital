package hospital.eureka_hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@SpringBootApplication
@EnableEurekaServer
public class EurekaHospitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaHospitalApplication.class, args);
	}

}
