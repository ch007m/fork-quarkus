package io.quarkus.quartz.test.customDelegate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;

import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.builder.BuildChainBuilder;
import io.quarkus.builder.BuildContext;
import io.quarkus.builder.BuildStep;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.quartz.test.SimpleJobs;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.test.QuarkusUnitTest;

public class DelegateNotIndexedTest {

    @RegisterExtension
    static final QuarkusUnitTest test = new QuarkusUnitTest()
            // add a mock pretending to provide Agroal Capability to pass our validation
            .addBuildChainCustomizer(new Consumer<>() {
                @Override
                public void accept(BuildChainBuilder buildChainBuilder) {
                    buildChainBuilder.addBuildStep(new BuildStep() {
                        @Override
                        public void execute(BuildContext context) {
                            context.produce(
                                    new CapabilityBuildItem(Capability.AGROAL, "fakeProvider"));
                        }
                    }).produces(CapabilityBuildItem.class).build();
                }
            })
            .assertException(t -> {
                assertEquals(ConfigurationException.class, t.getClass());
                Assertions.assertTrue(t.getMessage().contains(
                        "Custom JDBC delegate implementation class 'org.acme.DoesNotExist' was not found in Jandex index"));
            })
            .withApplicationRoot((jar) -> jar
                    .addClasses(SimpleJobs.class)
                    .addAsResource(new StringAsset(
                            "quarkus.quartz.driver-delegate=org.acme.DoesNotExist\nquarkus.quartz.store-type=jdbc-cmt"),
                            "application.properties"));

    @Test
    public void shouldFailWhenNotIndexed() {
        Assertions.fail();
    }
}
