package io.drahlek.smartbreedingtroughs.config;

import io.drahlek.dirigo.config.Config;
import io.drahlek.smartbreedingtroughs.Constants;

public class SmartBreedingTroughConfig extends Config<SmartBreedingTroughConfigData> {
    private static final SmartBreedingTroughConfig INSTANCE = new SmartBreedingTroughConfig();

    private SmartBreedingTroughConfig() {
        super(Constants.MOD_ID, Constants.MOD_NAME, Constants.MOD_ID + ".json", SmartBreedingTroughConfigData.class);
    }

    public static SmartBreedingTroughConfig instance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("SmartBreedingTroughConfig has not been initialized");
        }

        return INSTANCE;
    }

    public static SmartBreedingTroughConfigData data() {
        return instance().get();
    }
}
