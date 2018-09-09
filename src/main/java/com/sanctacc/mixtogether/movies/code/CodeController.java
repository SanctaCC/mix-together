package com.sanctacc.mixtogether.movies.code;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class CodeController {

    private final CodeRepository codeRepository;

    public CodeController(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @PostMapping("/api/codes")
    public ResponseEntity<Code> create(@RequestBody(required = false) Code code) {
        if (code == null || code.getCode().isEmpty()) {
            code = new Code();
        }
        Code save = codeRepository.save(code);
        //TODO
        final URI buildUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{code}").build(save.getCode());
        return ResponseEntity.created(buildUri).body(codeRepository.save(code));
    }
}