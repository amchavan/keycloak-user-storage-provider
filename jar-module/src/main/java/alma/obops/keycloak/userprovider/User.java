package alma.obops.keycloak.userprovider;

import java.util.Arrays;
import java.util.List;

/**
 * Rows from the 'account' table are mapped to this class -- a poor man's Hibernate.
 * @author amchavan, 30-Apr-2020
 */
public class User {

    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String password;
    private List<String> roles;

    /** Construct a user with no roles (empty roles list) */
    public User( String username, String firstName, String lastName, String email, String password ) {
        this( username, firstName, lastName, email, password, new String[0] );
    }

    /** Construct a user */
    public User(String username, String firstName, String lastName, String email, String password, String[] roles ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = Arrays.asList( roles );
    }

    public String getId() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles( List<String> roles ) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }
}
