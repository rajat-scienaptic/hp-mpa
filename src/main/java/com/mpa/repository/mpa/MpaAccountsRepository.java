package com.mpa.repository.mpa;

import com.mpa.model.mpa.MpaAccounts;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MpaAccountsRepository extends CrudRepository<MpaAccounts, Integer>, JpaSpecificationExecutor<MpaAccounts> {
    List<MpaAccounts> findAllByCountry(String country);
    MpaAccounts findByCountryAndDbaAndLocationId(String country, String dba, String locationId);
    MpaAccounts findByCountryAndLegalBusinessNameAndLocationId(String country, String legalBusinessName, String locationId);
    MpaAccounts findByCountryAndDba(String country, String dba);
    MpaAccounts findByCountryAndLegalBusinessName(String country, String legalBusinessName);
    List<MpaAccounts> findByMpaNumberAndCountry(String mpaNumber, String country);
}
