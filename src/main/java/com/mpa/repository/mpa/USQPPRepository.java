package com.mpa.repository.mpa;

import com.mpa.model.mpa.UsQPP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface USQPPRepository extends JpaRepository<UsQPP, Integer> {
    @Query(value = "Select case when u.locationId is null then u.account else concat(u.account, '|', u.locationId) " +
            "end from UsQPP u where u.account like :account% and " +
            "((u.qualifiedPrintStatus = 'Qualified' or u.qualifiedPrintStatus = 'Conditional Qualified') " +
            "or (u.qualifiedSuppliesStatus = 'Qualified' or u.qualifiedSuppliesStatus = 'Conditional Qualified'))")
    List<String> getMatchingQPP(String account);

    UsQPP findDataByAccountAndLocationId(String account, String locationId);
    UsQPP findTopDataByAccount(String account);
}
