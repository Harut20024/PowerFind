package com.powerfind.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PowerbankKafkaProducer
{

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String key, String message)
    {
        kafkaTemplate.send("powerbank-topic", key, message);
        log.info("Sent Kafka message: key={}, message={}", key, message);
    }
}