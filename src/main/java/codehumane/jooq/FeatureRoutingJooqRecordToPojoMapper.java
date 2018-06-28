package codehumane.jooq;

import codehumane.common.FeatureToggleProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.Record;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FeatureRoutingJooqRecordToPojoMapper implements JooqRecordToPojoMapper {

    private final CachedJooqRecordToPojoMapper cachedJooqRecordToPojoMapper;
    private final DefaultJooqRecordToPojoMapper defaultJooqRecordToPojoMapper;
    private final FeatureToggleProperties featureToggleProperties;

    @Override
    public <D, R extends Record> D map(R record, Class<D> destinationType) {
        return decideMapper().map(record, destinationType);
    }

    @Override
    public <D, R extends Record> D map(R record, D destination) {
        return decideMapper().map(record, destination);
    }

    private JooqRecordToPojoMapper decideMapper() {
        if (featureToggleProperties.isCachedJooqRecordMappingOn())
            return cachedJooqRecordToPojoMapper;
        return defaultJooqRecordToPojoMapper;
    }
}
