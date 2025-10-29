/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vistas;

import java.awt.Color;
import java.awt.Component;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gerso
 */
public class Vista extends javax.swing.JFrame {

    /**
     * Creates new form Vista
     */
    // --- Variables de Resaltado Dinámico ---
  
    private String estadoOrigenActivo = null;
    // Almacena el símbolo (encabezado de columna) de la transición actual
    private String simboloActivo = null;
    
    public Vista() {
        initComponents();
        this.setLocationRelativeTo(null);
        inicializarTablas(); 

    }
    
    // Dentro de la clase Vista.java, fuera de cualquier método
// Rutas de los recursos (DEBEN tener el slash inicial)
private static final String PATH_MANUAL_USUARIO = "manuales/usuario.txt";
private static final String PATH_MANUAL_TECNICO = "manuales/tecnico.txt";
    // Método para limpiar todas las tablas (usado en nuevoArchivo())
    public void limpiarTablas() {
        ((DefaultTableModel) jTableSimbolos.getModel()).setRowCount(0); // Símbolos
        // No limpiamos jTableTransiciones aquí, se limpia al crear el nuevo modelo.
        ((DefaultTableModel) jTableCadenas.getModel()).setRowCount(0); // Cadenas
        ((DefaultTableModel) JTableAceptacion.getModel()).setRowCount(0); // Estados de Aceptación
        // Inicializamos el modelo de transiciones a 0 columnas
        jTableTransiciones.setModel(new DefaultTableModel(new Object[][]{}, new String[]{}));
    }
    
    public void inicializarTablas() {
        // Inicializa y configura los modelos de tabla con encabezados predeterminados
        configurarTablaAceptacion();
        configurarTablaSimbolos();
        configurarTablaTransiciones(); // Configura el modelo inicial de 0 columnas
        configurarTablaCadenas();
       
        jTableTransiciones.setDefaultRenderer(Object.class, new TransicionCellRenderer());
        
        limpiarTablas();
        // Limpiar todas las tablas y asegurarse que jTableTransiciones esté vacía
        limpiarTablas();
    }
    
    // ----------------------------------------------------
    // ----------------------------------------------------
    private class TransicionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // 1. Obtener el estado (de la primera columna, si la columna actual no es la primera)
            // Se usa el DefaultTableModel de la tabla
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            String estadoFila = (String) model.getValueAt(row, 0); // La columna 0 siempre es el estado de origen

            // 2. Obtener el símbolo (del encabezado de columna)
            String simboloColumna = "";
            if (column > 0) { // La columna 0 es "Estados", no un símbolo
                simboloColumna = table.getColumnName(column);
            }

            // 3. Lógica de Coloreado
            if (estadoOrigenActivo != null && simboloActivo != null
                    && estadoFila.equals(estadoOrigenActivo)
                    && simboloColumna.equals(simboloActivo)) {
                // La transición es la activa: EstadoOrigen (fila) + Simbolo (columna)
                c.setBackground(new Color(135, 206, 250)); // Azul claro (SkyBlue)
                c.setForeground(Color.BLACK);
            } else {
                // Fila regular
                c.setBackground(table.getBackground());
                c.setForeground(table.getForeground());
            }

            // Asegurar que el alineamiento de texto sea correcto (centrado o a la izquierda)
            setHorizontalAlignment(JLabel.CENTER);

