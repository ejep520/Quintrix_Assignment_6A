package com.quintrix.jepsen.erik.sixthA.client;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import com.quintrix.jepsen.erik.sixthA.model.Person;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

public class RestClient {
  @Value("${sixthA.baseUri}")
  private String baseUri;
  @Value("${sixthA.timeoutMax}")
  private int timeoutMax;
  private HttpClient httpClient =
      HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMax)
          .responseTimeout(Duration.ofMillis(timeoutMax)).doOnConnected(
              conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeoutMax, TimeUnit.MILLISECONDS))
                  .addHandlerLast(new WriteTimeoutHandler(timeoutMax, TimeUnit.MILLISECONDS)));
  private WebClient webClient = WebClient.builder().baseUrl(baseUri)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.toString())
      .defaultUriVariables(Collections.singletonMap("url", baseUri))
      .clientConnector(new ReactorClientHttpConnector(httpClient)).build();

  public Person getReply(String uri, Object id) {
    return webClient.get().uri(uri, id).retrieve().bodyToMono(Person.class).block();
  }

  public Person[] getReplies(String uri, Object id) {
    List<Person> personList =
        webClient.get().uri(uri, id).retrieve().bodyToFlux(Person.class).collectList().block();
    return personList.toArray(new Person[personList.size()]);
  }

  public String deleteReply(String uri, Object id) {
    return webClient.delete().uri(uri, id).retrieve().bodyToMono(String.class).block();
  }

  public String postReply(String uri, MultiValueMap<String, String> formData) {
    return webClient.post().uri(uri).bodyValue(formData).retrieve().bodyToMono(String.class)
        .block();
  }
}
