package lifthrasir.toolkit.steinsgate.archive.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(
		basePackages = "lifthrasir.toolkit.steinsgate.archive.**.dao",
		sqlSessionFactoryRef = "stgateSqlSessionFactory"
)
public class StgateDatabaseConfiguration{
	@Bean(name = "stgateDataSource")
	@Primary
	@ConfigurationProperties(prefix = "spring.stgate.datasource.hikari")
	public DataSource stgateDataSource(){
		HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setMaximumPoolSize(6);
		hikariDataSource.setMinimumIdle(1);
		hikariDataSource.setMaxLifetime(180000);
        return hikariDataSource;
	}
	
	@Bean(name = "stgateSqlSessionFactory")
	@Primary
	public SqlSessionFactory sqlSessionFactory(
			@Qualifier("stgateDataSource") DataSource stgateDataSource,
			ApplicationContext applicationContext
	) throws Exception{
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(stgateDataSource);
		sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:sql/mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:sql/mapper/*.xml"));
		
		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean(name = "stgateSqlSessionTemplate")
	@Primary
	public SqlSessionTemplate stgateSqlSessionTemplate(
			@Qualifier("stgateSqlSessionFactory") SqlSessionFactory stgateSqlSessionFactory
	) throws Exception{
		return new SqlSessionTemplate(stgateSqlSessionFactory);
	}

	@Bean(name = "stgateTransactionManager")
	public DataSourceTransactionManager stgateTransactionManager(){
		return new DataSourceTransactionManager(stgateDataSource());
	}
}
