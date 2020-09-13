package com.mpa.repository;

import com.mpa.model.MEXICO_QPP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MEXICOQPPRepository extends JpaRepository<MEXICO_QPP, Integer> {
    @Query(value = "Select m.account from MEXICO_QPP m where m.account like :account%")
    List<String> getMatchingQPP(String account);

    @Query(value = "Select m.account from MEXICO_QPP m where m.country = :country and m.account like :account%")
    List<String> getMatchingQPPByCountry(String country, String account);

    @Query(value = "Select * from mexico_qpp where BINARY account like BINARY :account limit 1", nativeQuery = true)
    MEXICO_QPP findDataByAccount(String account);
}
