package hwan.diary.domain.diary.publisher;

import hwan.diary.config.RabbitMqConfig;
import hwan.diary.config.RabbitTopologyConfig;
import hwan.diary.domain.diary.publisher.event.DiaryAnalysisEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(Long diaryId, Long analysisId, String imageUrl) {

        DiaryAnalysisEvent diaryAnalysisEvent = new DiaryAnalysisEvent(diaryId, analysisId, imageUrl);

        rabbitTemplate.convertAndSend(
            RabbitTopologyConfig.EXCHANGE,
            RabbitTopologyConfig.ROUTING_KEY,
            diaryAnalysisEvent,
            new CorrelationData(String.valueOf(analysisId))
        );
    }
}
