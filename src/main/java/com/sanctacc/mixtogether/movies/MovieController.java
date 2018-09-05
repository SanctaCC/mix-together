package com.sanctacc.mixtogether.movies;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MovieController {

    private final MovieRepository movieRepository;

    private final MovieService movieService;

    public MovieController(MovieRepository movieRepository, MovieService movieService) {
        this.movieRepository = movieRepository;
        this.movieService = movieService;
    }

    @GetMapping("/api/movies/{code}")
    public ResponseEntity<Page<Movie>> moviesByCode(@PathVariable String code, Pageable pageable) {
        return ResponseEntity.ok(movieRepository.findAllByCode(code, pageable));
    }

    @PostMapping("/api/movies/{code}")
    public ResponseEntity<Movie> addMovie(@PathVariable String code, @RequestBody Movie movie) {
        return ResponseEntity.status(201).body(movieService.addMovie(code, movie));
    }

    @PutMapping(value = "/api/movies", params ={"id1","id2"})
    public ResponseEntity<?> swap(@RequestParam Long id1, @RequestParam Long id2) {
        movieService.swapTwo(id1, id2);
        return new ResponseEntity(HttpStatus.OK);
    }

}