package io.pivotal.test;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

@Component
public class Runner2 {

    private final Logger logger = LoggerFactory.getLogger(Runner2.class);

    private final CloudFoundryClient cloudFoundryClient;

    @Autowired
    Runner2(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    CountDownLatch run() {
        CountDownLatch latch = new CountDownLatch(2);

        for (int i = 0; i < 5; i++) {
            status(i);
            deploy(i);
        }

        return latch;
    }

    private void deploy(int i) {
        new Thread(() -> {
            for (; ; ) {
                try {
                    long start = System.currentTimeMillis();
                    try {
                        this.cloudFoundryClient.uploadApplication("java-main-application-" + i,
                            Paths.get("/Users/bhale/dev/sources/java-test-applications/java-main-application/build/libs/java-main-application-1.0.0.BUILD-SNAPSHOT.jar").toFile());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    this.cloudFoundryClient.restartApplication("java-main-application-" + i);
                    this.logger.info("Push Application {}: {} ms", i, System.currentTimeMillis() - start);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void status(int i) {
        new Thread(() -> {
            for (; ; ) {
                try {
                    long start = System.currentTimeMillis();
                    this.cloudFoundryClient.getApplication("java-main-application-" + i);
                    this.logger.info("Get Application {}: {} ms", i, System.currentTimeMillis() - start);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
