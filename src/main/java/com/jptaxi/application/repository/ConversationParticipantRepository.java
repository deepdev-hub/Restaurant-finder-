package com.jptaxi.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jptaxi.application.entity.ConversationParticipant;
import com.jptaxi.application.entity.ConversationParticipantId;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

    List<ConversationParticipant> findByUser_Id(String userId);

    @Query("""
            select distinct participant
            from ConversationParticipant participant
            join fetch participant.conversation conversation
            left join fetch conversation.restaurant restaurant
            left join fetch restaurant.owner
            join fetch conversation.participants allParticipants
            join fetch allParticipants.user
            where participant.user.id = :userId
            order by coalesce(conversation.lastMessageAt, conversation.updatedAt, conversation.createdAt) desc
            """)
    List<ConversationParticipant> findConversationListByUserId(@Param("userId") String userId);
}
