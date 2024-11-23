package com.grupo1.recursos_tic;

import com.grupo1.recursos_tic.model.*;
import com.grupo1.recursos_tic.repository.RatingRepo;
import com.grupo1.recursos_tic.repository.ResourceListsRepo;
import com.grupo1.recursos_tic.repository.ResourceRepo;
import com.grupo1.recursos_tic.repository.UserRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		var context = SpringApplication.run(Main.class, args);


		// Users
		UserRepo userRepository = context.getBean(UserRepo.class);
		var passwordEncoder = context.getBean(PasswordEncoder.class);

		var admin = User.builder().name("Administrador").email("admin@admin.es").role(UserRole.ADMIN).username("admin")
				.imageUrl("/img/user/noUser.png").password(passwordEncoder.encode("Admin1234")).build();
		var user1 = User.builder().name("Javier").email("a@a.es").role(UserRole.AUTHOR).username("javier")
				.imageUrl("/img/user/javier.png").password(passwordEncoder.encode("User1234")).build();
		var user2 = User.builder().name("Kevin").email("b@b.es").role(UserRole.AUTHOR).username("kevin")
				.imageUrl("/img/user/kevin.jpeg").password(passwordEncoder.encode("User1234")).build();
		var user3 = User.builder().name("Marina").email("c@c.es").role(UserRole.AUTHOR).username("marina")
				.imageUrl("/img/user/marina.jpeg").password(passwordEncoder.encode("User1234")).build();

		userRepository.saveAll(List.of(admin, user1, user2, user3));


		// Resources
		ResourceRepo resourceRepository = context.getBean(ResourceRepo.class);

		var resource1 = Resource.builder().title("Stack Overflow").type(ResourceType.COMMUNITY).author("Varios")
				.tags(Set.of(EnumTag.SOFTWARE, EnumTag.PROGRAMMING)).url("https://stackoverflow.com/").imageUrl("")
				.description("El mayor repositorio de consultas sobre programación en Internet.").build();
		var resource2 = Resource.builder().title("The Register").type(ResourceType.PORTAL).author("Situation Publishing")
				.tags(Set.of(EnumTag.HARDWARE, EnumTag.SOFTWARE, EnumTag.NEWS, EnumTag.HISTORY)).imageUrl("")
				.url("https://www.theregister.com/").description("Información tecnológica de actualidad.").build();

		resourceRepository.saveAll(List.of(resource1, resource2));


		// Resource lists
		ResourceListsRepo resourceListsRepository = context.getBean(ResourceListsRepo.class);

		var resourceList1 = ResourceList.builder().owner(user1).name("Tecnología")
				.description("Mis favoritos tech")
				.resources(Set.of(resource1, resource2)).build();
		var resourceList2 = ResourceList.builder().owner(admin).name("Noticias")
				.description("Medios de noticias tecnológicos")
				.resources(Set.of(resource1)).build();
		var resourceList3 = ResourceList.builder().owner(user1).name("Software")
				.description("Repositorios de software interesantes")
				.resources(Set.of(resource1, resource2)).build();

		resourceListsRepository.saveAll(List.of(resourceList1, resourceList2, resourceList3));


		// Ratings
		var ratingRepository = context.getBean(RatingRepo.class);

		var rating1 = Rating.builder().user(user1).resource(resource1).title("Imprescindible")
				.comment("Inigualable a la hora de encontrar soluciones aportadas por la comunidad.")
				.createdAt(LocalDateTime.now()).score(5).build();
		var rating2 = Rating.builder().user(user2).resource(resource2).title("Para estar al día")
				.comment("Uno de los mejores recursos con información actualizada. Está en inglés.")
				.createdAt(LocalDateTime.now()).score(4).build();

		ratingRepository.saveAll(List.of(rating1, rating2));
	}

}
