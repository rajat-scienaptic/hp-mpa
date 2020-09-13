package com.mpa.repository;

import com.mpa.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    Country findByCountryName(String customerMarket);
    @Query(value = "select c.id from Country c where c.countryName = :countryName")
    Integer getCountryId(String countryName);
}
