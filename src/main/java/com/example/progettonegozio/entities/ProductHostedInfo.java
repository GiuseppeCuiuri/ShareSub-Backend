package com.example.progettonegozio.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "products_hosted_info", schema = "orders")
public class ProductHostedInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private int id;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;

    @OneToOne
    @JoinColumn(name = "product_hosted")
    private ProductHosted productHosted;

    @Basic
    @Column(name = "login_user_information",nullable = false)
    private String loginUserInformation;

    @Basic
    @Column(name = "login_password_information",nullable = false)
    private String loginPasswordInformation;



}
