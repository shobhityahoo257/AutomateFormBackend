package com.formsv.AutomateForm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@SpringBootApplication
public class AutomateFormApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomateFormApplication.class, args);
	}

}
