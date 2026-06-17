package com.jptaxi.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.jptaxi.application.dto.ConversationDto;
import com.jptaxi.application.entity.Conversation;
import com.jptaxi.application.entity.ConversationParticipant;
import com.jptaxi.application.entity.ConversationParticipantId;
import com.jptaxi.application.entity.Restaurant;
import com.jptaxi.application.entity.User;
import com.jptaxi.application.repository.ConversationParticipantRepository;
import com.jptaxi.application.repository.ConversationRepository;
import com.jptaxi.application.repository.MessageRepository;
import com.jptaxi.application.repository.RestaurantRepository;
import com.jptaxi.application.repository.UserRepository;
import com.jptaxi.application.service.DtoMapper;

class ChatControllerTests {

    @Test
    void getConversationsPreservesRepositoryOrderAndMapsSummaryFields() {
        ConversationParticipantRepository participantRepository = mock(ConversationParticipantRepository.class);
        ConversationRepository conversationRepository = mock(ConversationRepository.class);
        ChatController controller = new ChatController(
                participantRepository,
                conversationRepository,
                mock(MessageRepository.class),
                mock(UserRepository.class),
                mock(RestaurantRepository.class),
                new DtoMapper()
        );

        Conversation firstConversation = conversation(
                "conv-1",
                user("u1", "Current User", "https://cdn/current.png"),
                user("u2", "Owner A", "https://cdn/owner-a.png"),
                restaurant("r1", "Quan A", "Ha Noi", "https://cdn/r1.png", "u2")
        );
        Conversation secondConversation = conversation(
                "conv-2",
                user("u1", "Current User", "https://cdn/current.png"),
                user("u3", "Owner B", "https://cdn/owner-b.png"),
                restaurant("r2", "Quan B", "Tokyo", "https://cdn/r2.png", "u3")
        );

        when(conversationRepository.findConversationIdsByUserId("u1")).thenReturn(List.of("conv-2", "conv-1"));
        when(conversationRepository.findConversationListByIds(List.of("conv-2", "conv-1")))
                .thenReturn(List.of(firstConversation, secondConversation));

        List<ConversationDto> result = controller.getConversations("u1");

        assertThat(result).extracting(ConversationDto::id).containsExactly("conv-2", "conv-1");
        assertThat(result.get(0).otherUserId()).isEqualTo("u3");
        assertThat(result.get(0).restaurantId()).isEqualTo("r2");
        assertThat(result.get(0).restaurantCoverImage()).isEqualTo("https://cdn/r2.png");
    }

    @Test
    void getConversationsReturnsEmptyWhenUserHasNoConversation() {
        ConversationRepository conversationRepository = mock(ConversationRepository.class);
        ChatController controller = new ChatController(
                mock(ConversationParticipantRepository.class),
                conversationRepository,
                mock(MessageRepository.class),
                mock(UserRepository.class),
                mock(RestaurantRepository.class),
                new DtoMapper()
        );

        when(conversationRepository.findConversationIdsByUserId("u1")).thenReturn(List.of());

        assertThat(controller.getConversations("u1")).isEmpty();
    }

    private Conversation conversation(String id, User currentUser, User otherUser, Restaurant restaurant) {
        Conversation conversation = new Conversation();
        conversation.setId(id);
        conversation.setRestaurant(restaurant);
        conversation.getParticipants().add(participant(conversation, currentUser));
        conversation.getParticipants().add(participant(conversation, otherUser));
        return conversation;
    }

    private ConversationParticipant participant(Conversation conversation, User user) {
        ConversationParticipant participant = new ConversationParticipant();
        participant.setId(new ConversationParticipantId(conversation.getId(), user.getId()));
        participant.setConversation(conversation);
        participant.setUser(user);
        return participant;
    }

    private User user(String id, String name, String avatar) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setAvatar(avatar);
        return user;
    }

    private Restaurant restaurant(String id, String nameJp, String address, String coverImage, String ownerId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setNameJp(nameJp);
        restaurant.setAddress(address);
        restaurant.setCoverImage(coverImage);
        User owner = new User();
        owner.setId(ownerId);
        restaurant.setOwner(owner);
        return restaurant;
    }
}
