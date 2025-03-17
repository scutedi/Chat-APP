package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.repository.Page;
import com.example.guiex1.repository.PagingRepository;
import jdk.jshell.execution.Util;

import java.sql.Connection;
import java.util.List;

public interface PrietenieRepository extends PagingRepository<Long, Prietenie> {
    Page<Prietenie> findAllonPage(Utilizator u, Pageable pageable);

}
