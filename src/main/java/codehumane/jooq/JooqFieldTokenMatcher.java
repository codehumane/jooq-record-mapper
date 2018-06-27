package codehumane.jooq;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

class JooqFieldTokenMatcher {

    private static final NumberIgnoreCamelCaseTokenizer camelCaseTokenizer = new NumberIgnoreCamelCaseTokenizer();
    private static final UnderscoreTokenizer underscoreTokenizer = new UnderscoreTokenizer();

    /**
     * Jooq Record의 필드명 형태(underscore)와 자바의 클래스 필드명 형태(camelcase)가 다르기 때문에, <br/>
     * 이름을 토큰으로 분해한 후 매칭여부를 결정한다.
     *
     * @param jooqField Jooq Record의 필드
     * @param pojoField Java Pojo의 필드
     * @return 매칭 여부
     */
    static boolean match(org.jooq.Field<?> jooqField, Field pojoField) {
        final String[] pojoTokens = camelCaseTokenizer.tokenize(pojoField.getName());
        final String[] jooqTokens = underscoreTokenizer.tokenize(
                jooqField.getName().toLowerCase());

        if (jooqTokens.length != pojoTokens.length)
            return false;

        for (int i = 0; i < jooqTokens.length; i++) {
            if (!jooqTokens[i].equalsIgnoreCase(pojoTokens[i]))
                return false;
        }

        return true;
    }

    /**
     * 숫자는 tokenize 기준으로 삼지 않는 camel case tokenizer
     */
    static class NumberIgnoreCamelCaseTokenizer {

        private static final String UPPER_AND_UPPER_LOWER = "(?<=[A-Z])(?=[A-Z][a-z0-9])"; // 예시) UFile -> [U,File], UpU1 -> [Up,U1]
        private static final String NON_UPPER_AND_UPPER = "(?<=[^A-Z])(?=[A-Z])"; // 예시) uP -> [u,P], u1P -> [u1,P]
        private static final String ETC = "(?<=[A-Za-z0-9])(?=[^A-Za-z0-9])"; // 예시) up_1 -> [up,_1]
        private static final Pattern camelCase = Pattern.compile(
                String.format("%s|%s|%s", UPPER_AND_UPPER_LOWER, NON_UPPER_AND_UPPER, ETC));

        String[] tokenize(String name) {
            return camelCase.split(name);
        }
    }

    /**
     * underscore tokenizer
     */
    static class UnderscoreTokenizer {

        private static final Pattern underscore = Pattern.compile("_");

        String[] tokenize(String name) {
            return underscore.split(name);
        }
    }
}
