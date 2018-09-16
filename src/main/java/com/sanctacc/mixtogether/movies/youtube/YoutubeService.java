package com.sanctacc.mixtogether.movies.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.Video;
import com.sanctacc.mixtogether.movies.Movie;
import com.sanctacc.mixtogether.movies.MovieRepository;
import com.sanctacc.mixtogether.movies.code.Code;
import com.sanctacc.mixtogether.movies.code.CodeRepository;
import com.sanctacc.mixtogether.movies.events.MovieUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnResource(resources = "youtube_api")
@PropertySource(name="yt",encoding="UTF-8",value={"classpath:youtube_api"})
@Transactional
@Component
@Slf4j
public class YoutubeService {

    private static final String APPLICATION_NAME = "mix-together";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final YouTube YOU_TUBE;
    private final CodeRepository codeRepository;
    private final MovieRepository movieRepository;
    private final ApplicationEventPublisher eventPublisher;

    public YoutubeService(CodeRepository codeRepository, MovieRepository movieRepository,
                          @Value("${youtube.api_key}") String api_key, ApplicationEventPublisher eventPublisher) {
        this.codeRepository = codeRepository;
        this.movieRepository = movieRepository;
        this.eventPublisher = eventPublisher;
        this.YOU_TUBE = new YouTube.Builder(new NetHttpTransport(), JSON_FACTORY, httpRequest -> {
        }).setYouTubeRequestInitializer(new YouTubeRequestInitializer(api_key)).
                        setApplicationName(APPLICATION_NAME).build();
    }

    public void addMoviesFromYTPlaylistId(String code, String playlistId) throws IOException {
        StopWatch stopWatch = new StopWatch();
        Code codeEntity = codeRepository.getOne(code);
        stopWatch.start();
        List<PlaylistItem> items = YOU_TUBE.playlistItems().list("snippet").setPlaylistId(playlistId)
                .setMaxResults(50L).execute().getItems();
        stopWatch.stop();
        log.info("Downloading playlist items from youtube data api took: {} ms",stopWatch.getLastTaskTimeMillis());
        Integer currentOrder = movieRepository.countAllByCode_Code(code);
        List<Movie> movieList = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            Movie m = Movie.builder().order(currentOrder + i).url(items.get(i).getSnippet().getResourceId().getVideoId())
                    .title(items.get(i).getSnippet().getTitle()).code(codeEntity).build();
            movieList.add(m);
        }
        stopWatch.start();
        movieRepository.saveAll(movieList);
        stopWatch.stop();
        log.info("Saving playlist items in db took: {} ms",stopWatch.getLastTaskTimeMillis());
        eventPublisher.publishEvent(MovieUpdatedEvent.of(movieList));
    }

    public String getMovieTitle(String id) {
        try {
            final List<Video> snippet = YOU_TUBE.videos().list("snippet").setId(id).execute().getItems();
            return snippet.get(0).getSnippet().getTitle();
        } catch (IndexOutOfBoundsException e) {
            throw new YoutubeException("Invalid movie id");
        } catch (Exception e) {
            log.warn("{}", e);
            throw new YoutubeException("Youtube api exception");
        }
    }
}