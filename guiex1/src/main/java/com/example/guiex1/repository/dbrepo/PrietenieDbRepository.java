package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.Enum.Status;
import com.example.guiex1.domain.*;
import com.example.guiex1.repository.Page;
import com.example.guiex1.repository.Repository;
import jdk.jshell.execution.Util;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PrietenieDbRepository implements PrietenieRepository{

    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;

    public PrietenieDbRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     * @param id - long, the id of a user to found
     * @return Optional<User> - the user with the given id
     *                        -Optional.empty() otherwise
     */
    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> id) {
        Prietenie friendship;
        Long id1 = id.getLeft();
        Long id2 = id.getRight();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            ResultSet resultSet = connection.createStatement().executeQuery(String.format("select * from PRIETENIE where id1 = '%d' and id2 = '%d' or id1='%d' and id2='%d'", id1,id2,id2,id1))) {
            if(resultSet.next()){
                friendship = createFriendFromResultSet(resultSet);
                return Optional.ofNullable(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    private Prietenie createFriendFromResultSet(ResultSet resultSet) {
        try {
            Long id1 = resultSet.getLong("id1");
            Long id2 = resultSet.getLong("id2");
            Timestamp date = resultSet.getTimestamp("f_date");
            String status = resultSet.getString("status");
            LocalDateTime localDateTime = date.toLocalDateTime();
            Status status1 = Status.valueOf(status);
            Prietenie friendship = new Prietenie(id1, id2, localDateTime,status1);
            return friendship;
        } catch (SQLException e) {
            return null;
        }
    }


    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from PRIETENIE");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Prietenie prietenie = createFriendFromResultSet(resultSet);
                friendships.add(prietenie);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    private Prietenie getFriendshipFromStatement(ResultSet resultSet) throws SQLException
    {
        Long id_user1 = resultSet.getLong("id1");
        Long id_user2 = resultSet.getLong("id2");
        Timestamp friendsfrom = resultSet.getTimestamp("f_date");
        Prietenie friendship = new Prietenie(id_user1, id_user2, friendsfrom.toLocalDateTime());
        return friendship;
    }

    @Override
    public Page<Prietenie> findAllonPage(Utilizator u, Pageable pageable)
    {
        String findAllPageStatement="SELECT * FROM PRIETENIE WHERE (id1 = ? OR id2 = ?) AND status = 'APPROVED' LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) AS count FROM PRIETENIE";
        List<Prietenie> prietenii = new ArrayList<>();
        try
        {
            PreparedStatement statement = DriverManager.getConnection(url,username,password).prepareStatement(findAllPageStatement);
            statement.setLong(1, u.getId());
            statement.setLong(2, u.getId());
            statement.setInt(3, pageable.getPageSize());
            statement.setInt(4, pageable.getPageSize() * pageable.getPageNumber());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
                prietenii.add(getFriendshipFromStatement(resultSet));

            PreparedStatement statementcount = DriverManager.getConnection(url,username,password).prepareStatement(count);
            ResultSet resultSetCount = statementcount.executeQuery();
            int totalCount = 0;
            if(resultSetCount.next()) {
                totalCount = resultSetCount.getInt("count");
            }

            return new Page<>(prietenii, totalCount);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        String sql = "insert into PRIETENIE (id1, id2,f_date,status) values (?, ?,?,?)";
        validator.validate(entity);
        Tuple<Long , Long> id = new Tuple<>(entity.getId().getLeft() , entity.getId().getRight());
        if(findOne(id).isPresent())
            return Optional.of(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(entity.getDate()));
            ps.setString(4, entity.getStatus().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
            return Optional.of(entity);
        }
        return Optional.empty();
    }


    @Override
    public Optional<Prietenie> delete(Tuple<Long,Long> id) {
        Long id1 = id.getLeft();
        Long id2 = id.getRight();
        String sql = "delete from PRIETENIE where id1 = ? and id2 = ? or id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            Optional<Prietenie> friendship = findOne(id);
            if(!friendship.isEmpty()) {
                ps.setLong(1, id1);
                ps.setLong(2, id2);
                ps.setLong(3,id2);
                ps.setLong(4,id1);
                ps.executeUpdate();
            }
            return friendship;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie friendship) {
        if(friendship == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(friendship);
        Long id1 = friendship.getId().getLeft();
        Long id2 = friendship.getId().getRight();
        Status status = friendship.getStatus();
        String sql = "update PRIETENIE set status = ? where id1 = ? and id2 = ? or id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toString());
            ps.setLong(2, id1);
            ps.setLong(3, id2);
            ps.setLong(4,id2);
            ps.setLong(5,id1);
            if( ps.executeUpdate() > 0 )
                return Optional.empty();
            return Optional.ofNullable(friendship);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
