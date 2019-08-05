/**
 * 
 */
package com.cloudpaas.service.pas.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.cloudpaas.common.mybatis.AbstractDataSourceConfig;
import com.cloudpaas.common.mybatis.MultiRoutingDataSource;
import com.cloudpaas.common.properties.MultiDataSourceProperties;
import com.cloudpaas.common.properties.MybatisProperties;
import com.github.pagehelper.PageInterceptor;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;



/**
 * @author 大鱼
 *
 * @date 2019年8月2日 下午2:19:32
 */
@Configuration
@AutoConfigureAfter(MybatisAutoConfiguration.class)  
public class MybatisMultiConfig extends AbstractDataSourceConfig {
	
	private static Logger log = LoggerFactory.getLogger(MybatisMultiConfig.class);
	
	@Autowired
	private MybatisProperties mybatisProperties;
	
	@Autowired
	private MultiDataSourceProperties dataSourceProperties;


	@Primary
    @Bean(name = "dataSource_dn1")
	@ConfigurationProperties(prefix = "spring.datasource.druid.dn[0]" )
    public DataSource dataSourceDn1(Environment env) throws Exception {
		String prefix = "spring.datasource.druid.dn[0].";
        return getDataSource(env,prefix,"dn1");
    }
	

//	
	@Bean(name = "dataSource_dn2")
	@ConfigurationProperties(prefix = "spring.datasource.druid.dn[1]" )
    public DataSource dataSourceDn2(Environment env) throws Exception {
		String prefix = "spring.datasource.druid.dn[1].";
        return getDataSource(env,prefix,"dn2");
    }
	

	
	@Bean("dynamicDataSource")
    public DataSource dynamicDataSource(@Qualifier("dataSource_dn1")DataSource dataSource_dn1,
    		@Qualifier("dataSource_dn2")DataSource dataSource_dn2) {
		
		log.info("------------------------");
		log.info(dataSourceProperties.getDn().length+"");
		log.info("------------------------");
		
		MultiRoutingDataSource dynamicDataSource = new MultiRoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put("dn1", dataSource_dn1);
        dataSourceMap.put("dn2", dataSource_dn2);
        // 将 master 数据源作为默认指定的数据源
        dynamicDataSource.setDefaultTargetDataSource(dataSource_dn1);
        // 将 master 和 slave 数据源作为指定的数据源
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.afterPropertiesSet();
        return dynamicDataSource;
    }
	
	@Bean(name="sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dynamicDataSource")DataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        // 配置数据源，此处配置为关键配置，如果没有将 dynamicDataSource作为数据源则不能实现切换
        sessionFactory.setDataSource(dynamicDataSource);
        sessionFactory.setTypeAliasesPackage("com.cloudpaas.**.model");    // 扫描Model
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources(mybatisProperties.getMapperLocations()));    // 扫描映射文件
        return sessionFactory;
    }
	
	@Bean
	public static MapperScannerConfigurer mapperScannerConfigurer(){
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
		mapperScannerConfigurer.setBasePackage("com.cloudpaas.**.mapper");
		return mapperScannerConfigurer;
	}
	

	


}
