package fr.insa.ms.firesensorservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class FireSensorController {

    private boolean fireDetected = false;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/fire")
    public boolean getFireState() {
        return fireDetected;
    }

    @PostMapping("/fire/simulate")
    public void simulateFire(@RequestParam("value") boolean newValue) {
        this.fireDetected = newValue;
        System.out.println("Nouvel état du capteur feu : " + newValue);
    }
    @PostMapping("/fire")
    public String setFire(@RequestParam boolean value) { 
    	this.fireDetected = value;
    	return restTemplate.postForObject(
	    	"http://localhost:8080/decision/evaluate",
	    	 null,
	    	 String.class
    	 );
    }
}