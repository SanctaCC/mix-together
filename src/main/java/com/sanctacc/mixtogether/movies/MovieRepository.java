package com.sanctacc.mixtogether.movies;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie,Long>, CustomMovieRepository {

    List<Movie> findAllByCode_CodeOrderByOrder(String code);

    List<Movie> findAllByCode_CodeAndOrderGreaterThanEqualOrderByOrder(String code, Integer fromOrder);

    Page<Movie> findAllByCode_Code(String code, Pageable pageable);

    Optional<Movie> findByCode_CodeAndOrder(String code, Integer order);

    Integer countAllByCode_Code(String code);
}