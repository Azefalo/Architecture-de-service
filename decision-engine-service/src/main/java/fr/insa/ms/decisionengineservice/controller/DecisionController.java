package fr.insa.ms.decisionengineservice.controller;

import fr.insa.ms.decisionengineservice.client.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DecisionController {

    private final GasSensorClient gasClient;
    private final FireSensorClient fireClient;
    private final AlarmButtonClient alarmClient;
    private final ActuatorClient actuatorClient;
    private final HistoryClient historyClient;

    public DecisionController(
            GasSensorClient gasClient,
            FireSensorClient fireClient,
            AlarmButtonClient alarmClient,
            ActuatorClient actuatorClient,
            HistoryClient historyClient
    ) {
        this.gasClient = gasClient;
        this.fireClient = fireClient;
        this.alarmClient = alarmClient;
        this.actuatorClient = actuatorClient;
        this.historyClient = historyClient;
    }

    @PostMapping("/decision/evaluate")
    public String evaluate() {

        double gas = gasClient.getGasLevel();
        boolean fire = fireClient.getFireState();
        boolean alarm = alarmClient.getAlarmState();

        System.out.println("État des capteurs : gas=" + gas + ", fire=" + fire + ", alarm=" + alarm);

        // 1) On log les lectures (1 ligne par capteur)
        historyClient.saveEvent("gas", String.valueOf(gas), "lecture");
        historyClient.saveEvent("fire", String.valueOf(fire), "lecture");
        historyClient.saveEvent("alarm", String.valueOf(alarm), "lecture");

        String actions = "";

        // RÈGLE 1 : Gaz
        if (gas > 50) {
            actuatorClient.activateSiren(true);
            actuatorClient.setLights(true);
            actuatorClient.setDoors("OPEN");

            String a = "Gaz eleve -> sirene ON, lumieres ON, portes OPEN";
            actions += a + "\n";

            // 2) On log l'action déclenchée
            historyClient.saveEvent("gas", String.valueOf(gas), a);
        }

        // RÈGLE 2 : Incendie
        if (fire) {
            actuatorClient.activateSiren(true);
            actuatorClient.setLights(true);
            actuatorClient.setDoors("OPEN");

            String a = "Feu detecte -> sirene ON, lumieres ON, portes OPEN";
            actions += a + "\n";

            historyClient.saveEvent("fire", "true", a);
        }

        // RÈGLE 3 : Bouton d’alarme
        if (alarm) {
            actuatorClient.activateSiren(true);
            actuatorClient.setLights(true);
            actuatorClient.setDoors("OPEN");

            String a = "Bouton alarme -> sirene ON, lumieres ON, portes OPEN";
            actions += a + "\n";

            historyClient.saveEvent("alarm", "true", a);
        }

        return actions.isEmpty() ? "Aucune action nécessaire." : actions;
    }
}
