package com.example.progettonegozio.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "subscription", schema = "orders")
public class Subscription {

    //Every subscription contain : id, user and a productHosted

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version; //lock ottimistico

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne
    @JoinColumn(name = "product_hosted_info")
    private ProductHostedInfo productHostedInfo;

    //boolean field to check if you want to renew the subscription
    @Basic
    @Column(name = "renew", nullable = false)
    private boolean renew;

}
