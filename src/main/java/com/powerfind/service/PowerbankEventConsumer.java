package com.powerfind.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PowerbankEventConsumer
{

    @KafkaListener(
            topics = "powerbank-topic",
            groupId = "powerbank-group"
    )
    public void listen(ConsumerRecord<String, String> record)
    {
        try
        {
            String key = record.key();
            String message = record.value();

            log.info("Kafka message received [key={}]: {}", key, message);

        } catch (Exception e)
        {
            log.error("Error processing Kafka message: {}", record.value(), e);
        }
    }
}
