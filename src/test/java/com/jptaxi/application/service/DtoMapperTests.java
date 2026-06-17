package com.jptaxi.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.jptaxi.application.dto.ConversationDto;
import com.jptaxi.application.entity.Conversation;
import com.jptaxi.application.entity.ConversationParticipant;
import com.jptaxi.application.entity.ConversationParticipantId;
import com.jptaxi.application.entity.Restaurant;
import com.jptaxi.application.entity.User;

class DtoMapperTests {

    @Test
    void conversationDtoIncludesOtherUserAndRestaurantMetadata() {
        DtoMapper mapper = new DtoMapper();
        User currentUser = user("u1", "Current User", "https://cdn/current.png");
        User otherUser = user("u2", "Restaurant Owner", "https://cdn/owner.png");
        Restaurant restaurant = new Restaurant();
        restaurant.setId("r1");
        restaurant.setNameJp("Pho Bac");
        restaurant.setAddress("Ha Noi");
        restaurant.setCoverImage("https://cdn/restaurant.png");
        restaurant.setOwner(otherUser);
        Conversation conversation = new Conversation();
        conversation.setId("conv-1");
        conversation.setRestaurant(restaurant);
        conversation.getParticipants().add(participant(conversation, currentUser));
        conversation.getParticipants().add(participant(conversation, otherUser));

        ConversationDto dto = mapper.toConversationDto(conversation, currentUser.getId());

        assertThat(dto.otherUserId()).isEqualTo("u2");
        assertThat(dto.otherUserName()).isEqualTo("Restaurant Owner");
        assertThat(dto.otherUserAvatar()).isEqualTo("https://cdn/owner.png");
        assertThat(dto.restaurantId()).isEqualTo("r1");
        assertThat(dto.restaurantOwnerId()).isEqualTo("u2");
        assertThat(dto.restaurantCoverImage()).isEqualTo("https://cdn/restaurant.png");
        assertThat(dto.restaurantAddress()).isEqualTo("Ha Noi");
    }

    private User user(String id, String name, String avatar) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setAvatar(avatar);
        return user;
    }

    private ConversationParticipant participant(Conversation conversation, User user) {
        ConversationParticipant participant = new ConversationParticipant();
        participant.setId(new ConversationParticipantId(conversation.getId(), user.getId()));
        participant.setConversation(conversation);
        participant.setUser(user);
        return participant;
    }
}
