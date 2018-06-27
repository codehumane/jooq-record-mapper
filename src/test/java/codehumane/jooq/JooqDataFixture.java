package codehumane.jooq;

import org.jooq.DSLContext;
import org.jooq.Record;

public class JooqDataFixture {

    private final DSLContext dslContext;

    public JooqDataFixture(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    void deleteTable() {
        dslContext.execute("drop table `table_for_jooq_test`");
    }

    void createTable() {
        dslContext.execute("create table `table_for_jooq_test`(" +
                "    `id` bigint(20) null," +
                "    `creator` int(5) null," +
                "    `modifier` int(5) null," +
                "    `create_date` datetime null," +
                "    `last_modified_date` datetime null," +
                "    `clob_aaa_bbb333` tinytext null," +
                "    `varchar` varchar(32) not null," +
                "    `tinyint` tinyint null," +
                "    `smallint` smallint null," +
                "    `integer11` int null," +
                "    `long22` bigint null," +
                "    `double321` double null," +
                "    `varchar_enum` varchar(10) null," +
                "    `tinyint_enum` tinyint null," +
                "    `integer_enum` int null," +
                "    `timestamp` timestamp null" +
                ")");
    }

    void insertRecord() {
        dslContext.execute("insert into table_for_jooq_test values" +
                "('1004', " +
                "'5', " +
                "'6', " +
                "'2016-12-26 17:17:32', " +
                "'2017-01-05 14:38:12', " +
                "'하하하하하하하하하하하하하', " +
                "'하하하하하하하', " +
                "3, " +
                "333, " +
                "3333333, " +
                "333333333333, " +
                "3333333333333333333, " +
                "'CODE_A', " +
                "1, " +
                "0, " +
                "'2016-12-26 17:17:32')"
        );
    }

    Record selectLastInsertedRecord() {
        return selectLastInsertedRecord(false);
    }

    Record selectLastInsertedRecord(boolean withProjection) {
        return dslContext.fetchOne(String.format(
                "select %s from table_for_jooq_test order by id desc limit 1",
                withProjection ? "id, creator, varchar_enum" : "*"
        ));
    }
}
