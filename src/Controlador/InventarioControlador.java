package Controlador;

import Modelo.Insumo;
import Modelo.InsumoDAO;
import Vistas.InventarioVista; // Tu vista de la imagen
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        this.vista.btnActualizar.addActionListener(this); // <--- NUEVO
        this.vista.btnEliminar.addActionListener(this);   // <--- NUEVO
        this.vista.btnLimpiar.addActionListener(this);
        this.vista.tablaInventario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = vista.tablaInventario.rowAtPoint(e.getPoint());

                // Pasamos los datos de la tabla a las cajas de texto
                vista.txtCodigo.setText(vista.tablaInventario.getValueAt(fila, 1).toString());
                vista.txtNombre.setText(vista.tablaInventario.getValueAt(fila, 2).toString());
                vista.txtCantidad.setText(vista.tablaInventario.getValueAt(fila, 3).toString());
            }
        });
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
        // --- ACTUALIZAR ---
        if (e.getSource() == vista.btnActualizar) {
            if ("".equals(vista.txtCodigo.getText())) {
                JOptionPane.showMessageDialog(null, "Seleccione una fila primero");
            } else {
                // Necesitamos el ID de la fila seleccionada para saber a quién editar
                int fila = vista.tablaInventario.getSelectedRow();
                int id = Integer.parseInt(vista.tablaInventario.getValueAt(fila, 0).toString());

                insumo.setId(id); // Seteamos el ID al objeto
                insumo.setCodigo(vista.txtCodigo.getText());
                insumo.setNombre(vista.txtNombre.getText());
                insumo.setCantidad(Integer.parseInt(vista.txtCantidad.getText()));

                if (insumoDao.modificar(insumo)) {
                    JOptionPane.showMessageDialog(null, "Insumo Modificado");
                    limpiarCampos();
                    listarTabla();
                }
            }
        }

        // --- ELIMINAR ---
        if (e.getSource() == vista.btnEliminar) {
            if (!"".equals(vista.txtCodigo.getText())) {

                int pregunta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar?");

                if (pregunta == 0) { // 0 = SI
                    int fila = vista.tablaInventario.getSelectedRow();
                    int id = Integer.parseInt(vista.tablaInventario.getValueAt(fila, 0).toString());

                    if (insumoDao.eliminar(id)) {
                        JOptionPane.showMessageDialog(null, "Insumo Eliminado");
                        limpiarCampos();
                        listarTabla();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar");
            }
        }

        // --- LIMPIAR ---
        if (e.getSource() == vista.btnLimpiar) {
            limpiarCampos();
        }
    }

    // Método para llenar la JTable
    public void listarTabla() {
        // Limpiamos las filas anteriores para no duplicar
        limpiarTabla();
        List<Insumo> lista = insumoDao.listarInsumos();
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
