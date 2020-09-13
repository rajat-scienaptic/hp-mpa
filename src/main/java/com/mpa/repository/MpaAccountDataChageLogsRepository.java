package com.mpa.repository;

import com.mpa.model.MpaAccountsDataChangeLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MpaAccountDataChageLogsRepository extends JpaRepository<MpaAccountsDataChangeLogs,Integer>{
    List<MpaAccountsDataChangeLogs> findAllByDataId(int id);
}
