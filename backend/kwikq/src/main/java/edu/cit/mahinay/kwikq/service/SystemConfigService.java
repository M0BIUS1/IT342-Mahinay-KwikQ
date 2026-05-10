package edu.cit.mahinay.kwikq.service;

import edu.cit.mahinay.kwikq.features.systemconfig.entity.SystemConfig;
import edu.cit.mahinay.kwikq.features.systemconfig.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository configRepository;

    public String getConfig(String key, String defaultValue) {
        return configRepository.findByKey(key)
                .map(SystemConfig::getValue)
                .orElse(defaultValue);
    }

    public SystemConfig setConfig(String key, String value, String description) {
        SystemConfig config = configRepository.findByKey(key)
                .orElse(new SystemConfig(key, value, description));
        config.setValue(value);
        config.setDescription(description);
        return configRepository.save(config);
    }

    public List<SystemConfig> getAllConfigs() {
        return configRepository.findAll();
    }

    public Double getFineRate() {
        return Double.parseDouble(getConfig("FINE_RATE", "10.0"));
    }

    public Integer getBorrowingLimit() {
        return Integer.parseInt(getConfig("BORROWING_LIMIT", "5"));
    }

    public Integer getLoanDays() {
        return Integer.parseInt(getConfig("LOAN_DAYS", "14"));
    }
}
