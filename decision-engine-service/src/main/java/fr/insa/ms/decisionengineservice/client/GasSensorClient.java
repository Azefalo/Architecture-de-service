package fr.insa.ms.decisionengineservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GasSensorClient {

    private final RestTemplate restTemplate;

    public GasSensorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getGasLevel() {
        String url = "http://localhost:8081/gas"; // le port de gas-sensor-service
        return restTemplate.getForObject(url, Double.class);
    }
}