            return c;
        }
    }

    // ----------------------------------------------------
    // Este método lo llamará el ControladorSimulacion en cada paso.
    // ----------------------------------------------------
    /**
     * Resalta la transición activa en la tabla.
     * @param estadoOrigen El estado de la fila que debe resaltarse.
     * @param simbolo El símbolo (columna) que debe resaltarse.
     */
    public void resaltarTransicionActiva(String estadoOrigen, String simbolo) {
        this.estadoOrigenActivo = estadoOrigen;
        this.simboloActivo = simbolo;
        
        // Forzar a la tabla a repintarse para que el Renderer aplique los nuevos colores.
        jTableTransiciones.repaint();
    }
    
    /**
     * Limpia el resaltado de la tabla al reiniciar la simulación.
     */
    public void limpiarResaltadoTransiciones() {
        this.estadoOrigenActivo = null;
        this.simboloActivo = null;
        jTableTransiciones.repaint();
    }

    private void configurarTablaAceptacion() {
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"No.", "Estados de Aceptación"}
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false;
             }
        };
        JTableAceptacion.setModel(model);
    }

    private void configurarTablaSimbolos() {
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"No.", "Símbolos"}
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false;
             }
        };
        jTableSimbolos.setModel(model);
    }

    // Inicializa jTableTransiciones con un modelo vacío (sin columnas)
    private void configurarTablaTransiciones() {
        // Modelo vacío, las columnas se definen en llenarTablaTransiciones()
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new String[]{}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                 return false;
             }
        };
        jTableTransiciones.setModel(model);
    }
    
    // NUEVO MÉTODO para configurar la tabla de Cadenas
    private void configurarTablaCadenas() {
        DefaultTableModel model = new DefaultTableModel(
            new Object [][] {},
            new String [] {"Cadena", "Resultado", "Longitud", "Estado Final"} // Encabezados de Simulación
        ) {
             // La implementación que previene el ArrayIndexOutOfBoundsException
            @Override
            public Class getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                    case 1:
                    case 3:
                        return String.class;
                    case 2:
                        return Integer.class;
                    default:
                        return Object.class;
                }
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Las celdas no deben ser editables
            }
        };
        jTableCadenas.setModel(model);
    }

    // MODIFICADO: Uso de List y fireTableDataChanged para refresco
    public void llenarTablaAceptacion(java.util.List<String> estadosAceptacion) {
        DefaultTableModel model = (DefaultTableModel) JTableAceptacion.getModel();
        
        model.setRowCount(0); // Limpiar la tabla antes de llenarla
        
        int i = 1;
        for (String estado : estadosAceptacion) {
            model.addRow(new Object[]{i++, estado});
        }
        
        model.fireTableDataChanged(); // Forzar el redibujo
    }

    public void llenarTablaSimbolos(java.util.List<String> simbolos) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) jTableSimbolos.getModel();
        model.setRowCount(0); // Limpiar tabla

        int contador = 1;
        for (String s : simbolos) {
            model.addRow(new Object[]{contador++, s});
        }
        model.fireTableDataChanged(); // Forzar el redibujo
    }

    // CRÍTICO: IMPLEMENTACIÓN SOLUCIONADA DE LA TABLA DE TRANSICIONES
    public void llenarTablaTransiciones(Set<String> estados, List<String> simbolos, Map<String, Map<String, String>> matrizTransiciones) {
        limpiarResaltadoTransiciones();
        // 1. Definir los encabezados de columna
        Vector<String> columnas = new Vector<>();
        columnas.add("Estados"); // Primera columna es para los estados
        columnas.addAll(simbolos); // El resto son los símbolos (0, 1, etc.)

        // 2. Preparar el Vector de datos para las filas
        Vector<Vector<Object>> datosFilas = new Vector<>();

        // 3. Ordenar los estados para consistencia visual
        List<String> estadosOrdenados = new java.util.ArrayList<>(estados);
        Collections.sort(estadosOrdenados);

        // 4. Llenar las filas de la tabla
        for (String estado : estadosOrdenados) {
            Vector<Object> fila = new Vector<>();
            fila.add(estado); // Añadir el estado a la primera columna
            
            Map<String, String> transicionesPorSimbolo = matrizTransiciones.get(estado);

            // Recorrer los símbolos para obtener los estados destino
            for (String simbolo : simbolos) {
                String estadoDestino = "";
                if (transicionesPorSimbolo != null) {
                    estadoDestino = transicionesPorSimbolo.getOrDefault(simbolo, "-"); 
                }
                fila.add(estadoDestino);
            }
            datosFilas.add(fila);
        }
        
        // CRÍTICO: CREAR Y ASIGNAR UN NUEVO MODELO COMPLETO
        DefaultTableModel nuevoModelo = new DefaultTableModel(datosFilas, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                 return false;
             }
        };
        jTableTransiciones.setModel(nuevoModelo);
        
        // --- PASOS CRÍTICOS PARA EL REPINTE FORZADO ---
        // 1. Validar la estructura de la tabla
        jTableTransiciones.validate();
        
        // 2. Forzar el repintado de la tabla
        jTableTransiciones.repaint();
        
        // 3. Forzar el repintado del contenedor (JScrollPane)
        // Esto asume que jScrollPane1 es el JScrollPane que contiene jTableTransiciones
        // y que jTableTransiciones no está directamente en el JFrame.
        jScrollPane1.validate(); 
        jScrollPane1.repaint();
    }
    
    public void llenarTablaCadenas(List<String> cadenas) {
        DefaultTableModel model = (DefaultTableModel) jTableCadenas.getModel();
        model.setRowCount(0);

        for (String cadena : cadenas) {
            // Asegúrate de que los datos se inserten para las 4 columnas
            model.addRow(new Object[]{
                cadena, 
                "Pendiente", // Columna 1: Resultado
                cadena.length(), // Columna 2: Longitud
                "" // Columna 3: Estado Final
            });
        }
        model.fireTableDataChanged(); // Forzar el redibujo
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt = new javax.swing.JTextArea();
        btnProcesarEntrada = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableSimbolos = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jPanelGrafo = new javax.swing.JPanel();
        lblGrafo = new javax.swing.JLabel();
        btnPasoSiguiente = new javax.swing.JButton();
        btnSimularTodo = new javax.swing.JButton();
        btnReiniciarSim = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lblEstadoActual = new javax.swing.JLabel();
        lblCadenaActual = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanelTitulo = new javax.swing.JPanel();
        txtfield = new javax.swing.JLabel();
        txtfield1 = new javax.swing.JLabel();
        jPanelTrans = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTransiciones = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtEstado = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableCadenas = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        JTableAceptacion = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        Nuevo = new javax.swing.JMenuItem();
        Abrir = new javax.swing.JMenuItem();
        Guardar = new javax.swing.JMenuItem();
        menuEjemplos = new javax.swing.JMenu();
        ejemplo1 = new javax.swing.JMenuItem();
        Ejemplo2 = new javax.swing.JMenuItem();
        Ejemplo3 = new javax.swing.JMenuItem();
        MenuAcerca = new javax.swing.JMenu();
        itemAcercaDe = new javax.swing.JMenuItem();
        itemAyuda = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txt.setColumns(20);
        txt.setRows(5);
        jScrollPane2.setViewportView(txt);

        btnProcesarEntrada.setText("Procesar");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(btnProcesarEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnProcesarEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableSimbolos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(jTableSimbolos);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Simbolos:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        jPanelGrafo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblGrafo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnPasoSiguiente.setText("Paso Siguiente");

        btnSimularTodo.setText("Simular Todo");

        btnReiniciarSim.setText("Reiniciar Simulacion");

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblEstadoActual.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblEstadoActual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblCadenaActual.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCadenaActual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Estado Actual:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Estado Actual Cadena");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(lblEstadoActual, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblCadenaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCadenaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEstadoActual, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout jPanelGrafoLayout = new javax.swing.GroupLayout(jPanelGrafo);
        jPanelGrafo.setLayout(jPanelGrafoLayout);
        jPanelGrafoLayout.setHorizontalGroup(
            jPanelGrafoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGrafoLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelGrafoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGrafoLayout.createSequentialGroup()
                        .addComponent(btnPasoSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSimularTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnReiniciarSim))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGrafoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblGrafo, javax.swing.GroupLayout.PREFERRED_SIZE, 652, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(302, 302, 302))
        );
        jPanelGrafoLayout.setVerticalGroup(
            jPanelGrafoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGrafoLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanelGrafoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGrafoLayout.createSequentialGroup()
                        .addGroup(jPanelGrafoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPasoSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSimularTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReiniciarSim, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblGrafo, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelTitulo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtfield.setFont(new java.awt.Font("Segoe UI Black", 0, 36)); // NOI18N
        txtfield.setText("Formales");

        txtfield1.setFont(new java.awt.Font("Segoe UI Black", 0, 36)); // NOI18N
        txtfield1.setText("Autómatas y Lenguajes");

        javax.swing.GroupLayout jPanelTituloLayout = new javax.swing.GroupLayout(jPanelTitulo);
        jPanelTitulo.setLayout(jPanelTituloLayout);
        jPanelTituloLayout.setHorizontalGroup(
            jPanelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTituloLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtfield, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTituloLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtfield1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelTituloLayout.setVerticalGroup(
            jPanelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTituloLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtfield1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtfield))
        );

        jPanelTrans.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableTransiciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTableTransiciones);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Transiciones:");

        javax.swing.GroupLayout jPanelTransLayout = new javax.swing.GroupLayout(jPanelTrans);
        jPanelTrans.setLayout(jPanelTransLayout);
        jPanelTransLayout.setHorizontalGroup(
            jPanelTransLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTransLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTransLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanelTransLayout.setVerticalGroup(
            jPanelTransLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTransLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Estado Inicial:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableCadenas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(jTableCadenas);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Cadenas:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(143, 143, 143))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        JTableAceptacion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(JTableAceptacion);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Estado de Aceptación:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setText("Archivo");

        Nuevo.setText("Nuevo");
        Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NuevoActionPerformed(evt);
            }
        });
        jMenu1.add(Nuevo);

        Abrir.setText("Abrir");
        Abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbrirActionPerformed(evt);
            }
        });
        jMenu1.add(Abrir);

        Guardar.setText("Guardar");
        Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarActionPerformed(evt);
            }
        });
        jMenu1.add(Guardar);

        menu.add(jMenu1);

        menuEjemplos.setText("Ejemplos");

        ejemplo1.setText("Ejemplo1");
        menuEjemplos.add(ejemplo1);

        Ejemplo2.setText("Ejemplo2");
        menuEjemplos.add(Ejemplo2);

        Ejemplo3.setText("Ejemplo3");
        menuEjemplos.add(Ejemplo3);

        menu.add(menuEjemplos);

        MenuAcerca.setText("Acerca de...");

        itemAcercaDe.setText("Acerca de");
        itemAcercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAcercaDeActionPerformed(evt);
            }
        });
        MenuAcerca.add(itemAcercaDe);

        itemAyuda.setText("Ayuda");
        itemAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAyudaActionPerformed(evt);
            }
        });
        MenuAcerca.add(itemAyuda);

        menu.add(MenuAcerca);

        setJMenuBar(menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanelGrafo, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                                .addComponent(jPanelTrans, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelGrafo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanelTrans, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NuevoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NuevoActionPerformed

    private void AbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AbrirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AbrirActionPerformed

    private void GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_GuardarActionPerformed

    private void itemAcercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAcercaDeActionPerformed
    AcercaDe ventana = new AcercaDe(this);
ventana.setVisible(true);
    }//GEN-LAST:event_itemAcercaDeActionPerformed

    private void itemAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAyudaActionPerformed
    
    VistaSelectorManual selector = new VistaSelectorManual(this); 
    selector.setVisible(true);

    }//GEN-LAST:event_itemAyudaActionPerformed
