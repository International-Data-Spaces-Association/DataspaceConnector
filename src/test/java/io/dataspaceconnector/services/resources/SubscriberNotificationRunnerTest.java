/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.services.resources;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriberNotificationRunnerTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private Mono<String> mono;

    private SubscriberNotificationRunner runner;

    private final UUID resourceId = UUID.randomUUID();

    private final URI subscriber1 = URI.create("https://subscriber-1.com");

    private final URI subscriber2 = URI.create("https://subscriber-2.com");

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(mono);
        when(mono.retryWhen(any())).thenReturn(mono);
        when(mono.onErrorResume(any())).thenReturn(mono);
    }

    @AfterEach
    public void reset() {
        Mockito.reset(webClient, requestBodyUriSpec, requestBodySpec, requestHeadersSpec,
                responseSpec, mono);
    }

    @Test
    public void run_oneSubscription_sendOneNotification() throws InterruptedException {
        /* ARRANGE */
        runner = new SubscriberNotificationRunner(resourceId, List.of(subscriber1));
        ReflectionTestUtils.setField(runner, "webClient", webClient);

        /* ACT */
        runner.run();

        Thread.sleep(1000);

        /* ASSERT */
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1)).uri(subscriber1);
        verify(requestBodySpec, times(1)).bodyValue(resourceId);
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(String.class);
    }

    @Test
    public void run_twoSubscriptions_sendTwoNotifications() throws InterruptedException {
        /* ARRANGE */
        runner = new SubscriberNotificationRunner(resourceId, List.of(subscriber1, subscriber2));
        ReflectionTestUtils.setField(runner, "webClient", webClient);

        /* ACT */
        runner.run();

        Thread.sleep(1000);

        /* ASSERT */
        verify(webClient, times(2)).post();
        verify(requestBodyUriSpec, times(1)).uri(subscriber1);
        verify(requestBodyUriSpec, times(1)).uri(subscriber2);
        verify(requestBodySpec, times(2)).bodyValue(resourceId);
        verify(requestHeadersSpec, times(2)).retrieve();
        verify(responseSpec, times(2)).bodyToMono(String.class);
    }

}
