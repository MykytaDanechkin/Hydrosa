package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.DTO.SignalRequest;
import com.mykyda.hydrosasim.app.data.entity.Signal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AsyncSignalSender {

    private RestTemplate restTemplate = new RestTemplate();
    private final QueueService queueService;
    private final String hydrosaUrl;

    public AsyncSignalSender(QueueService queueService, @org.springframework.beans.factory.annotation.Value("${app.hydrosa-url}") String hydrosaUrl) {
        this.queueService = queueService;
        this.hydrosaUrl = hydrosaUrl;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final int MAX_RETRIES = 3;

    @Scheduled(fixedRateString = "${app.schedules.queue-processing-freq}")
    public void processQueue() {

        while (true) {
            var task = queueService.poll();
            if (task == null) return;

            sendSignalWithRetry(task);
        }
    }

    private void sendSignalWithRetry(Signal signal) {

        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            try {
                attempt++;

                var request = SignalRequest.builder()
                        .stationId(signal.getStationId())
                        .azimuth(signal.getAzimuth())
                        .strength(signal.getStrength())
                        .build();

                restTemplate.postForEntity(
                        hydrosaUrl + "/api/signals",
                        request,
                        Void.class
                );

                log.debug("Signal sent: {}", request);
                return;

            } catch (Exception e) {

                log.warn("Send failed (attempt {}/{}): {}",
                        attempt, MAX_RETRIES, e.getMessage());

                try {
                    long backoff = (long) Math.pow(2, attempt) * 200;
                    Thread.sleep(backoff);
                } catch (InterruptedException ignored) {}
            }
        }

        log.error("Signal dropped after {} retries", MAX_RETRIES);
    }
}