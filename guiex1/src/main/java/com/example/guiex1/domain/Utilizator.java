package com.example.guiex1.domain;

import com.example.guiex1.Enum.Status;

import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private List<Tuple<Utilizator,Status>> friends;
    private byte[] cale_imagine;

    public Utilizator(){}

    public Utilizator(String firstName, String lastName , String username ,String password,byte[] cale_imagine) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cale_imagine = cale_imagine;
    }

    public Utilizator(String firstName, String lastName, String username ,String password, List<Tuple<Utilizator, Status>> friends, byte[] cale_imagine) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.friends = friends;
        this.password = password;
        this.cale_imagine = cale_imagine;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Tuple<Utilizator,Status>> getFriends() {
        return friends;
    }

    public void setFriends(List<Tuple<Utilizator,Status>> friends) {
        this.friends = friends;
    }

    public byte[] getCale_imagine() {
        return cale_imagine;
    }

    public void setCale_imagine(byte[] cale_imagine) {
        this.cale_imagine = cale_imagine;
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", friends=" + friends +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}