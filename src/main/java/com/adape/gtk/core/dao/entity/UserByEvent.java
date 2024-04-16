package com.adape.gtk.core.dao.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="UserByEvent")
@Table(name = "user_by_event")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserByEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5519972451871293247L;

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	@Setter
	@EqualsAndHashCode
	@Embeddable
	public static class UserByEventId implements Serializable{
		
	
		/**
		 * 
		 */
		private static final long serialVersionUID = -937652465239730114L;
		
		@Column(name = "user_id")
		private int userId;
		@Column(name = "event_id")
		private int eventId;	
	}

	@EmbeddedId
	private UserByEventId id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "event_id", insertable = false, updatable = false)
	private Event event;
	
	@Column(name = "owner")
	private boolean owner;
	
	@Column(name = "participant")
	private boolean participant;
	
	@Column(name = "registration_date")
	@NotNull
	private Timestamp registrationDate;
	
	public UserByEvent(User user, Event event) {
		// Create primary key
		this.id = new UserByEventId(user.getId(), event.getId());
		
		// Initialize attributes
		this.user = user;
		this.event = event;
		
		// Update relationships to assure referential integrity
		user.getEvents().add(this);
		event.getUsers().add(this);
	}
	
}
