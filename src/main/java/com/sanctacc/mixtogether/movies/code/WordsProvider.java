package com.sanctacc.mixtogether.movies.code;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StopWatch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class WordsProvider {

    @Getter
    private List<String> words;

    @SneakyThrows
    public WordsProvider() {
        Path path = new ClassPathResource("/words.txt").getFile().toPath();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        this.words = Files.readAllLines(path);
        stopWatch.stop();
        log.info("Reading all lines took: {} ms",stopWatch.getLastTaskTimeMillis());
    }

    public List<String> randomize(int amount) {
        List<String> random = new ArrayList<>(amount);
        for (int i=0; i < amount; i++) {
            random.add(words.get(ThreadLocalRandom.current().nextInt(0,words.size())));
        }
        return random;
    }
}