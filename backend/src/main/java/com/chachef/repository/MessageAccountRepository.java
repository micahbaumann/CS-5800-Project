package com.chachef.repository;

import com.chachef.entity.Booking;
import com.chachef.entity.MessageAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageAccountRepository extends JpaRepository<MessageAccount, UUID> {
    Optional<MessageAccount> findByUser_UserId(UUID user_UserId);
    Optional<MessageAccount> findByChef_ChefId(UUID chef_ChefId);
    Optional<MessageAccount> findMessageAccountByMessageAccountId(UUID messageAccountId);
}
