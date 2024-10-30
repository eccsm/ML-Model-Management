package net.casim.ml.mm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MlModelManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MlModelManagementApplication.class, args);
	}

}
