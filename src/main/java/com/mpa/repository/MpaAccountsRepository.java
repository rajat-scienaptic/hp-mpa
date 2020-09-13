package com.mpa.repository;

import com.mpa.model.MpaAccounts;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MpaAccountsRepository extends CrudRepository<MpaAccounts, Integer>, JpaSpecificationExecutor<MpaAccounts> {
    List<MpaAccounts> findAllByCountry(String country);
    MpaAccounts findByCountryAndAccount(String country, String account);
}
