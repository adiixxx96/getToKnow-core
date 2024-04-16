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

@Entity(name="Tag")
@Table(name = "tag")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tag implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5143595210326911390L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "tag")
	@NotNull
	private String tag;
	
	@Column(name = "active")
	@Builder.Default
	private boolean active = true;
	
	@OneToMany(mappedBy ="tag")
	private List<TagByEvent> events;

}
