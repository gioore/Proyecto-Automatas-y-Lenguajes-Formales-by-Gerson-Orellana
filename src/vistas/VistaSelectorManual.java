package vistas;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VistaSelectorManual extends JDialog implements ActionListener {

    private final Vista ownerVista;
    private final JButton btnUsuario;
    private final JButton btnTecnico;

 // Archivo VistaSelectorManual.java (o donde definas tus PATHs)

// ¡CRÍTICO! La ruta correcta para tu estructura
private static final String PATH_MANUAL_USUARIO = "/recursos/usuario.txt"; 
private static final String PATH_MANUAL_TECNICO = "/recursos/tecnico.txt";

    public VistaSelectorManual(Vista owner) {
        // Modal: Bloquea la ventana principal hasta que se cierre este diálogo
        super(owner, "Seleccionar Documentación", true); 
        this.ownerVista = owner;

        // Configuración del diálogo
        setSize(300, 150);
        setLocationRelativeTo(owner);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        add(new JLabel("Seleccione el manual que desea abrir:"));
        
        // Panel para los botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        
        btnUsuario = new JButton("Manual de Usuario");
        btnTecnico = new JButton("Manual Técnico");

        btnUsuario.addActionListener(this);
        btnTecnico.addActionListener(this);
        
        panelBotones.add(btnUsuario);
        panelBotones.add(btnTecnico);
        
        add(panelBotones);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String title;
        String path;

        if (e.getSource() == btnUsuario) {
            title = "Documentación: Manual de Usuario";
            path = PATH_MANUAL_USUARIO;
        } else if (e.getSource() == btnTecnico) {
            title = "Documentación: Manual Técnico";
            path = PATH_MANUAL_TECNICO;
        } else {
            return;
        }
        
        // Cierra el selector antes de abrir el manual grande
        dispose(); 
        
        // Abre el manual grande usando la VistaManual (la que tiene el JTextArea)
        // Usamos el 'owner' de este selector como la ventana principal (Frame)
        VistaManual dialogoManual = new VistaManual(ownerVista, title, path);
        dialogoManual.setVisible(true);
    }
}