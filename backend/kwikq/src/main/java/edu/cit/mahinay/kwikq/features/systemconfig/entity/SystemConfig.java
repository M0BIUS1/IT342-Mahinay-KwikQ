package edu.cit.mahinay.kwikq.features.systemconfig.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "system_config")
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key")
    private String key;

    @Column(name = "config_value")
    private String value;

    @Column(name = "config_description")
    private String description;

    public SystemConfig() {}

    public SystemConfig(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
