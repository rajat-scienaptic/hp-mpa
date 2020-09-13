package com.mpa.service;

import com.mpa.dto.AccountDataDTO;
import com.mpa.dto.MpaAccountsDTO;
import com.mpa.dto.QppDTO;
import com.mpa.model.MpaAccounts;
import com.mpa.model.MpaAccountsDataChangeLogs;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public interface MpaAccountsService {
    List<MpaAccounts> getMpaAccountsDataByCountry(final String country);
    AccountDataDTO getMpaDataByAccount(final String country, final String account);
    List<MpaAccounts> getMpaAccountsDataByFilter(final MpaAccountsDTO mpaAccountsDTO);
    Optional<MpaAccounts> saveOrUpdateMpaAccountsData(final MpaAccountsDTO mpaAccountsDTO, final String cookies) throws ParseException;
    List<MpaAccountsDataChangeLogs> archiveMpaAccountsData(final int id);
    List<String> getCountries();
    void getQPPAccounts();
    List<String> getMatchingQPP(final QppDTO qppDTO);
}
