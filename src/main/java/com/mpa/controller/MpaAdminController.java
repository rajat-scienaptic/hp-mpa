package com.mpa.controller;

import com.mpa.dto.ApiResponseDTO;
import com.mpa.dto.MpaAccountsDTO;
import com.mpa.dto.QppDTO;
import com.mpa.exceptions.CustomException;
import com.mpa.service.MpaAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDateTime;

@RequestMapping("/api/v1")
@RestController
public class MpaAdminController {
    @Autowired
    MpaAccountsService mpaAccountsService;

    @GetMapping("/getMpaAccountsDataByCountry/{country}")
    public final ResponseEntity<Object> getMpaAccountsDataByCountry(@PathVariable("country") final String country) {
        return new ResponseEntity<>(mpaAccountsService.getMpaAccountsDataByCountry(country), HttpStatus.OK);
    }

    @PostMapping("/getDataByAccount")
    public final ResponseEntity<Object> getDataByAccount(@RequestBody final QppDTO qppDTO) {
        return new ResponseEntity<>(mpaAccountsService.getMpaDataByAccount(qppDTO), HttpStatus.OK);
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

    @PostMapping("/getMatchingQPP")
    public final ResponseEntity<Object> getMatchingQPP(@RequestBody final QppDTO qppDTO) {
        return new ResponseEntity<>(mpaAccountsService.getMatchingQPP(qppDTO), HttpStatus.OK);
    }

    @PostMapping("/checkIfMpaExists")
    public final ResponseEntity<Object> checkIfMpaExists(@RequestBody final MpaAccountsDTO mpaAccountsDTO){
        try{
            mpaAccountsService.checkIfMpaExists(mpaAccountsDTO);
            return new ResponseEntity<>("Available !", HttpStatus.OK);
        }catch(CustomException e){
            return new ResponseEntity<>(ApiResponseDTO.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }
}
