package com.sanctacc.mixtogether.movies;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sanctacc.mixtogether.movies.code.Code;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder()
@Data
@NoArgsConstructor
@AllArgsConstructor
//@Table(uniqueConstraints =
//        @UniqueConstraint(columnNames = {"code_code", "order_specifier"}),
//        indexes = @Index(columnList = "code_code")
//)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String url;

    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="code")
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne(optional = false)
    private Code code;

    @Column(name = "order_specifier")
    private Integer order;

    public void decreaseOrder() {
        order--;
    }
}