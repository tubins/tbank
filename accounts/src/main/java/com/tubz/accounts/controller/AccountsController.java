package com.tubz.accounts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tubz.accounts.clients.CardsFeignClient;
import com.tubz.accounts.clients.LoansFeignClient;
import com.tubz.accounts.config.AccountsServiceConfig;
import com.tubz.accounts.model.*;
import com.tubz.accounts.repository.AccountsRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountsController {
    private static final String CORRELATION_ID = "tbank_correlation_id";

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    AccountsServiceConfig accountsConfig;

    @Autowired
    LoansFeignClient loansFeignClient;

    @Autowired
    CardsFeignClient cardsFeignClient;

    @PostMapping("/myAccount")
    @Timed(value = "getAccountDetails.time", description = "Time taken to return account details")
    public Accounts getAccountDetails(@RequestBody Customer customer) {
        return accountsRepository.findByCustomerId(customer.getCustomerId());
    }

    @GetMapping("/account/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
                accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
        String jsonStr = ow.writeValueAsString(properties);
        return jsonStr;
    }

    @PostMapping("/myCustomerDetails")
    @CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod = "getCustomerDetailsFallBack")
    public CustomerDetails getCustomerDetails(@RequestHeader(CORRELATION_ID) String correlationId, @RequestBody Customer customer) {
        Accounts byCustomerId = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Cards> cardDetails = cardsFeignClient.getCardDetails(correlationId, customer);
        List<Loans> loansDetails = loansFeignClient.getLoansDetails(correlationId, customer);
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(byCustomerId);
        customerDetails.setCards(cardDetails);
        customerDetails.setLoans(loansDetails);
        return customerDetails;
    }

    private CustomerDetails getCustomerDetailsFallBack(String correlationId,   Customer customer, Throwable throwable) {
        Accounts byCustomerId = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Cards> cardDetails = cardsFeignClient.getCardDetails(correlationId, customer);
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(byCustomerId);
        customerDetails.setCards(cardDetails);
        return customerDetails;
    }

    @GetMapping("/sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
    public String sayHello() {
        return "Hello, Welcome to TBank Kubernetes cluster";
    }

    private String sayHelloFallback(Throwable t) {
        return "Hi, Welcome to EazyBank";
    }

}
