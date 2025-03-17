package com.example.guiex1.services;


import com.example.guiex1.Enum.Status;
import com.example.guiex1.controller.ChatController;
import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.repository.Page;
import com.example.guiex1.repository.dbrepo.MessageDbRepository;
import com.example.guiex1.repository.dbrepo.Pageable;
import com.example.guiex1.repository.dbrepo.PrietenieDbRepository;
import com.example.guiex1.repository.dbrepo.UtilizatorDbRepository;
import com.example.guiex1.utils.events.ChangeEventType;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.MessageEntityChangeEvent;
import com.example.guiex1.utils.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.utils.observer.Observable;
import com.example.guiex1.utils.observer.Observer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorService implements Observable<Event> {
    private final UtilizatorDbRepository repo;
    private final PrietenieDbRepository repoFriend;
    private final MessageDbRepository repoMessage;
    private List<Observer<Event>> observers=new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public UtilizatorService(UtilizatorDbRepository repo, PrietenieDbRepository repoFriend , MessageDbRepository messageDbRepository) {
        this.repo = repo;
        this.repoFriend = repoFriend;
        this.repoMessage = messageDbRepository;
    }


    public Utilizator addUtilizator(Utilizator user) {
        if(repo.save(user).isEmpty()){
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, user);
            notifyObservers(event);
            return null;
        }
        return user;
    }

    public Utilizator deleteUtilizator(Long id){
        Optional<Utilizator> user = repo.delete(id);
        if (user.isPresent()) {
            notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, user.get()));
            return user.get();
        }
        return null;
    }

    public Page<Prietenie> getAllFriendshipsPage(Utilizator u , Pageable p)
    {
        return repoFriend.findAllonPage(u, p);
    }

    public List<Utilizator> getPrieteni(Utilizator U, Page<Prietenie> p){
        List<Prietenie> prieteni = StreamSupport.stream(p.getElementsOnPage().spliterator(),
                false).collect(Collectors.toList());

        List<Utilizator> users = StreamSupport.stream(prieteni.spliterator(), false)
                .map(u -> !u.getId().getLeft().equals(U.getId())
                        ? repo.findOne(u.getId().getLeft()).get()
                        : repo.findOne(u.getId().getRight()).get())
                .collect(Collectors.toList());
        return users;
    }

    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }

    public Prietenie addFriendship(Prietenie p){
        Prietenie friendship = repoFriend.findOne(p.getId()).orElse(null);
        if(friendship != null && friendship.getStatus().equals(Status.PENDING) && p.getId().getLeft().equals(friendship.getId().getRight())){
            repoFriend.update(new Prietenie(friendship.getId().getLeft(),friendship.getId().getRight(),friendship.getDate(), Status.APPROVED));
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, null);
            notifyObservers(event);
            return null;
        }
        if(repoFriend.save(p).isEmpty()){
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.FRIEND, null);
            notifyObservers(event);
            return null;
        }
        return p;
    }


    public List<Message> getAllMessages(Utilizator u1 , Utilizator u2) {
        Iterable<Message> messages1 = repoMessage.findConvo(u1.getId(), u2.getId());
        List<Message> sortedMessages = StreamSupport.stream(messages1.spliterator(), false)
                .sorted(Comparator.comparing(Message::getData))
                .toList();
        return sortedMessages;

    }

    public Message addMessage(Message message){
        Optional<Message> savedMessage = repoMessage.save(message);
        if(savedMessage.isPresent()){
            MessageEntityChangeEvent event = new MessageEntityChangeEvent(ChangeEventType.ADD, message);
            notifyObservers(event);
            return savedMessage.get();
        }
        return message;
    }

    public Prietenie searchFriendship(Tuple<Long,Long> id){
        return repoFriend.findOne(id).orElse(null);
    }

   public Prietenie deleteFriendship(Tuple<Long,Long> id){
        Optional<Prietenie> friend = this.repoFriend.delete(id);
        if (friend.isPresent()) {
            notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, null));
            return friend.get();
        }
        return null;
    }

    public Utilizator searchUser(Long id){
        return repo.findOne(id).orElse(null);
    }

    /*public Message searchChat(Long id1 , Long id2){
        return repoMessage.findChat(id1 , id2);
    }*/

    public Utilizator searchName(String firstName, String lastName){
        List<Utilizator> list = StreamSupport.stream(repo.findAll().spliterator(),false)
                .filter(u->u.getFirstName().equals(firstName)&&u.getLastName().equals(lastName))
                .collect(Collectors.toList());
        if(list.isEmpty()){
            return null;
        }
        else return list.get(0);
    }

    public List<Utilizator> usersFiltru(){
        Iterable<Utilizator> messages = getAll();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());

        return users;
    }

    public List<Utilizator> prieteniPending(Utilizator utilizator){
        Iterable<Tuple<Utilizator,Status>> messages = utilizator.getFriends();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .filter(u-> Status.PENDING.equals(u.getRight())).map(u->u.getLeft())
                .collect(Collectors.toList());

        return users;
    }

    public List<Utilizator> prieteniAcceptati(Utilizator utilizator){
        Iterable<Tuple<Utilizator,Status>> messages = utilizator.getFriends();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .filter(u-> Status.APPROVED.equals(u.getRight())).map(u->u.getLeft())
                .collect(Collectors.toList());

        return users;
    }

    public int nrPrieteniAcceptati(Utilizator utilizator){
        Iterable<Tuple<Utilizator,Status>> messages = utilizator.getFriends();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .filter(u-> Status.APPROVED.equals(u.getRight())).map(u->u.getLeft())
                .collect(Collectors.toList());


        return users.size();
    }

    public List<Utilizator> filtruPrietenieNeadaugati(Utilizator utilizator1){
        Iterable<Utilizator> messages = getAll();
        Iterable<Tuple<Utilizator, Status>> messages1 = utilizator1.getFriends();


        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .filter(utilizator -> StreamSupport.stream(messages1.spliterator(), false)
                        .noneMatch(tuple ->
                                Objects.equals(utilizator.getId(), tuple.getLeft().getId()) ||
                                        Objects.equals(utilizator1.getId(), utilizator.getId())
                        )
                        && !(Objects.equals(utilizator1.getId(), utilizator.getId()))
                )
                .collect(Collectors.toList());

        return users;
    }

    public Utilizator searchUserName(String UserName){
        List<Utilizator> list = StreamSupport.stream(repo.findAll().spliterator(),false)
                .filter(u->u.getUsername().equals(UserName))
                .collect(Collectors.toList());
        if(list.isEmpty()){
            return null;
        }
        else return list.get(0);
    }

    public Iterable<Prietenie> getAllFriends(){
        return repoFriend.findAll();
    }


    @Override
    public void addObserver(Observer<Event> e) {
        observers.add(e);

    }


    @Override
    public void removeObserver(Observer<Event> e) {
        //observers.remove(e);
    }



    @Override
    public void notifyObservers(Event t) {


    }

    public Utilizator updateUtilizator(Utilizator u) {
        Optional<Utilizator> oldUser=repo.findOne(u.getId());
        if(oldUser.isPresent()) {
            Optional<Utilizator> newUser=repo.update(u);
            if (newUser.isEmpty())
                notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, u, oldUser.get()));
            return newUser.orElse(null);
        }
        return oldUser.orElse(null);
    }


}
