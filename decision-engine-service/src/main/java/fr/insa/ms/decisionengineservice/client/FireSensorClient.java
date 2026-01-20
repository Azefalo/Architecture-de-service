package fr.insa.ms.decisionengineservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FireSensorClient {

    private final RestTemplate restTemplate;

    public FireSensorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean getFireState() {
        Boolean v = restTemplate.getForObject("http://localhost:8082/fire", Boolean.class);
        return v != null && v;
    }
}
