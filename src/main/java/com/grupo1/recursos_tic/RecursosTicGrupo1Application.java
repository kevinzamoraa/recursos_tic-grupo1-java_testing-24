package com.grupo1.recursos_tic;

import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.repository.RatingRepo;
import com.grupo1.recursos_tic.repository.UserRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RecursosTicGrupo1Application {

	public static void main(String[] args) {

		var context = SpringApplication.run(RecursosTicGrupo1Application.class, args);

		UserRepo userRepository = context.getBean(UserRepo.class);

		long numeroUsuarios = userRepository.count();
		if (numeroUsuarios > 0)
			return;

		var user1 = User.builder().name("Javi").email("a@a.es").password("AbCd4321").role(User.roleOptions.author).build();
		var user2 = User.builder().name("Marina").email("b@b.es").password("DcBa1234").role(User.roleOptions.reader).build();
		userRepository.save(user1);
		userRepository.save(user2);

		var ratingRepository = context.getBean(RatingRepo.class);
		if (ratingRepository.count() == 0) {
			// insertar fabricantes
			var rating1 = Rating.builder().user(user1).comment("Comentario de prueba 1").score(3).build();
			var rating2 = Rating.builder().user(user2).comment("Comentario de prueba 2").score(3).build();
			ratingRepository.saveAll(
					List.of(rating1, rating2)
			);
		}
	}

}
