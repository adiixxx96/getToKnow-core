package com.adape.gtk.core.dao.entity;

import java.io.Serializable;
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

@Entity(name = "Literal")
@Table(name = "literal")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Literal implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8276093546122332908L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "literal")
	@NotNull
	private String literal;
	
	@Column(name = "type")
	@NotNull
	private Integer type;
	
	@Column(name = "active")
	@Builder.Default
	private boolean active = true;
	
	@OneToMany(mappedBy = "literal")
	private List<BlockByUser> blocks;
	
	@OneToMany(mappedBy = "literal")
	private List<DeregistrationByUser> deregistrations;
	
	@OneToMany(mappedBy = "literal")
	private List<ReportByEvent> reports;

}