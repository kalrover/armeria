/*
 * Copyright 2019 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.spring;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableList;

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.grpc.GrpcServiceBuilder;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaAutoConfigurationGrpcTest.TestConfiguration;
import com.linecorp.armeria.spring.ArmeriaAutoConfigurationTest.HelloGrpcService;
import com.linecorp.armeria.spring.GrpcServiceRegistrationBean.ExampleRequest;
import com.linecorp.armeria.spring.test.grpc.main.Hello.HelloRequest;
import com.linecorp.armeria.spring.test.grpc.main.HelloServiceGrpc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles({ "local", "autoConfTest" })
@DirtiesContext
public class ArmeriaAutoConfigurationGrpcTest {

    @SpringBootApplication
    public static class TestConfiguration {
        @Bean
        public GrpcServiceRegistrationBean helloGrpcService() {
            return new GrpcServiceRegistrationBean()
                    .setServiceName("helloGrpcService")
                    .setService(new GrpcServiceBuilder()
                                        .addService(new HelloGrpcService())
                                        .supportedSerializationFormats(GrpcSerializationFormats.values())
                                        .enableUnframedRequests(true)
                                        .build())
                    .setDecorators(LoggingService.newDecorator())
                    .setExampleRequests(ImmutableList.of(ExampleRequest.of(HelloServiceGrpc.SERVICE_NAME,
                                                                           "Hello",
                                                                           HelloRequest.newBuilder()
                                                                                       .setName("Armeria")
                                                                                       .build())));
        }
    }

    @Inject
    @Nullable
    private Server server;

    @Test
    public void testArmeriaServerShouldStarted() throws Exception {
        assertThat(server).isNotNull();
    }
}
