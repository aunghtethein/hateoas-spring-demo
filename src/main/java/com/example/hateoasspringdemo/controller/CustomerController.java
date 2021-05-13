package com.example.hateoasspringdemo.controller;

import com.example.hateoasspringdemo.dao.AddressesDao;
import com.example.hateoasspringdemo.dao.CustomersDao;
import com.example.hateoasspringdemo.ds.Address;
import com.example.hateoasspringdemo.ds.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@RestController
public class CustomerController {

    public static final Class<CustomerController> CONTROLLER_CLASS=CustomerController.class;
    @Autowired
    private CustomersDao customersDao;
    @Autowired
    private AddressesDao addressesDao;

    @GetMapping("/customers/{id}")
    public EntityModel<Customer> getCustomer(@PathVariable int id){
        Optional<Customer> customer = customersDao.findById(id);
        if (!customer.isPresent()){
            throw new EntityNotFoundException("Id-"+id);
        }
        EntityModel<Customer> resource=EntityModel.of(customer.get());
        resource.add(linkTo(methodOn(CONTROLLER_CLASS).getCustomer(id)).withSelfRel());
        resource.add(linkTo(methodOn(CONTROLLER_CLASS).getCustomer(id)).withRel("customer"));
        resource.add(linkTo(methodOn(CONTROLLER_CLASS).listAddresses(id)).withRel("addresses"));
        return resource;
    }

    @GetMapping("/customers")
    public CollectionModel<EntityModel<Customer>> listCustomers(){
        List<EntityModel<Customer>> customerEntityModel=
                StreamSupport.stream(customersDao.findAll().spliterator(),false)
                .map(cus -> EntityModel.of(cus,linkTo(methodOn(CONTROLLER_CLASS).getCustomer(cus.getId())).withSelfRel(),
                        linkTo(methodOn(CONTROLLER_CLASS).getCustomer(cus.getId())).withRel("customer"),
                        linkTo(methodOn(CONTROLLER_CLASS).listAddresses(cus.getId())).withRel("addresses")))
                .collect(Collectors.toList());
        Link customersLink = linkTo(methodOn(CONTROLLER_CLASS).listCustomers()).withSelfRel();
        return CollectionModel.of(customerEntityModel,customersLink);
    }

    public CollectionModel<EntityModel<Address>> listAddresses(@PathVariable int customerId){



        return null;
    }

}
