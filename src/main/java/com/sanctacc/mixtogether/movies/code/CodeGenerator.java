package com.sanctacc.mixtogether.movies.code;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Properties;
import java.util.stream.Collectors;

public class CodeGenerator implements IdentifierGenerator, Configurable {

    private WordsProvider wordsProvider;

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
        wordsProvider = new WordsProvider();
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return wordsProvider.randomize(4).stream().map(StringUtils::capitalize).collect(Collectors.joining());
    }
}