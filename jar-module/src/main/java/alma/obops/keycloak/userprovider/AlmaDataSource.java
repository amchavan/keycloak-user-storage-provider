package alma.obops.keycloak.userprovider;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.io.*;
import java.util.Properties;

/**
 * Configure the application's data source according to ALMA's conventions, e.g.
 * storing the data source parameters in
 * <em>$ACSDATA/config/archiveConfig.properties</em>
 * <p>
 *     Adapted from alma.obops.cas.AlmaDataSourceConfiguration
 *
 * @author amchavan, 30-Apr-2020
 */

public class AlmaDataSource {

	private static final String PATH_TO_PROPS = "/config/archiveConfig.properties";
	private static final Logger LOGGER = Logger.getLogger(AlmaDataSource.class);
	public static final String CONNECTION_PROPERTY = "archive.relational.connection";
	public static final String USER_PROPERTY = "archive.relational.user";
	public static final String PASSWORD_PROPERTY = "archive.relational.passwd";

	private String url;
	private String username;
	private String password;

	public AlmaDataSource() throws IOException {
		String acsdata = System.getenv( "ACSDATA" );
		if( acsdata == null ) {
			throw new RuntimeException( "Environment variable ACSDATA not found" );
		}

		String propsPathname = acsdata + PATH_TO_PROPS;
		loadProperties( propsPathname );
	}

	private void loadProperties( String propsPathname ) throws IOException {
		LOGGER.debugv( ">>> loadProperties(): propsPathname={0}", propsPathname );

		InputStreamReader reader = new InputStreamReader( new FileInputStream( propsPathname ));
		Properties props = new Properties();
		props.load( reader );

      	this.url = props.getProperty( CONNECTION_PROPERTY );
      	checkForNull( CONNECTION_PROPERTY, this.url );

		this.username = props.getProperty( USER_PROPERTY );
		checkForNull( USER_PROPERTY, this.username );

		this.password = props.getProperty(PASSWORD_PROPERTY);
		checkForNull(PASSWORD_PROPERTY, this.password );

		LOGGER.info( "Database URL : " + url );
		LOGGER.info( "Database user: " + username );
	}

	private void checkForNull( String propertyName, String propertyValue ) {
		if ( propertyValue == null ) {
			throw new RuntimeException( "Undefined property: " + propertyName );
		}
	}

	public DataSource getDataSource() {

		java.util.Properties props = new java.util.Properties();
		props.put("v$session.program", "Keycloak");

		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setProperties( props );
		dataSource.setUser( username );
		dataSource.setPassword( password );
		dataSource.setJdbcUrl( url );

		return dataSource;
	}
}

