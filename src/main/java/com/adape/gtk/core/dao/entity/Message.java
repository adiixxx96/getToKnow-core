package com.adape.gtk.core.dao.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Message")
@Table(name = "message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message implements Serializable{



	/**
	 * 
	 */
	private static final long serialVersionUID = -1604676790800468902L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "message")
	@NotNull
	private String message;
	
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "user1_id", referencedColumnName = "user1_id"),
		@JoinColumn(name = "user2_id", referencedColumnName = "user2_id")
	})
	private Chat chat;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "read")
	private boolean read;
	
	@Column(name = "creation_date")
	private Timestamp creationDate;
	
}
