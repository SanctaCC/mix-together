package com.sanctacc.mixtogether.movies;

import com.sanctacc.mixtogether.movies.hateoas.MovieResource;
import com.sanctacc.mixtogether.movies.hateoas.MovieResourceAssembler;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/codes/{code}/movies")
public class MovieController {

    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final MovieResourceAssembler assembler;

    public MovieController(MovieRepository movieRepository, MovieService movieService, MovieResourceAssembler assembler) {
        this.movieRepository = movieRepository;
        this.movieService = movieService;
        this.assembler = assembler;
    }

    @ExceptionHandler(value = IndexOutOfBoundsException.class)
    public void indexExc(Exception e, HttpServletResponse response) throws IOException {
        response.sendError(400, "Either code doesn't exits or invalid update request.");
    }

    @GetMapping("/{order}")
    public ResponseEntity<MovieResource> getOne(@PathVariable String code, @PathVariable Integer order) {
        return movieRepository.findByCode_CodeAndOrder(code, order)
                .map(MovieResource::new).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<PagedResources<MovieResource>> moviesByCode(@PathVariable String code,
                                                                        @PageableDefault(sort = "order", size = 500) Pageable pageable,
                                                                        PagedResourcesAssembler pagedAssembler) {
        return ResponseEntity.ok(pagedAssembler
                .toResource(movieRepository.findAllByCode_Code(code, pageable), assembler));
    }

    @PostMapping
    public ResponseEntity<MovieResource> addMovie(@PathVariable String code, @RequestBody Movie movie) {
        Movie createdMovie = movieService.addMovie(code, movie);
        URI uri = linkTo(methodOn(getClass()).getOne(code, movie.getOrder())).toUri();
        return ResponseEntity.created(uri).body(assembler.toResource(createdMovie));
    }

//    @PutMapping(params ={"id1","id2"})
//    public ResponseEntity<?> swap(@RequestParam Long id1, @RequestParam Long id2) {
//        movieService.swapTwo(id1, id2);
//        return ResponseEntity.ok().build();
//    }

    @PutMapping
    public ResponseEntity changePlaces(@PathVariable String code, @RequestBody UpdateRequest updateRequest) {
        movieService.changePlace(code, updateRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping(params = "shuffle")
    public ResponseEntity shuffle(@PathVariable String code) {
        movieService.shuffle(code);
        return ResponseEntity.ok().build();
//        return ResponseEntity.ok(movieService.shuffle(code).stream().map(assembler::toResource)
//            .collect(Collectors.toList()));
    }

    @DeleteMapping(value="/{order}")
    public ResponseEntity delete(@PathVariable String code, @PathVariable Integer order) {
        movieService.deleteMovie(code, order);
        return ResponseEntity.ok().build();
    }
}