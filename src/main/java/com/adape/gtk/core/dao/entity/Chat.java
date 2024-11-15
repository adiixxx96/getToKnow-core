package com.adape.gtk.core.dao.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "user1_id")
	private User user1;
	
	@ManyToOne
	@JoinColumn(name = "user2_id")
	private User user2;
	
	@Column(name = "status")
	private Boolean status;
	
	@Column(name = "creation_date")
	@NotNull
	private Timestamp creationDate;
	
	@OneToMany(mappedBy = "chat")
	private List<Message> messages;
	
}