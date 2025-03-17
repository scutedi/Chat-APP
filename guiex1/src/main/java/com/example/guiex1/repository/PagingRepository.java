package com.example.guiex1.repository;

import com.example.guiex1.domain.Entity;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.repository.dbrepo.Pageable;

public interface PagingRepository<ID, E extends Entity<Tuple<ID,ID>>> extends Repository<Tuple<ID,ID>, E> {
    Page<E> findAllonPage(Utilizator u, Pageable pageable);
}
