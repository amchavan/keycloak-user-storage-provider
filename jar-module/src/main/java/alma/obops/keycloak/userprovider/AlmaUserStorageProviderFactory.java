package alma.obops.keycloak.userprovider;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.io.IOException;
import java.util.List;

/**
 * See https://www.keycloak.org/docs/latest/server_development/index.html#_user-storage-spi
 *
 * @author amchavan, 30-Apr-2020
 */
@AutoService(UserStorageProviderFactory.class)
public class AlmaUserStorageProviderFactory implements UserStorageProviderFactory<AlmaUserStorageProvider> {

    public static final String USER_PROVIDER_NAME = "alma-user-provider";
    private static final Logger LOGGER = Logger.getLogger(AlmaUserStorageProviderFactory.class);

    @Override
    public void init(Config.Scope config) {

        // this configuration is pulled from the SPI configuration of this provider in the standalone[-ha] / domain.xml
        // see setup.cli

        String someProperty = config.get("someProperty");
        LOGGER.infov("Configured {0} with someProperty: {1}", this, someProperty);
    }

    @Override
    public AlmaUserStorageProvider create(KeycloakSession session, ComponentModel model) {

        try {
            AlmaDataSource almaDataSource = new AlmaDataSource();
            AlmaUserRepository repository = new AlmaUserRepository( almaDataSource.getDataSource() );
            LOGGER.infov( "create(): creating UserStorageProvider with repository={0}", repository );
            return new AlmaUserStorageProvider(session, model, repository);
        }
        catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public String getId() {
        return USER_PROVIDER_NAME;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        // this configuration is configurable in the admin-console
        return ProviderConfigurationBuilder.create()
                .property()
                .name("almaParam1")
                .label("ALMA Parameter 1")
                .helpText("Description of ALMA Parameter 1")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("1")
                .add()
                // more properties
                // .property()
                // .add()
                .build();
    }
}
