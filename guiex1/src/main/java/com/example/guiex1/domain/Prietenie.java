package com.example.guiex1.domain;

import com.example.guiex1.domain.Entity;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.Enum.Status;

import java.time.LocalDateTime;
import java.util.Objects;

public class Prietenie extends Entity<Tuple<Long,Long>> {
    private final LocalDateTime date;
    private Status status = Status.PENDING;

    /***
     * Constructor Prietenie fara data
     * @param userId1 id utilizator nr 1
     * @param userId2 id utilizator nr 2
     */
    public Prietenie(Long userId1, Long userId2) {
        this.date = LocalDateTime.now();
        Tuple<Long,Long> id = new Tuple<>(userId1, userId2);
        this.setId(id);
    }

    /***
     * Constructor Prietenie cu data pentru scrierea din fisier
     * @param userId1 id utilizator nr 1
     * @param userId2 id utilizator nr 2
     */
    public Prietenie(Long userId1, Long userId2, LocalDateTime date) {
        this.date = date;
        Tuple<Long,Long> id = new Tuple<>(userId1, userId2);
        this.setId(id);
    }

    public Prietenie(Long userId1, Long userId2, LocalDateTime date, Status status) {
        this.date = date;
        Tuple<Long,Long> id = new Tuple<>(userId1, userId2);
        this.setId(id);
        this.status = status;
    }


    public LocalDateTime getDate() {
        return date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prietenie that = (Prietenie) o;
        return Objects.equals(date, that.date) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(date);
    }

    @Override
    public String toString() {
        return this.getId().getLeft().toString() + " " + this.getId().getRight().toString() + " " + this.getDate().toString();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

