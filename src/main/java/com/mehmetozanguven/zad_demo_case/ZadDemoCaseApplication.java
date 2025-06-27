package com.mehmetozanguven.zad_demo_case;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@SpringBootApplication
@Modulith(sharedModules = {"core"})
public class ZadDemoCaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZadDemoCaseApplication.class, args);
	}

}
