package io.pivotal.test;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@SpringBootApplication
public class CfPerformanceApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(CfPerformanceApplication.class, args)
            .getBean(Runner.class)
//            .getBean(Runner2.class)
            .run()
            .await();
    }

//    @Bean
//    CloudCredentials cloudCredentials(@Value("${test.password}") String password,
//                                      @Value("${test.username}") String username) {
//
//        return new CloudCredentials(username, password);
//    }
//
//    @Bean(initMethod = "login")
//    CloudFoundryClient cloudFoundryClient(CloudCredentials cloudCredentials,
//                                          @Value("${test.apiHost}") String apiHost,
//                                          @Value("${test.organization}") String organization,
//                                          @Value("${test.space}") String space,
//                                          @Value("${test.skipSslValidation:false}") Boolean skipSslValidation) throws MalformedURLException {
//
//        return new CloudFoundryClient(cloudCredentials, URI.create("https://" + apiHost).toURL(), organization, space, skipSslValidation);
//    }

    @Bean
    String application(@Value("${test.application}") String application) {
        return application;
    }

    @Bean
    ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();
    }

    @Bean
    DefaultCloudFoundryOperations cloudFoundryOperations(CloudFoundryClient cloudFoundryClient,
                                                         @Value("${test.organization}") String organization,
                                                         @Value("${test.space}") String space) {
        return DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(cloudFoundryClient)
            .organization(organization)
            .space(space)
            .build();
    }

    @Bean
    DefaultConnectionContext connectionContext(@Value("${test.apiHost}") String apiHost,
                                               @Value("${test.skipSslValidation:false}") Boolean skipSslValidation) {

        return DefaultConnectionContext.builder()
            .apiHost(apiHost)
            .skipSslValidation(skipSslValidation)
            .build();
    }

    @Bean
    PasswordGrantTokenProvider tokenProvider(@Value("${test.password}") String password,
                                             @Value("${test.username}") String username) {

        return PasswordGrantTokenProvider.builder()
            .password(password)
            .username(username)
            .build();
    }

}
