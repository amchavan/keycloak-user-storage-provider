package alma.obops.keycloak.userprovider;

import org.jboss.logging.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

//import org.jboss.logging.Logger;

/**
 * An implementation of UserRepository for the ALMA User Registry
 *
 * @author amchavan, 30-Apr-2020
 */
public class AlmaUserRepository implements UserRepository {

    private static final String SELECT_ACCOUNT =
        "SELECT account_id, email, firstname, lastname, password_digest " +
        "FROM   account " +
        "WHERE  account_id = :account_id " +
        "AND    active = 'T' ";

    private static final String SELECT_ACCOUNT_BY_EMAIL =
        "SELECT account_id, email, firstname, lastname, password_digest " +
        "FROM   account " +
        "WHERE  email = :email " +
        "AND    active = 'T' ";

    private static final String SELECT_ALL =
        "SELECT account_id, email, firstname, lastname, password_digest " +
        "FROM account " +
        "WHERE active = 'T' " +
        "ORDER BY account_id";

    public static final String PAGINATION = " OFFSET :offset ROWS FETCH NEXT :count ROWS ONLY";
    
    private static final String SELECT_ALL_WITH_PAGINATION = SELECT_ALL + PAGINATION;

    private static final String SELECT_SOME =
        "SELECT account_id, email, firstname, lastname, password_digest " +
        "FROM account " +
        "WHERE active = 'T' " +
        "AND   (account_id like :substring OR email like :substring ) " +
        "ORDER BY account_id";

    private static final String SELECT_SOME_WITH_PAGINATION = SELECT_SOME + PAGINATION;

    private static final String COUNT_ALL =
        "SELECT count(*) FROM account WHERE active = 'T'";

    private static final String SELECT_ROLES =
        "SELECT    application, name " +
        "FROM      role " +
        "LEFT JOIN account_role ON role.role_no = account_role.role_no " +
        "WHERE     account_role.account_id = :username " +
        "ORDER BY  application, name";


	private final Logger LOGGER = Logger.getLogger( AlmaUserRepository.class.getSimpleName() );
	private final NamedParameterJdbcTemplate jdbcTemplate;
    public static final Map<String, String> NO_PARAMS = Map.of();

    public AlmaUserRepository( DataSource dataSource ) {
        LOGGER.infov( "create(): creating AlmaUserRepository with dataSource={0}", dataSource );
		this.jdbcTemplate = new NamedParameterJdbcTemplate( dataSource );
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
            LOGGER.error( "MD5 hash computation failed", e );
            throw new RuntimeException(e);
        }
    }

    private List<String> retrieveUserRoles(String username ) {
        Map<String,String> namedParameters = Map.of( "username", username );
        List<Map<String, Object>> rows = jdbcTemplate.queryForList( SELECT_ROLES, namedParameters );
        return rows.stream().map( convertDbRowToRole() ).collect( Collectors.toList());
    }

    private Function<Map<String, Object>, String> convertDbRowToRole() {
        return row ->
                row.get( "application" ).toString().trim()
                + "/"
                + row.get( "name" ).toString().trim();
    }

    /** A poor man's Hibernate */
    private User convertDbRowToUser( Map<String, Object> row ) {

        final var account_id = row.getOrDefault("account_id", "").toString();
        final var firstname = row.getOrDefault("firstname", "").toString();
        final var lastname = row.getOrDefault("lastname", "").toString();
        final var email = row.getOrDefault("email", "").toString();
        final var password_digest = row.getOrDefault("password_digest", "").toString();
        final User ret = new User(account_id, firstname, lastname, email, password_digest);

        List<String> roles = retrieveUserRoles( ret.getUsername() );
        ret.setRoles( roles );
//        LOGGER.infov( "convertDbRowToUser(): set roles of {0} to {1}", ret.getUsername(), ret.getRoles() );
        return ret;
    }

	@Override
	public List<User> getAllUsers() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList( SELECT_ALL, NO_PARAMS );
        return rows.stream().map( this::convertDbRowToUser ).collect( Collectors.toList() );
	}

    @Override
    public List<User> getAllUsers(int firstRow, int rowCount) {
        Map<String,Integer> namedParameters = Map.of( "offset", firstRow, "count", rowCount );
        List<Map<String, Object>> rows = jdbcTemplate.queryForList( SELECT_ALL_WITH_PAGINATION, namedParameters );
        return rows.stream().map( this::convertDbRowToUser ).collect( Collectors.toList() );
    }

    @Override
	public int getAllUsersCount() {
		Integer count = jdbcTemplate.queryForObject( COUNT_ALL, NO_PARAMS, Integer.class );
		if( count == null ) {
			throw new RuntimeException( "getUsersCount(): SQL query returned null" );
		}
		return count;
	}

    @Override
    public User findUserById( String username ) {

        LOGGER.infov( "findUserById(): looking for " + username );
        if( username == null ) {
            return null;
        }

        try {
            Map<String,String> namedParameters = Map.of( "account_id", username );
            Map<String, Object> userRow = jdbcTemplate.queryForMap( SELECT_ACCOUNT, namedParameters ) ;
            LOGGER.infov( "findUserById(): found: " + userRow );
            return convertDbRowToUser( userRow );
        }
        catch (EmptyResultDataAccessException e) {
            return null;        // found no rows
        }
    }

    @Override
    public User findUserByEmail( String email ) {

        LOGGER.infov( "findUserByEmail(): looking for " + email );
        if( email == null ) {
            return null;
        }

        try {
            Map<String,String> namedParameters = Map.of( "email", email );
            Map<String, Object> userRow = jdbcTemplate.queryForMap( SELECT_ACCOUNT_BY_EMAIL, namedParameters ) ;
            return convertDbRowToUser( userRow );
        }
        catch (EmptyResultDataAccessException e) {
            return null;        // found no rows
        }
    }

	@Override
	public List<User> findUsers( String query ) {
	    String likeThis = "%" + query + "%";
        Map<String,String> namedParameters = Map.of( "substring", likeThis );
        List<Map<String, Object>> rows = jdbcTemplate.queryForList( SELECT_SOME, namedParameters );
//        System.out.println( "findUsers(): rows: size=" + rows.size() );
        return rows.stream().map( this::convertDbRowToUser ).collect( Collectors.toList() );
	}

    @Override
    public List<User> findUsers(String query, int firstRow, int rowCount) {
        String likeThis = "%" + query + "%";
        Map<String,Object> namedParameters = Map.of( "substring", likeThis, "offset", firstRow, "count", rowCount );
        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList( SELECT_SOME_WITH_PAGINATION, namedParameters );
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
