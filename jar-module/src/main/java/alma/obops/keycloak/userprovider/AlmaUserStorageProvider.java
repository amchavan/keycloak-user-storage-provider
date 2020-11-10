package alma.obops.keycloak.userprovider;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * See https://www.keycloak.org/docs/latest/server_development/index.html#_user-storage-spi
 *
 * @author amchavan, 30-Apr-2020
 */
public class AlmaUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        UserQueryProvider,
        CredentialInputValidator {

    private static final Logger LOGGER = Logger.getLogger( AlmaUserStorageProvider.class.getSimpleName() );

    private final KeycloakSession session;
    private final ComponentModel model;
    private final UserRepository repository;

    public AlmaUserStorageProvider(KeycloakSession session, ComponentModel model, AlmaUserRepository repository) {
//        LOGGER.infov( "AlmaUserStorageProvider: session={0} model={1} repository={2}", session, model, repository );
        this.session = session;
        this.model = model;
        this.repository = repository;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
//        LOGGER.infov( "supportsCredentialType: credentialType={0}", credentialType );
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
//        LOGGER.infov( "isConfiguredFor: realm={0} user={1} credentialType={2}", realm.getId(), user.getUsername(), credentialType );
        return supportsCredentialType(credentialType);
    }

    /**
     * Checks if credentials given by the user are valid
     */
    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
//        LOGGER.infov( "isValid: realm={0} user={1} input={2}", realm.getId(), user.getUsername(), input );
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }

        UserCredentialModel cred = (UserCredentialModel) input;
        return repository.validateCredentials(user.getUsername(), cred.getChallengeResponse());
    }

    @Override
    public void preRemove(RealmModel realm) {
//        LOGGER.infov("pre-remove realm");
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
//        LOGGER.infov("pre-remove group");
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        LOGGER.infov("pre-remove role");
    }

    @Override
    public void close() {
        LOGGER.infov( "Closing" );
    }

    @Override
    public UserModel getUserById( String id, RealmModel realm ) {
//        LOGGER.infov("getUserById(): realm={0} userId={1}", realm.getId(), id);
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(username, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
//        LOGGER.infov("getUserByUsername(): realm={0} username={1}", realm.getId(), username);
        final var user = repository.findUserById(username);
        if( user == null ) {
            return null;
        }
        return new UserAdapter( session, realm, model, user );
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        final var user = repository.findUserByEmail(email);
        if( user == null ) {
            return null;
        }
//        LOGGER.infov("lookup user by email: realm={0} email={1}", realm.getId(), email);
        return new UserAdapter( session, realm, model, user );
    }

    @Override
    public int getUsersCount(RealmModel realm) {
//        LOGGER.infov("getUsersCount: realm={0}", realm.getId());
        return repository.getAllUsersCount();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
//        LOGGER.infov("list users: realm={0}", realm.getId());
        return repository.getAllUsers().stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
//        LOGGER.infov("list users: realm={0} firstResult={1} maxResults={2}", realm.getId(), firstResult, maxResults);
        return repository.getAllUsers( firstResult, maxResults ).stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
//        LOGGER.infov("search for users: realm={0} search={1}", realm.getId(), search);
        return repository.findUsers(search).stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
//        LOGGER.infov( "search for users: realm={0} search={1} firstResult={2} maxResults={3}",
//                      realm.getId(), search, firstResult, maxResults );
        return repository.findUsers(search, firstResult, maxResults ).stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    /** Not implemented */
    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        LOGGER.infov("search for users with params: realm={0} params={1}", realm.getId(), params);
        throw new RuntimeException( "Not implemented: searchForUser(Map, RealmModel)" );
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        LOGGER.infov( "search for users with params: realm={0} params={1} firstResult={2} maxResults={3}",
                      realm.getId(), params, firstResult, maxResults);
        if( params.size() == 0 ) {
            return searchForUser( "", realm, firstResult, maxResults );
        }

        throw new RuntimeException( "Not implemented: searchForUser(Map, RealmModel, int, int" );
    }

    /** Not implemented */
    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        LOGGER.warn( "Not implemented: getGroupMembers()" );
        return Collections.emptyList();
    }

    /** Not implemented */
    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        LOGGER.warn( "Not implemented: getGroupMembers()" );
        return Collections.emptyList();
    }

    /** Not implemented */
    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        LOGGER.warn( "Not implemented: searchForUserByUserAttribute()" );
        return Collections.emptyList();
    }
}
