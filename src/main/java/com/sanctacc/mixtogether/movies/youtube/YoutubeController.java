package com.sanctacc.mixtogether.movies.youtube;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@ConditionalOnBean(value = YoutubeService.class)
public class YoutubeController {

    private final YoutubeService youtubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @PostMapping(value = "/api/codes/{code}/movies", params = "playlistId")
    public ResponseEntity<?> addFromYoutubePlaylist(@PathVariable String code, @RequestParam String playlistId)
            throws IOException {

        return ResponseEntity.status(201).body(youtubeService.addMoviesFromYTPlaylistId(code, playlistId));
    }
}