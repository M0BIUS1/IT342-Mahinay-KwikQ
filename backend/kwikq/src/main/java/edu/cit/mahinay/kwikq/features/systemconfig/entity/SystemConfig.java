package edu.cit.mahinay.kwikq.features.systemconfig.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "system_config")
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "config_key")
    private String configKey;
    @Column(name = "config_value")
    private String value;
    private String description;

    public SystemConfig() {}

    public SystemConfig(String configKey, String value, String description) {
        this.configKey = configKey;
        this.value = value;
        this.description = description;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
