package io.drahlek.smartfeedingtroughs.config;

import io.drahlek.dirigo.annotation.ConfigSetting;
import lombok.Data;

@Data
public class SmartFeedingTroughConfigData {
    @ConfigSetting(value = "range", min = 1, max = 32, defaultValue = "16")
    private int range = 16;

    @ConfigSetting(value = "maxClaimedAnimals", min = 4, max = 24, defaultValue = "24")
    private int maxClaimedAnimals = 24;

    @ConfigSetting(value = "troughClaimCheckInterval", min = 1, defaultValue = "600") //30 second default
    private int troughClaimCheckInterval = 600;

    @ConfigSetting(value = "feedChance", min = 0.01f, max = 1f, defaultValue = "0.25f")
    private float feedChance = 0.25f;
}
