package com.mpa.controller;

import com.mpa.dto.MpaAccountsDTO;
import com.mpa.dto.QppDTO;
import com.mpa.service.MpaAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RequestMapping("/api/v1")
@RestController
public class MpaAdminController {
    @Autowired
    MpaAccountsService mpaAccountsService;

    @GetMapping("/getMpaAccountsDataByCountry/{country}")
    public final ResponseEntity<Object> getMpaAccountsDataByCountry(@PathVariable("country") final String country) {
        return new ResponseEntity<>(mpaAccountsService.getMpaAccountsDataByCountry(country), HttpStatus.OK);
    }

    @GetMapping("/getDataByAccount/{country}/{account}")
    public final ResponseEntity<Object> getDataByAccount(@PathVariable("country") final String country, @PathVariable("account") final String account) {
        return new ResponseEntity<>(mpaAccountsService.getMpaDataByAccount(country, account), HttpStatus.OK);
    }

    @PostMapping("/getMpaAccountsDataByFilter")
    public final ResponseEntity<Object> getMpaAccountsDataByFilter(@RequestBody final MpaAccountsDTO mpaAccountsDTO) {
        return new ResponseEntity<>(mpaAccountsService.getMpaAccountsDataByFilter(mpaAccountsDTO), HttpStatus.OK);
    }

    @GetMapping("/getCountries")
    public final ResponseEntity<Object> getCountries() {
        return new ResponseEntity<>(mpaAccountsService.getCountries(), HttpStatus.OK);
    }

    @PostMapping("/saveOrUpdateMpaAccountsData")
    public final ResponseEntity<Object> updateMpaAccountsData(@RequestBody final MpaAccountsDTO mpaAccountsDTO, @RequestHeader(value = "Cookie", required = false) final String cookie) throws ParseException {
        return new ResponseEntity<>(mpaAccountsService.saveOrUpdateMpaAccountsData(mpaAccountsDTO, cookie), HttpStatus.OK);
    }

    @GetMapping("/archive/{id}")
    public final ResponseEntity<Object> archiveMpaAccountsData(@PathVariable("id") final int id) {
        return new ResponseEntity<>(mpaAccountsService.archiveMpaAccountsData(id), HttpStatus.OK);
    }

    @GetMapping("/getQPPAccounts")
    public final void getQPPAccounts() {
        mpaAccountsService.getQPPAccounts();
    }

    @PostMapping("/getMatchingQPP")
    public final ResponseEntity<Object> getMatchingQPP(@RequestBody final QppDTO qppDTO) {
        return new ResponseEntity<>(mpaAccountsService.getMatchingQPP(qppDTO), HttpStatus.OK);
    }
}
