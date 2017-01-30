package io.pivotal.test;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SignalType;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@Component
final class Runner {

    private final String application;

    private final CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    Runner(String application, CloudFoundryOperations cloudFoundryOperations) {
        this.application = application;
        this.cloudFoundryOperations = cloudFoundryOperations;
    }

    CountDownLatch run() {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        this.cloudFoundryOperations.applications()
            .get(GetApplicationRequest.builder()
                .name(this.application)
                .build())
            .log("stream.postRequest", Level.INFO, SignalType.CANCEL, SignalType.ON_ERROR, SignalType.ON_COMPLETE)
            .elapsed()
            .map(Tuple2::getT1)
            .repeat()
            .doOnError(Throwable::printStackTrace)
            .retry(t -> true)
            .subscribe(elapsed -> {
                int count = counter.getAndIncrement();
                if (count % 250 == 0) {
                    System.out.printf("\n%s - %d\t", Instant.now().atZone(ZoneId.systemDefault()).toLocalTime(), count);
                }

                if (elapsed < 1_000) {
                    System.out.print(".");
                } else {
                    System.out.printf(" %d ", elapsed);
                }
            }, t -> {
                t.printStackTrace();
                latch.countDown();
            }, latch::countDown);

        return latch;
    }

}
