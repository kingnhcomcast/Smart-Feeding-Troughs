package io.drahlek.smartfeedingtroughs.config;

import io.drahlek.dirigo.config.Config;
import io.drahlek.smartfeedingtroughs.Constants;

public class SmartFeedingTroughConfig extends Config<SmartFeedingTroughConfigData> {
    private static final SmartFeedingTroughConfig INSTANCE = new SmartFeedingTroughConfig();

    private SmartFeedingTroughConfig() {
        super(Constants.MOD_ID, Constants.MOD_NAME, Constants.MOD_ID + ".json", SmartFeedingTroughConfigData.class);
    }

    public static SmartFeedingTroughConfig instance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("SmartFeedingTroughConfig has not been initialized");
        }

        return INSTANCE;
    }

    public static SmartFeedingTroughConfigData data() {
        return instance().get();
    }
}
