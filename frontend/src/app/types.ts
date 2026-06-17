export type Role = "guest" | "diner" | "owner" | "admin";

export interface User {
  id: string;
  name: string;
  nameJp?: string;
  email: string;
  phone?: string;
  address?: string;
  role: Role;
  avatar?: string;
}

export interface MenuItem {
  id: string;
  nameVn: string;
  nameJp: string;
  price: number;
  description?: string;
  image?: string;
}

export interface Review {
  id: string;
  restaurantId: string;
  userId: string;
  userName: string;
  userAvatar?: string;
  rating: number;
  comment: string;
  date: string;
  images?: string[];
  likes: number;
  dislikes: number;
  userLiked?: boolean;
  userDisliked?: boolean;
}

export interface RestaurantSummary {
  id: string;
  ownerId: string;
  nameVn: string;
  nameJp: string;
  address: string;
  coverImage: string;
  openHours: string;
  avgPrice: number;
  tags: string[];
  rating: number;
  reviewCount: number;
  distance?: number;
  status: "draft" | "open" | "closed" | "hidden" | "deleted";
  lat: number;
  lng: number;
}

export interface Restaurant extends RestaurantSummary {
  addressJp?: string;
  phone: string;
  description: string;
  descriptionJp?: string;
  images: string[];
  menu: MenuItem[];
  priceRange: string;
}

export interface Message {
  id: string;
  senderId: string;
  receiverId: string;
  restaurantId?: string;
  content: string;
  timestamp: string;
  read: boolean;
}

export interface Conversation {
  id: string;
  participants: string[];
  lastMessage: string;
  lastTimestamp: string;
  restaurantId?: string;
  restaurantName?: string;
  restaurantOwnerId?: string;
  restaurantCoverImage?: string;
  restaurantAddress?: string;
  otherUserId?: string;
  otherUserName?: string;
  otherUserAvatar?: string;
}
