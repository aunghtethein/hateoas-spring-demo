package com.example.hateoasspringdemo.ds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Address {      //one customer has many addresses
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String addressName;
    private Integer streetNumber;
    private String streetName;
    private Integer aptNumber;
    private String city;
    private String state;
    private String zipCode;

    @ManyToOne(fetch = FetchType.EAGER)      //many is owner and mappedBy is reversed owner
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", addressName='" + addressName + '\'' +
                ", streetNumber=" + streetNumber +
                ", streetName='" + streetName + '\'' +
                ", aptNumber=" + aptNumber +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }

    public Address() {
    }
}
