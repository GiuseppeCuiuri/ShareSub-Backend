package com.example.progettonegozio.entities;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="user",schema="orders")

public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id",nullable = false)
    private int id;

    @Basic
    @Column(name="first_name",nullable = false)
    private String firstName;

    @Basic
    @Column(name="last_name",nullable = false)
    private String lastName;

    //un utente è identificato dall'email
    @Basic
    @Column(name="email",nullable = false,unique = true)
    private String email;

    @Basic
    @Column(name="balance",nullable = false)
    private double balance;


    //costruttori
    public User(String email,String firstName,String lastName){
        this.email=email;
        this.firstName=firstName;
        this.lastName=lastName;
        this.balance=0;
    }
    public User(){}
}
