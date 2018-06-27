package codehumane.jooq;

import lombok.val;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class JooqTestConfig {

    @Bean
    public DataSource dataSource() {
        val dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Bean
    public DefaultDSLContext dslContext(DataSource dataSource) {
        val configuration = new DefaultConfiguration();
        configuration.setDataSource(dataSource);
        return new DefaultDSLContext(configuration);
    }

    @Bean
    public JooqDataFixture jooqDataFixture(DSLContext dslContext) {
        return new JooqDataFixture(dslContext);
    }

    @Bean
    public JooqRecordToPojoMapper jooqRecordToPojoMapper() {
        return new DefaultJooqRecordToPojoMapper();
    }
}
