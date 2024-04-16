package com.adape.gtk.core.dao.entity;

import java.io.Serializable;

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

@Entity(name = "BlockByUser")
@Table(name = "block_by_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockByUser implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4637110369227399049L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "blocked_id")
	private User blocked;
	
	@ManyToOne
	@JoinColumn(name = "reporter_id")
	private User reporter;
	
	@ManyToOne
	@JoinColumn(name = "literal_id")
	private Literal literal;

}
