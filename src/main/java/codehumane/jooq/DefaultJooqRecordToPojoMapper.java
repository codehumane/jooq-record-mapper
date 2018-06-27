package codehumane.jooq;

import lombok.extern.slf4j.Slf4j;
import org.jooq.EnumType;
import org.jooq.Record;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * {@link Record}를 Pojo 클래스로 변환 (자세한 내용과 쓰임은 JooqRecordToPojoMapperTest 참고)
 */
@Slf4j
public class DefaultJooqRecordToPojoMapper implements JooqRecordToPojoMapper {

    private static final Set<FieldMapper> fieldMappers = new LinkedHashSet<>();

    static {
        fieldMappers.add(new EncodableFieldMapper<>()); // 순서 중요
        fieldMappers.add(new DefaultFieldMapper());
    }

    public <D, R extends Record> D map(R source, Class<D> destinationType) {
        final D destination;

        try {
            destination = ReflectionUtil.constructByDefault(destinationType);
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new JooqRecordMappingException("Failed instantiation", e);
        } catch (NoSuchMethodException e) {
            throw new JooqRecordMappingException("Failed instantiation - No default constructor.", e);
        }

        return map(source, destination);
    }

    public <D, R extends Record> D map(R source, D destination) {
        final List<Field> destinationFields = ReflectionUtil.getAllFields(destination.getClass());
        if (destinationFields.isEmpty()) {
            log.warn("No fields on destination({}).", destination.getClass());
            return destination;
        }

        final org.jooq.Field<?>[] sourceFields = source.fields();
        if (sourceFields == null || sourceFields.length < 1) {
            log.warn("No fields on source({}).", source.getClass());
            return destination;
        }

        // 불필요한 변환을 막기 위해 Pojo의 필드를 기준으로 루프 순환
        destinationFields.forEach(destinationField ->
                fieldMap(source, sourceFields, destination, destinationField));

        return destination;
    }

    // 필드에 대한 변환 수행
    private <D, R extends Record> void fieldMap(
            R source, org.jooq.Field<?>[] sourceFields, D destination, Field destinationField) {

        Stream.of(sourceFields)
                .filter(matchToken(destinationField))
                .map(source::get)
                .filter(Objects::nonNull)
                .forEach(delegateToFieldMapper(destination, destinationField));
    }

    private Predicate<org.jooq.Field<?>> matchToken(Field destinationField) {
        return sourceField -> JooqFieldTokenMatcher.match(sourceField, destinationField);
    }

    private <D> Consumer<Object> delegateToFieldMapper(D destination, Field destinationField) {

        return value -> {
            final FieldMapper delegate = fieldMappers.stream()
                    .filter(m ->
                            m.match(value, destinationField))
                    .findFirst()
                    .orElseThrow(() ->
                            new JooqRecordMappingException(String.format(
                                    "임시 예외 반환: Name matched but type not matched. [%s]",
                                    destinationField.getName()
                            )));

            delegate.set(value, destination, destinationField);
        };
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
}
