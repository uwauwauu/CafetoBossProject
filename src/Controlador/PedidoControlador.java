package Controlador;

import Modelo.*;
import Vistas.PedidoVista;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PedidoControlador implements ActionListener {

    private PedidoVista vista;
    private ProductoDAO prodDao;
    private PedidoDAO pedDao;
    private CategoriaDAO catDao;
    private DefaultTableModel modeloTabla;

    public PedidoControlador(PedidoVista vista) {
        this.vista = vista;
        this.prodDao = new ProductoDAO();
        this.pedDao = new PedidoDAO();
        this.catDao = new CategoriaDAO(); //
        this.modeloTabla = new DefaultTableModel();

        // Configuración de la tabla
        String[] titulos = {"ID", "Producto", "Cantidad", "Precio U.", "Subtotal"};
        modeloTabla.setColumnIdentifiers(titulos);
        this.vista.tablaPedidos.setModel(modeloTabla);

        // --- LISTENERS ---
        this.vista.btnAgregar.addActionListener(this);
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnBorrar.addActionListener(this);
        this.vista.btnActualizar.addActionListener(this);

        // Listener especial para cuando cambias la Categoría
        this.vista.cbxCategoria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarProductosPorCategoria();
            }
        });

        // Listener especial para mostrar el precio cuando eliges un producto
        this.vista.cbxProductos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarPrecioProducto();
            }
        });
        this.vista.tablaPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                llenarCamposDesdeTabla();
            }
        });
        cargarCategorias(); // Cargar categorías al abrir la ventana
    }



    @Override
    public void actionPerformed(ActionEvent e) {

        // --- AGREGAR A LA TABLA ---
        if (e.getSource() == vista.btnAgregar) {
            Producto prod = (Producto) vista.cbxProductos.getSelectedItem();

            // Validar cantidad
            int cantidad = Integer.parseInt(vista.spinnerCantidad.getValue().toString());

            if (prod != null && cantidad > 0) {
                double subtotal = prod.getPrecio() * cantidad;

                Object[] fila = new Object[5];
                fila[0] = prod.getId();
                fila[1] = prod.getNombre();
                fila[2] = cantidad;
                fila[3] = prod.getPrecio();
                fila[4] = subtotal;

                modeloTabla.addRow(fila);

                calcularTotalGeneral(); // <--- ¡AQUÍ LLAMAMOS AL CÁLCULO!
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione producto y cantidad mayor a 0");
            }
        }

        // --- BOTÓN GUARDAR (A la Base de Datos) ---
        if (e.getSource() == vista.btnGuardar) {
            Pedido pedido = new Pedido();
            pedido.setTotal(Double.parseDouble(vista.txtTotal.getText()));

            List<DetallePedido> detalles = new ArrayList<>();

            // Recorrer la tabla visual para llenar la lista
            for (int i = 0; i < vista.tablaPedidos.getRowCount(); i++) {
                DetallePedido det = new DetallePedido();
                det.setId_producto((int) vista.tablaPedidos.getValueAt(i, 0));
                det.setCantidad((int) vista.tablaPedidos.getValueAt(i, 2));
                det.setPrecio((double) vista.tablaPedidos.getValueAt(i, 3));
                det.setSubtotal((double) vista.tablaPedidos.getValueAt(i, 4));
                detalles.add(det);
            }

            if (pedDao.registrarPedido(pedido, detalles)) {
                JOptionPane.showMessageDialog(null, "¡Pedido registrado!");
                modeloTabla.setRowCount(0); // Limpiar tabla
            }
        }
        
        // --- BOTÓN BORRAR ---
        if (e.getSource() == vista.btnBorrar) {
            int fila = vista.tablaPedidos.getSelectedRow();

            if (fila >= 0) {
                // Simplemente eliminamos la fila del modelo
                modeloTabla.removeRow(fila);
                // Recalcular el total después de borrar
                calcularTotalGeneral();
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione una fila para borrar");
            }
        }

        // --- BOTÓN ACTUALIZAR ---
        if (e.getSource() == vista.btnActualizar) {
            int fila = vista.tablaPedidos.getSelectedRow();

            if (fila >= 0) {
                // Obtener la nueva cantidad del spinner
                int nuevaCantidad = Integer.parseInt(vista.spinnerCantidad.getValue().toString());

                if (nuevaCantidad > 0) {
                    // Obtener el precio unitario que ya estaba en la tabla (Columna 3)
                    double precioUnitario = Double.parseDouble(modeloTabla.getValueAt(fila, 3).toString());

                    // Calcular nuevo subtotal
                    double nuevoSubtotal = precioUnitario * nuevaCantidad;

                    // Actualizar los valores en la tabla
                    modeloTabla.setValueAt(nuevaCantidad, fila, 2); // Columna Cantidad
                    modeloTabla.setValueAt(nuevoSubtotal, fila, 4); // Columna Subtotal

                    // Recalcular el total final
                    calcularTotalGeneral();

                    JOptionPane.showMessageDialog(null, "Producto actualizado");
                } else {
                    JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla para modificar");
            }
        }
    }

        // Llenar el combo de Categorías
    private void cargarCategorias() {
        vista.cbxCategoria.removeAllItems();
        List<Categoria> lista = catDao.listarCategorias();
        for (Categoria cat : lista) {
            vista.cbxCategoria.addItem(cat);
        }
    }

    // Filtrar productos (Se llama al cambiar categoría)
    private void cargarProductosPorCategoria() {
        Categoria catSeleccionada = (Categoria) vista.cbxCategoria.getSelectedItem();
        vista.cbxProductos.removeAllItems();

        if (catSeleccionada != null) {
            // Usamos el método con filtro que creamos en ProductoDAO
            List<Producto> lista = prodDao.listarProductosPorCategoria(catSeleccionada.getId());
            for (Producto prod : lista) {
                vista.cbxProductos.addItem(prod);
            }
        }
    }

    // Mostrar precio unitario en el textfield
    private void mostrarPrecioProducto() {
        Producto prod = (Producto) vista.cbxProductos.getSelectedItem();
        if (prod != null) {
            // Asumiendo que tu campo de precio unitario se llama txtPrecio o lblPrecio
            vista.txtPrecioUnitario.setText(String.valueOf(prod.getPrecio()));
        }
    }
    
    // Llenar campos de las tablas
    private void llenarCamposDesdeTabla(){
        int fila = vista.tablaPedidos.getSelectedRow();
        if (fila >= 0) {
            // Obtenemos la cantidad de la columna 2 (0=ID, 1=Nombre, 2=Cantidad...)
            int cantidad = Integer.parseInt(modeloTabla.getValueAt(fila, 2).toString());
            vista.spinnerCantidad.setValue(cantidad);
        }
    }

    // Calcular el total sumando la columna de la tabla
    private void calcularTotalGeneral() {
        double total = 0.00;
        int columnaSubtotal = 4; // La columna "Subtotal" es la 4 (0,1,2,3,4)

        for (int i = 0; i < vista.tablaPedidos.getRowCount(); i++) {
            total += Double.parseDouble(modeloTabla.getValueAt(i, columnaSubtotal).toString());
        }

        vista.txtTotal.setText(String.valueOf(total));
    }
}
