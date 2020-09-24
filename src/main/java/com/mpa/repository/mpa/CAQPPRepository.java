package com.mpa.repository.mpa;

import com.mpa.model.mpa.CaQPP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CAQPPRepository extends JpaRepository<CaQPP, Integer> {
    @Query(value = "Select case when c.locationId is null then c.companyLegalName " +
            "else concat(c.companyLegalName, '|', c.locationId) end from CaQPP c " +
            "where c.companyLegalName like :companyLegalName% and " +
            "((c.qualifiedPrintStatus = 'Qualified' or c.qualifiedPrintStatus = 'Conditional Qualified') " +
            "or (c.qualifiedSuppliesStatus = 'Qualified' or c.qualifiedSuppliesStatus = 'Conditional Qualified'))")
    List<String> getMatchingQPP(String companyLegalName);

    CaQPP findDataByCompanyLegalNameAndLocationId(String companyLegalName, String locationId);
    CaQPP findTopDataByCompanyLegalName(String companyLegalName);
}
