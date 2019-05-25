package com.github.daggerok.shared;

public interface Gateway {
  void save(String... messages);

  Iterable<String> findAllMessages();

  Iterable<String> findLastMessages(int amount);
}
