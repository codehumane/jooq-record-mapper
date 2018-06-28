package codehumane.jooq;

import codehumane.common.Encodable;
import codehumane.common.ReflectionUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.EnumType;
import org.jooq.Record;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * {@link Record}를 Pojo 클래스로 변환 (자세한 내용과 쓰임은 JooqRecordToPojoMapperTest 참고)
 */
@Slf4j
public class CachedJooqRecordToPojoMapper implements JooqRecordToPojoMapper {

    private static final Set<FieldMapper> fieldMappers = new LinkedHashSet<>();
    private static final FieldMappingCache fieldMappingCache = new FieldMappingCache();

    static {
        fieldMappers.add(new EncodableFieldMapper<>()); // 순서 중요
        fieldMappers.add(new DefaultFieldMapper());
    }

    public <D, R extends Record> D map(R source, Class<D> destinationType) {
        final D destination = instantiateDestination(destinationType);
        return map(source, destination);
    }

    private <D> D instantiateDestination(Class<D> destinationType) {

        try {
            return ReflectionUtil.constructByDefault(destinationType);
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new JooqRecordMappingException("Failed instantiation", e);
        } catch (NoSuchMethodException e) {
            throw new JooqRecordMappingException("Failed instantiation - No default constructor.", e);
        }
    }

    public <D, R extends Record> D map(R source, D destination) {
        val sourceFields = source.fields();
        if (sourceFields == null || sourceFields.length < 1) {
            log.warn("No fields on source({}).", source.getClass());
            return destination;
        }

        val destinationFields = ReflectionUtil.getAllFields(destination.getClass());
        if (destinationFields.isEmpty()) {
            log.warn("No fields on destination({}).", destination.getClass());
            return destination;
        }

        val mappings = getMappings(source, destination, sourceFields, destinationFields);
        mappings.forEach(fieldMapping -> mapField(source, destination, fieldMapping));

        return destination;
    }

    private <R extends Record, D> Set<FieldMapping> getMappings(R source,
                                                                D destination,
                                                                org.jooq.Field<?>[] sourceFields,
                                                                List<Field> destinationFields) {

        val key = FieldMappingCache.Key.of(source, destination.getClass());
        val cached = fieldMappingCache.get(key);

        return cached.orElseGet(() -> {
            final Set<FieldMapping> generated = generateMappings(sourceFields, destinationFields);
            fieldMappingCache.put(key, generated);
            log.debug("mapping cached. key: {}", key);
            return generated;
        });
    }

    private Set<FieldMapping> generateMappings(org.jooq.Field<?>[] sourceFields,
                                               List<Field> destinationFields) {

        val mappings = new HashSet<FieldMapping>();

        destinationFields
                .forEach(d -> Stream
                        .of(sourceFields)
                        .filter(s -> JooqFieldTokenMatcher.match(s, d))
                        .map(s -> new FieldMapping(s, d))
                        .forEach(mappings::add));

        return mappings;
    }

    private <D, R extends Record> void mapField(R source, D destination, FieldMapping fieldMapping) {
        val sourceValue = source.get(fieldMapping.getSource());
        if (Objects.isNull(sourceValue))
            return;

        val fieldMapper = getFieldMapper(fieldMapping, sourceValue);
        fieldMapper.set(sourceValue, destination, fieldMapping.getTarget());
    }

    private FieldMapper getFieldMapper(FieldMapping fieldMapping, Object value) {
        final Predicate<FieldMapper> matching = m -> m.match(value, fieldMapping.getTarget());
        final Supplier<JooqRecordMappingException> throwing = () ->
                new JooqRecordMappingException(String.format(
                        "임시 예외 반환: Name matched but type not matched. [%s]",
                        fieldMapping.getTarget().getName()
                ));

        return fieldMappers
                .stream()
                .filter(matching)
                .findFirst()
                .orElseThrow(throwing);
    }


    /**
     * {@link Record} {@link org.jooq.Field}의 값을 Pojo 필드에 할당하기 위한 변환 모듈
     */
    interface FieldMapper {

        /**
         * {@link Record} {@link org.jooq.Field}의 값이 목적지 필드에 할당될 수 있는지 여부 검사
         *
         * @param sourceValue {@link Record} {@link org.jooq.Field}의 값
         * @param destination 목적지 필드
         * @return 매칭 여부
         */
        boolean match(Object sourceValue, Field destination);

