package com.example.hateoasspringdemo.dao;


import com.example.hateoasspringdemo.ds.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomersDao extends CrudRepository<Customer,Integer> {
}
