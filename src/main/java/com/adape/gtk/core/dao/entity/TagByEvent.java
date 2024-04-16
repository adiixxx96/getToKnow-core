package com.adape.gtk.core.dao.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="TagByEvent")
@Table(name = "tag_by_event")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class TagByEvent implements Serializable{

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
	public static class TagByEventId implements Serializable{
		
		/**
		 * 
		 */
	    private static final long serialVersionUID = -596100882272259L;
	
		@Column(name = "tag_id")
		private int tagId;
		@Column(name = "event_id")
		private int eventId;	
	}

	@EmbeddedId
	private TagByEventId id;
	
	@ManyToOne
	@JoinColumn(name = "tag_id", insertable = false, updatable = false)
	private Tag tag;
	
	@ManyToOne
	@JoinColumn(name = "event_id", insertable = false, updatable = false)
	private Event event;

	public TagByEvent(Tag tag, Event event) {
		// Create primary key
		this.id = new TagByEventId(tag.getId(), event.getId());
		
		// Initialize attributes
		this.tag = tag;
		this.event = event;
		
		// Update relationships to assure referential integrity
		tag.getEvents().add(this);
		event.getTags().add(this);
	}
	
}
