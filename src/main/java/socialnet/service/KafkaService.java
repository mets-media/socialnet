package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import socialnet.api.request.KafkaMessageRq;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaTemplate<String, KafkaMessageRq> kafkaTemplate;

    @Value(value = "${spring.kafka.consumer.topic-name}")
    private String topicName;

    public void sendMessage(KafkaMessageRq message) {
        kafkaTemplate.send(topicName, message);
    }
}
