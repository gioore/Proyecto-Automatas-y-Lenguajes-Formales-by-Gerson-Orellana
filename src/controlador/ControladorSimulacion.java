package controlador;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import modelo.ModeloAutomata;
import vistas.Vista;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorSimulacion extends MouseAdapter implements ActionListener { 

    private Vista vista;
    private ModeloAutomata automata;
    private ControladorTexto ctrlTexto; 

    private String estadoActual;
    private String cadenaActual;
    private int indiceActual;
    private int filaSeleccionada = -1;
    

    public ControladorSimulacion(Vista vista, ControladorTexto ctrlTexto) {
        this.vista = vista;
        this.ctrlTexto = ctrlTexto;
        
        
        this.vista.getBtnSimularTodo().addActionListener(this);
        this.vista.getBtnPasoSiguiente().addActionListener(this);
        this.vista.getBtnReiniciarSim().addActionListener(this);
        
        this.vista.getJTableCadenas().addMouseListener(this); 
    }

    public void setAutomata(ModeloAutomata automata) {
        this.automata = automata;
        // Asume que si se carga un autómata, se selecciona la primera cadena (índice 0)
        // Si no hay cadenas, se maneja dentro de reiniciarSimulacion
        reiniciarSimulacion(0); 
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == vista.getBtnPasoSiguiente()) {
            simularPasoSiguiente();
        } else if (e.getSource() == vista.getBtnReiniciarSim()) {
            // Si el botón Reiniciar es presionado, reinicia a la cadena seleccionada actualmente
            reiniciarSimulacion(filaSeleccionada);
        } else if (e.getSource() == vista.getBtnSimularTodo()) {
            simularTodasCadenas();
        }
    }
    
    // --- MANEJO DEL CLIC EN LA TABLA ---
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == vista.getJTableCadenas()) {
            int fila = vista.getJTableCadenas().getSelectedRow();
            
            if (fila != -1) {
                // Reinicia la simulación con la cadena seleccionada
                reiniciarSimulacion(fila); 
            }
        }
    }
    
    public void reiniciarSimulacion(int fila) {
        
        // 1. Limpiar resaltado de la tabla de transiciones
        vista.limpiarResaltadoTransiciones();
        
        if (automata == null || fila < 0 || automata.getCadenasAAnalizar() == null || fila >= automata.getCadenasAAnalizar().size()) {
            this.filaSeleccionada = -1;
            this.estadoActual = null;
            this.cadenaActual = null;
            this.indiceActual = 0;
            if (automata != null && automata.getEstadoInicial() != null) {
                // Redibujar el grafo al estado inicial (si existe)
                ctrlTexto.generarImagenAutomata(automata.getEstadoInicial(), null, null); 
            }
            
            vista.getLblEstadoActual().setText("");
            vista.getLblCadenaActual().setText("");
            vista.getBtnPasoSiguiente().setEnabled(false);
            return;
        }

        this.filaSeleccionada = fila;
        this.cadenaActual = automata.getCadenasAAnalizar().get(fila);
        this.estadoActual = automata.getEstadoInicial();
        this.indiceActual = 0;

        DefaultTableModel model = (DefaultTableModel) vista.getJTableCadenas().getModel(); 
        if (fila < model.getRowCount()) {
             model.setValueAt("Pendiente", fila, 1); 
             model.setValueAt("", fila, 3); 
        }

        actualizarEtiquetasSimulacion(false);
        // 2. Dibujar el grafo resaltando el estado inicial (Origen: estadoActual, Destino: null, Transición: null)
        ctrlTexto.generarImagenAutomata(estadoActual, null, null);
        vista.getBtnPasoSiguiente().setEnabled(true);
    }

    private void simularPasoSiguiente() {
        if (filaSeleccionada == -1 || cadenaActual == null || estadoActual == null || estadoActual.contains("Rechazo")) {
            JOptionPane.showMessageDialog(vista, "Seleccione una cadena en la tabla para iniciar la simulación o la simulación ha terminado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (indiceActual >= cadenaActual.length()) {
            // FIN DE LA CADENA, determinar resultado final
            actualizarResultadoFinal();
            // Pinta el estado final de la simulación (Origen = final, Destino y Transición = null)
            ctrlTexto.generarImagenAutomata(estadoActual, null, null); 
            // 1. Limpiar el resaltado de la tabla
            vista.limpiarResaltadoTransiciones();
            return;
        }

        // --- Simulación de un paso ---
        String estadoOrigen = estadoActual; 
        String simbolo = String.valueOf(cadenaActual.charAt(indiceActual));
        
        String proximoEstado = automata.obtenerSiguienteEstado(estadoOrigen, simbolo);

        if (proximoEstado != null) {
            // Transición exitosa
            estadoActual = proximoEstado; // Estado 'destino'
            indiceActual++;
            
            // 2. Resaltar la transición en la tabla
            vista.resaltarTransicionActiva(estadoOrigen, simbolo);
            
            // 3. Dibujar el grafo con animación
            ctrlTexto.generarImagenAutomata(
                estadoOrigen,          
                estadoActual,          
                estadoOrigen + simbolo // Clave para pintar la flecha
            ); 
            
            // 4. Actualizar etiquetas para el siguiente símbolo
            actualizarEtiquetasSimulacion(false);
            
        } else {
            // Transición no definida (Error/Rechazo)
            estadoActual = "Rechazo (Transición no definida)";
            indiceActual = cadenaActual.length(); // Termina la simulación
            
            // 1. Limpiar el resaltado de la tabla
            vista.limpiarResaltadoTransiciones();
            
            // 2. Finalizar visualización y etiquetas
            actualizarEtiquetasSimulacion(true);
            actualizarResultadoFinal();
            
            // Pinta el estado de rechazo (Origen = estado de rechazo, Destino y Transición = null)
            ctrlTexto.generarImagenAutomata(estadoActual, null, null); 
        }
    }
    
    private void actualizarEtiquetasSimulacion(boolean terminado) {
        vista.getLblEstadoActual().setText(estadoActual);
        
        String cadenaFormateada = "<html>";
        for (int i = 0; i < cadenaActual.length(); i++) {
            String simbolo = String.valueOf(cadenaActual.charAt(i));
            
            if (i == indiceActual && !terminado) {
                // Resalta el símbolo actual de la cadena en rojo
                cadenaFormateada += "<b style=\"color:red;\">" + simbolo + "</b>";
            } else if (i < indiceActual || terminado) {
                // Símbolos ya consumidos
                cadenaFormateada += simbolo;
            } else {
                // Símbolos por consumir
                cadenaFormateada += "<span style=\"color:gray;\">" + simbolo + "</span>";
            }
        }
        cadenaFormateada += "</html>";
        vista.getLblCadenaActual().setText(cadenaFormateada);
        
        // Deshabilita el botón si la simulación ha terminado
        vista.getBtnPasoSiguiente().setEnabled(!terminado && !estadoActual.contains("Rechazo"));
    }
    
    private void actualizarResultadoFinal() {
        boolean esAceptado = automata.esEstadoDeAceptacion(estadoActual);
        String resultado = esAceptado ? "Aceptada" : "Rechazada";
        
        DefaultTableModel model = (DefaultTableModel) vista.getJTableCadenas().getModel();
        
        if (filaSeleccionada != -1 && filaSeleccionada < model.getRowCount()) {
            model.setValueAt(resultado, filaSeleccionada, 1); 
            model.setValueAt(estadoActual.contains("Rechazo") ? "Error" : estadoActual, filaSeleccionada, 3);
        }
    }

    private void simularTodasCadenas() {
        if (automata == null) {
            JOptionPane.showMessageDialog(vista, "Cargue un autómata primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) vista.getJTableCadenas().getModel(); 
        
        for (int i = 0; i < automata.getCadenasAAnalizar().size(); i++) {
            String cadena = automata.getCadenasAAnalizar().get(i);
            
            String estadoFinal = simularCadenaCompleta(cadena);
            boolean esAceptado = automata.esEstadoDeAceptacion(estadoFinal);
            
            String resultado = esAceptado ? "Aceptada" : "Rechazada";
            
            if (i < model.getRowCount()) {
                model.setValueAt(resultado, i, 1); 
                model.setValueAt(estadoFinal.contains("Rechazo") ? "Error" : estadoFinal, i, 3); 
            }
        }
        
        // Limpia el estado de simulación (cadena seleccionada y resaltado de tabla)
        reiniciarSimulacion(-1); 
        vista.limpiarResaltadoTransiciones();
        
        JOptionPane.showMessageDialog(vista, "Simulación de todas las cadenas terminada.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String simularCadenaCompleta(String cadena) {
        String estado = automata.getEstadoInicial();
        
        for (int i = 0; i < cadena.length(); i++) {
            String simbolo = String.valueOf(cadena.charAt(i));
            String proximoEstado = automata.obtenerSiguienteEstado(estado, simbolo);

            if (proximoEstado == null) {
                return "Rechazo (Transición no definida)"; 
            }
            estado = proximoEstado;
        }

        return estado;
    }
}