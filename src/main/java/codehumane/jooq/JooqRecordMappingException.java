package codehumane.jooq;

class JooqRecordMappingException extends RuntimeException {

    JooqRecordMappingException(String message) {
        super(message);
    }

    JooqRecordMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
