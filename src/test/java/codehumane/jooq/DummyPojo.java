package codehumane.jooq;

import codehumane.common.Encodable;
import codehumane.common.Identifiable;
import codehumane.common.YNBoolean;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Jooq 테스트를 위한 더미 객체
 */
@Getter
@SuppressWarnings("UnusedDeclaration")
class DummyPojo implements Identifiable<Long> {

    @Setter
    Long id;
    String clobAaaBbb333;
    String varchar;
    Byte tinyint;
    Short smallint;
    Integer integer11;
    Long long22;
    Double double321;
    VarcharEnum varcharEnum;
    ByteEnum tinyintEnum;
    IntegerEnum integerEnum;
    Date timestamp;
    YNBoolean jooqEnum;

    DummyPojo() {
        // 접근자가 public이 아닌 생성자에 대해서도 변환 가능해야 한다.
    }

    DummyPojo(String clobAaaBbb333,
              String varchar,
              Byte tinyint,
              Short smallint,
              Integer integer11,
              Long long22,
              Double double321,
              VarcharEnum varcharEnum,
              ByteEnum tinyintEnum,
              IntegerEnum integerEnum,
              Date timestamp,
              YNBoolean jooqEnum) {

        this.clobAaaBbb333 = clobAaaBbb333;
        this.varchar = varchar;
        this.tinyint = tinyint;
        this.smallint = smallint;
        this.integer11 = integer11;
        this.long22 = long22;
        this.double321 = double321;
        this.varcharEnum = varcharEnum;
        this.tinyintEnum = tinyintEnum;
        this.integerEnum = integerEnum;
        this.timestamp = timestamp;
        this.jooqEnum = jooqEnum;
    }


    @SuppressWarnings("unused")
    public enum VarcharEnum implements Encodable<String> {

        TYPE_A("CODE_A"), TYPE_B("CODE_B");

        private final String code;

        VarcharEnum(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }
    }

    @SuppressWarnings("unused")
    public enum ByteEnum implements Encodable<Byte> {

        Y(Byte.valueOf("1")), N(Byte.valueOf("0"));

        private final Byte code;

        ByteEnum(Byte code) {
            this.code = code;
        }

        @Override
        public Byte getCode() {
            return code;
        }
    }

    @SuppressWarnings("unused")
    public enum IntegerEnum implements Encodable<Integer> {

        Y(1), N(0);

        private final Integer code;

        IntegerEnum(Integer code) {
            this.code = code;
        }

        @Override
        public Integer getCode() {
            return code;
        }
    }
}
