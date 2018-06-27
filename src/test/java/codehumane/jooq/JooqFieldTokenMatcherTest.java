package codehumane.jooq;

import static org.junit.Assert.*;

import org.junit.Test;

public class JooqFieldTokenMatcherTest {

    @Test
    public void tokenize_camelcase를_기반으로_하되_숫자를_토크나이저_기준으로_삼지는_않는다() throws Exception {
        // given
        final JooqFieldTokenMatcher.NumberIgnoreCamelCaseTokenizer tokenizer = new JooqFieldTokenMatcher.NumberIgnoreCamelCaseTokenizer();

        // then
        assertArrayEquals(new String[]{"up", "File1"}, tokenizer.tokenize("upFile1"));
        assertArrayEquals(new String[]{"up", "File12"}, tokenizer.tokenize("upFile12"));
        assertArrayEquals(new String[]{"H", "Ab1d"}, tokenizer.tokenize("HAb1d"));
        assertArrayEquals(new String[]{"Hello", "Universe", "Q"}, tokenizer.tokenize("HelloUniverseQ"));
        assertArrayEquals(new String[]{"up", "_1"}, tokenizer.tokenize("up_1"));
    }
}