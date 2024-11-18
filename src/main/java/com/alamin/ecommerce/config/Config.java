@Configuration(proxyBeanMethods = false)
@EnableJdbcHttpSession 
public class Config {

	@Bean
	public EmbeddedDatabase dataSource() {
		return new EmbeddedDatabaseBuilder() 
			.setType(EmbeddedDatabaseType.H2)
			.addScript("org/springframework/session/jdbc/schema-h2.sql")
			.build();
	}

	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource); 
	}

}
