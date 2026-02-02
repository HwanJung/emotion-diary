package hwan.diary.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMqConfig {
    @Bean
    Jackson2JsonMessageConverter jacksonConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    RabbitTemplate rabbitTemplate(
        ConnectionFactory cf,
        Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(converter);

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Publish to RabbitMQ success: analysis_id={}", correlationData.getId());
            } else {
                log.warn("Publish to RabbitMQ failed: analysis_id={}, cause={}", correlationData.getId(), cause);
            }
        });

        return template;
    }
}
