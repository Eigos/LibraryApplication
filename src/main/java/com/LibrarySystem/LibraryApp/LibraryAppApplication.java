package com.LibrarySystem.LibraryApp;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryAppApplication {

	public static void main(String[] args) {
		System.out.println("Setting the timezone"+TimeZone.getTimeZone("UTC").getID());
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(LibraryAppApplication.class, args);
	}
}
