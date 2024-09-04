package com.banquito.corecobros.order.service;

import com.banquito.corecobros.order.config.RabbitMQConfig;
import com.banquito.corecobros.order.dto.AccountTransactionDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendTransaction(AccountTransactionDTO transactionDTO) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, transactionDTO);
        System.out.println("Transacción enviada a RabbitMQ: " + transactionDTO);
    }
}
