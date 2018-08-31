package com.sanctacc.mixtogether.movies;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface MovieRepository extends JpaRepository<Movie,Long> {

    @Query("SELECT m FROM Movie m JOIN m.code c where c.code=?1")
    Page<Movie> findAllByCode(String code, Pageable pageable);
}