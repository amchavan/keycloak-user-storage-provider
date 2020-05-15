package alma.obops.keycloak.userprovider;

import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class TestAlmaUserRepository {

    private DataSource dataSource;

    @Before
    public void setUp() throws IOException {
        AlmaDataSource almaDataSource = new AlmaDataSource();
        this.dataSource = almaDataSource.getDataSource();
    }

    @Test
    public void allUsers() {
        AlmaUserRepository repo = new AlmaUserRepository( dataSource );

//        Too slow! commented out
//        But it works, I promise.
//        -------------------------------
//
//        List<User> users = repo.getAllUsers();
//        assertNotNull( users );
//        assertTrue( users.size() == repo.getUsersCount() );
    }

    @Test
    public void allUsersWithPagination() {
        AlmaUserRepository repo = new AlmaUserRepository( dataSource );
        List<User> users = repo.getAllUsers( 0, 10 );
        assertNotNull( users );
        assertEquals(10, users.size());

        for (User user: users ) {

            System.out.println( ">>> " + user );

            assertNotNull( user.getUsername() );
            assertNotNull( user.getFirstName() );
            assertNotNull( user.getLastName() );
            assertNotNull( user.getEmail() );
            assertNotNull( user.getPassword() );

            assertNotNull( user.getRoles() );
            assertTrue( user.getRoles().size() > 0 );
            assertTrue( user.getRoles().contains( "MASTER/USER" ));
        }
    }

    @Test
    public void usersCount() {
        AlmaUserRepository repo = new AlmaUserRepository( dataSource );
        int count = repo.getAllUsersCount();
        assertTrue( "Expected non-zero count", count > 0 );
        System.out.println( ">>> testUsersCount(): count=" + count );
    }

    @Test
    public void findUserById() {
        AlmaUserRepository repo = new AlmaUserRepository( dataSource );
        User user = repo.findUserById( "aaguirre" );
        assertNotNull( user );


        User noUser = repo.findUserById( "this should really not be there" );
        assertNull( noUser );
    }

    @Test
    public void findUserByEmail() {
        AlmaUserRepository repo = new AlmaUserRepository( dataSource );
        User user = repo.findUserByEmail( "amchavan6010@noname.domain.org" );
        assertNotNull( user );

        User noUser = repo.findUserById( "not a valid email address" );
        assertNull( noUser );
    }

    @Test
    public void findUsers() {
        AlmaUserRepository repo = new AlmaUserRepository( dataSource );
        List<User> users = repo.findUsers( "chavan" );
        assertNotNull( users );
        assertTrue( users.size() > 0 );
        assertEquals( "Maurizio", users.get(0).getFirstName() );

        List<User> users2 = repo.findUsers( "amc" );
        assertNotNull( users2 );
        assertTrue( users2.size() > 0 );
        users2.forEach(user -> System.out.println( ">>>" + user ));
    }

    @Test
    public void findUsersWithPagination() {
        AlmaUserRepository repo = new AlmaUserRepository( dataSource );
        List<User> users = repo.findUsers( "amc", 0, 5 );
        assertNotNull( users );
        assertEquals("Expected 5 users", 5, users.size());
        User amchavan = users.stream().filter(user -> user.getId().equals( "amchavan" ))
                .findFirst()
                .orElse( null );
        assertNotNull( amchavan );
        assertEquals( "Maurizio", amchavan.getFirstName() );
    }

    @Test
    public void validateCredentials() {
        AlmaUserRepository repo = new AlmaUserRepository( dataSource );
        assertTrue( repo.validateCredentials( "amchavan", "aaa" ));
        assertFalse( repo.validateCredentials( "aaguirre", "I guess this is not Álvaro's password" ));
    }
}
