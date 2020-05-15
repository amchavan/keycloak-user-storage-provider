package alma.obops.keycloak.userprovider;

import org.junit.Test;

import javax.sql.DataSource;

import static org.junit.Assert.*;

import java.io.IOException;

public class TestAlmaDataSource {

    @Test
    public void dataSourceCreation() throws IOException {
        AlmaDataSource almaDataSource = new AlmaDataSource();
        assertNotNull( almaDataSource );

        DataSource dataSource = almaDataSource.getDataSource();
        assertNotNull( dataSource );
    }
}
