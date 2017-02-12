package io.pivotal.test;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

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

        for (int i = 0; i < 5; i++) {
            status(i);
            deploy(i);
        }

        return latch;
    }

    private void deploy(int i) {
        AtomicLong startTime = new AtomicLong();

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(Paths.get("/Users/bhale/dev/sources/java-test-applications/java-main-application/build/libs/java-main-application-1.0.0.BUILD-SNAPSHOT.jar"))
                .name("java-main-application-" + i)
                .host("ben-java-main-application-" + i)
                .build())
            .doOnSubscribe(s -> startTime.set(System.currentTimeMillis()))
            .doOnTerminate((v, t) -> {
                if (t != null) {
                    t.printStackTrace();
                }

                this.logger.info("Push Application {}: {} ms", i, System.currentTimeMillis() - startTime.get());
            })
            .repeat()
            .retry()
            .subscribe();
    }

    private void status(int i) {
        AtomicLong startTime = new AtomicLong();

        this.cloudFoundryOperations.applications()
            .list()
            .collectList()
            .doOnSubscribe(s -> startTime.set(System.currentTimeMillis()))
            .doOnTerminate((v, t) -> {
                if (t != null) {
                    t.printStackTrace();
                }

                this.logger.info("List Applications {}: {} ms", i, System.currentTimeMillis() - startTime.get());
            })
            .repeat()
            .retry()
            .subscribe();
    }

}
