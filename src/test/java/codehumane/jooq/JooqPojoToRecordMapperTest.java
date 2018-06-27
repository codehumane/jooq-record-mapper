package codehumane.jooq;

import org.jooq.DSLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@Import(JooqTestConfig.class)
public class JooqPojoToRecordMapperTest {

    @Autowired
    private DSLContext dslContext;

    private JooqPojoToRecordMapper mapper = new JooqPojoToRecordMapper();

    @Test
    public void map_기본적인_타입의_필드를_매핑할_수_있다() throws Exception {

        // given
        final DummyJooqTableRecord record = dslContext.newRecord(DummyJooqTable.DUMMY_JOOQ_TABLE);

        // given
        final DummyPojo dummyPojo = new DummyPojo();
        dummyPojo.clobAaaBbb333 = "클롭";
        dummyPojo.varchar = "바차";
        dummyPojo.tinyint = Byte.valueOf("1");
        dummyPojo.smallint = Short.valueOf("2");
        dummyPojo.integer11 = 3;
        dummyPojo.long22 = 4L;
        dummyPojo.double321 = Double.valueOf("5");

        // when
        mapper.map(dummyPojo, record);

        // then
        assertEquals(dummyPojo.clobAaaBbb333, record.getClobAaaBbb333());
        assertEquals(dummyPojo.varchar, record.getVarchar());
        assertEquals(dummyPojo.tinyint, record.getTinyint());
        assertEquals(dummyPojo.smallint, record.getSmallint());
        assertEquals(dummyPojo.integer11, record.getInteger11());
        assertEquals(dummyPojo.long22, record.getLong22());
        assertEquals(dummyPojo.double321, record.getDouble321());
    }

    @Test
    public void map_Encodable_타입의_필드를_매핑할_수_있다() throws Exception {

        // given
        final DummyJooqTableRecord record = dslContext.newRecord(DummyJooqTable.DUMMY_JOOQ_TABLE);

        // given
        final DummyPojo dummyPojo = new DummyPojo();
        dummyPojo.integerEnum = DummyPojo.IntegerEnum.N;
        dummyPojo.tinyintEnum = DummyPojo.ByteEnum.Y;
        dummyPojo.varcharEnum = DummyPojo.VarcharEnum.TYPE_B;

        // when
        mapper.map(dummyPojo, record);

        // then
        assertEquals(dummyPojo.integerEnum.getCode(), record.getIntegerEnum());
        assertEquals(dummyPojo.tinyintEnum.getCode(), record.getTinyintEnum());
        assertEquals(dummyPojo.varcharEnum.getCode(), record.getVarcharEnum());
    }

    @Test
    public void map_Encodable_타입의_필드를_Jooq_Enum_타입의_필드로_매핑할_수_있다() throws Exception {

        // given
        final DummyJooqTableRecord record = dslContext.newRecord(DummyJooqTable.DUMMY_JOOQ_TABLE);
        final DummyPojo dummyPojo = new DummyPojo();
        dummyPojo.jooqEnum = YNBoolean.N;

        // when
        mapper.map(dummyPojo, record);

        // then
        assertEquals(dummyPojo.jooqEnum.getCode(), record.getJooqEnum().getLiteral());
    }
}