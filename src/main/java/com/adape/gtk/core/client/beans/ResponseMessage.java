package com.adape.gtk.core.client.beans;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMessage implements Serializable{

	private static final long serialVersionUID = -1522386375934397966L;

	private int status;
	
	private Object message;
	
	public void setStatus(HttpStatus status) {
		this.status = status.value();
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isOK() {
		return HttpStatus.resolve(status).is2xxSuccessful() && !isNoContent();
	}

	public boolean isNoContent() {
		return HttpStatus.resolve(status).equals(HttpStatus.NO_CONTENT);
	}

	public boolean isError() {
		return HttpStatus.resolve(status).isError();
	}
}
