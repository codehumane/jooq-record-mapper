package codehumane.jooq;

import org.jooq.EnumType;
import org.jooq.Schema;

/**
 * Jooq 테스트를 위한 더미 객체
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public enum DummyJooqEnum implements EnumType {

    Y("Y"),
    N("N");

    private final String literal;

    private DummyJooqEnum(String literal) {
        this.literal = literal;
    }

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public String getName() {
        return "jooq_enum_type";
    }

    @Override
    public String getLiteral() {
        return literal;
    }
}
