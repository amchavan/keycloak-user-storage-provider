package alma.obops.keycloak.userprovider;

import org.apache.commons.dbcp.BasicDataSource;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
	private static final Logger LOGGER = Logger.getLogger(AlmaDataSource.class.getSimpleName());
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

		try( InputStreamReader reader = new InputStreamReader( new FileInputStream( propsPathname ))) {

			Properties props = new Properties();
			props.load(reader);

			this.url      = getRequiredProperty(props, CONNECTION_PROPERTY);
			this.username = getRequiredProperty(props, USER_PROPERTY);
			this.password = getRequiredProperty(props, PASSWORD_PROPERTY);

			LOGGER.info("Database URL : " + url);
			LOGGER.info("Database user: " + username);
		}
	}

	private String getRequiredProperty( Properties props, String propertyName ) {
		String propertyValue = props.getProperty(propertyName);
		if ( propertyValue == null ) {
			throw new RuntimeException( "Undefined property: " + propertyName );
		}
		return propertyValue;
	}

	public DataSource getDataSource() {

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl( url );
		dataSource.setUsername( username );
		dataSource.setPassword( password );

		return dataSource;
	}
}

