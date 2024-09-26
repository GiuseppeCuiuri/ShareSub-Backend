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
@Table(name = "product", schema = "orders")

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version; //lock ottimistico

    @Basic
    @Column(name = "name", nullable = false)
    private String name;

    @Basic
    @Column(name="price",nullable = false)
    private Double price;

    @Basic
    @Column(name = "description", nullable = false)
    private String description;

    @Basic
    @Column(name="url_image",nullable = true,length = 500)
    @ToString.Exclude
    private String urlImage;

    @Basic
    @Column(name = "renew_rate",nullable = false)
    private int renewRate;

    @Basic
    @Column(name = "price_per_user",nullable = false)
    private Double pricePerUser;

    @Basic
    @Column(name = "max_users",nullable = false)
    private int maxUsers;

    @Basic
    @Column(name="type", nullable = true)
    private Type type;

    public enum Type{
        Musica, Film, Libro, Videogioco, IA
    }


}
