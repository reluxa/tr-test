package org.reluxa.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import oracle.jdbc.xa.client.OracleXADataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
public class DbConfig {

  @Bean
  public DataSource dataSource() throws SQLException {
    AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
    atomikosDataSourceBean.setXaDataSourceClassName("oracle.jdbc.xa.client.OracleXADataSource");
    Properties xaProperties = new Properties();
    xaProperties.put("user","test");
    xaProperties.put("password", "test");
    xaProperties.put("URL", "jdbc:oracle:thin:@localhost:2521:xe");
    atomikosDataSourceBean.setXaProperties(xaProperties);
    atomikosDataSourceBean.setMinPoolSize(1);
    atomikosDataSourceBean.setPoolSize(5);
    atomikosDataSourceBean.setMaxPoolSize(10);
    atomikosDataSourceBean.setUniqueResourceName("ORAXLE");
    atomikosDataSourceBean.setTestQuery("SELECT 1 FROM DUAL");
    return atomikosDataSourceBean;
  }


}
