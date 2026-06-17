package com.jptaxi.application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jptaxi.application.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, String> {

    @Query("""
            select conversation
            from Conversation conversation
            join conversation.participants senderParticipant
            join conversation.participants receiverParticipant
            where senderParticipant.user.id = :senderId
              and receiverParticipant.user.id = :receiverId
              and (:restaurantId is null or conversation.restaurant.id = :restaurantId)
            """)
    Optional<Conversation> findConversationForMessage(String senderId, String receiverId, String restaurantId);

    @Query("""
            select conversation.id
            from ConversationParticipant participant
            join participant.conversation conversation
            where participant.user.id = :userId
            order by coalesce(conversation.lastMessageAt, conversation.updatedAt, conversation.createdAt) desc
            """)
    List<String> findConversationIdsByUserId(@Param("userId") String userId);

    @Query("""
            select distinct conversation
            from Conversation conversation
            left join fetch conversation.restaurant restaurant
            left join fetch restaurant.owner
            join fetch conversation.participants participant
            join fetch participant.user
            where conversation.id in :conversationIds
            """)
    List<Conversation> findConversationListByIds(@Param("conversationIds") List<String> conversationIds);
}
