package com.adape.gtk.core.dao.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Chat")
@Table(name = "chat")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Chat implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6163284289752870530L;

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	@Setter
	@EqualsAndHashCode
	@Embeddable
	public static class ChatId implements Serializable{
		

		/**
		 * 
		 */
		private static final long serialVersionUID = -156567518984046966L;
		
		@Column(name = "user1_id")
		private int user1Id;
		@Column(name = "user2_id")
		private int user2Id;	
	}

	@EmbeddedId
	private ChatId id;
	
	@ManyToOne
	@JoinColumn(name = "user1_id", insertable = false, updatable = false)
	private User user1;
	
	@ManyToOne
	@JoinColumn(name = "user2_id", insertable = false, updatable = false)
	private User user2;
	
	@Column(name = "status")
	private boolean status;
	
	@OneToMany(mappedBy = "chat")
	private List<Message> messages;

	public Chat(User user1, User user2) {
		// Create primary key
		this.id = new ChatId(user1.getId(), user2.getId());
		
		// Initialize attributes
		this.user1 = user1;
		this.user2 = user2;
	}
	
}