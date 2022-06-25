package com.tubz.cards.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tubz.cards.config.CardsServiceConfig;
import com.tubz.cards.model.Cards;
import com.tubz.cards.model.Customer;
import com.tubz.cards.model.Properties;
import com.tubz.cards.repository.CardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CardsController {
    public static final String CORRELATION_ID = "tbank_correlation_id";

    @Autowired
    private CardsRepository cardsRepository;
    @Autowired
    private CardsServiceConfig cardsConfig;

    @PostMapping("/myCards")
    public List<Cards> getCardDetails(@RequestHeader(CORRELATION_ID) String correlationId, @RequestBody Customer customer) {
        return cardsRepository.findByCustomerId(customer.getCustomerId());
    }

    @GetMapping("/card/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(cardsConfig.getMsg(), cardsConfig.getBuildVersion(),
                cardsConfig.getMailDetails(), cardsConfig.getActiveBranches());
        String jsonStr = ow.writeValueAsString(properties);
        return jsonStr;
    }
}
