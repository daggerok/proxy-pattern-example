package com.github.daggerok.proxy;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayCachedProxyTest {

  @Test
  void should_find_all_messages_by_using_cache() {
    System.out.println("\n\t should_find_all_messages");

    GatewayCachedProxy gateway = new GatewayCachedProxy();
    gateway.evictCache();

    gateway.save("Hello!", "Hola!", "Привет!", "Hej!");
    assertThat(gateway.getCache().get("findAllMessages")).isNullOrEmpty();

    gateway.findAllMessages();
    assertThat(gateway.getCache().get("findAllMessages")).hasSize(4);
    gateway.findAllMessages();
  }

  @Test
  @SuppressWarnings("unchecked")
  void test_find_not_more_then_given_amount_of_last_saved_messages_by_using_cache() {
    System.out.println("\n\t test_find_not_more_then_given_amount_of_last_saved_messages_by_using_cache");

    GatewayCachedProxy gateway = new GatewayCachedProxy();
    gateway.evictCache();

    Stream.of("Hello!",
              "Hola!",
              "Привет!",
              "Hej!")
          .peek(s -> Try.run(() -> TimeUnit.MICROSECONDS.sleep(200)))
          .forEach(gateway::save);

    String key = "findLastMessages 3";
    assertThat(gateway.getCache().get(key)).isNullOrEmpty();

    gateway.findLastMessages(3);
    Iterable<String> descendingOrderedMessagesFromCache = gateway.getCache().get(key);
    assertThat(descendingOrderedMessagesFromCache).hasSize(3);
    Iterable<String> descendingOrderedMessages = gateway.findLastMessages(3);
    assertThat(descendingOrderedMessagesFromCache).isEqualTo(descendingOrderedMessages);

    Iterator<String> iterator = descendingOrderedMessagesFromCache.iterator();
    assertThat(iterator.next()).isEqualTo("Hej!");
    assertThat(iterator.next()).isEqualTo("Привет!");
    assertThat(iterator.next()).isEqualTo("Hola!");
    assertThat(iterator.hasNext()).isFalse();
  }
}
