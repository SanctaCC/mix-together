package com.sanctacc.mixtogether;

import com.sanctacc.mixtogether.movies.Movie;
import com.sanctacc.mixtogether.movies.code.Code;
import com.sanctacc.mixtogether.movies.code.CodeRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
public class MixTogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(MixTogetherApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner(CodeRepository codeRepository) {
		Movie movie = new Movie();
		movie.setUrl("youtube");
		movie.setOrder(1L);

		Movie movie2 = new Movie();
		movie2.setUrl("youtube");
		movie2.setOrder(2L);

		Code code = new Code();
		code.setMovies(new HashSet<>(Arrays.asList(movie,movie2)));
		movie.setCode(code);
		movie2.setCode(code);
		return args -> codeRepository.save(code);
	}
}