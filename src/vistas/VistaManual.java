package vistas;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.nio.charset.StandardCharsets;

public class VistaManual extends JDialog {

    private JTextArea manualText;

    /**
     * @param owner La ventana principal (JFrame o JDialog) para centrar el diálogo.
     * @param title El título del manual (e.g., "Manual de Usuario").
     * @param resourcePath La ruta interna del recurso (e.g., "/recursos/manuales/usuario.txt").
     */
    public VistaManual(java.awt.Frame owner, String title, String resourcePath) {
        // Modal: Bloquea la ventana principal hasta que se cierre el diálogo.
        super(owner, title, true); 
        
        setSize(700, 500); // Tamaño profesional
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        manualText = new JTextArea();
        manualText.setEditable(false);
        manualText.setLineWrap(true);
        manualText.setWrapStyleWord(true);
        
        // Usa una fuente monoespaciada para mantener el formato de alineación
        manualText.setFont(new Font("Monospaced", Font.PLAIN, 14)); 

        JScrollPane scrollPane = new JScrollPane(manualText);
        add(scrollPane, BorderLayout.CENTER);

        // Llama al método para cargar el contenido del archivo de recurso
        cargarContenido(resourcePath);
    }
    
    // Sobrecarga del constructor para aceptar la propia Vista (si es un JFrame)
    public VistaManual(javax.swing.JFrame owner, String title, String resourcePath) {
        this((java.awt.Frame) owner, title, resourcePath);
    }
    
    // Sobrecarga del constructor para aceptar la Vista Selectora (JDialog)
    

private void cargarContenido(String resourcePath) {
    InputStream is = null;
    String finalPath = resourcePath;
    
    try {
        // Intento 1: Búsqueda Absoluta desde la raíz (Ej: /recursos/manuales/usuario.txt)
        is = getClass().getResourceAsStream(resourcePath);

        if (is == null && resourcePath.startsWith("/")) {
            // Intento 2: Si falla el absoluto, probar sin el slash inicial (Búsqueda Relativa al paquete)
            finalPath = resourcePath.substring(1); 
            is = getClass().getResourceAsStream(finalPath);
        }
        
        if (is == null) {
            // Intento 3: Usar el ClassLoader del sistema (el más robusto, siempre con la ruta absoluta)
            finalPath = resourcePath;
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(finalPath);
        }
        
        if (is == null) {
            // Si todo falla, muestra el error detallado
            manualText.setText("ERROR CRÍTICO: No se pudo encontrar el recurso. Las rutas probadas fueron:\n" + 
                               "1. Ruta absoluta: " + resourcePath + 
                               "\n2. Ruta relativa: " + resourcePath.substring(1) + 
                               "\n\nSOLUCIÓN: Verifique que la carpeta 'recursos' esté dentro de 'src' y marcada como Source Folder en su IDE.");
            return;
        }

        // --- Lógica de lectura si el stream fue encontrado ---
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        manualText.setText(sb.toString());

    } catch (Exception e) {
        manualText.setText("Error interno al leer el manual: " + e.getMessage());
        e.printStackTrace();
    } finally {
        if (is != null) {
            try {
                is.close();
            } catch (java.io.IOException ignored) {}
        }
    }
}
}