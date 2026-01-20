package fr.insa.ms.decisionengineservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class HistoryClient {

  private final RestTemplate restTemplate;

  public HistoryClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public void saveEvent(String sensorType, String value, String actionTriggered) {
    String url = "http://localhost:8085/history/save";

    Map<String, Object> body = new HashMap<>();
    body.put("sensorType", sensorType);
    body.put("value", value);
    body.put("actionTriggered", actionTriggered);
    body.put("timestamp", LocalDateTime.now().withNano(0).toString()); // "2026-01-14T12:40:00"

    restTemplate.postForObject(url, body, Void.class);
  }
}
