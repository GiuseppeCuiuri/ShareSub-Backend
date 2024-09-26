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
@Table(name = "chat", schema = "orders")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;

    @OneToOne
    @JoinColumn(name = "product_hosted")
    private ProductHosted productHosted;

}
