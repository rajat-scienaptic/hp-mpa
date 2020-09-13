package com.mpa.repository;

import com.mpa.model.US_QPP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface USQPPRepository extends JpaRepository<US_QPP, Integer> {
    @Query(value = "Select u.account from US_QPP u where u.account like :account%")
    List<String> getMatchingQPP(String account);

    @Query(value = "Select * from us_qpp where BINARY account like BINARY :account", nativeQuery = true)
    US_QPP findDataByAccount(String account);
}
