package com.sanctacc.mixtogether.movies.events;

import com.sanctacc.mixtogether.movies.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class MovieUpdatedEvent {

    private List<Movie> updated;

}