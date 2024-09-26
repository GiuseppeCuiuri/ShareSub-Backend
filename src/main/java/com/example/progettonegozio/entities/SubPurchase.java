package com.example.progettonegozio.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sub_purchase", schema = "orders")
public class SubPurchase{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version; //lock ottimistico

    @Basic
    @Column(name = "price", nullable = false)
    private double price;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "purchase_time")
    private Date purchaseTime;

    @ManyToOne
    @JoinColumn(name = "buyer")
    @JsonIgnore
    private User buyer;


    @OneToOne
    @JoinColumn(name = "product")
    private Product product;
}
