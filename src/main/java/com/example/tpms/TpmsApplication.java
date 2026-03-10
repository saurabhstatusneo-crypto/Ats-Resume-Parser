package com.example.tpms;

import com.example.tpms.entity.UserProfile;
import com.example.tpms.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@SpringBootApplication
public class TpmsApplication implements CommandLineRunner {
     private final UserRepository userRepository ;
     private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(TpmsApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        UserProfile userProfile = new UserProfile();
         userProfile.setName("admin");
         userProfile.setPassword(passwordEncoder.encode("admin@1234"));
         userProfile.setUsername("admin");
         userRepository.save(userProfile);
    }
}
