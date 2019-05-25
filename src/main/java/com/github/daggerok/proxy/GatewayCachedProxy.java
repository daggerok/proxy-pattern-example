package com.github.daggerok.proxy;

import com.github.daggerok.backend.GatewayImpl;
import com.github.daggerok.shared.Gateway;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * This internal interface is going to be used to control cache of Proxy object only.
 */
interface Cacheable {
  Map<String, Iterable<String>> getCache();

  void evictCache();
}

/**
 * Purpose of this Proxy implementation is provide additional caching functionality, which is missing
 * in original {@link com.github.daggerok.backend.GatewayImpl} implementation we still would like to
 * use, but don't wanna or cannot modify (like it usually happens when we are using non open-sourced
 * 3rd party libraries for example)
 */
@Slf4j
public class GatewayCachedProxy implements Gateway, Cacheable {

  private final /* static */ ThreadLocal<Gateway> gateway = ThreadLocal.withInitial(GatewayImpl::new);
  private final /* static */ ThreadLocal<Map<String, Iterable<String>>> cache =
      ThreadLocal.withInitial(ConcurrentHashMap::new);

  @Override
  public Map<String, Iterable<String>> getCache() {
    return cache.get();
  }

  @Override
  public void evictCache() {
    cache.get().clear();
  }

  @Override
  public void save(String... messages) {
    log.info(format("calling save method within arguments: %s...", asList(messages)));
    Gateway gw = gateway.get();
    gw.save(messages);
    gateway.set(gw);
  }

  @Override
  public Iterable<String> findAllMessages() {
    log.info("calling findAllMessages method...");

    String key = "findAllMessages";
    Iterable<String> messages = cache.get().get(key);
    if (Objects.nonNull(messages) && messages.iterator().hasNext()) return messages;

    Map<String, Iterable<String>> c = cache.get();
    c.put(key, gateway.get().findAllMessages());
    cache.set(c);
    return cache.get().get(key);
  }

  @Override
  public Iterable<String> findLastMessages(int amount) {
    log.info(format("calling findLastMessages method with amount argument: %s...", amount));

    String key = format("findLastMessages %s", amount);
    Iterable<String> messages = cache.get().get(key);
    if (Objects.nonNull(messages) && messages.iterator().hasNext())
      return messages;

    Map<String, Iterable<String>> c = cache.get();
    c.put(key, gateway.get().findLastMessages(amount));
    cache.set(c);
    return cache.get().get(key);
  }
}
