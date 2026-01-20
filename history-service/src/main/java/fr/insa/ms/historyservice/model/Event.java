package fr.insa.ms.historyservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Column(name = "sensor_type")
    private String sensorType;

    private String value;

    @Column(name = "action_triggered")
    private String actionTriggered;

    public Event() {}

    public Long getId() { return id; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getSensorType() { return sensorType; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getActionTriggered() { return actionTriggered; }
    public void setActionTriggered(String actionTriggered) { this.actionTriggered = actionTriggered; }
}
