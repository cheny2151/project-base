package com.cheney.service.mongo.impl;

import cn.cheny.toolbox.other.order.Orders;
import cn.cheny.toolbox.other.page.Page;
import cn.cheny.toolbox.other.page.Pageable;
import com.cheney.service.mongo.BaseMongoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * mongo service base impl
 *
 * @Date 2021/3/2
 * @Created by chenyi
 */
public class BaseMongoServiceImpl<T, ID> implements BaseMongoService<T, ID> {

    private final MongoRepository<T, ID> repository;

    @Resource
    private MongoTemplate mongoTemplate;

    public BaseMongoServiceImpl(MongoRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public MongoTemplate getTemplate() {
        return mongoTemplate;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> iterable) {
        if (iterable != null) {
            repository.saveAll(iterable);
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(Orders orders) {
        return repository.findAll(ordersToSort(orders));
    }

    @Override
    public <S extends T> S insert(S entity) {
        return repository.insert(entity);
    }

    @Override
    public <S extends T> List<S> insert(Iterable<S> iterable) {
        return repository.insert(iterable);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return repository.findAll(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Orders orders) {
        return repository.findAll(example, ordersToSort(orders));
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return repository.findOne(example);
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return repository.count(example);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return repository.exists(example);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        org.springframework.data.domain.Page<T> page = repository.findAll(coverPageable(pageable, null));
        return coverPage(page);
    }

    @Override
    public Page<T> findAll(Pageable pageable, Orders orders) {
        org.springframework.data.domain.Page<T> page = repository.findAll(coverPageable(pageable, orders));
        return coverPage(page);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable, Orders orders) {
        org.springframework.data.domain.Page<S> page = repository.findAll(example, coverPageable(pageable, orders));
        return coverPage(page);
    }

    private Sort ordersToSort(Orders orders) {
        if (orders != null && orders.size() > 0) {
            Sort sort = null;
            for (Orders.Order o : orders) {
                Sort by = Sort.by(Orders.OrderType.asc.equals(o.getType()) ? Sort.Direction.ASC : Sort.Direction.DESC, o.getProperty());
                if (sort == null) {
                    sort = by;
                } else {
                    sort.and(by);
                }
            }
            return sort;
        } else {
            return Sort.unsorted();
        }
    }

    private org.springframework.data.domain.Pageable coverPageable(Pageable pageable, Orders orders) {
        Sort sort = ordersToSort(orders);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private <P> Page<P> coverPage(org.springframework.data.domain.Page<P> page) {
        List<P> content = page.getContent();
        int number = page.getNumber();
        int size = page.getSize();
        long total = page.getTotalElements();
        return Page.build(total, size, number, content);
    }
}
