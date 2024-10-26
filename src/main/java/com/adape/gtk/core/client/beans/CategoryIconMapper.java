package com.adape.gtk.core.client.beans;

import java.util.HashMap;
import java.util.Map;

public class CategoryIconMapper {
    private static final Map<String, String> iconMap = new HashMap<>();
    private static final Map<String, String> colorMap = new HashMap<>();
    
    static {
        iconMap.put("Viajes", "fa-solid fa-plane");
        colorMap.put("Viajes", "#20c0d5");
   
        iconMap.put("Deporte", "fa-solid fa-futbol");
        colorMap.put("Deporte", "#fa9e00");
        
        iconMap.put("Al aire libre", "fa-solid fa-tree");
        colorMap.put("Al aire libre", "#10d54b");
        
        iconMap.put("Hobbies & Juegos", "fa-solid fa-gamepad");
        colorMap.put("Hobbies & Juegos", "#a814b3");
        
        iconMap.put("Comida & Bebida", "fa-solid fa-utensils");
        colorMap.put("Comida & Bebida", "#555e77");
        
        iconMap.put("Fiesta", "fa-solid fa-martini-glass");
        colorMap.put("Fiesta", "#5e0ea0");
        
        iconMap.put("Arte & Cultura", "fa-solid fa-palette");
        colorMap.put("Arte & Cultura", "#FFD43b");
        
        iconMap.put("Idiomas", "fa-solid fa-comment-dots");
        colorMap.put("Idiomas", "#ff0066");
    }

    public static String getIconForCategory(String category) {
        return iconMap.get(category);
    }
    
    public static String getColorForCategory(String category) {
        return colorMap.get(category);
    }
}