package alma.obops.keycloak.userprovider;

import org.junit.Before;
import org.junit.Test;


import java.util.List;

import static alma.obops.keycloak.userprovider.UserAdapter.ALMA_ROLES_ATTRIBUTE;
import static org.junit.Assert.*;


public class TestUserAdapter {

    private User user;
    private MockComponentModel componentModel;

    @Before
    public void setUp() {
        DemoUserRepository repo = new DemoUserRepository();
        user = repo.getAllUsers().get( 4 );    // User 4 has actual roles
        componentModel = new MockComponentModel();
    }

    @Test
    public void failingCreation() {
        try {
            new UserAdapter( null, null, componentModel, null );
            fail( "Expected IllegalArgumentException" );
        }
        catch( IllegalArgumentException e ) {
            // no-op, expected
        }
    }

    @Test
    public void creation() {

        UserAdapter ua = new UserAdapter( null, null, componentModel, user );

        assertEquals( user.getUsername(),  ua.getUsername() );
        assertEquals( user.getEmail(),     ua.getEmail() );
        assertEquals( user.getFirstName(), ua.getFirstName() );
        assertEquals( user.getLastName(),  ua.getLastName() );
        assertEquals( user.getRoles(),     ua.getAttribute( ALMA_ROLES_ATTRIBUTE ));
    }
}
