package proyectoautomatas.gersonorellana;

import controlador.ControladorTexto;
import vistas.Vista;


public class ProyectoAutomatasGersonOrellana {

    public static void main(String[] args) { 
        java.awt.EventQueue.invokeLater(() -> {
            Vista vista = new Vista();
            ControladorTexto ctrlTexto = new ControladorTexto(vista); 
            vista.setVisible(true);
        });
    }
}