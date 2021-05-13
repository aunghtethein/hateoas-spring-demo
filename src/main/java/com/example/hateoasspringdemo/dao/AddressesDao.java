package com.example.hateoasspringdemo.dao;


import com.example.hateoasspringdemo.ds.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressesDao extends CrudRepository<Address,Integer> {
}
