package alma.obops.keycloak.userprovider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A demo implementation of UserRepository, for development and testing.<br>
 * Derived from https://github.com/thomasdarimont/keycloak-user-storage-provider-demo
 *
 * @author amchavan, 30-Apr-2020
 */
class DemoUserRepository implements UserRepository {

    private final List<User> users;

    public DemoUserRepository() {

        String[] bnewmRoles = {"user", "author"};
        String[] lthomRoles = {"user", "editor"};

        users = Arrays.asList(
                new User( "kwash", "Katie",   "Washington", "kwash@example.com", "kwashp" ),
                new User( "eperk", "Enrique", "Perkins",    "eperk@example.com", "eperkp" ),
                new User( "jlett", "Joshua",  "Lett",       "jlett@example.com", "jlettp" ),
                new User( "bnewm", "Billie",  "Newman",     "bnewm@example.com", "bnewmp", bnewmRoles ),
                new User( "lthom", "Leslie",  "Thompson",   "lthom@example.com", "lthomp", lthomRoles )
        );
    }

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public List<User> getAllUsers(int firstRow, int rowNumber) {
        return getAllUsers().subList( firstRow, firstRow + rowNumber );
    }

    @Override
    public int getAllUsersCount() {
        return users.size();
    }

    @Override
    public User findUserById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals( id ))
                .findFirst()
                .orElse( null );
    }

    @Override
    public User findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase( email ))
                .findFirst()
                .orElse( null );
    }

    @Override
    public List<User> findUsers(String query) {
        return users.stream()
                .filter(user -> user.getUsername().contains(query))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsers(String query, int firstRow, int rowNumber) {
        return findUsers( query ).subList( firstRow, firstRow + rowNumber );
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        return findUserById(username).getPassword().equals(password);
    }
}
