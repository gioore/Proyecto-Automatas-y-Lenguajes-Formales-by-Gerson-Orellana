package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections; // Necesario para el ordenamiento

public class ModeloAutomata {
    private List<String> simbolos;
    private Set<String> estados; 
    private String estadoInicial;
    private Set<String> estadosAceptacion;
    // <EstadoOrigen, <Simbolo, EstadoDestino>>
    private Map<String, Map<String, String>> matrizTransiciones; 
    private List<String> cadenasAAnalizar;

    public ModeloAutomata() {
        this.matrizTransiciones = new HashMap<>();
        this.estadosAceptacion = new HashSet<>();
    }

    // --- Getters y Setters (Omitidos para brevedad) ---

    public List<String> getSimbolos() {
        return simbolos;
    }

    public void setSimbolos(List<String> simbolos) {
        this.simbolos = simbolos;
    }

    public Set<String> getEstados() {
        return estados;
    }

    public void setEstados(Set<String> estados) {
        this.estados = estados;
    }
    
    public void setMatrizTransiciones(Map<String, Map<String, String>> matrizTransiciones) {
        this.matrizTransiciones = matrizTransiciones;
    }
    
    public Map<String, Map<String, String>> getMatrizTransiciones() {
        return matrizTransiciones;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public Set<String> getEstadosAceptacion() {
        return estadosAceptacion;
    }

    public void setEstadosAceptacion(List<String> estadosAceptacion) {
        this.estadosAceptacion = new HashSet<>(estadosAceptacion);
    }
    
    public List<String> getCadenasAAnalizar() {
        return cadenasAAnalizar;
    }

    public void setCadenasAAnalizar(List<String> cadenasAAnalizar) {
        this.cadenasAAnalizar = cadenasAAnalizar;
    }
    
    // --- Métodos de Lógica ---
    
    public String obtenerSiguienteEstado(String estadoActual, String simbolo) {
        Map<String, String> transicionesDeOrigen = matrizTransiciones.get(estadoActual);
        if (transicionesDeOrigen != null) {
            String siguiente = transicionesDeOrigen.get(simbolo);
            if (siguiente == null || siguiente.equals("-")) {
                return null;
            }
            return siguiente;
        }
        return null;
    }

    public boolean esEstadoDeAceptacion(String estado) {
        return estadosAceptacion.contains(estado);
    }
    
    /**
     * Genera el contenido DOT para Graphviz, coloreando la transición activa.
     * @param estadoOrigen Estado del que sale la transición (se colorea naranja).
     * @param estadoDestino Estado al que llega la transición (se colorea verde).
     * @param transicionActiva Clave única para la transición (Ej: "q1a") para colorear la flecha.
     * Debe ser null para el inicio o fin sin transición.
     */
    public String generarDot(String estadoOrigen, String estadoDestino, String transicionActiva) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph G {\n");
        dot.append("rankdir=LR;\n"); 
        dot.append("size=\"15,5\";\n"); 
        dot.append("node [shape=circle];\n"); 

        // 1. Dibuja el estado inicial invisible para la flecha de inicio
        dot.append("  inicio [shape=none, label=\"\"];\n");

        // 2. Define y dibuja los ESTADOS, aplicando colores de simulación
        Set<String> todosLosEstados = new HashSet<>(getEstados());
        if(estadoOrigen != null) todosLosEstados.add(estadoOrigen);
        if(estadoDestino != null) todosLosEstados.add(estadoDestino);

        for (String estado : todosLosEstados) { 
            if (estado.contains("Rechazo")) continue; // Ignora el estado virtual de rechazo
            
            String shape = getEstadosAceptacion().contains(estado) ? "doublecircle" : "circle";
            String fillcolor = "white"; // Color por defecto

            if (estado.equals(estadoOrigen)) {
                fillcolor = "orange"; // Estado de donde sale (Paso anterior/actual)
            }
            if (estado.equals(estadoDestino)) {
                fillcolor = "green"; // Estado al que llega (Paso actual/siguiente)
            }
            // Si Origen y Destino son el mismo (loop), se prioriza el verde/destino
            if (estado.equals(estadoOrigen) && estado.equals(estadoDestino)) {
                 fillcolor = "green";
            }
            
            dot.append("  ").append(estado).append(" [shape=").append(shape);
            dot.append(", style=filled, fillcolor=\"").append(fillcolor).append("\"");
            dot.append("];\n");
        }

        // 3. Dibuja la TRANSICIÓN del nodo inicial
        if (getEstadoInicial() != null) {
            dot.append("  inicio -> ").append(getEstadoInicial()).append(";\n");
        }
        
        // 4. Dibuja TODAS las transiciones (flechas)
        List<String> estadosOrdenados = new ArrayList<>(getEstados());
        Collections.sort(estadosOrdenados);

        for (String estado : estadosOrdenados) {
            Map<String, String> transiciones = matrizTransiciones.getOrDefault(estado, Collections.emptyMap());
            for (Map.Entry<String, String> entry : transiciones.entrySet()) {
                String simbolo = entry.getKey();
                String destino = entry.getValue();
                
                if (destino != null && !destino.equals("-") && !destino.isEmpty()) {
                    String claveTransicion = estado + simbolo;
                    
                    dot.append("  ").append(estado).append(" -> ").append(destino);
                    dot.append(" [label=\"").append(simbolo).append("\"");

                    // Colorea la flecha si es la transición activa
                    if (claveTransicion.equals(transicionActiva)) {
                        dot.append(", color=\"#00BFFF\", penwidth=2"); // Azul claro/celeste
                    }
                    
                    dot.append("];\n");
                }
            }
        }
        
        // Cierre del grafo
        dot.append("}\n");
        return dot.toString();
    }
}
// ModeloTexto.java no requiere cambios ya que solo parsea el archivo.
