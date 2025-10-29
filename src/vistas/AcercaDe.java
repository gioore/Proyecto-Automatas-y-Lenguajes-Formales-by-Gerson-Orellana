package vistas;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AcercaDe extends JDialog {
    public AcercaDe(JFrame parent) {
        super(parent, "Acerca de", true);
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Logo o ícono
        JLabel icono = new JLabel();
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        icono.setIcon(UIManager.getIcon("OptionPane.informationIcon")); // Puedes poner tu propio logo aquí

        // Título
        JLabel titulo = new JLabel("Automátas y Lenguajes Formales");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(33, 47, 61));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Universidad
        JLabel universidad = new JLabel("Universidad Mariano Gálvez de Guatemala");
        universidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        universidad.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tu nombre
        JLabel autor = new JLabel("Desarrollado por: Gerson Giovanni Orellana Véliz");
        autor.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        autor.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fecha
        JLabel fecha = new JLabel("Fecha: " + LocalDate.now());
        fecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fecha.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón Cerrar
        JButton cerrar = new JButton("Cerrar");
        cerrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cerrar.addActionListener(e -> dispose());

        // Añadir componentes
        panel.add(icono);
        panel.add(Box.createVerticalStrut(15));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(universidad);
        panel.add(Box.createVerticalStrut(10));
        panel.add(autor);
        panel.add(Box.createVerticalStrut(10));
        panel.add(fecha);
        panel.add(Box.createVerticalStrut(20));
        panel.add(cerrar);

        add(panel, BorderLayout.CENTER);
    }
}