// --- GETTERS ---
    public javax.swing.JLabel getLblGrafo() {
        return lblGrafo;
    }
    public javax.swing.JMenuItem getNuevo() {
        return Nuevo;
    }
     public javax.swing.JMenuItem getEjemplo1() {
        return ejemplo1;
    }
      public javax.swing.JMenuItem getEjemplo2() {
        return Ejemplo2;
    }
       public javax.swing.JMenuItem getEjemplo3() {
        return Ejemplo3;
    }

    public javax.swing.JMenuItem getAbrir() {
        return Abrir;
    }

    public javax.swing.JMenuItem getGuardar() {
        return Guardar;
    }

    public javax.swing.JTextArea getTxt() {
        return txt;
    }

    public void setEstadoInicial(String estado) {
        txtEstado.setText(estado);
    }

  

    public javax.swing.JTable getJTableCadenas() {
        return jTableCadenas;
    }
    public javax.swing.JTable jTableTransiciones() {
        return jTableTransiciones;
    }

 

    public javax.swing.JButton getBtnSimularTodo() {
        return btnSimularTodo;
    }

    public javax.swing.JButton getBtnPasoSiguiente() {
        return btnPasoSiguiente;
    }

    public javax.swing.JButton getBtnReiniciarSim() {
        return btnReiniciarSim;
    }

    public javax.swing.JLabel getLblEstadoActual() {
        return lblEstadoActual;
    }

    public javax.swing.JLabel getLblCadenaActual() {
        return lblCadenaActual;
    }
    public javax.swing.JButton getBtnProcesarEntrada() {
        return btnProcesarEntrada;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Vista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Vista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Vista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Vista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Vista().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Abrir;
    private javax.swing.JMenuItem Ejemplo2;
    private javax.swing.JMenuItem Ejemplo3;
    private javax.swing.JMenuItem Guardar;
    private javax.swing.JTable JTableAceptacion;
    private javax.swing.JMenu MenuAcerca;
    private javax.swing.JMenuItem Nuevo;
    private javax.swing.JButton btnPasoSiguiente;
    private javax.swing.JButton btnProcesarEntrada;
    private javax.swing.JButton btnReiniciarSim;
    private javax.swing.JButton btnSimularTodo;
    private javax.swing.JMenuItem ejemplo1;
    private javax.swing.JMenuItem itemAcercaDe;
    private javax.swing.JMenuItem itemAyuda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanelGrafo;
    private javax.swing.JPanel jPanelTitulo;
    private javax.swing.JPanel jPanelTrans;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTableCadenas;
    private javax.swing.JTable jTableSimbolos;
    private javax.swing.JTable jTableTransiciones;
    private javax.swing.JLabel lblCadenaActual;
    private javax.swing.JLabel lblEstadoActual;
    private javax.swing.JLabel lblGrafo;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenu menuEjemplos;
    private javax.swing.JTextArea txt;
    private javax.swing.JTextField txtEstado;
    private javax.swing.JLabel txtfield;
    private javax.swing.JLabel txtfield1;
    // End of variables declaration//GEN-END:variables
}
