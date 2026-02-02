package hwan.diary.domain.diary.publisher;

import hwan.diary.config.RabbitTopologyConfig;
import hwan.diary.domain.diary.publisher.event.DiaryAnalysisEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnalysisEventPublisherTest {

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    AnalysisEventPublisher publisher;

    @Captor ArgumentCaptor<Object> payloadCaptor;
    @Captor ArgumentCaptor<CorrelationData> cdCaptor;

    @Test
    void publish_callsRabbitTemplateWithCorrectArgs() {
        Long diaryId = 1L;
        Long analysisId = 10L;
        String imageUrl = "https://example.com/img";

        publisher.publish(diaryId, analysisId, imageUrl);

        verify(rabbitTemplate).convertAndSend(
            eq(RabbitTopologyConfig.EXCHANGE),
            eq(RabbitTopologyConfig.ROUTING_KEY),
            payloadCaptor.capture(),
            cdCaptor.capture()
        );

        assertTrue(payloadCaptor.getValue() instanceof DiaryAnalysisEvent);
        DiaryAnalysisEvent e = (DiaryAnalysisEvent) payloadCaptor.getValue();
        assertEquals(diaryId, e.diaryId());
        assertEquals(analysisId, e.analysisId());
        assertEquals(imageUrl, e.imageUrl());

        assertEquals(String.valueOf(analysisId), cdCaptor.getValue().getId());
    }

}
