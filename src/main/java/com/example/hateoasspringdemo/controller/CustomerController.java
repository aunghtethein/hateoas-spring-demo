package com.example.hateoasspringdemo.controller;

import com.example.hateoasspringdemo.dao.AddressesDao;
import com.example.hateoasspringdemo.dao.CustomersDao;
import com.example.hateoasspringdemo.ds.Address;
import com.example.hateoasspringdemo.ds.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @GetMapping("/customers/{customerId}/addresses/{addressId}")
    public EntityModel<Address> getAddress(@PathVariable int customerId,@PathVariable int addressId){
        Customer customer=customersDao.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Address customerAddress=customer.getAddresses()
                .stream()
                .filter(address -> address.getId().equals(addressId))
                .findAny()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        return EntityModel.of(customerAddress,linkTo(methodOn(CONTROLLER_CLASS).getAddress(customerId,addressId))
        .withSelfRel(),linkTo(methodOn(CONTROLLER_CLASS).getCustomer(customerId)).withRel("customer"));
    }


    @GetMapping("/customers/{customerId}/addresses")
    public CollectionModel<EntityModel<Address>> listAddresses(@PathVariable int customerId){

        Customer customer = getCustomerById(customerId);
        List<EntityModel<Address>> addresses=customer.getAddresses()
                .stream().map(address -> EntityModel.of(address,linkTo(methodOn(CONTROLLER_CLASS).getAddress(customerId,address.getId())).withSelfRel(),
                         linkTo(methodOn(CONTROLLER_CLASS).getCustomer(address.getCustomer().getId())).withRel("customer")))
                .collect(Collectors.toList());
        return CollectionModel.of(addresses);
    }

    private Customer getCustomerById(int customerId) {
        return customersDao.findById(customerId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    //curl -X POST -H 'Content-Type: application/json' -d '{"code":"CS","firstName":"Cjsr",lastName":"Shre"}' http://localhost:8080/customers
    @PostMapping("/customers")
    public EntityModel<Customer> createCustomer(@RequestBody Customer customers){
        Customer createdCustomer=customersDao.save(customers);

        return EntityModel.of(createdCustomer,
                linkTo(methodOn(CONTROLLER_CLASS).getCustomer(createdCustomer.getId())).withSelfRel());
    }

    //curl -X POST -H 'Content-Type: application/json' -d '{"addressName":"Apartment Address","streetNumber":4232,"streetName":"Love Lane","aptNumber":4,"city":"NewYork","state":"AL","zipCode":"34343"}' http://localhost:8080/customers/4/addresses
    @PostMapping("/customers/{id}/addresses")
    public EntityModel<Address> createAddress(@PathVariable int id,@RequestBody Address address){
        Customer customer = getCustomerById(id);

        address.setCustomer(customer);
        customer.getAddresses().add(address);
        customer.getAddresses().add(address);
        customer=customersDao.save(customer);

        return EntityModel.of(address,
                linkTo(methodOn(CONTROLLER_CLASS).getAddress(customer.getId(),address.getId())).withSelfRel());

    }

}
