package ru.practicum.stat.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatClientImpl implements StatClient {
    private final RestClient client;

    @Autowired
    public StatClientImpl(@Value("${server.url}") String serverUrl) {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(serverUrl);
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        client = RestClient.builder()
                .uriBuilderFactory(uriBuilderFactory)
                .build();
    }

    @Override
    public void create(EndpointHit endpointHit) {
        ResponseEntity<Void> response = client.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHit)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String uri = UriComponentsBuilder.newInstance()
                .path("/stats")
                .queryParam("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .toUriString();

        return client.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
