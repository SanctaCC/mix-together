package com.sanctacc.mixtogether;

import com.sanctacc.mixtogether.movies.Movie;
import com.sanctacc.mixtogether.movies.code.Code;
import com.sanctacc.mixtogether.movies.code.CodeRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootApplication
public class MixTogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(MixTogetherApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner(CodeRepository codeRepository) {
		Movie movie = new Movie();
		movie.setUrl("youtube");
		movie.setOrder(1);

		Code code = new Code();
		code.setCode("12345");
		code.setMovies(Collections.singleton(movie));
		movie.setCode(code);
		return args -> codeRepository.save(code);
	}
}