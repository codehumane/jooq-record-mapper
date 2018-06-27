package codehumane.jooq;

import org.jooq.Record;

/**
 * {@link org.jooq.Record} to POJO 변환기
 */
public interface JooqRecordToPojoMapper {

    /**
     * Jooq의 Record를 주어진 타입의 객체로 변환 (JOOQ에서 할 수 없는 {@link Encodable} 타입을 적절히 변환하는 작업을 포함함)
     *
     * @param record          JOOQ Record
     * @param destinationType 변환 결과 타입 클래스
     * @param <R>             JOOQ Record 구현체
     * @param <D>             변환 결과 타입
     * @return Record 대응 Map
     */
    <D, R extends Record> D map(R record, Class<D> destinationType);

    /**
     * Jooq의 Record 값을 객체에 할당 (JOOQ에서 할 수 없는 {@link Encodable} 타입을 적절히 변환하는 작업을 포함함)
     *
     * @param record      JOOQ Record
     * @param destination 할당 대상
     * @param <R>         JOOQ Record 구현체
     * @param <D>         변환 결과 타입
     */
    <D, R extends Record> D map(R record, D destination);
}
