package com.mpa.repository.qpp;

import com.mpa.model.qpp.MexicoQPP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MexicoQPPRepository extends JpaRepository<MexicoQPP, Integer> {
    @Query(value = "Select case when m.locationId is null then m.account else concat(m.account, '|', m.locationId)" +
            " end from MexicoQPP m where m.account like :account% " +
            "and ((m.printsStatus = 'Calificado' or m.printsStatus = 'Certificado') " +
            "or (m.suppliesStatus = 'Calificado' or m.suppliesStatus = 'Certificado'))")
    List<String> getMatchingQPP(String account);

    MexicoQPP findDataByAccountAndLocationId(String account, String locationId);
    MexicoQPP findTopDataByAccount(String account);
}
