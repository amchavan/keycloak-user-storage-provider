package alma.obops.keycloak.userprovider;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.List;

public class TestDemoUserRepository {

    @Test
    public void allUsers() {
        DemoUserRepository repo = new DemoUserRepository();
        List<User> users = repo.getAllUsers();
        assertNotNull( users );
        assertTrue( users.size() > 0 );
    }

    @Test
    public void allUsersWithPagination() {
        DemoUserRepository repo = new DemoUserRepository();
        List<User> users = repo.getAllUsers( 2, 2 );
        assertNotNull( users );
        assertEquals(2, users.size());
        assertEquals( "Billie", users.get(1).getFirstName() );
    }

    @Test
    public void usersCount() {
        DemoUserRepository repo = new DemoUserRepository();
        int count = repo.getAllUsersCount();
        assertTrue( count > 0 );
    }

    @Test
    public void findUserById() {
        DemoUserRepository repo = new DemoUserRepository();
        User user = repo.findUserById( "kwash" );
        assertNotNull( user );
        assertEquals( "Katie", user.getFirstName() );
    }

    @Test
    public void findUserByEmail() {
        UserRepository repo = new DemoUserRepository();
        User user = repo.findUserByEmail( "jlett@example.com" );
        assertNotNull( user );
        assertEquals( "Joshua", user.getFirstName() );
    }

    @Test
    public void findUsers() {
        DemoUserRepository repo = new DemoUserRepository();
        List<User> users = repo.findUsers( "e" );
        assertNotNull( users );
        assertEquals("Expected three users", 3, users.size());
        assertEquals( "Enrique", users.get(0).getFirstName() );
    }

    @Test
    public void findUsersWithPagination() {
        DemoUserRepository repo = new DemoUserRepository();
        List<User> users = repo.findUsers( "e", 0, 2 );
        assertNotNull( users );
        assertEquals("Expected two users", 2, users.size());
        assertEquals( "Enrique", users.get(0).getFirstName() );
    }

    @Test
    public void validateCredentials() {
        DemoUserRepository repo = new DemoUserRepository();
        assertTrue(  repo.validateCredentials( "kwash", "kwashp" ));
        assertFalse( repo.validateCredentials( "eperk", "?"      ));
    }
}
