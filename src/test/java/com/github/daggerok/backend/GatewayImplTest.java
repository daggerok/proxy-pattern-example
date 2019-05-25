package com.github.daggerok.backend;

import com.github.daggerok.shared.Gateway;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayImplTest {

  @Test
  void should_save_message() {
    System.out.println("\n\t should_save_message");

    Gateway gateway = new GatewayImpl();
    assertThat(gateway.findAllMessages()).isNotNull()
                                         .isEmpty();
    gateway.save("Hello!");
    assertThat(gateway.findAllMessages()).isNotNull()
                                         .hasSizeGreaterThan(0);
  }

  @Test
  void should_find_all_messages() {
    System.out.println("\n\t should_find_all_messages");

    Gateway gateway = new GatewayImpl();
    assertThat(gateway.findAllMessages()).isNotNull()
                                         .isEmpty();
    gateway.save("Hello!", "Hola!", "Привет!", "Hej!");
    assertThat(gateway.findAllMessages()).isNotNull()
                                         .hasSize(4);
  }

  @Test
  void test_find_not_more_then_given_amount_of_last_saved_messages() {
    System.out.println("\n\t test_find_not_more_then_given_amount_of_last_saved_messages");

    Gateway gateway = new GatewayImpl();
    assertThat(gateway.findAllMessages()).isNotNull()
                                         .isEmpty();
    Stream.of("Hello!",
              "Hola!",
              "Привет!",
              "Hej!")
          .peek(s -> Try.run(() -> TimeUnit.MICROSECONDS.sleep(200)))
          .forEach(gateway::save);

    int expectedSize = 3;
    Iterable<String> descendingOrderedMessages = gateway.findLastMessages(expectedSize);
    assertThat(descendingOrderedMessages).hasSize(expectedSize);

    Iterator<String> iterator = descendingOrderedMessages.iterator();
    assertThat(iterator.next()).isEqualTo("Hej!");
    assertThat(iterator.next()).isEqualTo("Привет!");
    assertThat(iterator.next()).isEqualTo("Hola!");
    assertThat(iterator.hasNext()).isFalse();
  }
}
