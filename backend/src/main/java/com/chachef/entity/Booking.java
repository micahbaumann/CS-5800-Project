package com.chachef.entity;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Booking {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)   // store as CHAR
    @Column(name = "booking_id", length = 50, updatable = false, nullable = false)
    private UUID bookingId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "chef_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Chef chef;

    @Column(name = "start", nullable = false)
    public LocalDateTime start;

    @Column(name = "end", nullable = false)
    public LocalDateTime end;

    @Column(name = "address", nullable = false, length = 500)
    public String address;

    @Column(name = "status", nullable = false)
    public String status = "Pending";

    public Booking() {}

    public Booking(User user, Chef chef, LocalDateTime start, LocalDateTime end, String address, String status) {
        this.user = user;
        this.chef = chef;
        this.start = start;
        this.end = end;
        this.address = address;
        this.status = status;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
