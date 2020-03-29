package com.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.domain.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, Long> {

}
