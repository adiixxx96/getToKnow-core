package com.adape.gtk.core.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="Event")
@Table(name = "event")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6444521277357771900L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "title")
	@NotNull
	private String title;
	
	@Column(name = "description")
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "category_id", insertable = false, updatable = false)
	private Category category;
	
	@Column(name = "event_date")
	@NotNull
	private Timestamp eventDate;
	
	@Column(name = "creation_date")
	@NotNull
	private Timestamp creationDate;
	
	@Column(name = "province")
	private Integer province;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "address")
	private String address;
	
	@Column(name="price")
	private BigDecimal price;
	
	@Column(name = "max_participants")
	private Integer maxParticipants;
	
	@OneToMany(mappedBy ="event")
	private List<TagByEvent> tags;
	
	@OneToMany(mappedBy ="event")
	private List<Comment> comments;
	
	@OneToMany(mappedBy ="event")
	private List<UserByEvent> users;
	
	@OneToMany(mappedBy = "event")
	private List<ReportByEvent> reports;
	
	@OneToMany(mappedBy = "event")
	private List<DeregistrationByUser> deregistrations;

}
