package com.banquito.corecobros.order.util.account;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service")
public interface AccountClient {

    @GetMapping("/accounts/{accountNumber}/balance")
    BigDecimal getAccountBalance(@PathVariable("accountNumber") String accountNumber);

    @PostMapping("/accounts/{accountNumber}/debit")
    void debitAccount(@PathVariable("accountNumber") String accountNumber, @RequestBody BigDecimal amount);
}