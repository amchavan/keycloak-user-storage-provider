package alma.obops.keycloak.userprovider;

import org.jboss.logging.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

//import org.jboss.logging.Logger;

import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An implementation of UserRepository for the ALMA User Registry
 *
 * @author amchavan, 30-Apr-2020
 */
public class AlmaUserRepository implements UserRepository {

    private static final String SELECT_ACCOUNT =
        "SELECT account_id, email, firstname, lastname, password_digest " +
        "FROM   account " +
        "WHERE  account_id = ? " +
        "AND    active = 'T' ";

    private static final String SELECT_ACCOUNT_BY_EMAIL =
        "SELECT account_id, email, firstname, lastname, password_digest " +
        "FROM   account " +
        "WHERE  email = ? " +
        "AND    active = 'T' ";

    private static final String SELECT_ALL =
        "SELECT account_id, email, firstname, lastname, password_digest " +
        "FROM account " +
        "WHERE active = 'T' " +
        "ORDER BY account_id";

    public static final String PAGINATION = " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    
    private static final String SELECT_ALL_WITH_PAGINATION = SELECT_ALL + PAGINATION;

    private static final String SELECT_SOME =
        "SELECT account_id, email, firstname, lastname, password_digest " +
        "FROM account " +
        "WHERE active = 'T' " +
        "AND   (account_id like ? OR email like ? ) " +
        "ORDER BY account_id";

    private static final String SELECT_SOME_WITH_PAGINATION = SELECT_SOME + PAGINATION;

    private static final String COUNT_ALL =
        "SELECT count(*) FROM account WHERE active = 'T'";

    private static final String SELECT_ROLES =
        "SELECT    application, name " +
        "FROM      role " +
        "LEFT JOIN account_role ON role.role_no = account_role.role_no " +
        "WHERE     account_role.account_id = ? " +
        "ORDER BY  application, name";


	private final Logger LOGGER = Logger.getLogger( AlmaUserRepository.class.getSimpleName() );
	private final JdbcTemplate jdbcTemplate;

    public AlmaUserRepository( DataSource dataSource ) {
        LOGGER.infov( "create(): creating AlmaUserRepository with dataSource={0}", dataSource );
		this.jdbcTemplate = new JdbcTemplate( dataSource );
    }

    // From https://www.baeldung.com/java-md5
	private String computeMD5Hash(String password ) {
		try {
            MessageDigest md = MessageDigest.getInstance( "MD5" );
            md.update(password.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary( digest ).toLowerCase();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<String> retrieveUserRoles(String username ) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList( SELECT_ROLES, username );
        return rows.stream().map( convertRowToRole() ).collect( Collectors.toList());
    }

    private Function<Map<String, Object>, String> convertRowToRole() {
        return row ->
                row.get( "application" ).toString().trim()
                + "/"
                + row.get( "name" ).toString().trim();
    }

    /** A poor man's Hibernate */
    private User convertDbRowToUser( Map<String, Object> row ) {

        final User ret = new User(
                row.get( "account_id" ).toString(),
                row.get( "firstname" ).toString(),
                row.get( "lastname" ).toString(),
                row.get( "email" ).toString(),
                row.get( "password_digest" ).toString()
        );

        List<String> roles = retrieveUserRoles( ret.getUsername() );
        ret.setRoles( roles );

        return ret;
    }

	@Override
	public List<User> getAllUsers() {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList( SELECT_ALL );
        return rows.stream().map( this::convertDbRowToUser ).collect( Collectors.toList() );
	}

    @Override
    public List<User> getAllUsers(int firstRow, int rowCount) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList( SELECT_ALL_WITH_PAGINATION, firstRow, rowCount );
        return rows.stream().map( this::convertDbRowToUser ).collect( Collectors.toList() );
    }

    @Override
	public int getAllUsersCount() {
		Integer count = jdbcTemplate.queryForObject( COUNT_ALL, Integer.class );
		if( count == null ) {
			throw new RuntimeException( "getUsersCount(): SQL query returned null" );
		}
		return count;
	}

    @Override
    public User findUserById( String username ) {
//        new RuntimeException( ">>> here" ).printStackTrace();
        LOGGER.infov( "findUserById(): looking for " + username );
        if( username == null ) {
            throw new IllegalArgumentException( "Null username" );
        }

        try {
            Map<String, Object> userRow = jdbcTemplate.queryForMap( SELECT_ACCOUNT, username) ;
            LOGGER.infov( "findUserById(): found: " + userRow );
            return convertDbRowToUser( userRow );
        }
        catch (EmptyResultDataAccessException e) {
            // found no rows
            return null;
        }
    }

    @Override
    public User findUserByEmail( String email ) {
        try {
            Map<String, Object> userRow = jdbcTemplate.queryForMap( SELECT_ACCOUNT_BY_EMAIL, email ) ;
            return convertDbRowToUser( userRow );
        }
        catch (EmptyResultDataAccessException e) {
            // found no rows
            return null;
        }
    }

	@Override
	public List<User> findUsers( String query ) {
	    String likeThis = "%" + query + "%";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList( SELECT_SOME, likeThis, likeThis );
//        System.out.println( "findUsers(): rows: size=" + rows.size() );
        return rows.stream().map( this::convertDbRowToUser ).collect( Collectors.toList() );
	}

    @Override
    public List<User> findUsers(String query, int firstRow, int rowCount) {
        String likeThis = "%" + query + "%";
        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList( SELECT_SOME_WITH_PAGINATION, likeThis, likeThis, firstRow, rowCount );
//        System.out.println( "findUsers(): rows: size=" + rows.size() );
        return rows.stream().map( this::convertDbRowToUser ).collect( Collectors.toList() );
    }

    @Override
	public boolean validateCredentials( String username, String password ) {

        User user = findUserById( username );
        if( user == null ) {
            return false;
        }

        String passwordDigest = computeMD5Hash( password );
        //        System.out.println( ">>> validateCredentials: passwordDigest=" + passwordDigest +
        //                            ", user.getPassword()=" + user.getPassword() );
        return passwordDigest.equals( user.getPassword() );
	}
}
