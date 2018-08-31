package com.sanctacc.mixtogether.movies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {

    @Autowired private MovieRepository movieRepository;

    @RequestMapping("/api/movies/{code}")
    public ResponseEntity<Page<Movie>> moviesByCode(@PathVariable String code, Pageable pageable) {
        return ResponseEntity.ok(movieRepository.findAllByCode(code, pageable));
    }
}