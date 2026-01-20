package fr.insa.ms.decisionengineservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AlarmButtonClient {

    private final RestTemplate restTemplate;

    public AlarmButtonClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean getAlarmState() {
        Boolean v = restTemplate.getForObject("http://localhost:8083/alarm", Boolean.class);
        return v != null && v;
    }
}
