package com.cheney.service.mongo;

import cn.cheny.toolbox.other.order.Orders;
import cn.cheny.toolbox.other.page.Page;
import cn.cheny.toolbox.other.page.Pageable;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

/**
 * @Date 2021/3/2
 * @Created by chenyi
 */
public interface BaseMongoService<T, ID> {

    MongoTemplate getTemplate();

    <S extends T> List<S> saveAll(Iterable<S> iterable);

    List<T> findAll();

    List<T> findAll(Orders orders);

    <S extends T> S insert(S entity);

    <S extends T> List<S> insert(Iterable<S> iterable);

    <S extends T> List<S> findAll(Example<S> example);

    <S extends T> List<S> findAll(Example<S> example, Orders orders);

    <S extends T> Optional<S> findOne(Example<S> example);

    <S extends T> long count(Example<S> example);

    <S extends T> boolean exists(Example<S> example);

    Page<T> findAll(Pageable pageable);

    Page<T> findAll(Pageable pageable, Orders orders);

    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable, Orders orders);
}
