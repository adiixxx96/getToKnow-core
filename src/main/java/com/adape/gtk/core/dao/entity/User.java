package com.adape.gtk.core.dao.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name= "User")
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7534398052010065617L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "fullname")
	private String fullname;
	
	@Column(name = "email")
	@NotNull
	private String email;
	
	@Column(name = "password")
	@NotNull
	private String password;
	
	@Column(name = "role")
	private boolean role;
	
	@Column(name = "profile_image")
	private String profileImage;
	
	@Column(name = "creation_date")
	@NotNull
	private Timestamp creationDate;
	
	@Column(name = "birth_date")
	private Timestamp birthDate;
	
	@Column(name = "active")
	private boolean active;
	
	@OneToMany(mappedBy ="user")
	private List<Comment> comments;
	
	@OneToMany(mappedBy ="user")
	private List<UserByEvent> events;
	
	@OneToMany(mappedBy ="user")
	private List<Notification> notifications;
	
	@OneToMany(mappedBy = "blocked")
	private List<BlockByUser> blocks;
	
	@OneToMany(mappedBy = "user")
	private List<DeregistrationByUser> deregistrations;
	
	@OneToMany(mappedBy = "user1")
	private List<Chat> chats;
	
	@OneToMany(mappedBy = "user2")
	private List<Chat> chatsAsUser2;
	
	@OneToMany(mappedBy = "user")
	private List<Message> messages;
	
}
