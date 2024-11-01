package com.grupo1.recursos_tic;

import com.grupo1.recursos_tic.model.*;
import com.grupo1.recursos_tic.repository.RatingRepo;
import com.grupo1.recursos_tic.repository.ResourceListRepo;
import com.grupo1.recursos_tic.repository.ResourceRepo;
import com.grupo1.recursos_tic.repository.UserRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		var context = SpringApplication.run(Main.class, args);

		UserRepo userRepository = context.getBean(UserRepo.class);
		var user1 = User.builder().name("Javi").email("a@a.es")
				.username("javi").password("AbCd4321").role(userRole.AUTHOR).build();
		var user2 = User.builder().name("Marina").email("b@b.es")
				.username("marina").password("DcBa1234").role(userRole.READER).build();
		var user3 = User.builder().name("Kevin").email("c@c.es")
				.username("kevin").password("DcBa1234").role(userRole.READER).build();
		var admin = User.builder().name("Admin").email("admin@admin.es").username("admin")
				.password("admin1234").role(userRole.ADMIN).build();
		userRepository.saveAll(List.of(user1, user2, user3, admin));

		ResourceRepo resourceRepository = context.getBean(ResourceRepo.class);
		var resource1 = Resource.builder().title("Libro 1")
				.description("Descripción del libro 1").build();
		var resource2 = Resource.builder().title("Libro 2")
				.description("Descripción del libro 2").build();
		resourceRepository.saveAll(List.of(resource1, resource2));

		ResourceListRepo resourceListRepository = context.getBean(ResourceListRepo.class);
		var resourceList1 = ResourceList.builder().owner(user1).name("Lista 1")
				.description("Descripción de la lista 1")
				.resources(Set.of(resource1, resource2)).build();
		resourceListRepository.save(resourceList1);

		var ratingRepository = context.getBean(RatingRepo.class);
		var rating1 = Rating.builder().user(user1).resource(resource2)
				.comment("Comentario de prueba 1").score(3).build();
		var rating2 = Rating.builder().user(user2).resource(resource1)
				.comment("Comentario de prueba 2").score(3).build();
		ratingRepository.saveAll(List.of(rating1, rating2));
	}

}
