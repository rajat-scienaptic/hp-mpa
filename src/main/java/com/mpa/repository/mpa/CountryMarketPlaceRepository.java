package com.mpa.repository.mpa;

import com.mpa.model.mpa.CountryMarketPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryMarketPlaceRepository extends JpaRepository<CountryMarketPlace, Integer> {
    @Query(value = "select c.marketPlace from CountryMarketPlace c where c.countryId = :countryId")
    List<String> findByCountryId(int countryId);
}
