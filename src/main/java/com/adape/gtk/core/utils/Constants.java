package com.adape.gtk.core.utils;

public class Constants {
	
	protected Constants() {}
	
	// ENTITY
	public static final String ENTITY_CREATE_SUCCESSFULLY = "Entity %s created successfully: %s";
	public static final String ENTITY_CREATE_ERROR = "Error creating entity %s. Error: %s";
	public static final String ENTITY_EDIT_SUCCESSFULLY = "Entity %s updated successfully: %s";
	public static final String ENTITY_EDIT_ERROR = "Error on update entity %s. Error: %s";
	public static final String ENTITY_GET_SUCCESSFULLY = "Entity %s retrieved successfully: %s";
	public static final String ENTITY_GET_NOT_FOUND = "Entity %s not found: %s";
	public static final String ENTITY_GET_ERROR = "Error getting entity %s. Error: %s";
	public static final String ENTITY_DELETE_SUCCESSFULLY = "Entity %s deleted successfully: %s";
	public static final String ENTITY_DELETE_ERROR = "Error on delete entity %s. Error: %s";
	public static final String ENTITY_EXIST = "Entity %s exist";
	public static final String ENTITY_NOT_EXIST = "Entity %s does not exist";
	public static final String INVALID_OPERATOR = "Operator %s not valid for the type %s";
	public static final String ENTITY_REQUIRED = "%s must not be null";
	public static final String RESPONSE_OK = "Response %s";
	public static final String UNEXPECTED_ERROR = "An unexpected error has ocurred: %s";
	public static final String BAD_REQUEST = "Incorrect parameters given: %s";
	public static final String CALLING = "Calling {}";
	public static final String ENTITY_BREAK = "-Break Ent-";
	
	// HEADERS
	public static final String HEADER_ENTITY_ID = "X-ENTITY-ID";
	public static final String HEADER_USER_ID = "X-USER-ID";
	public static final String HEADER_ENTITY_TYPE = "X-ENTITY-TYPE";
	public static final String HEADER_ATTACHMENT_COUNT = "X-HEADER-ATTACHMENT-COUNT";
	public static final String HEADER_ENTITY_ACTION = "X-ENTITY-ACTION";
	public static final String HEADER_OLD_ENTITY = "X-OLD-ENTITY";
	public static final String HEADER_NEW_ENTITY = "X-NEW-ENTITY";
	public static final String HEADER_CHANGES_ENTITY = "X-CHANGES-ENTITY";
	public static final String WITHOUT_CHANGES = "Without changes";

}
