package com.mpa.repository;

import com.mpa.model.CA_QPP;
import com.mpa.model.US_QPP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CAQPPRepository extends JpaRepository<CA_QPP, Integer> {
    @Query(value = "Select c.companyLegalName from CA_QPP c where c.companyLegalName like :companyLegalName%")
    List<String> getMatchingQPP(String companyLegalName);

    @Query(value = "Select * from ca_qpp where BINARY company_legal_name like BINARY :companyLegalName", nativeQuery = true)
    CA_QPP findDataByAccount(String companyLegalName);
}
