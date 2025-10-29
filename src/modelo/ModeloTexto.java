package modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModeloTexto {
    private String contenido;

    public ModeloTexto(String contenido) { 
        this.contenido = contenido;
    }

    public ModeloAutomata parsearAutomata() {
        ModeloAutomata automata = new ModeloAutomata();
        String[] lineas = contenido.split("\n");
        
        Set<String> estados = new HashSet<>(); 
        List<String> simbolosList = new ArrayList<>();
        // Almacenará los destinos en el orden de aparición: [[Q2, Q1], [Q3, Q0], ...]
        List<String[]> transicionesIncompletasPorLinea = new ArrayList<>(); 
        List<String> cadenas = new ArrayList<>();

        boolean enTransiciones = false;
        boolean enCadenas = false;

        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            
            // --- Lectura de Propiedades ---
             if (linea.startsWith("Simbolos:")) {
                String valores = linea.substring(linea.indexOf(":") + 1).trim();
                simbolosList = Arrays.asList(valores.split("\\s*,\\s*"));
                automata.setSimbolos(simbolosList);
                enTransiciones = false; enCadenas = false;
            } else if (linea.startsWith("Estados:")) {
                String valores = linea.substring(linea.indexOf(":") + 1).trim();
                estados.addAll(Arrays.asList(valores.split("\\s*,\\s*")));
                enTransiciones = false; enCadenas = false;
            } else if (linea.startsWith("Estado inicial:")) {
                automata.setEstadoInicial(linea.substring(linea.indexOf(":") + 1).trim());
                enTransiciones = false; enCadenas = false;
            } else if (linea.startsWith("Estados de aceptación:")) {
                String valores = linea.substring(linea.indexOf(":") + 1).trim();
                automata.setEstadosAceptacion(Arrays.asList(valores.split("\\s*,\\s*")));
                enTransiciones = false; enCadenas = false;
            } 
            
            // --- Marcadores de sección ---
            else if (linea.startsWith("Transiciones:")) {
                enTransiciones = true;
                enCadenas = false;
            } else if (linea.startsWith("Cadenas a analizar:")) { 
                enTransiciones = false;
                enCadenas = true;
            } 
            
            // --- Lógica de Transiciones (Almacenar los pares/listas de destinos) ---
            else if (enTransiciones) {
                String[] destinos = linea.split("\\s*,\\s*");
                if (destinos.length > 0) {
                    transicionesIncompletasPorLinea.add(destinos);
                    // Añade todos los destinos a la lista de estados para la lógica de ordenamiento
                    for(String d : destinos) {
                        estados.add(d.trim());
                    }
                }
            } 
            
            // --- Lógica de Cadenas ---
            else if (enCadenas) {
                String cadenaLimpia = linea.replaceAll(",", "").trim();
                if (!cadenaLimpia.isEmpty()) {
                    cadenas.add(cadenaLimpia);
                }
            }
        }
        
        // Reconstruir la MATRIZ DE TRANSICIONES con la regla universal ---
        Map<String, Map<String, String>> matrizTransiciones = new HashMap<>();
        
        // 1. Ordenar los estados (Q0, Q1, Q2,...) para garantizar el orden de llenado
        List<String> estadosOrdenados = new ArrayList<>(estados);
        Collections.sort(estadosOrdenados);
        
        int numSimbolos = simbolosList.size();
        
        // 2. Iterar sobre los estados para asignar las transiciones
        for (int i = 0; i < estadosOrdenados.size(); i++) {
            String origen = estadosOrdenados.get(i);
            matrizTransiciones.putIfAbsent(origen, new HashMap<>());
            
            // Verificar si tenemos una línea de transiciones disponible para este estado
            if (i < transicionesIncompletasPorLinea.size()) {
                String[] destinosLinea = transicionesIncompletasPorLinea.get(i);
                
                // Asignar los destinos a los símbolos en orden
                for (int j = 0; j < numSimbolos; j++) {
                    String simbolo = simbolosList.get(j);
                    
                    if (j < destinosLinea.length) {
                        // Asigna el destino si existe en la línea
                        matrizTransiciones.get(origen).put(simbolo, destinosLinea[j].trim());
                    } else {
                        // Si la línea de entrada no tiene suficientes destinos
                        matrizTransiciones.get(origen).put(simbolo, "-");
                    }
                }
            } else {
                // Si no hay línea de transiciones para este estado, todo es no definido
                for (String simbolo : simbolosList) {
                    matrizTransiciones.get(origen).put(simbolo, "-");
                }
            }
        }
        
        automata.setEstados(estados);
        automata.setMatrizTransiciones(matrizTransiciones);
        automata.setCadenasAAnalizar(cadenas);
        
        return automata;
    }
}