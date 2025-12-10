package com.chachef.repository;

import com.chachef.entity.Message;
import com.chachef.entity.MessageAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    Optional<Message> findMessageByMessageId(UUID messageId);
    Optional<List<Message>> findByToAccount_MessageAccountId(UUID messageAccountId);
    Optional<List<Message>> findByFromAccount_MessageAccountId(UUID messageAccountId);
}
