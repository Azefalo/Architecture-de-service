package fr.insa.ms.decisionengineservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ActuatorClient {

    private final RestTemplate restTemplate;

    public ActuatorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void activateSiren(boolean value) {
        restTemplate.postForObject("http://localhost:8084/actuators/siren?value=" + value, null, Void.class);
    }

    public void setLights(boolean value) {
        restTemplate.postForObject("http://localhost:8084/actuators/lights?value=" + value, null, Void.class);
    }

    public void setDoors(String state) {
        restTemplate.postForObject("http://localhost:8084/actuators/doors?state=" + state, null, Void.class);
    }
}
