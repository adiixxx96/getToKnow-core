package com.adape.gtk.core.client.beans;

public enum ProvinceEnum {

	A_CORUNA(1), ALAVA(2), ALBACETE(3), ALICANTE(4), ALMERIA(5), ASTURIAS(6),
	AVILA(7), BADAJOZ(8), BALEARES(9), BARCELONA(10), BURGOS(11), CACERES(12),
	CADIZ(13), CANTABRIA(14), CASTELLON(15), CIUDAD_REAL(16), CORDOBA(17), CUENCA(18),
	GIRONA(19), GRANADA(20), GUADALAJARA(21), GIPUZKOA(22), HUELVA(23), HUESCA(24),
	JAEN(25), LA_RIOJA(26), LAS_PALMAS(27), LEON(28), LLEIDA(29), LUGO(30),
	MADRID(31), MALAGA(32), MURCIA(33), NAVARRA(34), OURENSE(35), PALENCIA(36),
	PONTEVEDRA(37), SALAMANCA(38), SEGOVIA(39), SEVILLA(40), SORIA(41), TARRAGONA(42),
	SANTA_CRUZ_DE_TENERIFE(43), TERUEL(44), TOLEDO(45), VALENCIA(46), VALLADOLID(47),
	VIZCAYA(48), ZAMORA(49), ZARAGOZA(50), CEUTA(51), MELILLA(52);
	
	private int type;
	
	ProvinceEnum(int type){
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
	
	public static ProvinceEnum setStatus(int type){
	       switch(type){
	          case 1: return ProvinceEnum.A_CORUNA;
	          case 2: return ProvinceEnum.ALAVA;
	          case 3: return ProvinceEnum.ALBACETE;
	          case 4: return ProvinceEnum.ALICANTE;
	          case 5: return ProvinceEnum.ALMERIA;
	          case 6: return ProvinceEnum.ASTURIAS;
	          case 7: return ProvinceEnum.AVILA;
	          case 8: return ProvinceEnum.BADAJOZ;
	          case 9: return ProvinceEnum.BALEARES;
	          case 10: return ProvinceEnum.BARCELONA;
	          case 11: return ProvinceEnum.BURGOS;
	          case 12: return ProvinceEnum.CACERES;
	          case 13: return ProvinceEnum.CADIZ;
	          case 14: return ProvinceEnum.CANTABRIA;
	          case 15: return ProvinceEnum.CASTELLON;
	          case 16: return ProvinceEnum.CIUDAD_REAL;
	          case 17: return ProvinceEnum.CORDOBA;
	          case 18: return ProvinceEnum.CUENCA;
	          case 19: return ProvinceEnum.GIRONA;
	          case 20: return ProvinceEnum.GRANADA;
	          case 21: return ProvinceEnum.GUADALAJARA;
	          case 22: return ProvinceEnum.GIPUZKOA;
	          case 23: return ProvinceEnum.HUELVA;
	          case 24: return ProvinceEnum.HUESCA;
	          case 25: return ProvinceEnum.JAEN;
	          case 26: return ProvinceEnum.LA_RIOJA;
	          case 27: return ProvinceEnum.LAS_PALMAS;
	          case 28: return ProvinceEnum.LEON;
	          case 29: return ProvinceEnum.LLEIDA;
	          case 30: return ProvinceEnum.LUGO;
	          case 31: return ProvinceEnum.MADRID;
	          case 32: return ProvinceEnum.MALAGA;
	          case 33: return ProvinceEnum.MURCIA;
	          case 34: return ProvinceEnum.NAVARRA;
	          case 35: return ProvinceEnum.OURENSE;
	          case 36: return ProvinceEnum.PALENCIA;
	          case 37: return ProvinceEnum.PONTEVEDRA;
	          case 38: return ProvinceEnum.SALAMANCA;
	          case 39: return ProvinceEnum.SEGOVIA;
	          case 40: return ProvinceEnum.SEVILLA;
	          case 41: return ProvinceEnum.SORIA;
	          case 42: return ProvinceEnum.TARRAGONA;
	          case 43: return ProvinceEnum.SANTA_CRUZ_DE_TENERIFE;
	          case 44: return ProvinceEnum.TERUEL;
	          case 45: return ProvinceEnum.TOLEDO;
	          case 46: return ProvinceEnum.VALENCIA;
	          case 47: return ProvinceEnum.VALLADOLID;
	          case 48: return ProvinceEnum.VIZCAYA;
	          case 49: return ProvinceEnum.ZAMORA;
	          case 50: return ProvinceEnum.ZARAGOZA;
	          case 51: return ProvinceEnum.CEUTA;
	          case 52: return ProvinceEnum.MELILLA;
	          default:return ProvinceEnum.MADRID;
	       }
	}
	
}