        /**
         * {@link Record} {@link org.jooq.Field}의 값을 목적지 필드에 할당
         *
         * @param sourceValue      {@link Record} {@link org.jooq.Field}의 값
         * @param destination      목적지 클래스
         * @param destinationField 목적지 필드
         * @param <D>              목적지 클래스 타입
         */
        <D> void set(Object sourceValue, D destination, Field destinationField);
    }


    /**
     * {@link Record} {@link org.jooq.Field}의 값을 Encodable 타입의 필드에 할당하기 위한 {@link FieldMapper}
     *
     * @param <T> Encodable의 code 값 타입
     * @param <E> Encodable 구현체의 타입
     */
    static class EncodableFieldMapper<T, E extends Enum<E> & Encodable<T>> implements FieldMapper {

        @Override
        @SuppressWarnings("unchecked")
        public boolean match(Object sourceValue, Field destination) {
            if (!destination.getType().isEnum())
                return false;

            if (!Encodable.class.isAssignableFrom(destination.getType()))
                return false;

            final Class<?> sourceType;
            if (EnumType.class.isAssignableFrom(sourceValue.getClass())) {
                sourceType = ((EnumType) sourceValue).getLiteral().getClass();
            } else {
                sourceType = sourceValue.getClass();
            }

            final E firstEncodableValue = (E) destination.getType().getEnumConstants()[0];
            return sourceType == firstEncodableValue.getCode().getClass();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> void set(Object sourceValue, D destination, Field destinationField) {
            final Class<E> destinationFieldType = (Class<E>) destinationField.getType();

            final T encodableCode;
            if (EnumType.class.isAssignableFrom(sourceValue.getClass())) {
                encodableCode = (T) ((EnumType) sourceValue).getLiteral();
            } else {
                encodableCode = (T) sourceValue;
            }

            final E encodable = Encodable.codeOf(
                    destinationFieldType.getEnumConstants(), encodableCode);

            try {
                ReflectionUtil.setField(destination, destinationField, encodable);
            } catch (IllegalAccessException e) {
                throw new JooqRecordMappingException("Field set failed.", e);
            }
        }
    }


    /**
     * Encodable이 아닌 일반 데이터 타입의 변환을 위한 {@link FieldMapper}
     */
    static class DefaultFieldMapper implements FieldMapper {

        @Override
        public boolean match(Object sourceValue, Field destination) {
            return ReflectionUtil.isAssignable(destination.getType(), sourceValue.getClass());
        }

        @Override
        public <D> void set(Object sourceValue, D destination, Field destinationField) {
            try {
                ReflectionUtil.setField(destination, destinationField, sourceValue);
            } catch (IllegalAccessException e) {
                throw new JooqRecordMappingException("Field set failed.", e);
            }
        }
    }


    static class FieldMappingCache {

        private final Map<Key, Set<FieldMapping>> keyToMappings;

        FieldMappingCache() {
            keyToMappings = new ConcurrentHashMap<>();
        }

        Optional<Set<FieldMapping>> get(Key key) {
            return Optional.ofNullable(keyToMappings.get(key));
        }

        void put(Key key, Set<FieldMapping> mappings) {
            keyToMappings.put(key, mappings);
        }

        @Getter
        @ToString
        @EqualsAndHashCode
        static class Key {

            private final Class<?> sourceClazz;
            private final org.jooq.Field<?>[] sourceFields; // `sourceClass`가 `org.jooq.RecordImpl`인 경우가 다수여서 구분자가 더 필요함.
            private final Class<?> destinationClazz;

            private Key(Class<?> sourceClazz, org.jooq.Field<?>[] sourceFields,
                        Class<?> destinationClazz) {

                this.sourceClazz = sourceClazz;
                this.sourceFields = sourceFields;
                this.destinationClazz = destinationClazz;
            }

            static Key of(Record record, Class<?> destinationClazz) {
                return new Key(record.getClass(), record.fields(), destinationClazz);
            }
        }
    }


    @Getter
    private static class FieldMapping {

        private final org.jooq.Field<?> source;
        private final Field target;

        FieldMapping(org.jooq.Field<?> source, Field target) {
            this.source = source;
            this.target = target;
        }
    }
}