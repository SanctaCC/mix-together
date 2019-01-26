package com.sanctacc.mixtogether.movies.code;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class WordsProvider {

    @Getter
    private List<String> words;

    @SneakyThrows
    public WordsProvider() {
        InputStream inputStream = new ClassPathResource("/words.txt").getInputStream();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        words = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());
        stopWatch.stop();
        log.info("Reading all lines took: {} ms",stopWatch.getLastTaskTimeMillis());
    }

    public List<String> randomize(int amount) {
        List<String> random = new ArrayList<>(amount);
        for (int i=0; i < amount; i++) {
            String randomWord = words.get(ThreadLocalRandom.current().nextInt(0, words.size()));
            if (random.contains(randomWord)) { //no duplicates
                i--;
                continue;
            }
            random.add(randomWord);
        }
        return random;
    }
}