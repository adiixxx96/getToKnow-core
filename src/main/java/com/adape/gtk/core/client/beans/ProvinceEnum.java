package com.adape.gtk.core.client.beans;

public enum ProvinceEnum {

	A_CORUNA(1, "A Coruña"), ALAVA(2, "Álava"), ALBACETE(3, "Albacete"), ALICANTE(4, "Alicante"), ALMERIA(5, "Almería"), ASTURIAS(6, "Asturias"),
	AVILA(7, "Ávila"), BADAJOZ(8, "Badajoz"), BALEARES(9, "Baleares"), BARCELONA(10, "Barcelona"), BURGOS(11, "Burgos"), CACERES(12, "Cáceres"),
	CADIZ(13, "Cádiz"), CANTABRIA(14, "Cantabria"), CASTELLON(15, "Castellón"), CIUDAD_REAL(16, "Ciudad Real"), CORDOBA(17, "Córdoba"), CUENCA(18, "Cuenca"),
	GIRONA(19, "Girona"), GRANADA(20, "Granada"), GUADALAJARA(21, "Guadalajara"), GIPUZKOA(22, "Gipuzkoa"), HUELVA(23, "Huelva"), HUESCA(24, "Huesca"),
	JAEN(25, "Jaén"), LA_RIOJA(26, "La Rioja"), LAS_PALMAS(27, "Las Palmas"), LEON(28, "León"), LLEIDA(29, "Lleida"), LUGO(30, "Lugo"),
	MADRID(31, "Madrid"), MALAGA(32, "Málaga"), MURCIA(33, "Murcia"), NAVARRA(34, "Navarra"), OURENSE(35, "Ourense"), PALENCIA(36, "Palencia"),
	PONTEVEDRA(37, "Pontevedra"), SALAMANCA(38, "Salamanca"), SEGOVIA(39, "Segovia"), SEVILLA(40, "Sevilla"), SORIA(41, "Soria"), TARRAGONA(42, "Tarragona"),
	SANTA_CRUZ_DE_TENERIFE(43, "Santa Cruz de Terenife"), TERUEL(44, "Teruel"), TOLEDO(45, "Toledo"), VALENCIA(46, "Valencia"), VALLADOLID(47, "Valladolid"),
	VIZCAYA(48, "Vizcaya"), ZAMORA(49, "Zamora"), ZARAGOZA(50, "Zaragoza"), CEUTA(51, "Ceuta"), MELILLA(52, "Melilla");
	
	private int id;
	private String name;
	
	ProvinceEnum(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return name;
	}
	
	public static ProvinceEnum setProvince(int id){
	       switch(id){
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
