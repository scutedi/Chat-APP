package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.*;
import com.example.guiex1.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDbRepository implements Repository<Long, Message> {

    private String url;
    private String username;
    private String password;

    public MessageDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Optional<Message> delete(Long id) {
        return Optional.empty();
    }

    public Optional<Message> update(Message m) {
        return Optional.empty();
    }


    public Optional<Message> findOne(Long id) {
        Message msg;
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE id1 = ?")){
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                msg = createMessageFromResultSet(resultSet);
                return Optional.ofNullable(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        return null;
    }

    public Message findChat(Long id1 , Long id2) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM message WHERE ((FROM1 = ? AND TO1 = ?) OR (TO1 = ? AND FROM1 = ?)) and reply_to is not NULL"
             )) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id1);
            statement.setLong(4, id2);

            ResultSet resultSet = statement.executeQuery();
            Message mes = new Message();
            while(resultSet.next()) {
                 mes = createMessageFromResultSet(resultSet);
            }
            return mes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Iterable<Message>findConvo(Long id1, Long id2) {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM message WHERE ((FROM1 = ? AND TO1 = ?) OR (TO1 = ? AND FROM1 = ?))"
             )) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id1);
            statement.setLong(4, id2);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Message message = createMessageFromResultSet(resultSet);
                if(message.getReply()!=null){
                    Message reply = findOne(message.getReply().getId()).get();
                    message.setReply(reply);
                }
                messages.add(message);
            }
            System.out.println();
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }


    private Message createMessageFromResultSet(ResultSet resultSet) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementU1 = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
             PreparedStatement statementU2 = connection.prepareStatement("SELECT * FROM users WHERE id = ?");)
        {
            statementU1.setLong(1, resultSet.getLong("FROM1"));
            statementU2.setLong(1, resultSet.getLong("TO1"));
            ResultSet resultSetU1 = statementU1.executeQuery();
            ResultSet resultSetU2 = statementU2.executeQuery();
            Long id = resultSet.getLong("id1");

            Utilizator u1 = null;
            Utilizator u2 = null;

            if(resultSetU1.next()) {
                u1 = new Utilizator(resultSetU1.getString("first_name"), resultSetU1.getString("last_name") , resultSetU1.getString("username") , resultSetU1.getString("password") , resultSetU1.getBytes("imagine"));
                u1.setId(resultSetU1.getLong("id"));
            }
            if(resultSetU2.next()) {
                u2 = new Utilizator(resultSetU2.getString("first_name"), resultSetU2.getString("last_name") , resultSetU1.getString("username") , resultSetU1.getString("password"), resultSetU1.getBytes("imagine"));
                u2.setId(resultSetU2.getLong("id"));
            }
            Message replyMsg = new Message();
            //Long id2 = resultSet.getLong("id2");
            Timestamp date = resultSet.getTimestamp("data1");
            LocalDateTime localDateTime = date.toLocalDateTime();
            Long reply = resultSet.getLong("reply_to");
            if(resultSet.wasNull())
                reply = null;
            else{
                replyMsg.setId(reply);
            }
            //UtilizatorDbRepository ut = new UtilizatorDbRepository(url, username , password, new UtilizatorValidator());
            String text = resultSet.getString("message1");
            Message msg = new Message(u1, List.of(u2),text,localDateTime,reply==null ? null : replyMsg);
            msg.setId(id);
            return msg;
        } catch (SQLException e) {
            return null;
        }
    }

    public Message findReplier(Long id){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE reply_to = ?"))
        {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return createMessageFromResultSet(resultSet);
            }
            return null;
        }
        catch (SQLException e) {
            return null;
        }
    }



    @Override
    public Optional<Message> save(Message entity) {
        entity.getTo().forEach(user->{
            String sql = "insert into MESSAGE (from1, to1,message1,data1,reply_to) values (?, ?,?,?,?)";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setLong(1, entity.getFrom().getId());
                ps.setLong(2, user.getId());
                ps.setString(3, entity.getMessage());
                ps.setTimestamp(4, Timestamp.valueOf(entity.getDate()));


                if(entity.getReply()!=null){
                    ps.setLong(5, entity.getReply().getId());
                }
                else{
                    ps.setNull(5, Types.INTEGER);
                }
                ps.executeUpdate();
                ResultSet resultSet = ps.getGeneratedKeys();
                resultSet.next();
                entity.setId(resultSet.getLong(1));
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        });
        return Optional.of(entity);
    }
}