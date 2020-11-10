package alma.obops.keycloak.userprovider;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.*;

/**
 * Maps an instance of the User class to what Keycloak requires.
 * <p>
 * User roles are made available via the {@value ALMA_ROLES_ATTRIBUTE} attribute to avoid conflicts with
 * Keycloak usage of "roles".
 *
 * @author amchavan, 30-Apr-2020
 */
public class UserAdapter extends AbstractUserAdapterFederatedStorage implements UserModel {

    /** Keycloak user attribute holding the users' role list */
    public static final String ALMA_ROLES_ATTRIBUTE = "user_roles";

    private final User user;
    private final String keycloakId;
    private final Map<String, List<String>> attributes;

    private final Logger LOGGER = Logger.getLogger( UserAdapter.class.getSimpleName() );

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, User user) {

        super(session, realm, model);
        if( user == null ) {
            throw new IllegalArgumentException( "User cannot be null" );
        }

        this.user       = user;
        this.keycloakId = StorageId.keycloakId(model, user.getId());
        this.attributes = new HashMap<>();
        this.attributes.put( ALMA_ROLES_ATTRIBUTE, user.getRoles() );
        LOGGER.infov( "UserAdapter(): set attribute {0} to {1}", ALMA_ROLES_ATTRIBUTE, this.attributes.get( ALMA_ROLES_ATTRIBUTE ) );
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String username) {
        // no-op
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        // no-op
    }

    @Override
    public String getFirstName() {
        return user.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        // no-op
    }

    @Override
    public String getLastName() {
        return user.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        // no-op
    }

    @Override
    public List<String> getAttribute( String attributeName ) {
        var attributeValue = this.attributes.get( attributeName );
        if( attributeValue == null ) {
            return super.getAttribute( attributeName );
        }
        LOGGER.infov( "getAttribute(): for: {0} returning: {1}", attributeName, attributeValue );
        return attributeValue;
    }
}
