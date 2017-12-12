package org.sapient.ace.studentrecord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class StudentrecordApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentrecordApplication.class, args);
	}
}
