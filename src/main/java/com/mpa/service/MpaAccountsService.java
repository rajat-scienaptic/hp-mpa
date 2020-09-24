package com.mpa.service;

import com.mpa.dto.AccountDataDTO;
import com.mpa.dto.MpaAccountsDTO;
import com.mpa.dto.QppDTO;
import com.mpa.model.mpa.MpaAccounts;
import com.mpa.model.mpa.MpaAccountsDataChangeLogs;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public interface MpaAccountsService {
    List<MpaAccounts> getMpaAccountsDataByCountry(final String country);
    AccountDataDTO getMpaDataByAccount(final QppDTO qppDTO);
    List<MpaAccounts> getMpaAccountsDataByFilter(final MpaAccountsDTO mpaAccountsDTO);
    Optional<MpaAccounts> saveOrUpdateMpaAccountsData(final MpaAccountsDTO mpaAccountsDTO, final String cookies) throws ParseException;
    List<MpaAccountsDataChangeLogs> archiveMpaAccountsData(final int id);
    List<String> getCountries();
    //void getQPPAccounts();
    Object getMatchingQPP(final QppDTO qppDTO);
    String checkIfMpaExists(final MpaAccountsDTO mpaAccountsDTO);
}
