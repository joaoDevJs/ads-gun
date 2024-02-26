package io.desentupidora.adsgun.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.desentupidora.adsgun.domain.model.input.TargetInput;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.*;

@Service
public class RequesterService {

    private static final String AGENTS = "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148";
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
    private final ConcurrentSkipListSet<TargetInput> validTargetInputs = new
            ConcurrentSkipListSet<>(Comparator.comparing(TargetInput::uuid));

    public RequesterService() {
        start();
    }

    private void start() {

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("http://localhost:8080/targets")).GET().build();

        service.scheduleAtFixedRate(() -> {

            this.validTargetInputs.clear();
            CompletableFuture<HttpResponse<String>> sentAsync = httpClient
                    .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
            try {
                String jsonBody = sentAsync.join().body();
                LinkedList<TargetInput> targetInputs = new ObjectMapper().readValue(jsonBody, new TypeReference<>() {
                });
                this.validTargetInputs.addAll(targetInputs);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            attack();

        }, 0, 4, TimeUnit.MINUTES);
    }

    private void attack() {
        while (!this.validTargetInputs.isEmpty()) {
            this.validTargetInputs.forEach(targetInput -> {
                try {
                    TimeUnit.SECONDS.sleep(15);
                    Connection.Response response = Jsoup.newSession().url(targetInput.url()).userAgent(AGENTS).execute();
                    System.out.println("====================================================" +
                            "\n"+targetInput.title() + "\n" +
                            "status: " + response.statusCode());
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
