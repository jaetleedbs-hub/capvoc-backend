package buttle.apptpl.common.config.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;

@Configuration("mainDatabaseConfig")
@EnableJpaRepositories(
        basePackages = {"buttle.apptpl"},
        entityManagerFactoryRef = "mainEntityManager",
        transactionManagerRef = "mainTransactionManager")
public class MainDataSourceConfig {

    @Bean(value = "mainHibernateProperties")
    @ConfigurationProperties("spring.datasource.main.hibernate.property")
    public HashMap<String, Object> mainHibernateProperties() {
        return new HashMap<String, Object>();
    }

    @Primary
    @Bean(name = "mainEntityManager")
    public LocalContainerEntityManagerFactoryBean mainEntityManager() throws SQLException {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mainDataSource());
        em.setPackagesToScan("buttle.apptpl");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = mainHibernateProperties();
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "mainTransactionManager")
    public PlatformTransactionManager mainTransactionManager() throws SQLException {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mainEntityManager().getObject());
        return transactionManager;
    }


    @Bean(value = "mainHikariConfig")
    @ConfigurationProperties(prefix="spring.datasource.main.hikari")
    public HikariConfig mainHikariConfig() {
        return new HikariConfig();
    }

    @Primary
    @Bean(value = "mainDataSource")
    public DataSource mainDataSource() throws SQLException {
        return new HikariDataSource(mainHikariConfig());
    }
}
