package Controlador;

import Modelo.Insumo;
import Modelo.InsumoDAO;
import Vistas.InventarioVista; // Tu vista de la imagen
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class InventarioControlador implements ActionListener {

    private InventarioVista vista;
    private InsumoDAO insumoDao;
    private DefaultTableModel modeloTabla; // Solo la declaramos aquí
    private Insumo insumo;
    
    public InventarioControlador(InventarioVista vista) {
        this.vista = vista;
        this.insumoDao = new InsumoDAO();
        this.insumo = new Insumo();
        
        // Configurar Columnas
        this.modeloTabla = new DefaultTableModel();

        // Columnas personalizadas
        String[] titulos = {"ID Insumo", "Código", "Nombre", "Cantidad"};
        this.modeloTabla.setColumnIdentifiers(titulos);

        // pegamos este modelo a la tabla visual
        this.vista.tablaInventario.setModel(modeloTabla);

        // Cargamos los Listeners
        this.vista.btnCrear.addActionListener(this);
        this.vista.btnListar.addActionListener(this);

        // Configurar Tabla
        listarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // --- BOTÓN CREAR (Guardar nuevo insumo) ---
        if (e.getSource() == vista.btnCrear) {
            if (vista.txtCodigo.getText().isEmpty()
                    || vista.txtNombre.getText().isEmpty()
                    || vista.txtCantidad.getText().isEmpty()) {

                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            } else {
                try {
                    // Recoger datos
                    insumo.setCodigo(vista.txtCodigo.getText());
                    insumo.setNombre(vista.txtNombre.getText());

                    // Validar que cantidad sea numero
                    int cant = Integer.parseInt(vista.txtCantidad.getText());
                    insumo.setCantidad(cant);

                    // Guardar
                    if (insumoDao.registrar(insumo)) {
                        JOptionPane.showMessageDialog(null, "Insumo Registrado");
                        limpiarTabla();
                        listarTabla();
                        limpiarCampos();
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "La cantidad debe ser un número entero.");
                }
            }
        }
    }

    // Método para llenar la JTable
    public void listarTabla() {
        List<Insumo> lista = insumoDao.listarInsumos();

        // Limpiamos las filas anteriores para no duplicar
        limpiarTabla();

        Object[] ob = new Object[4];
        for (int i = 0; i < lista.size(); i++) {
            ob[0] = lista.get(i).getId();
            ob[1] = lista.get(i).getCodigo();
            ob[2] = lista.get(i).getNombre();
            ob[3] = lista.get(i).getCantidad();
            modeloTabla.addRow(ob);
        }
        vista.tablaInventario.setModel(modeloTabla);
    }

    public void limpiarTabla() {
        modeloTabla.setRowCount(0);
    }

    public void limpiarCampos() {
        vista.txtCodigo.setText("");
        vista.txtNombre.setText("");
        vista.txtCantidad.setText("");
    }
}
