package com.grupo1.recursos_tic;

import com.grupo1.recursos_tic.model.*;
import com.grupo1.recursos_tic.repository.RatingRepo;
import com.grupo1.recursos_tic.repository.ResourceListsRepo;
import com.grupo1.recursos_tic.repository.ResourceRepo;
import com.grupo1.recursos_tic.repository.UserRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		var context = SpringApplication.run(Main.class, args);


		// Users
		UserRepo userRepository = context.getBean(UserRepo.class);
		var passwordEncoder = context.getBean(PasswordEncoder.class);

		var admin = User.builder().name("Administrador").email("admin@admin.es").username("admin")
				.password(passwordEncoder.encode("admin1234")).build();
		var user1 = User.builder().name("Javier").email("a@a.es").username("javier")
				.password(passwordEncoder.encode("AbCd4321")).build();
		var user2 = User.builder().name("Kevin").email("b@b.es").username("kevin")
				.password(passwordEncoder.encode("DcBa1234")).build();
		var user3 = User.builder().name("Marina").email("c@c.es").username("marina")
				.password(passwordEncoder.encode("DcBa1234")).build();

		userRepository.saveAll(List.of(admin, user1, user2, user3));


		// Resources
		ResourceRepo resourceRepository = context.getBean(ResourceRepo.class);

		var resource1 = Resource.builder().title("Libro 1").type(ResourceType.BOOK)
				.author("Autor1").description("Descripción del libro 1").url("#").build();
		var resource2 = Resource.builder().title("Libro 2").type(ResourceType.VIDEO)
				.author("Autor2").description("Descripción del libro 2").url("#").build();

		resourceRepository.saveAll(List.of(resource1, resource2));


		// Resource lists
		ResourceListsRepo resourceListsRepository = context.getBean(ResourceListsRepo.class);

		var resourceList1 = ResourceList.builder().owner(user1).name("Lista 1")
				.description("Descripción de la lista 1")
				.resources(Set.of(resource1, resource2)).build();

		resourceListsRepository.save(resourceList1);


		// Ratings
		var ratingRepository = context.getBean(RatingRepo.class);

		var rating1 = Rating.builder().user(user1).resource(resource2)
				.comment("Comentario de prueba 1").score(3).build();
		var rating2 = Rating.builder().user(user2).resource(resource1)
				.comment("Comentario de prueba 2").score(3).build();

		ratingRepository.saveAll(List.of(rating1, rating2));
	}

}
