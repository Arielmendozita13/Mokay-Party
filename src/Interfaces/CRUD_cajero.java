package Interfaces;

import control.conexionBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.*;

public class CRUD_cajero extends JFrame implements ActionListener{

    private JComboBox<String> comboTablas;
    private JButton btnConsultar, btnAgregar, btnActualizar, btnSalir, btnConsulProd;
    private JTable tablaDatos;
    private JScrollPane scrollTabla;

    public CRUD_cajero() {
        setTitle("Panel del Cajero - CRUD limitado");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel superior ---
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBackground(new Color(121, 170, 211));

        JLabel lblSeleccion = new JLabel("Seleccionar tabla:");
        comboTablas = new JComboBox<>(new String[]{
                "pedidos", "detalles_pedidos", "facturas"
        });

        btnConsultar = new JButton("Consultar");
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnConsulProd = new JButton("Consultar Productos");
        btnSalir = new JButton("Salir");

        panelSuperior.add(lblSeleccion);
        panelSuperior.add(comboTablas);
        panelSuperior.add(btnConsultar);
        panelSuperior.add(btnAgregar);
        panelSuperior.add(btnActualizar);
        panelSuperior.add(btnConsulProd);
        panelSuperior.add(btnSalir);

        add(panelSuperior, BorderLayout.NORTH);

        // --- Tabla central ---
        tablaDatos = new JTable();
        tablaDatos.setBackground(new Color(172, 220, 255));
        tablaDatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollTabla = new JScrollPane(tablaDatos);
        add(scrollTabla, BorderLayout.CENTER);

        // --- Eventos básicos ---
        btnConsultar.addActionListener(e -> consultarDatos());
        btnAgregar.addActionListener(e -> agregarRegistro());
        btnActualizar.addActionListener(e -> actualizarRegistro());
        btnConsulProd.addActionListener(e -> consultarProductos());
        btnSalir.addActionListener(e -> System.exit(0));

        consultarDatos();

    }

    // ---------------- MÉTODOS CRUD ----------------

    private void consultarDatos() {
        String tabla = (String) comboTablas.getSelectedItem();

        try (Connection cn = conexionBD.connectCajero();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + tabla)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnas = meta.getColumnCount();
            DefaultTableModel modelo = new DefaultTableModel();

            for (int i = 1; i <= columnas; i++) {
                modelo.addColumn(meta.getColumnName(i));
            }

            while (rs.next()) {
                Object[] fila = new Object[columnas];
                for (int i = 0; i < columnas; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                modelo.addRow(fila);
            }

            conexionBD.disconnect(cn);

            tablaDatos.setModel(modelo);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultarProductos() {
        try (Connection cn = conexionBD.connectCajero();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM productos")) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnas = meta.getColumnCount();
            DefaultTableModel modelo = new DefaultTableModel();

            for (int i = 1; i <= columnas; i++) {
                modelo.addColumn(meta.getColumnName(i));
            }

            while (rs.next()) {
                Object[] fila = new Object[columnas];
                for (int i = 0; i < columnas; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                modelo.addRow(fila);
            }

            conexionBD.disconnect(cn);

            tablaDatos.setModel(modelo);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarRegistro() {
        String tabla = (String) comboTablas.getSelectedItem();
        try (Connection cn = conexionBD.connectCajero()){
            switch (tabla) {
                case "pedidos" -> insertPedido(cn);
                case "detalles_pedidos"  -> insertDetallePedido(cn);
                case "facturas" -> insertFactura(cn);
                default -> JOptionPane.showMessageDialog(this, "Esa tabla no tiene formulario configurado.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al insertar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        consultarDatos();
    }

    private void insertPedido(Connection cn) throws SQLException {
        int ID_cliente = Integer.parseInt(JOptionPane.showInputDialog(this, "ID del cliente:"));
        String sql = "INSERT INTO pedidos (ID_cliente) VALUES (?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, ID_cliente);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Pedido agregado.");
        }
    }

    private void insertDetallePedido(Connection cn) throws SQLException {
        int ID_pedido = Integer.parseInt(JOptionPane.showInputDialog(this, "ID del pedido:"));
        int ID_producto = Integer.parseInt(JOptionPane.showInputDialog(this, "ID del producto:"));
        int cantidad = Integer.parseInt(JOptionPane.showInputDialog(this, "Cantidad:"));
        String sql = "INSERT INTO detalles_pedidos (ID_pedido, ID_producto, cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, ID_pedido);
            ps.setInt(2, ID_producto);
            ps.setInt(3, cantidad);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "sub-pedido agregado.");
        }
    }

    private void insertFactura(Connection cn) throws SQLException {
        int ID_pedido = Integer.parseInt(JOptionPane.showInputDialog(this, "ID del pedido:"));
        Object[] options = {"Efectivo", "Transferencia", "Tarjeta", "App", "Otro"};
        int selec = JOptionPane.showOptionDialog(this,
                "Seleccione el metodo de Pago",
                "Metodo de Pago",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        String metodoPago = options[selec].toString();
        String sql = "INSERT INTO facturas (ID_pedido, metodoPago) VALUES (?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, ID_pedido);
            ps.setString(2, metodoPago);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Factura agregada.");
        }
    }

    private void actualizarRegistro() {
        if (tablaDatos.getSelectedRow() != -1) {
            String tabla = (String) comboTablas.getSelectedItem();
            try (Connection cn = conexionBD.connectCajero()){
                switch (tabla) {
                    case "pedidos" -> actualPedido(cn);
                    case "detalles_pedidos"  -> actualDetallePedido(cn);
                    case "facturas" -> JOptionPane.showMessageDialog(this, "No tienes permiso para actualizar esta Tabla.");
                    default -> JOptionPane.showMessageDialog(this, "Esa tabla no tiene formulario configurado.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al insertar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            consultarDatos();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para actualizar.");
        }
    }

    private void actualPedido(Connection cn) throws SQLException {
        int ID_pedido = Integer.parseInt(tablaDatos.getValueAt(tablaDatos.getSelectedRow(), 0).toString());
        Object[] options = {"Entregado", "Cancelado"};
        int selec = JOptionPane.showOptionDialog(this,
                "Seleccione el nuevo estado del pedido",
                "Actualizacion de pedido",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        String estado = options[selec].toString();
        String sql = "UPDATE pedidos SET estado = ? WHERE ID_pedido = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, ID_pedido);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Pedido actualizado.");
        }
    }

    private void actualDetallePedido(Connection cn) throws SQLException {
        int ID_detalle = Integer.parseInt(tablaDatos.getValueAt(tablaDatos.getSelectedRow(), 0).toString());
        int cantidad = Integer.parseInt(JOptionPane.showInputDialog(this, "Nueva Cantidad:"));
        String sql = "UPDATE detalles_pedidos SET cantidad = ? WHERE ID_detalle = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, ID_detalle);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Pedido actualizado.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
