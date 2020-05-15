package alma.obops.keycloak.userprovider;

import java.util.List;

/**
 * @author amchavan, 29-Apr-2020
 */
public interface UserRepository {

    List<User> getAllUsers();

    /** Paginated version of getAllUsers() */
    List<User> getAllUsers( int firstRow, int rowNumber );

    int getAllUsersCount();

    User findUserById( String username );

    User findUserByEmail( String email );

    List<User> findUsers( String query );

    /** Paginated version of findUsers() */
    List<User> findUsers( String query, int firstRow, int rowNumber );

    boolean validateCredentials(String username, String password);
}
