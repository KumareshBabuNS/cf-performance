package io.pivotal.test;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

//@Component
public class Runner2 {

    private final Logger logger = LoggerFactory.getLogger(Runner2.class);

    private final CloudFoundryClient cloudFoundryClient;

    @Autowired
    Runner2(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    CountDownLatch run() {
        CountDownLatch latch = new CountDownLatch(2);

        new Thread(() -> {
            for (; ; ) {
                long start = System.currentTimeMillis();
                this.cloudFoundryClient.getApplication("java-main-application");
                this.logger.info("Get Application: {} ms", System.currentTimeMillis() - start);
            }
        }).start();

//        new Thread(() -> {
//            for (; ; ) {
//                long start = System.currentTimeMillis();
//                try {
//                    this.cloudFoundryClient.uploadApplication("java-main-application",
//                        Paths.get("/Users/bhale/dev/sources/java-test-applications/java-main-application/build/libs/java-main-application-1.0.0.BUILD-SNAPSHOT.jar").toFile());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                this.cloudFoundryClient.restartApplication("java-main-application");
//                this.logger.info("Push Application: {} ms", System.currentTimeMillis() - start);
//
//            }
//        }).start();

        return latch;
    }

}
