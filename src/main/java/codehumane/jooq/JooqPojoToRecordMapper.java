package codehumane.jooq;

import codehumane.common.Encodable;
import codehumane.common.Identifiable;
import codehumane.common.ReflectionUtil;
import org.jooq.EnumType;
import org.jooq.Record;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 자바의 Pojo를 Jooq의 {@link Record}로 변환 (보다 자세한 내용과 쓰임은 JooqPojoToRecordMapperTest 참고)
 */
class JooqPojoToRecordMapper {

    private static final Set<FieldMapper> fieldMappers = new LinkedHashSet<>();

    static {
        fieldMappers.add(new EncodableFieldMapper<>()); // 순서 중요
        fieldMappers.add(new DefaultFieldMapper());
    }

    <R extends Record, S extends Identifiable> void map(S source, R record) {
        final List<Field> sourceFields = ReflectionUtil.getAllFields(source.getClass());
        for (org.jooq.Field<?> recordField : record.fields()) {
            for (Field sourceField : sourceFields) {
                mapField(source, record, recordField, sourceField);
            }
        }
    }

    private <R extends Record, S extends Identifiable> void mapField(
            S source, R record, org.jooq.Field<?> recordField, Field sourceField) {

        final Object sourceValue = getSourceValue(source, sourceField);
        if (!JooqFieldTokenMatcher.match(recordField, sourceField)) {
            return;
        }

        if (Objects.isNull(sourceValue)) {
            // JOOQ는 UPDATE문 생성시에 set 절에 포함시킬 필드를 결정하기 위해 changed 여부를 검사한다.
            // changed 상태가 되기 위해서는 `record.set`이 명시적으로 호출되어야 함. (혹은 `changed`를 호출할수도 있으나 별로 좋은 방법은 아님)
            // 그리고 UPDATE 시에는 `null`로 필드값을 할당할 수 있어야 한다.
            // 따라서, 값이 null이더라도 set을 호출함.
            record.set(recordField, null);
            return;
        }

        final FieldMapper delegate = fieldMappers.stream()
                .filter(x ->
                        x.match(sourceValue, recordField))
                .findFirst()
                .orElseThrow(() ->
                        new JooqRecordMappingException(String.format(
                                "임시 예외 반환: Name matched but type not matched. [%s]",
                                recordField.getName()
                        )));

        delegate.map(sourceField, sourceValue, record, recordField);
    }

    private <S extends Identifiable> Object getSourceValue(S source, Field sourceField) {

        try {
            return ReflectionUtil.getField(source, sourceField);
        } catch (IllegalAccessException e) {
            throw new JooqRecordMappingException("Source field not accessible.", e);
        }
    }

    interface FieldMapper {

        boolean match(Object sourceValue, org.jooq.Field<?> destinationField);

        <R extends Record> void map(
                Field sourceField, Object sourceValue, R destination, org.jooq.Field<?> destinationField);

        default <R extends Record> void invokeRecordSetterMethod(R record, Field sourceField, Object setterValue) {
            final String recordSetterName = "set".concat(StringUtils.capitalize(sourceField.getName()));
            ReflectionUtil.invokeMethodForSingleArgument(record, recordSetterName, setterValue);
        }
    }


    static class EncodableFieldMapper<T, E extends Enum<E> & Encodable<T>> implements FieldMapper {

        @Override
        @SuppressWarnings("unchecked")
        public boolean match(Object sourceValue, org.jooq.Field<?> destinationField) {
            if (!sourceValue.getClass().isEnum() ||
                    !Encodable.class.isAssignableFrom(sourceValue.getClass())) {
                return false;
            }

            final Class<?> encodableCodeClass = ((E) sourceValue).getCode().getClass();
            final Class<?> destinationFieldType = destinationField.getType();

            if (destinationFieldType.isEnum()) {
                // org.jooq.EnumType에 대응되는 Encodable은 code 타입이 String인 것만 허용
                return encodableCodeClass == String.class
                        && EnumType.class.isAssignableFrom(destinationFieldType);
            } else {
                return destinationFieldType == encodableCodeClass;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R extends Record> void map(
                Field sourceField, Object sourceValue, R destination, org.jooq.Field<?> destinationField) {

            final E sourceValueAsEncodable = (E) sourceValue;
            final T sourceCode = sourceValueAsEncodable.getCode();

            final Object setterValue;
            if (destinationField.getType().isEnum()) {
                final String sourceCodeAsString = (String) sourceCode;
                final Class<? extends Enum> destinationFieldType =
                        (Class<? extends Enum>) destinationField.getType();

                setterValue = Enum.valueOf(destinationFieldType, sourceCodeAsString);
            } else {
                setterValue = sourceCode;
            }

            invokeRecordSetterMethod(destination, sourceField, setterValue);
        }
    }


    static class DefaultFieldMapper implements FieldMapper {

        @Override
        public boolean match(Object sourceValue, org.jooq.Field<?> destinationField) {
            return ReflectionUtil.isAssignable(sourceValue.getClass(), destinationField.getType());
        }

        @Override
        public <R extends Record> void map(
                Field sourceField, Object sourceValue, R destination, org.jooq.Field<?> destinationField) {
            Object finalSourceValue = downcastValueIfRequired(destinationField, sourceValue);
            invokeRecordSetterMethod(destination, sourceField, finalSourceValue);
        }

        /**
         * reflection method invoke 시, <br/>
         * 하위 타입의 파라미터에 상위 타입의 값을 넘길 수 없음. <br/>
         * Jooq에서 사용되는 타입들을 대상으로 한정하여, 적절히 변환 수행.
         */
        @SuppressWarnings("unchecked")
        private Object downcastValueIfRequired(org.jooq.Field<?> recordField, Object sourceValue) {

            if (sourceValue.getClass() == Date.class) {
                // java.util.Date 타입의 값을 하위 타입으로 적절히 변환
                final Date sourceValueAsDate = (Date) sourceValue;
                if (recordField.getType() == java.sql.Date.class) {

                    return new java.sql.Date(sourceValueAsDate.getTime());
                } else if (recordField.getType() == java.sql.Timestamp.class) {
                    return new java.sql.Date(sourceValueAsDate.getTime());
                }
            }

            return sourceValue;
        }

    }
}
