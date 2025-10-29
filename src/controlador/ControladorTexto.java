package controlador;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import modelo.ModeloAutomata;
import modelo.ModeloTexto; 
import vistas.Vista;

public class ControladorTexto implements java.awt.event.ActionListener {
    
    private Vista vista;
    private ControladorSimulacion ctrlSimulacion;
    // 🟢 NUEVA VARIABLE: Almacena el autómata procesado
    private ModeloAutomata automataActual; 
    
    private static final String RECURSOS_DIR = "/recursos/";
    private static final String EJEMPLO1_FILE = "ejemplo1_afd.txt";
    private static final String EJEMPLO2_FILE = "ejemplo2_afn.txt";
    private static final String EJEMPLO3_FILE = "ejemplo3_cadenas.txt";
    
    // **¡IMPORTANTE!** Cambia esto a la ruta de tu dot.exe
    private static final String GRAPHVIZ_DOT_PATH = "C:\\Program Files\\Graphviz\\bin\\dot.exe"; 

    public ControladorTexto(Vista vista) {
        this.vista = vista;
        
        this.ctrlSimulacion = new ControladorSimulacion(vista, this); 
        
        this.vista.getAbrir().addActionListener(this);
        this.vista.getNuevo().addActionListener(this);
        this.vista.getBtnProcesarEntrada().addActionListener(this); 
        this.vista.getGuardar().addActionListener(this);
        this.vista.getEjemplo1().addActionListener(this); 
        this.vista.getEjemplo2().addActionListener(this);
        this.vista.getEjemplo3().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == vista.getAbrir()) {
            abrirArchivo();
        } else if (source == vista.getNuevo()) {
            nuevoArchivo();
        } 
        else if (source == vista.getGuardar()) {
            guardarArchivo();
        } 
        else if (source == vista.getBtnProcesarEntrada()) {
            procesarEntradaDesdeTexto();
        } else if (source == vista.getEjemplo1()) {
            cargarEjemplo(EJEMPLO1_FILE);
        } else if (source == vista.getEjemplo2()) {
            cargarEjemplo(EJEMPLO2_FILE);
        } else if (source == vista.getEjemplo3()) {
            cargarEjemplo(EJEMPLO3_FILE);
        }
    }

    // --- Métodos de Control y Parsing ---

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(vista);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String contenido = new String(Files.readAllBytes(selectedFile.toPath()));
                vista.getTxt().setText(contenido);
                vista.getTxt().setEditable(true); 
                procesarContenido(contenido);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(vista, "Error al leer el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void procesarEntradaDesdeTexto() {
        String contenido = vista.getTxt().getText();
        if (contenido.trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El área de texto está vacía.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        procesarContenido(contenido);
    }
    
    private void procesarContenido(String contenido) {
        try {
            ModeloTexto modeloTexto = new ModeloTexto(contenido);
            ModeloAutomata automata = modeloTexto.parsearAutomata();
            
            // GUARDAR EL AUTOMATA EN LA INSTANCIA
            this.automataActual = automata;
            
            mostrarAutomata(automata);
            
            ctrlSimulacion.setAutomata(automata);
            
            // AJUSTE: Muestra el autómata con solo el estado inicial pintado
            // LLAMADA SIN EL OBJETO AUTOMATA, USA LA INSTANCIA
            generarImagenAutomata(automata.getEstadoInicial(), null, null); 
            
            vista.llenarTablaCadenas(automata.getCadenasAAnalizar());
            
            vista.getTxt().setEditable(false); 
            
            JOptionPane.showMessageDialog(vista, "Archivo de entrada procesado correctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            Logger.getLogger(ControladorTexto.class.getName()).log(Level.SEVERE, "Error al parsear el autómata", ex);
            JOptionPane.showMessageDialog(vista, "Error al procesar el archivo. Verifique el formato.", "Error", JOptionPane.ERROR_MESSAGE);
            vista.getTxt().setEditable(true); 
        }
    }

    private void mostrarAutomata(ModeloAutomata automata) {
        vista.setEstadoInicial(automata.getEstadoInicial());
        vista.llenarTablaAceptacion(new java.util.ArrayList<>(automata.getEstadosAceptacion()));
        vista.llenarTablaSimbolos(automata.getSimbolos());
        
        vista.llenarTablaTransiciones(
            automata.getEstados(), 
            automata.getSimbolos(), 
            automata.getMatrizTransiciones()
        );
    }
    
    private void nuevoArchivo() { 
        vista.getTxt().setText(""); 
        vista.setEstadoInicial(""); 
        vista.limpiarTablas(); 
        vista.getLblGrafo().setIcon(null); 
        ctrlSimulacion.reiniciarSimulacion(-1); 
        vista.getTxt().setEditable(true); 
        this.automataActual = null; // 🟢 Limpiar el autómata actual
    }
    
    public void guardarArchivo() {
        // 1. Obtener el contenido del JTextArea
        String contenido = vista.getTxt().getText(); 

        // 2. Crear el FileChooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Definición del Autómata");
        
        // 3. Establecer un filtro para archivos de texto (.txt)
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        // 4. Mostrar el diálogo "Guardar"
        int userSelection = fileChooser.showSaveDialog(vista);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // 5. Asegurar la extensión .txt si el usuario no la puso
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                fileToSave = new File(filePath + ".txt");
            }

            try (PrintWriter writer = new PrintWriter(fileToSave, StandardCharsets.UTF_8)) {
                // 6. Escribir el contenido en el archivo
                writer.print(contenido);
                JOptionPane.showMessageDialog(vista, 
                                              "Archivo guardado exitosamente en: " + fileToSave.getName(), 
                                              "Guardar", 
                                              JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(vista, 
                                              "Error al guardar el archivo: " + ex.getMessage(), 
                                              "Error de Guardado", 
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cargarEjemplo(String fileName) {
        String resourcePath = RECURSOS_DIR + fileName; 
        
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            
            if (is == null) {
                JOptionPane.showMessageDialog(vista, 
                    "El recurso de ejemplo NO se encontró dentro del proyecto: " + resourcePath + 
                    "\nVerifique que la carpeta 'recursos' esté DENTRO de la carpeta 'src'.", 
                    "Error de Recurso Crítico", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String contenido;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                contenido = sb.toString();
            }

            vista.getTxt().setText(contenido);
            
            procesarContenido(contenido);

        } catch (Exception ex) {
            Logger.getLogger(ControladorTexto.class.getName()).log(Level.SEVERE, "Error al cargar/procesar ejemplo", ex);
            JOptionPane.showMessageDialog(vista, 
                "Error al leer/procesar el ejemplo: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            vista.getTxt().setEditable(true); 
        }
    }
    
    /**
     * Llama al modelo para obtener el DOT y ejecuta Graphviz, coloreando 
     * el estado Origen (Naranja), el estado Destino (Verde) y la Transición (Azul Claro).
     *  Ahora usa la variable de instancia 'automataActual' en lugar de un parámetro.
     */
    public void generarImagenAutomata(String estadoOrigen, String estadoDestino, String transicionActiva) {
        if (automataActual == null) return; // Si no hay autómata, no hacer nada
        
        try {
            // **CRÍTICO:** Llama al método del ModeloAutomata con los tres estados/transición para colorear.
            String dotContent = automataActual.generarDot(estadoOrigen, estadoDestino, transicionActiva); 

            File dotFile = File.createTempFile("automata", ".dot");
            try (FileWriter writer = new FileWriter(dotFile)) {
                writer.write(dotContent);
            }

            File imageFile = File.createTempFile("automata", ".png");

            ProcessBuilder pb = new ProcessBuilder(
                GRAPHVIZ_DOT_PATH, 
                "-Tpng", 
                "-o", imageFile.getAbsolutePath(), 
                dotFile.getAbsolutePath()
            );
            Process process = pb.start();
            
            int exitCode = process.waitFor(); 
            if (exitCode != 0) {
                String error = "";
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    error = sb.toString();
                } 
                
                JOptionPane.showMessageDialog(vista, 
                    "Error al generar grafo con Graphviz. Código: " + exitCode + "\n" + error, 
                    "Error de Graphviz", JOptionPane.ERROR_MESSAGE);
            } else {
                ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());
                vista.getLblGrafo().setIcon(imageIcon);
            }

            dotFile.deleteOnExit();
            imageFile.deleteOnExit();

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ControladorTexto.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(vista, 
                "Error de sistema al generar el grafo. Revise la ruta de Graphviz: " + GRAPHVIZ_DOT_PATH, 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}