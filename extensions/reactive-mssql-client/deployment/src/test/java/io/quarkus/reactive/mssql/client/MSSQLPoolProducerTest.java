package io.quarkus.reactive.mssql.client;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.vertx.sqlclient.Pool;

public class MSSQLPoolProducerTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("application-default-datasource.properties")
            .withApplicationRoot((jar) -> jar
                    .addClasses(BeanUsingBareMSSQLClient.class)
                    .addClasses(BeanUsingMutinyMSSQLClient.class));

    @Inject
    BeanUsingBareMSSQLClient beanUsingBare;

    @Inject
    BeanUsingMutinyMSSQLClient beanUsingMutiny;

    @Test
    public void testVertxInjection() {
        beanUsingBare.verify()
                .thenCompose(v -> beanUsingMutiny.verify())
                .toCompletableFuture()
                .join();
    }

    @ApplicationScoped
    static class BeanUsingBareMSSQLClient {

        @Inject
        Pool mssqlClient;

        public CompletionStage<?> verify() {
            return mssqlClient.query("SELECT 1").execute().toCompletionStage();
        }
    }

    @ApplicationScoped
    static class BeanUsingMutinyMSSQLClient {

        @Inject
        io.vertx.mutiny.sqlclient.Pool mssqlClient;

        public CompletionStage<Void> verify() {
            return mssqlClient.query("SELECT 1").execute()
                    .onItem().ignore().andContinueWithNull()
                    .subscribeAsCompletionStage();
        }
    }
}
