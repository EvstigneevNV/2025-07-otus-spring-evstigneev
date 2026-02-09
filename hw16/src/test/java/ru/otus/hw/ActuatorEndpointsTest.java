package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorEndpointsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthShouldBeAvailableAndContainCustomIndicator() {
        ResponseEntity<String> resp = restTemplate.getForEntity(url("/actuator/health"), String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).contains("library");
    }

    @Test
    void customMetricShouldBeAvailable() {
        ResponseEntity<String> resp = restTemplate.getForEntity(url("/actuator/metrics/library.books.count"), String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).contains("library.books.count");
    }

    @Test
    void logfileShouldBeAvailable() {
        ResponseEntity<String> resp = restTemplate.getForEntity(url("/actuator/logfile"), String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isNotBlank();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
