package com.chachef.entity;

import jakarta.persistence.*;

@Entity
public class Chef {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="chef_id")
    private int id;

    public String name;
}
