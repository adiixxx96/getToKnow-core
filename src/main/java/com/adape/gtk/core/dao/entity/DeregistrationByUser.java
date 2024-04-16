package com.adape.gtk.core.dao.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "DeregistrationByUser")
@Table(name = "deregistration_by_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeregistrationByUser implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4014530360521835204L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event event;
	
	@ManyToOne
	@JoinColumn(name = "literal_id")
	private Literal literal;
	
	@Column(name = "deregistration_date")
	private Timestamp deregistrationDate;
	
	@Column(name = "voluntary")
	private boolean voluntary;


}
