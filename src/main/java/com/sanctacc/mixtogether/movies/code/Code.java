package com.sanctacc.mixtogether.movies.code;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sanctacc.mixtogether.movies.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of ="code")
@NoArgsConstructor
@AllArgsConstructor
public class Code implements Persistable<String> {

    @Id
    @GenericGenerator(name="codeGenerator", strategy="com.sanctacc.mixtogether.movies.code.CodeGenerator")
    @GeneratedValue(generator = "codeGenerator")
    private String code;

    @OneToMany(mappedBy = "code", cascade = {CascadeType.MERGE, CascadeType.PERSIST},
    fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy(value = "order")
    private Set<Movie> movies;

    @Override
    @JsonIgnore
    public String getId() {
        return code;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return code == null;
    }
}