package com.mpa.service.impl;

import com.mpa.dto.AccountDataDTO;
import com.mpa.dto.MpaAccountsDTO;
import com.mpa.dto.QppDTO;
import com.mpa.exceptions.CustomException;
import com.mpa.model.*;
import com.mpa.repository.*;
import com.mpa.service.MpaAccountsService;
import com.mpa.service.UserValidationService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.io.FileReader;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MpaAccountsServiceImpl implements MpaAccountsService {
    @Autowired
    MpaAccountsRepository mpaAccountsRepository;
    @Autowired
    MpaAccountDataChageLogsRepository qualifiedMarketPlaceLogRepository;
    @Autowired
    MEXICOQPPRepository mexicoqppRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    UserValidationService userValidationService;
    @Autowired
    CountryMarketPlaceRepository countryMarketPlaceRepository;
    @Autowired
    USQPPRepository usqppRepository;
    @Autowired
    CAQPPRepository caqppRepository;

    Logger logger = LoggerFactory.getLogger(MpaAccountsServiceImpl.class);

    @Override
    public final List<MpaAccounts> getMpaAccountsDataByCountry(final String country) {
        return mpaAccountsRepository.findAllByCountry(country);
    }

    @Override
    public final AccountDataDTO getMpaDataByAccount(final String country, final String account) {
        List<String> upcomingCountryMarketPlaces = new LinkedList<>();
        List<String> existingCountryMarketPlaces;
        AccountDataDTO accountDataDTO = new AccountDataDTO();

        MpaAccounts mpaAccounts = mpaAccountsRepository.findByCountryAndAccount(country, account);

        if (mpaAccounts != null) {
            if (mpaAccounts.getMarketPlaces() != null) {
                existingCountryMarketPlaces = Arrays.asList(mpaAccounts.getMarketPlaces().split(","));
            } else {
                existingCountryMarketPlaces = new ArrayList<>();
            }
            accountDataDTO.setId(mpaAccounts.getId());
            accountDataDTO.setExistingCountryMarketPlaces(existingCountryMarketPlaces);
            accountDataDTO.setMpaDate(mpaAccounts.getMpaDate());
            accountDataDTO.setMpaNumber(mpaAccounts.getMpaNumber());
            accountDataDTO.setPartnerId(mpaAccounts.getPartnerId());
            accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
            accountDataDTO.setStatus(mpaAccounts.getStatus());
            accountDataDTO.setStoreFrontName(mpaAccounts.getStoreFrontName());
        }

        if (country.equalsIgnoreCase("usa")) {
            US_QPP usQpp = usqppRepository.findDataByAccount(account);
            if (usQpp != null) {
                upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(country));
                accountDataDTO.setLocationId(usQpp.getLocationId());
                accountDataDTO.setCountry(country);
                accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
                accountDataDTO.setDbaOrLegalBusinessName(usQpp.getDba());
            } else {
                upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(mpaAccounts.getCountry()));
                accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
            }
        } else if (country.equalsIgnoreCase("canada")) {
            CA_QPP caQpp = caqppRepository.findDataByAccount(account);
            if (caQpp != null) {
                upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(country));
                accountDataDTO.setLocationId(caQpp.getLocationId());
                accountDataDTO.setCountry(country);
                accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
                accountDataDTO.setDbaOrLegalBusinessName(caQpp.getDba());
            } else {
                upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(mpaAccounts.getCountry()));
                accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
            }
        } else {
            MEXICO_QPP mexicoQpp = mexicoqppRepository.findDataByAccount(account);
            if (mexicoQpp != null) {
                upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(mexicoQpp.getCountry()));
                accountDataDTO.setLocationId(mexicoQpp.getLocationId());
                accountDataDTO.setRfc(mexicoQpp.getRfc());
                accountDataDTO.setCountry(mexicoQpp.getCountry());
                accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
                accountDataDTO.setDbaOrLegalBusinessName(mexicoQpp.getDbaOrLegalBusinessName());
            } else {
                if (mpaAccounts != null) {
                    upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(mpaAccounts.getCountry()));
                }
                accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
            }
        }
        return accountDataDTO;
    }

    private Integer getCountryIdByName(final String countryName) {
        if (countryRepository.getCountryId(countryName) == null) {
            throw new CustomException("Country with name: " + countryName + " not found !", HttpStatus.NOT_FOUND);
        }
        return countryRepository.getCountryId(countryName);
    }

    @Override
    public final List<MpaAccounts> getMpaAccountsDataByFilter(final MpaAccountsDTO mpaAccountsDTO) {
        return findQualifiedMarketPlaceDataByCriteria(mpaAccountsDTO);
    }

    public List<MpaAccounts> findQualifiedMarketPlaceDataByCriteria(final MpaAccountsDTO mpaAccountsDTO) {
        return mpaAccountsRepository.findAll(
                (root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (mpaAccountsDTO.getCountry() != null && !mpaAccountsDTO.getCountry().isEmpty()) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("country"), mpaAccountsDTO.getCountry().trim())));
                    }
                    if (mpaAccountsDTO.getAccount() != null && !mpaAccountsDTO.getAccount().isEmpty()) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("account"), mpaAccountsDTO.getAccount().trim() + "%")));
                    }
                    if (mpaAccountsDTO.getLocationId() != null && !mpaAccountsDTO.getLocationId().isEmpty()) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("locationId"), mpaAccountsDTO.getLocationId().trim())));
                    }
                    if (mpaAccountsDTO.getStoreFrontName() != null && !mpaAccountsDTO.getStoreFrontName().isEmpty()) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("storeFrontName"), mpaAccountsDTO.getStoreFrontName().trim() + "%")));
                    }
                    if (mpaAccountsDTO.getRfc() != null && !mpaAccountsDTO.getRfc().isEmpty()) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("rfc"), mpaAccountsDTO.getRfc().trim() + "%")));
                    }
                    if (mpaAccountsDTO.getStatus() != null && !mpaAccountsDTO.getStatus().isEmpty()) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("status"), mpaAccountsDTO.getStatus().trim())));
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                });
    }

    @Override
    public Optional<MpaAccounts> saveOrUpdateMpaAccountsData(final MpaAccountsDTO mpaAccountsDTO, final String cookie) throws ParseException {
        if (mpaAccountsDTO.getId() != null) {
            Optional<MpaAccounts> mpaAccounts = mpaAccountsRepository.findById(mpaAccountsDTO.getId());
            if (mpaAccounts.isPresent()) {
                MpaAccounts mpa = mpaAccounts.get();
                mpa = updateMpaAccount(mpa, mpaAccountsDTO);
                mpaAccountsRepository.save(mpa);
                updateMpaAccountsLog(mpa, cookie);
                String message = "Record with id " + mpaAccountsDTO.getId() + " has been successfully updated !";
                logger.info(message);
                return mpaAccountsRepository.findById(mpaAccountsDTO.getId());
            } else {
                throw new CustomException("Record that you are trying to update is not found !", HttpStatus.NOT_FOUND);
            }
        }
        return mpaAccountsRepository.findById(saveAccountData(mpaAccountsDTO));
    }

    public final Integer saveAccountData(final MpaAccountsDTO mpaAccounts) {
        MpaAccounts mpaAccount = mpaAccountsRepository.findByCountryAndAccount(mpaAccounts.getCountry(), mpaAccounts.getAccount());
        if (mpaAccount != null) {
            throw new CustomException("Account with name: " + mpaAccount.getAccount() + " already exists !", HttpStatus.FOUND);
        }
        return mpaAccountsRepository.save(MpaAccounts.builder()
                .account(mpaAccounts.getAccount())
                .dbaOrLegalBusinessName(mpaAccounts.getDbaOrLegalBusinessName())
                .country(mpaAccounts.getCountry())
                .locationId(mpaAccounts.getLocationId())
                .rfc(mpaAccounts.getRfc())
                .mpaDate(mpaAccounts.getMpaDate())
                .partnerId(mpaAccounts.getPartnerId())
                .mpaNumber(mpaAccounts.getMpaNumber())
                .marketPlaces(mpaAccounts.getMarketPlaces())
                .status(mpaAccounts.getStatus())
                .storeFrontName(mpaAccounts.getStoreFrontName())
                .build()).getId();
    }

    private MpaAccounts updateMpaAccount(MpaAccounts mpa, MpaAccountsDTO mpaAccountsDTO) {
        MpaAccounts mpaAccounts = new MpaAccounts();
        BeanUtils.copyProperties(mpa, mpaAccounts);
        if (mpaAccountsDTO.getStatus() != null && !mpaAccountsDTO.getStatus().isEmpty()) {
            mpaAccounts.setStatus(mpaAccountsDTO.getStatus());
        }
        if (mpaAccountsDTO.getPartnerId() != null && !mpaAccountsDTO.getPartnerId().isEmpty()) {
            mpaAccounts.setPartnerId(mpaAccountsDTO.getPartnerId());
        }
        if (mpaAccountsDTO.getMpaDate() != null) {
            mpaAccounts.setMpaDate(mpaAccountsDTO.getMpaDate());
        }
        if (mpaAccountsDTO.getMpaNumber() != null && !mpaAccountsDTO.getMpaNumber().isEmpty()) {
            mpaAccounts.setMpaNumber(mpaAccountsDTO.getMpaNumber());
        }
        if (mpaAccountsDTO.getMarketPlaces() != null && !mpaAccountsDTO.getMarketPlaces().isEmpty()) {
            mpaAccounts.setMarketPlaces(mpaAccountsDTO.getMarketPlaces());
        }
        if (mpaAccountsDTO.getStoreFrontName() != null && !mpaAccountsDTO.getStoreFrontName().isEmpty()) {
            mpaAccounts.setStoreFrontName(mpaAccountsDTO.getStoreFrontName());
        }
        return mpaAccounts;
    }

    private void updateMpaAccountsLog(final MpaAccounts mpaAccounts, final String cookie) {
        qualifiedMarketPlaceLogRepository.save(MpaAccountsDataChangeLogs.builder()
                .account(mpaAccounts.getAccount())
                .dbaOrLegalBusinessName(mpaAccounts.getDbaOrLegalBusinessName())
                .contact(mpaAccounts.getContact())
                .mpaDate(mpaAccounts.getMpaDate())
                .mpaNumber(mpaAccounts.getMpaNumber())
                .email(mpaAccounts.getEmail())
                .storeFrontName(mpaAccounts.getStoreFrontName())
                .locationId(mpaAccounts.getLocationId())
                .country(mpaAccounts.getCountry())
                .partnerId(mpaAccounts.getPartnerId())
                .marketPlaces(mpaAccounts.getMarketPlaces())
                .pbm(mpaAccounts.getPbm())
                .dataId(mpaAccounts.getId())
                .userName(userValidationService.getUserNameFromCookie(cookie))
                .lastModifiedTimestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public List<MpaAccountsDataChangeLogs> archiveMpaAccountsData(final int id) {
        List<MpaAccountsDataChangeLogs> sdnDataChangeLogsList = qualifiedMarketPlaceLogRepository.findAllByDataId(id);
        if (!sdnDataChangeLogsList.isEmpty()) {
            return sdnDataChangeLogsList;
        } else {
            throw new CustomException("No history found for record with id : " + id, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<String> getCountries() {
        List<Country> countries = countryRepository.findAll();
        List<String> countryList = new LinkedList<>();
        countries.forEach(country -> countryList.add(country.getCountryName()));
        return countryList;
    }

    @Override
    public void getQPPAccounts() {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/qpp.json"));
            JSONArray jsonArray = (JSONArray) jsonObject.get("qpp");
            for (Object object : jsonArray) {
                JSONObject qppObject = (JSONObject) object;
                mexicoqppRepository.save(
                        MEXICO_QPP.builder()
                                .account((String) qppObject.get("account"))
                                .dbaOrLegalBusinessName((String) qppObject.get("legalBusinessName"))
                                .commercialConsumer((String) qppObject.get("commercialConsumer"))
                                .locationId((String) qppObject.get("locationId"))
                                .printStatus((String) qppObject.get("printStatus"))
                                .supplyStatus((String) qppObject.get("supplyStatus"))
                                .comment((String) qppObject.get("comment"))
                                .relationship((String) qppObject.get("relationship"))
                                .rfc((String) qppObject.get("rfc"))
                                .totalRevenue((String) qppObject.get("totalRevenue"))
                                .build()
                );
            }
            logger.info("Records inserted.....");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<String> getMatchingQPP(final QppDTO qppDTO) {
        if (qppDTO.getCountry() != null && !qppDTO.getCountry().isEmpty()) {
            if (qppDTO.getCountry().equalsIgnoreCase("usa")) {
                return usqppRepository.getMatchingQPP(qppDTO.getAccount());
            } else if (qppDTO.getCountry().equalsIgnoreCase("canada")) {
                return caqppRepository.getMatchingQPP(qppDTO.getAccount());
            } else {
                return mexicoqppRepository.getMatchingQPPByCountry(qppDTO.getCountry(), qppDTO.getAccount());
            }
        }
        return mexicoqppRepository.getMatchingQPP(qppDTO.getAccount());
    }

}
