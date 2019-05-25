package com.github.daggerok.backend;

import com.github.daggerok.shared.Gateway;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;

/**
 * This is a real backend service we are going to use for general purposes.
 * Our assumptions is we cannot modify it at all. It's mean that whenever
 * we need update it's behavior we will got a problems. Fortunately we can
 * write our own Proxy around it (similar to decorator or adapter patterns)
 * and use that Proxy instead. Anyway, any useful business login from here
 * still can be reused in Proxy:
 * see {@link com.github.daggerok.proxy.GatewayCachedProxy}
 */
@Slf4j
public class GatewayImpl implements Gateway {

  private final AtomicLong identifiers = new AtomicLong(Long.MIN_VALUE);
  private final ThreadLocal<Map<Long, Message>> repository = ThreadLocal
      .withInitial(() -> new ConcurrentSkipListMap<>((o1, o2) -> -o1.compareTo(o2)));

  @Override
  public void save(String... messages) {
    Stream.of(messages)
          .map(message -> singletonMap(identifiers.incrementAndGet(), message))
          .map(pair -> Message.of(pair.keySet().iterator().next(),
                                  pair.values().stream().findFirst().orElseThrow(RuntimeException::new),
                                  LocalDateTime.now()))
          .peek(message -> log.info("save: {}", message))
          .forEach(message -> repository.get().put(message.getId(), message));
  }

  @Override
  public Iterable<String> findAllMessages() {
    return repository.get()
                     .values()
                     .stream()
                     .peek(message -> log.info("findAllMessages: {}", message))
                     .map(Message::getContent)
                     .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
  }

  @Override
  public Iterable<String> findLastMessages(int amount) {
    return repository.get()
                     .values()
                     .stream()
                     .peek(message -> log.info("findLastMessages: {}", message))
                     .map(Message::getContent)
                     .limit(amount)
                     .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
  }

  @Value(staticConstructor = "of")
  private static final class Message {
    private final Long id;
    private final String content;
    private final LocalDateTime at;
  }
}
