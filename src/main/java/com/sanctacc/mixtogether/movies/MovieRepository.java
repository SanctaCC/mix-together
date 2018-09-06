package com.sanctacc.mixtogether.movies;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Long> {

    List<Movie> findAllByCode_CodeOrderByOrder(String code);

    Page<Movie> findAllByCode_Code(String code, Pageable pageable);

    Integer countAllByCode_Code(String code);
}