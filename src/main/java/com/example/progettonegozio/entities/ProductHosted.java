package com.example.progettonegozio.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "products_hosted", schema = "orders")
public class ProductHosted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private int id;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;

    @OneToOne
    @JoinColumn(name = "product_associated")
    private Product productAssociated;

    @OneToOne
    @JoinColumn(name = "hosted_by")
    private User hostedBy;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "hosting_start",nullable = false)
    private Date hostingStartDate;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_renew",nullable = false)
    private Date lastRenewDate;

    @Basic
    @Column(name = "continueToHost",nullable = false)
    private boolean continueToHost;

    @Basic
    @Column(name = "sub_available",nullable = false)
    private boolean subAvailable;

    @Basic
    @Column(name = "sub_users",nullable = false)
    private int subUsers;




}
