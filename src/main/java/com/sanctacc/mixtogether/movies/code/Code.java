package com.sanctacc.mixtogether.movies.code;

import com.sanctacc.mixtogether.movies.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of ="code")
@NoArgsConstructor
@AllArgsConstructor
public class Code {

    @Id
    private String code;

    @OneToMany(mappedBy = "code", cascade = {CascadeType.MERGE,CascadeType.PERSIST},
    fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Movie> movies;
}