package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.Enum.Status;
import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.Validator;
import com.example.guiex1.repository.Repository;
import jdk.jshell.execution.Util;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.guiex1.utils.Hashing.hashPassword;

public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private String url;
    private String username1;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDbRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username1 = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     * @param id - long, the id of a user to found
     * @return Optional<User> - the user with the given id
     *                        -Optional.empty() otherwise
     */
    @Override
    public Optional<Utilizator> findOne(Long id) {
        Utilizator user;
        try(Connection connection = DriverManager.getConnection(url, username1, password);
            ResultSet resultSet = connection.createStatement().executeQuery(String.format("select * from users U where U.id = '%d'", id))) {
            if(resultSet.next()){
                user = createUserFromResultSet(resultSet);
                List<Tuple<Utilizator,Status>> friends = new ArrayList<>();
                PreparedStatement statementF = connection.prepareStatement(
                        "select id,first_name,last_name,username,password,imagine,status from PRIETENIE f\n" +
                                "join users u on u.id != ? and (u.id = f.id1 or u.id = f.id2)\n" +
                                "where id1 = ? or id2 = ?"
                );
                statementF.setLong(1, id);
                statementF.setLong(2, id);
                statementF.setLong(3, id);
                ResultSet resultSetF = statementF.executeQuery();
                while (resultSetF.next()) {
                    Utilizator utilizator = createUserFromResultSet(resultSetF);
                    String status = resultSetF.getString("status");
                    Status status1 = Status.valueOf(status);
                    Tuple<Utilizator, Status> friend = new Tuple<>(utilizator, status1);
                    friends.add(friend);
                }
                user.setFriends(friends);
                return Optional.ofNullable(user);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    private Utilizator createUserFromResultSet(ResultSet resultSet) {
        try {
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            byte[] cale_imagine = resultSet.getBytes("imagine");

            Long idd = resultSet.getLong("id");
            Utilizator user = new Utilizator(firstName, lastName , username , password,cale_imagine);
            user.setId(idd);
            return user;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username1, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Utilizator user = createUserFromResultSet(resultSet);
                Long id = user.getId();
                List<Tuple<Utilizator,Status>> friends = new ArrayList<>();
                PreparedStatement statementF = connection.prepareStatement(
                        "select id,first_name,last_name,username,password,imagine,status from PRIETENIE f\n" +
                                "join users u on u.id != ? and (u.id = f.id1 or u.id = f.id2)\n" +
                                "where id1 = ? or id2 = ?"
                );
                statementF.setLong(1, id);
                statementF.setLong(2, id);
                statementF.setLong(3, id);
                ResultSet resultSetF = statementF.executeQuery();
                while (resultSetF.next()) {
                    Utilizator utilizator = createUserFromResultSet(resultSetF);
                    String status = resultSetF.getString("status");
                    Status status1 = Status.valueOf(status);
                    Tuple<Utilizator,Status> friend = new Tuple<>(utilizator, status1);
                    friends.add(friend);
                }
                user.setFriends(friends);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        String sql = "insert into users (first_name, last_name , username , password , imagine) values (?, ? , ? ,? , ?)";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username1, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getUsername());

            String hashedPassword = hashPassword(entity.getPassword());

            ps.setString(4, hashedPassword);
            ps.setBytes(5, entity.getCale_imagine());

            ps.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> delete(Long id) {
        String sql = "delete from users where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username1, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            Optional<Utilizator> user = findOne(id);
            if(!user.isEmpty()) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> update(Utilizator user) {
        if(user == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(user);
        String sql = "update users set first_name = ?, last_name = ?, username = ?, password = ? , imagine = ? where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username1, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1,user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPassword());
            ps.setBytes(5, user.getCale_imagine());
            ps.setLong(6, user.getId());
            if( ps.executeUpdate() > 0 )
                return Optional.empty();
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
