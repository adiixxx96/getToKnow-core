package com.adape.gtk.core.client.beans;

import java.io.Serializable;
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
public class UserDTO implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4523443098622917890L;

	private Integer id;

	private String fullname;
	
	private String email;
	
	private String password;
	
	@Builder.Default
	private Boolean role = false;
	
	private String profileImage;
	
	private Timestamp creationDate;
	
	private Timestamp birthDate;
	
	@Builder.Default
	private Boolean active = true;
	
	private List<CommentDTO> comments;
	
	private List<UserByEventDTO> events;
	
	private List<NotificationDTO> notifications;
	
	private List<BlockByUserDTO> blocks;
	
	private List<BlockByUserDTO> blockReports;
	
	private List<DeregistrationByUserDTO> deregistrations;
	
	private List<ReportByEventDTO> reports;
	
	private List<ChatDTO> chats;
	
	private List<ChatDTO> chatsAsUser2;
	
	private List<MessageDTO> messages;
	
}
