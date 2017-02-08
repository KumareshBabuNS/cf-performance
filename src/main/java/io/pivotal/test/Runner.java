package io.pivotal.test;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

@Component
final class Runner {

    private final Logger logger = LoggerFactory.getLogger(Runner.class);

    private final String application;

    private final CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    Runner(String application, CloudFoundryOperations cloudFoundryOperations) {
        this.application = application;
        this.cloudFoundryOperations = cloudFoundryOperations;
    }

    CountDownLatch run() {
        CountDownLatch latch = new CountDownLatch(1);

//        for (int i = 0; i < 10; i++) {
            status();
//            deploy();
//        }

        return latch;
    }

    private void deploy() {
        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(Paths.get("/Users/bhale/dev/sources/java-test-applications/java-main-application/build/libs/java-main-application-1.0.0.BUILD-SNAPSHOT.jar"))
                .name("java-main-application")
                .build())
            .elapsed()
            .doOnNext(consumer((elapsed, v) -> this.logger.info("Push Application: {} ms", elapsed)))
            .repeat()
            .retry()
            .subscribe();
    }

    private void status() {
        this.cloudFoundryOperations.applications()
            .get(GetApplicationRequest.builder()
                .name(this.application)
                .build())
            .elapsed()
            .doOnNext(consumer((elapsed, application) -> this.logger.info("Get Application: {} ms", elapsed)))
            .repeat()
            .retry()
            .subscribe();
    }

}
