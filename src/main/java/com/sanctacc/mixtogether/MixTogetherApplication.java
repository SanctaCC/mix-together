package com.sanctacc.mixtogether;

import com.sanctacc.mixtogether.movies.code.Code;
import com.sanctacc.mixtogether.movies.code.CodeRepository;
import com.sanctacc.mixtogether.movies.youtube.YoutubeService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;

@SpringBootApplication
public class MixTogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(MixTogetherApplication.class, args);
	}

	@Bean
	public ThreadPoolTaskExecutor executorPool() {
		ThreadPoolTaskExecutor tpte = new ThreadPoolTaskExecutor();
		tpte.setThreadNamePrefix("nio executor");
		tpte.setCorePoolSize(2);
		tpte.setMaxPoolSize(10);
		tpte.initialize();
		return tpte;
	}

	@Bean
	@ConditionalOnMissingBean(value = YoutubeService.class)
	public ApplicationRunner runner(CodeRepository codeRepository) {
		Code code = new Code();
		return args -> codeRepository.save(code);
	}

	@Bean
	@ConditionalOnBean(value = YoutubeService.class)
	public ApplicationRunner runner2(CodeRepository codeRepository, YoutubeService youtubeService,
									 ThreadPoolTaskExecutor executorPool) {

		Runnable run = () -> {
            Code code = new Code();
            codeRepository.save(code);
			try {
				youtubeService.addMoviesFromYTPlaylistId(code.getCode(), "PLjv48fZt_9Zy0yDzmjV9GUMPI-cX0WkDB");
			} catch (IOException e) {
				e.printStackTrace();
			}
		};

		return args -> executorPool.execute(run);
	}

}