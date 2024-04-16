package com.adape.gtk.core.utils;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomMapper {
	private static ModelMapper modelMapper = new ModelMapper();
	
	public static <D> D map(Object sourceEntity, Class<D> destinationDTO){
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		D x = null;
		try {
			x =  modelMapper.map(sourceEntity, destinationDTO);
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e.toString());
		}
		return x;
	}
}
