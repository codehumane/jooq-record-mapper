package codehumane.jooq;

import lombok.Getter;

/**
 * @author ykpark@woowahan.com
 */
@Getter
public enum YNBoolean implements Encodable<String> {

    Y("Y"), N("N");

    private final String code;

    YNBoolean(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    public static YNBoolean codeOf(String code) {
        return Encodable.codeOf(values(), code);
    }
}
