package com.adape.gtk.core.client.beans;

public enum LiteralTypeEnum {

	BLOCK(1), DEREGISTRATION(2), REPORT(3);
	
	private int type;
	
	LiteralTypeEnum(int type){
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
	
	public static LiteralTypeEnum setStatus(int type){
	       switch(type){
	          case 1: return LiteralTypeEnum.BLOCK;
	          case 2: return LiteralTypeEnum.DEREGISTRATION;
	          case 3: return LiteralTypeEnum.REPORT;
	          default:return LiteralTypeEnum.BLOCK;
	       }
	}
	
}
