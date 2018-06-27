package codehumane.jooq;

import org.jooq.Field;
import org.jooq.Record;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@Import(JooqTestConfig.class)
public class JooqRecordToPojoMapperTest<T extends JooqRecordToPojoMapper> {

    @Autowired
    private JooqDataFixture jooqDataFixture;

    @Autowired
    private JooqRecordToPojoMapper mapper;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Before
    public void setup() throws IllegalAccessException, InstantiationException {
        jooqDataFixture.createTable();
    }

    @After
    public void tearDown() throws Exception {
        jooqDataFixture.deleteTable();
    }

    @Test
    public void map() throws Exception {
        // given
        jooqDataFixture.insertRecord();
        final Record record = jooqDataFixture.selectLastInsertedRecord();

        // when
        final DummyPojo mapped = mapper.map(record, DummyPojo.class);

        // then
        assertEquals("자신에게 없는 부모의 부모 속성(Long)도 변환되야 한다.", Long.valueOf(1004), mapped.getId());
        assertEquals("Clob(tinytext) 형태는 String으로 변환되야 한다.", "하하하하하하하하하하하하하", mapped.clobAaaBbb333);
        assertEquals("varchar(32)는 String으로 변환되야 한다.", "하하하하하하하", mapped.varchar);
        assertEquals("tinyint는 Byte로 변환되야 한다.", Byte.valueOf("3"), mapped.tinyint);
        assertEquals("smallint는 Short로 변환되야 한다.", Short.valueOf("333"), mapped.smallint);
        assertEquals("int는 Integer로 변환되야 한다.", Integer.valueOf("3333333"), mapped.integer11);
        assertEquals("bigint는 Long으로 변환되야 한다.", Long.valueOf("333333333333"), mapped.long22);
        assertEquals("double는 Double으로 변환되야 한다.", Double.valueOf("3333333333333333333"), mapped.double321);
        assertEquals("varchar는 Encodable<String>으로 변환될 수 있어야 한다.", DummyPojo.VarcharEnum.TYPE_A, mapped.varcharEnum);
        assertEquals("tinyint는 Encodable<Byte>로 변환될 수 있어야 한다.", DummyPojo.ByteEnum.Y, mapped.tinyintEnum);
        assertEquals("int는 Encodable<Integer>로 변환될 수 있어야 한다.", DummyPojo.IntegerEnum.N, mapped.integerEnum);
        assertEquals("timestamp는 Date로 변환될 수 있어야 한다.", dateFormat.parse("2016-12-26 17:17:32"), mapped.timestamp);
    }

    @Test
    public void map_jooq의_EnumType_구현체에_대해서도_변환_가능하다() throws Exception {
        // given
        Map<String, Object> expected = new HashMap<>();
        expected.put("id", Short.valueOf("3"));
        expected.put("jooq_enum", DummyJooqEnum.N);

        final Record record = generateRecordForJooqEnum(expected);

        // when
        final PojoForJooqEnum mapped = mapper.map(record, PojoForJooqEnum.class);

        // then
        assertNotNull(mapped);
        assertEquals(expected.get("id"), mapped.id);
        assertEquals(YNBoolean.N, mapped.jooqEnum);
    }

    @SuppressWarnings("unchecked")
    private Record generateRecordForJooqEnum(Map<String, Object> expected) {
        final Record record = mock(Record.class);
        List<Field<?>> fields = new ArrayList<>();

        expected.forEach((name, value) -> {
            final Field field = mock(Field.class);
            given(field.getName()).willReturn(name);
            given(record.get(field)).willReturn(value);
            fields.add(field);
        });

        final Field[] fieldsArray = Arrays.copyOf(
                fields.toArray(), fields.size(), Field[].class);
        given(record.fields()).willReturn(fieldsArray);

        return record;
    }


    public static class PojoForJooqEnum {

        PojoForJooqEnum() {
            // 접근자가 public이 아닌 생성자에 대해서도 변환 가능해야 한다.
        }

        Short id;
        YNBoolean jooqEnum;
    }

}