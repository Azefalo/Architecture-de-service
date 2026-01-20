package fr.insa.ms.gassensorservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class GasSensorController {

    private double gasLevel = 60.0;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @GetMapping("/gas")
    public double getGasLevel() {
        return gasLevel;
    }

    @PostMapping("/gas/simulate")
    public void simulateGas(@RequestParam("value") double newValue) {
        this.gasLevel = newValue;
        System.out.println("Nouveau niveau de gaz simulé : " + newValue);
    }
    @PostMapping("/gas")
    public String setGas(@RequestParam double value) { 
    	this.gasLevel = value; 
	    return restTemplate.postForObject(
	    	      "http://localhost:8080/decision/evaluate",
	    	      null,
	    	      String.class
	    	    );
    }
}
