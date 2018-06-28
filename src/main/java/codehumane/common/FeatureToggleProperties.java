package codehumane.common;

import org.springframework.core.env.Environment;

public class FeatureToggleProperties {

    private Environment environment;

    public FeatureToggleProperties(Environment environment) {
        this.environment = environment;
    }

    public boolean isCachedJooqRecordMappingOn() {
        return environment.getProperty(
                "feature.toggle.cachedJooqRecordMapping",
                Boolean.class,
                false
        );
    }
}
