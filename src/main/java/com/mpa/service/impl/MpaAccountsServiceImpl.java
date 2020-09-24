package com.mpa.service.impl;

import com.mpa.dto.AccountDataDTO;
import com.mpa.dto.MexicoQPPResponseDTO;
import com.mpa.dto.MpaAccountsDTO;
import com.mpa.dto.QppDTO;
import com.mpa.exceptions.CustomException;
import com.mpa.model.mpa.*;
import com.mpa.model.qpp.MexicoQPP;
import com.mpa.repository.mpa.*;
import com.mpa.repository.qpp.MexicoQPPRepository;
import com.mpa.service.MpaAccountsService;
import com.mpa.service.UserValidationService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MpaAccountsServiceImpl implements MpaAccountsService {
    @Autowired
    MpaAccountsRepository mpaAccountsRepository;
    @Autowired
    MpaAccountDataChageLogsRepository mpaAccountDataChageLogsRepository;
    @Autowired
    MexicoQPPRepository mexicoqppRepository;
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
    @Autowired
    protected RestTemplate restTemplate;

    protected Logger logger = LoggerFactory.getLogger(MpaAccountsServiceImpl.class);

    //Method to get MPA Accounts By Country
    @Override
    public final List<MpaAccounts> getMpaAccountsDataByCountry(final String country) {
        return mpaAccountsRepository.findAllByCountry(country);
    }

    //Method to get Account Data for a selected MPA Account
    @Override
    public final AccountDataDTO getMpaDataByAccount(final QppDTO qppDTO) {
        List<String> upcomingCountryMarketPlaces;
        List<String> existingCountryMarketPlaces;

        AccountDataDTO accountDataDTO = new AccountDataDTO();
        accountDataDTO.setDba(qppDTO.getAccount());

        MpaAccounts mpaAccounts;

        //Checking if the the location ID is empty or null
        if (qppDTO.getLocationId() == null || qppDTO.getLocationId().isEmpty()) {
            //Checking if the selected country is USA or CANADA
            if (qppDTO.getCountry().equalsIgnoreCase("usa") || qppDTO.getCountry().equalsIgnoreCase("canada")) {
                mpaAccounts = mpaAccountsRepository.findByCountryAndLegalBusinessName(qppDTO.getCountry(), qppDTO.getAccount());
            } else {
                mpaAccounts = mpaAccountsRepository.findByCountryAndDba(qppDTO.getCountry(), qppDTO.getAccount());
            }
        } else {
            if (qppDTO.getCountry().equalsIgnoreCase("usa") || qppDTO.getCountry().equalsIgnoreCase("canada")) {
                mpaAccounts = mpaAccountsRepository.findByCountryAndLegalBusinessNameAndLocationId(qppDTO.getCountry(), qppDTO.getAccount(), qppDTO.getLocationId());
            } else {
                mpaAccounts = mpaAccountsRepository.findByCountryAndDbaAndLocationId(qppDTO.getCountry(), qppDTO.getAccount(), qppDTO.getLocationId());
            }
        }

        //If the an MPA Account was found then Fetching details
        if (mpaAccounts != null) {
            if (mpaAccounts.getMarketPlaces() != null) {
                existingCountryMarketPlaces = Arrays.asList(mpaAccounts.getMarketPlaces().split(","));
            } else {
                existingCountryMarketPlaces = new ArrayList<>();
            }
            upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(mpaAccounts.getCountry()));
            accountDataDTO.setId(mpaAccounts.getId());
            accountDataDTO.setMpaDate(mpaAccounts.getMpaDate());
            accountDataDTO.setMpaNumber(mpaAccounts.getMpaNumber());
            accountDataDTO.setPartnerId(mpaAccounts.getPartnerId());
            if (!existingCountryMarketPlaces.isEmpty()) {
                accountDataDTO.setExistingCountryMarketPlaces(existingCountryMarketPlaces);
            }
            accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
            accountDataDTO.setStatus(mpaAccounts.getStatus());
            accountDataDTO.setStoreFrontName(mpaAccounts.getStoreFrontName());
        }

        //If the selected country is "USA" then Fetching data from USA QPP Database
        if (qppDTO.getCountry().equalsIgnoreCase("usa")) {
            UsQPP usQpp;
            //Location ID Null Check for USA
            if (qppDTO.getLocationId() == null || qppDTO.getLocationId().isEmpty()) {
                //Fetching just account names
                usQpp = usqppRepository.findTopDataByAccount(qppDTO.getAccount());
            } else {
                try {
                    //Fetching merged value of account and location id for USA
                    usQpp = usqppRepository.findDataByAccountAndLocationId(qppDTO.getAccount(), qppDTO.getLocationId());
                } catch (Exception e) {
                    throw new CustomException(getNonUniqueResultExceptionMessage(qppDTO), HttpStatus.BAD_REQUEST);
                }
            }

            //If the a QPP Account was found then Fetching details
            if (usQpp != null) {
                upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(qppDTO.getCountry()));
                accountDataDTO.setLocationId(usQpp.getLocationId());
                accountDataDTO.setCountry(qppDTO.getCountry());
                accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
                accountDataDTO.setLegalBusinessName(usQpp.getDba());
            }
        } else if (qppDTO.getCountry().equalsIgnoreCase("canada")) { //If the selected country is "CANADA" then Fetching data from CANADA QPP Database
            CaQPP caQpp;
            //Location ID Null Check for CANADA
            if (qppDTO.getLocationId() == null || qppDTO.getLocationId().isEmpty()) {
                //Fetching just account names
                caQpp = caqppRepository.findTopDataByCompanyLegalName(qppDTO.getAccount());
            } else {
                try {
                    //Fetching merged value of account and location id for CANADA
                    caQpp = caqppRepository.findDataByCompanyLegalNameAndLocationId(qppDTO.getAccount(), qppDTO.getLocationId());
                } catch (Exception e) {
                    throw new CustomException(getNonUniqueResultExceptionMessage(qppDTO), HttpStatus.BAD_REQUEST);
                }
            }

            //If the a QPP Account was found then Fetching details
            if (caQpp != null) {
                upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(qppDTO.getCountry()));
                accountDataDTO.setLocationId(caQpp.getLocationId());
                accountDataDTO.setCountry(qppDTO.getCountry());
                accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
                accountDataDTO.setLegalBusinessName(caQpp.getDba());
            }
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            MexicoQPP mexicoQpp;

            //Location ID Nul Check for Mexico
            if (qppDTO.getLocationId() == null || qppDTO.getLocationId().isEmpty()) {
                //Fetching just account names
                mexicoQpp = objectMapper.convertValue(mexicoqppRepository.findTopDataByAccount(qppDTO.getAccount()), MexicoQPP.class);
            } else {
                try {
                    //Fetching merged value of account and location id for Mexico
                    mexicoQpp = objectMapper.convertValue(mexicoqppRepository.findDataByAccountAndLocationId(qppDTO.getAccount(), qppDTO.getLocationId()), MexicoQPP.class);
                } catch (Exception e) {
                    throw new CustomException(getNonUniqueResultExceptionMessage(qppDTO), HttpStatus.BAD_REQUEST);
                }
            }

            MexicoQPPResponseDTO mexicoQPPResponseDTO = new MexicoQPPResponseDTO();
            BeanUtils.copyProperties(mexicoQpp, mexicoQPPResponseDTO);
            mexicoQPPResponseDTO.setCountry("Mexico");
            upcomingCountryMarketPlaces = countryMarketPlaceRepository.findByCountryId(getCountryIdByName(mexicoQPPResponseDTO.getCountry()));
            accountDataDTO.setLocationId(mexicoQPPResponseDTO.getLocationId());
            accountDataDTO.setRfc(mexicoQPPResponseDTO.getRfc());
            accountDataDTO.setCountry(mexicoQPPResponseDTO.getCountry());
            accountDataDTO.setUpcomingCountryMarketPlaces(upcomingCountryMarketPlaces);
            accountDataDTO.setLegalBusinessName(mexicoQPPResponseDTO.getLegalBusinessName());
        }
        return accountDataDTO;
    }

    //Method to get an ID for a given country
    private Integer getCountryIdByName(final String countryName) {
        if (countryRepository.getCountryId(countryName) == null) {
            throw new CustomException("Country with name: " + countryName + " not found !", HttpStatus.NOT_FOUND);
        }
        return countryRepository.getCountryId(countryName);
    }

    //Method to filter MPA Accounts Data
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
                    if (mpaAccountsDTO.getLegalBusinessName() != null && !mpaAccountsDTO.getLegalBusinessName().isEmpty()) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("legalBusinessName"), mpaAccountsDTO.getLegalBusinessName().trim() + "%")));
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

    //Method to Save or Update Accounts Data
    @Override
    public Optional<MpaAccounts> saveOrUpdateMpaAccountsData(final MpaAccountsDTO mpaAccountsDTO, final String cookie) throws ParseException {
        if (mpaAccountsDTO.getId() != null) {
            Optional<MpaAccounts> mpaAccounts = mpaAccountsRepository.findById(mpaAccountsDTO.getId());
            if (mpaAccounts.isPresent()) {
                MpaAccounts mpa = mpaAccounts.get();
                mpa = updateMpaAccount(mpa, mpaAccountsDTO);
                mpaAccounts = mpaAccountsRepository.findById(mpaAccountsRepository.save(mpa).getId());
                mpaAccounts.ifPresent(accounts -> updateMpaAccountsLog(accounts, cookie));
                String message = "Record with id " + mpaAccountsDTO.getId() + " has been successfully updated !";
                logger.info(message);
                return mpaAccountsRepository.findById(mpaAccountsDTO.getId());
            } else {
                throw new CustomException("Record that you are trying to update is not found !", HttpStatus.NOT_FOUND);
            }
        }
        return mpaAccountsRepository.findById(saveAccountData(mpaAccountsDTO, cookie));
    }

    //Method to save MPA Account data and return its ID
    public final Integer saveAccountData(final MpaAccountsDTO mpaAccountsDTO, String cookie) {
        MpaAccounts newMpaAccount = new MpaAccounts();

        //Status NULL Check for MPA Accounts
        if (mpaAccountsDTO.getStatus() == null || mpaAccountsDTO.getStatus().isEmpty()) {
            throw new CustomException("Status is a mandatory field !", HttpStatus.BAD_REQUEST);
        } else {
            //Check to ensure that MPA Number is available when the status is either Active or Accepted
            if ((mpaAccountsDTO.getStatus().equalsIgnoreCase("active")
                    || mpaAccountsDTO.getStatus().equalsIgnoreCase("accepted (but no storefront)"))
                    && (mpaAccountsDTO.getMpaNumber() == null || mpaAccountsDTO.getMpaNumber().isEmpty())) {
                throw new CustomException("MPA Number is mandatory if the status is: " + mpaAccountsDTO.getStatus(), HttpStatus.BAD_REQUEST);
            }
        }

        //Saving Account Data to Legal Business Name Column in case of USA and CANADA
        if (mpaAccountsDTO.getCountry().equalsIgnoreCase("usa") || mpaAccountsDTO.getCountry().equalsIgnoreCase("canada")) {
            newMpaAccount.setDba(mpaAccountsDTO.getDba());
            newMpaAccount.setLegalBusinessName(mpaAccountsDTO.getAccount());
        } else {
            //Saving Account Data to DBA Column in case of USA and CANADA
            newMpaAccount.setDba(mpaAccountsDTO.getAccount());
            newMpaAccount.setLegalBusinessName(mpaAccountsDTO.getLegalBusinessName());
        }

        //Checking if an MPA number exists in the DB
        checkIfMpaExists(mpaAccountsDTO);

        newMpaAccount.setCountry(mpaAccountsDTO.getCountry());
        newMpaAccount.setLocationId(mpaAccountsDTO.getLocationId());
        newMpaAccount.setRfc(mpaAccountsDTO.getRfc());
        newMpaAccount.setMpaDate(mpaAccountsDTO.getMpaDate());
        newMpaAccount.setMpaNumber(mpaAccountsDTO.getMpaNumber());
        newMpaAccount.setMarketPlaces(mpaAccountsDTO.getMarketPlaces());
        newMpaAccount.setStoreFrontName(mpaAccountsDTO.getStoreFrontName());
        newMpaAccount.setPartnerId(mpaAccountsDTO.getPartnerId());
        newMpaAccount.setStatus(mpaAccountsDTO.getStatus().trim());

        //Saving an MPA Account to DB and getting its ID
        int id = mpaAccountsRepository.save(newMpaAccount).getId();

        //Finding the record that was just saved
        Optional<MpaAccounts> mpaAccounts = mpaAccountsRepository.findById(id);

        //Updating MPA Accounts Data Chane Logs
        mpaAccounts.ifPresent(accounts -> updateMpaAccountsLog(accounts, cookie));

        //Returning the saved MPA Account ID
        return id;
    }


    //Method to update an MPA Account
    private MpaAccounts updateMpaAccount(MpaAccounts mpa, MpaAccountsDTO mpaAccountsDTO) {
        MpaAccounts mpaAccounts = new MpaAccounts();
        BeanUtils.copyProperties(mpa, mpaAccounts);

        if (mpaAccountsDTO.getStatus() == null) {
            throw new CustomException("Status is a mandatory field !", HttpStatus.BAD_REQUEST);
        } else {
            if ((mpaAccountsDTO.getStatus().equalsIgnoreCase("active")
                    || mpaAccountsDTO.getStatus().equalsIgnoreCase("accepted (but no storefront)"))
                    && (mpaAccountsDTO.getMpaNumber() == null || mpaAccountsDTO.getMpaNumber().isEmpty())) {
                throw new CustomException("MPA Number is mandatory if the status is: " + mpaAccountsDTO.getStatus(), HttpStatus.BAD_REQUEST);
            }
        }

        if (mpaAccountsDTO.getStatus() != null) {
            mpaAccounts.setStatus(mpaAccountsDTO.getStatus().trim());
        }
        if (mpaAccountsDTO.getPartnerId() != null) {
            mpaAccounts.setPartnerId(mpaAccountsDTO.getPartnerId().trim());
        }
        if (mpaAccountsDTO.getMpaDate() != null) {
            mpaAccounts.setMpaDate(mpaAccountsDTO.getMpaDate());
        }

        if (mpaAccountsDTO.getMpaNumber() != null && !mpaAccountsDTO.getMpaNumber().isEmpty()) {
            if(!mpaAccountsDTO.getMpaNumber().equals(mpaAccounts.getMpaNumber())){
                mpaAccounts.setMpaNumber(checkIfMpaExists(mpaAccountsDTO));
            }else{
                mpaAccounts.setMpaNumber(mpaAccountsDTO.getMpaNumber());
            }
        }

        if (mpaAccountsDTO.getMarketPlaces() != null) {
            mpaAccounts.setMarketPlaces(mpaAccountsDTO.getMarketPlaces());
        }
        if (mpaAccountsDTO.getStoreFrontName() != null) {
            mpaAccounts.setStoreFrontName(mpaAccountsDTO.getStoreFrontName());
        }
        return mpaAccounts;
    }

    private void updateMpaAccountsLog(final MpaAccounts mpaAccounts, final String cookie) {
        mpaAccountDataChageLogsRepository.save(MpaAccountsDataChangeLogs.builder()
                .dba(mpaAccounts.getDba())
                .legalBusinessName(mpaAccounts.getLegalBusinessName())
                .contact(mpaAccounts.getContact())
                .mpaDate(mpaAccounts.getMpaDate())
                .mpaNumber(mpaAccounts.getMpaNumber())
                .email(mpaAccounts.getEmail())
                .storeFrontName(mpaAccounts.getStoreFrontName())
                .locationId(mpaAccounts.getLocationId())
                .status(mpaAccounts.getStatus())
                .rfc(mpaAccounts.getRfc())
                .country(mpaAccounts.getCountry())
                .partnerId(mpaAccounts.getPartnerId())
                .marketPlaces(mpaAccounts.getMarketPlaces())
                .pbm(mpaAccounts.getPbm())
                .dataId(mpaAccounts.getId())
                .userName(userValidationService.getUserNameFromCookie(cookie))
                //.userName("ether2test")
                .lastModifiedTimestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public List<MpaAccountsDataChangeLogs> archiveMpaAccountsData(final int id) {
        List<MpaAccountsDataChangeLogs> sdnDataChangeLogsList = mpaAccountDataChageLogsRepository.findAllByDataId(id);
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
    public Object getMatchingQPP(final QppDTO qppDTO) {
        if (qppDTO.getCountry() != null && !qppDTO.getCountry().isEmpty()) {
            if (qppDTO.getCountry().equalsIgnoreCase("usa")) {
                return usqppRepository.getMatchingQPP(qppDTO.getAccount());
            } else if (qppDTO.getCountry().equalsIgnoreCase("canada")) {
                return caqppRepository.getMatchingQPP(qppDTO.getAccount());
            } else {
                return mexicoqppRepository.getMatchingQPP(qppDTO.getAccount());
            }
        }
        return mexicoqppRepository.getMatchingQPP(qppDTO.getAccount());
    }

    @Override
    public String checkIfMpaExists(final MpaAccountsDTO mpaAccountsDTO) {
        if (mpaAccountsDTO.getMpaNumber() != null) {
            List<MpaAccounts> mpaAccounts = ifMpaExists(mpaAccountsDTO.getMpaNumber().trim(), mpaAccountsDTO.getCountry());
            if (!mpaAccounts.isEmpty()) {
                throw new CustomException("An account with MPA Number : " + mpaAccountsDTO.getMpaNumber() + " already exists !", HttpStatus.BAD_REQUEST);
            }
        }
        return mpaAccountsDTO.getMpaNumber();
    }

    private String getNonUniqueResultExceptionMessage(final QppDTO qppDTO) {
        return "More than 1 entries found for the account: " + qppDTO.getAccount() + " with location id: " + qppDTO.getLocationId();
    }

    private List<MpaAccounts> ifMpaExists(String mpaNumber, String country) {
        return mpaAccountsRepository.findByMpaNumberAndCountry(mpaNumber, country);
    }

//    @Override
//    public void getQPPAccounts() {
//        JSONParser jsonParser = new JSONParser();
//        try {
//            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("src/main/resources/qpp.json"));
//            JSONArray jsonArray = (JSONArray) jsonObject.get("qpp");
//            for (Object object : jsonArray) {
//                JSONObject qppObject = (JSONObject) object;
//                mexicoqppRepository.save(
//                        MEXICO_QPP.builder()
//                                .account((String) qppObject.get("account"))
//                                .dbaOrLegalBusinessName((String) qppObject.get("legalBusinessName"))
//                                .commercialConsumer((String) qppObject.get("commercialConsumer"))
//                                .locationId((String) qppObject.get("locationId"))
//                                .printStatus((String) qppObject.get("printStatus"))
//                                .supplyStatus((String) qppObject.get("supplyStatus"))
//                                .comment((String) qppObject.get("comment"))
//                                .relationship((String) qppObject.get("relationship"))
//                                .rfc((String) qppObject.get("rfc"))
//                                .totalRevenue((String) qppObject.get("totalRevenue"))
//                                .build()
//                );
//            }
//            logger.info("Records inserted.....");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}
