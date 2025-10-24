package com.chachef.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
public class Chef {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)   // store as CHAR
    @Column(name = "chef_id", length = 50, updatable = false, nullable = false)
    private UUID chefId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "price", nullable = false)
    public double price;

    @Column(name = "listing_name", nullable = false)
    public String listingName;

    public Chef() {}

    public Chef(User user, double price, String listingName) {
        this.user = user;
        this.price = price;
        this.listingName = listingName;
    }

    public UUID getChefId() {
        return chefId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getListingName() {
        return listingName;
    }

    public void setListingName(String listingName) {
        this.listingName = listingName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
