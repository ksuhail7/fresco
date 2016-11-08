package com.suhailkandanur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FrescoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrescoApplication.class, args);
	}
}
