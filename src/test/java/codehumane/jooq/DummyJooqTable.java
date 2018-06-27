/**
 * This class is generated by jOOQ
 */
package codehumane.jooq;


import org.jooq.Field;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.sql.Timestamp;


/**
 * Jooq 테스트를 위한 더미 객체
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
class DummyJooqTable extends TableImpl<DummyJooqTableRecord> {

    private static final long serialVersionUID = -303390578;

    /**
     * The reference instance of <code>dev_dums.table_for_jooq_test</code>
     */
    public static final DummyJooqTable DUMMY_JOOQ_TABLE = new DummyJooqTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DummyJooqTableRecord> getRecordType() {
        return DummyJooqTableRecord.class;
    }

    /**
     * The column <code>dev_dums.table_for_jooq_test.id</code>.
     */
    public final TableField<DummyJooqTableRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.creator</code>.
     */
    public final TableField<DummyJooqTableRecord, Integer> CREATOR = createField("creator", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.modifier</code>.
     */
    public final TableField<DummyJooqTableRecord, Integer> MODIFIER = createField("modifier", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.create_date</code>.
     */
    public final TableField<DummyJooqTableRecord, Timestamp> CREATE_DATE = createField("create_date", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.last_modified_date</code>.
     */
    public final TableField<DummyJooqTableRecord, Timestamp> LAST_MODIFIED_DATE = createField("last_modified_date", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.clob_aaa_bbb333</code>.
     */
    public final TableField<DummyJooqTableRecord, String> CLOB_AAA_BBB333 = createField("clob_aaa_bbb333", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.varchar</code>.
     */
    public final TableField<DummyJooqTableRecord, String> VARCHAR = createField("varchar", org.jooq.impl.SQLDataType.VARCHAR.length(32).nullable(false), this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.tinyint</code>.
     */
    public final TableField<DummyJooqTableRecord, Byte> TINYINT = createField("tinyint", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.smallint</code>.
     */
    public final TableField<DummyJooqTableRecord, Short> SMALLINT = createField("smallint", org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.integer11</code>.
     */
    public final TableField<DummyJooqTableRecord, Integer> INTEGER11 = createField("integer11", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.long22</code>.
     */
    public final TableField<DummyJooqTableRecord, Long> LONG22 = createField("long22", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.double321</code>.
     */
    public final TableField<DummyJooqTableRecord, Double> DOUBLE321 = createField("double321", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.varchar_enum</code>.
     */
    public final TableField<DummyJooqTableRecord, String> VARCHAR_ENUM = createField("varchar_enum", org.jooq.impl.SQLDataType.VARCHAR.length(10), this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.tinyint_enum</code>.
     */
    public final TableField<DummyJooqTableRecord, Byte> TINYINT_ENUM = createField("tinyint_enum", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.integer_enum</code>.
     */
    public final TableField<DummyJooqTableRecord, Integer> INTEGER_ENUM = createField("integer_enum", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>dev_dums.table_for_jooq_test.timestamp</code>.
     */
    public final TableField<DummyJooqTableRecord, Timestamp> TIMESTAMP = createField("timestamp", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    public final TableField<DummyJooqTableRecord, DummyJooqEnum> JOOQ_ENUM = createField("jooq_enum", org.jooq.util.mysql.MySQLDataType.VARCHAR.asEnumDataType(DummyJooqEnum.class), this, "");

    /**
     * Create a <code>dev_dums.table_for_jooq_test</code> table reference
     */
    public DummyJooqTable() {
        this("table_for_jooq_test", null);
    }

    /**
     * Create an aliased <code>dev_dums.table_for_jooq_test</code> table reference
     */
    public DummyJooqTable(String alias) {
        this(alias, DUMMY_JOOQ_TABLE);
    }

    private DummyJooqTable(String alias, Table<DummyJooqTableRecord> aliased) {
        this(alias, aliased, null);
    }

    private DummyJooqTable(String alias, Table<DummyJooqTableRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return DummyJooqScheme.DUMMY_JOOQ_SCHEME_DUMS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DummyJooqTable as(String alias) {
        return new DummyJooqTable(alias, this);
    }

    /**
     * Rename this table
     */
    public DummyJooqTable rename(String name) {
        return new DummyJooqTable(name, null);
    }
}
