package com.grupo1.recursos_tic;

import com.grupo1.recursos_tic.model.*;
import com.grupo1.recursos_tic.repository.RatingRepo;
import com.grupo1.recursos_tic.repository.ResourceListsRepo;
import com.grupo1.recursos_tic.repository.ResourceRepo;
import com.grupo1.recursos_tic.repository.UserRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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
				.password(passwordEncoder.encode("admin1234")).role(UserRole.ADMIN).build();
		var user1 = User.builder().name("Javier").email("a@a.es").role(UserRole.AUTHOR).username("javier")
				.password(passwordEncoder.encode("AbCd4321")).build();
		var user2 = User.builder().name("Kevin").email("b@b.es").role(UserRole.AUTHOR).username("kevin")
				.password(passwordEncoder.encode("DcBa1234")).build();
		var user3 = User.builder().name("Marina").email("c@c.es").role(UserRole.AUTHOR).username("marina")
				.password(passwordEncoder.encode("DcBa1234")).build();

		userRepository.saveAll(List.of(admin, user1, user2, user3));


		// Resources
		ResourceRepo resourceRepository = context.getBean(ResourceRepo.class);

		var resource1 = Resource.builder().title("Título R1").type(ResourceType.BOOK).description("Descripción recurso 1")
				.tags(Set.of(EnumTag.SOFTWARE, EnumTag.ACCESSIBILITY)).author("Autor1").url("#").build();
		var resource2 = Resource.builder().title("Título R2").type(ResourceType.VIDEO).description("Descripción recurso 2")
				.tags(Set.of(EnumTag.HARDWARE, EnumTag.HISTORY, EnumTag.RESEARCH)).author("Autor2").url("#").build();

		resourceRepository.saveAll(List.of(resource1, resource2));


		// Resource lists
		ResourceListsRepo resourceListsRepository = context.getBean(ResourceListsRepo.class);

		var resourceList1 = ResourceList.builder().owner(user1).name("Lista 1")
				.description("Descripción de la lista 1")
				.resources(Set.of(resource1, resource2)).build();
		var resourceList2 = ResourceList.builder().owner(admin).name("Lista 2")
				.description("Descripción de la lista 2")
				.resources(Set.of(resource1)).build();
		var resourceList3 = ResourceList.builder().owner(user1).name("Lista 3")
				.description("Descripción de la lista 3")
				.resources(Set.of(resource1, resource2)).build();

		resourceListsRepository.saveAll(List.of(resourceList1, resourceList2, resourceList3));


		// Ratings
		var ratingRepository = context.getBean(RatingRepo.class);

		var rating1 = Rating.builder().user(user1).resource(resource2).title("Título de la valoración 1")
				.createdAt(LocalDate.now()).comment("Valoración de prueba 1").score(3).build();
		var rating2 = Rating.builder().user(user2).resource(resource1).title("Título de la valoración 2")
				.createdAt(LocalDate.now()).comment("Valoración de prueba 2").score(4).build();

		ratingRepository.saveAll(List.of(rating1, rating2));
	}

}
