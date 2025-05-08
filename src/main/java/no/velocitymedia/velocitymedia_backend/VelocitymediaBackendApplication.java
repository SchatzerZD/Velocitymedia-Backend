package no.velocitymedia.velocitymedia_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.UserService;

@SpringBootApplication
public class VelocitymediaBackendApplication{


	public static void main(String[] args) {
		SpringApplication.run(VelocitymediaBackendApplication.class, args);
	}

	@Component
	public class CommandLineAppStartupRunner implements CommandLineRunner{
		@Autowired
		UserService userService;

		@Override
		public void run(String... args) throws Exception {
			try {
				UserEntity admin = new UserEntity();
				admin.setUsername("admin");
				admin.setPassword("admin");
				userService.addUser(admin);
			} catch (IllegalArgumentException e) {
				System.out.println("Admin already added");
			}
		}
		
	}


}
