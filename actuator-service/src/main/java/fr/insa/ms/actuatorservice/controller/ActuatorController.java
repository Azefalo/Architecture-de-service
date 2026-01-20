package fr.insa.ms.actuatorservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActuatorController {

    private boolean sirenOn = false;
    private boolean lightsOn = false;
    private String doorsState = "CLOSED";

    // LIRE L'ÉTAT DES ACTIONNEURS

    @GetMapping("/actuators/state")
    public String getState() {
        return "siren=" + sirenOn +
               ", lights=" + lightsOn +
               ", doors=" + doorsState;
    }

    // COMMANDER LA SIRÈNE 

    @PostMapping("/actuators/siren")
    public void setSiren(@RequestParam boolean value) {
        this.sirenOn = value;
        System.out.println("Sirène -> " + (value ? "ON" : "OFF"));
    }

    // COMMANDER LES LUMIÈRES 

    @PostMapping("/actuators/lights")
    public void setLights(@RequestParam boolean value) {
        this.lightsOn = value;
        System.out.println("Lumières d'urgence -> " + (value ? "ON" : "OFF"));
    }

    // COMMANDER LES PORTES

    @PostMapping("/actuators/doors")
    public void setDoors(@RequestParam String state) {
        if (!state.equals("OPEN") && !state.equals("CLOSED")) {
            System.out.println("État invalide pour les portes");
            return;
        }
        this.doorsState = state;
        System.out.println("Portes coupe-feu -> " + state);
    }
}
