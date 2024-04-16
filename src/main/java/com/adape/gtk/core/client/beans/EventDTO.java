package com.adape.gtk.core.client.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class EventDTO implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5190799147647063398L;

	private Integer id;

	private String title;
	
	private String description;
	
	private CategoryDTO category;
	
	private Timestamp eventDate;
	
	private Timestamp creationDate;
	
	private Integer province;
	
	private String city;
	
	private String address;
	
	@Builder.Default
	private BigDecimal price = new BigDecimal(0);
	
	private Integer maxParticipants;
	
	private List<TagByEventDTO> tags;
	
	private List<CommentDTO> comments;
	
	private List<UserByEventDTO> users;
	
	private List<ReportByEventDTO> reports;
	
	private List<DeregistrationByUserDTO> deregistrations;
	
}
