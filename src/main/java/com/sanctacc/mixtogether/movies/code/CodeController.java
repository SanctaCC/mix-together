package com.sanctacc.mixtogether.movies.code;

import com.sanctacc.mixtogether.movies.MovieController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CodeController {

    private final CodeRepository codeRepository;

    public CodeController(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @PostMapping("/api/codes")
    public ResponseEntity<Resource<Code>> create(@RequestBody(required = false) Code code) {
        if (code == null || code.getCode().isEmpty()) {
            code = new Code();
        }
        Code save = codeRepository.save(code);
        Link link = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MovieController.class)
                .moviesByCode(code.getCode(), Pageable.unpaged(), null)).withRel("movies");
        Resource<Code> codeResource = new Resource<>(save, link);
        return ResponseEntity.status(HttpStatus.CREATED).body(codeResource);
    }
}