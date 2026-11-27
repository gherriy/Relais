package fr.gherriy.relais.api;

import static org.assertj.core.api.Assertions.assertThat;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class RelaisApiApplicationTests {

	@Test
	void doitDemarrerAvecUnSchemaConformeAuxEntites() {
    }
	@Test
    void doitAppliquerLesMigrationsAuDemarrage(@Autowired Flyway flyway) {
        assertThat(flyway.info().applied())
                .extracting(migration -> migration.getVersion().getVersion())
                .containsExactly("1", "2");
    }

}
