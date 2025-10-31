package Interfaces;

import control.conexionBD;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;


public class CRUD_admin extends JFrame {

    // Paleta de colores de interfaz
    private static final Color PRIMARY = new Color(0x1E40AF);
    private static final Color ACCENT  = new Color(0x123998);
    private static final Color BG      = new Color(0x465567);
    private static final Color CARD    = Color.WHITE;
    private static final Color MUTED   = new Color(0xFFFFFF);

    // UI
    private final JTextField tfSearch = new JTextField();
    private static DefaultTableModel model = new DefaultTableModel();
    private JComboBox<String> cbFiltro = new JComboBox<>();
    private final static JTable table = new JTable(model);

    private final StatCard scTotal     = new StatCard("Total", "0", new Color(0x0EA5E9));
    private final StatCard scAdmins    = new StatCard("Admin", "0", new Color(0x22C55E));
    private final StatCard scEmployees = new StatCard("Empleados", "0", new Color(0xA855F7));
    private final StatCard scActive    = new StatCard("Activo", "0", new Color(0x14B8A6));
    private final StatCard scInactive  = new StatCard("Inactivo", "0", new Color(0xF97316));

    public CRUD_admin() {
        setTitle("Usuario Administrador :) ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1120, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(10,10));

        // App
        JPanel appbar = new JPanel(new BorderLayout());
        appbar.setBackground(PRIMARY);
        appbar.setBorder(new EmptyBorder(12,16,12,16));
        JLabel title = new JLabel("Usuario Administrador");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18));
        appbar.add(title, BorderLayout.WEST);

        JButton btnRefresh = toolButton("Refrescar pagina");
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tools.setOpaque(false);
        tools.add(btnRefresh);
        appbar.add(tools, BorderLayout.EAST);
        add(appbar, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setResizeWeight(0.22);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        cbFiltro = new JComboBox<>(new String[]{
                "Clientes", "Productos", "Ventas", "Ventas_Diarias", "Pedidos", "Detalles_Pedidos", "Facturas"
        });
        cbFiltro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cbFiltro.setBackground(new Color(0x123997));
        cbFiltro.setForeground(new Color(0xFFFFFF));
        cbFiltro.setBorder(BorderFactory.createLineBorder(new Color(0xFFFFFF), 2, true));
        cbFiltro.setFocusable(false);
        cbFiltro.setBorder(new EmptyBorder(4, 8, 4, 8));

        tools.add(cbFiltro);

        // Sidebar (métricas)
        JPanel sidebar = new JPanel(new GridLayout(5,1,10,10));
        sidebar.setBackground(BG);
        sidebar.setBorder(new EmptyBorder(12,12,12,6));
        sidebar.add(scTotal);
        sidebar.add(scAdmins);
        sidebar.add(scEmployees);
        sidebar.add(scActive);
        sidebar.add(scInactive);
        split.setLeftComponent(sidebar);

        // Content (card simple)
        JPanel content = new RoundedPanel(16, CARD);
        content.setLayout(new BorderLayout(10,10));
        content.setBorder(new EmptyBorder(12,12,12,12));

        // Header con título y búsqueda
        JPanel header = new JPanel(new BorderLayout(8,8));
        header.setOpaque(false);
        JLabel hTitle = new JLabel("Registro de Clientes");
        hTitle.setFont(hTitle.getFont().deriveFont(Font.BOLD, 16f));
        tfSearch.setToolTipText("Busca por Nombre o por Apellido paterno");
        tfSearch.setPreferredSize(new Dimension(200, 28));
        tfSearch.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        tfSearch.setOpaque(true);
        header.add(hTitle, BorderLayout.WEST);
        header.add(tfSearch, BorderLayout.EAST);

        content.add(header, BorderLayout.NORTH);

        // Tabla
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(0xDBEAFE));
        table.setSelectionForeground(Color.BLACK);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0xEFF2F7));
        th.setReorderingAllowed(false);

        /*
        DefaultTableCellRenderer zebra = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                if (!sel) c.setBackground((row % 2 == 0) ? Color.WHITE : new Color(0xFAFAFA));
                setBorder(new EmptyBorder(4,10,4,10));
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, zebra);
        table.getColumnModel().getColumn(3).setCellRenderer(new ChipRenderer(new Color(0xE0E7FF), new Color(0x1D4ED8)));
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusChipRenderer());

        */
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        // Botones de accion
        JButton btnNew    = primary("Nuevo");
        JButton btnEdit   = neutral("Editar");
        JButton btnDelete = danger("Eliminar");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        actions.setOpaque(false);
        actions.add(btnDelete); actions.add(btnEdit); actions.add(btnNew);
        content.add(actions, BorderLayout.SOUTH);

        split.setRightComponent(content);

        // eventos
        tfSearch.getDocument().addDocumentListener(new SimpleChange(() -> applyFilter()));
        btnRefresh.addActionListener(e -> { applyFilter(); /* updateStats(); */ toast("Refrescar pagina"); });

        cbFiltro.addActionListener(e -> {
            String tabla = (String) cbFiltro.getSelectedItem();
            switch (tabla) {
                case "Clientes":
                    tfSearch.setToolTipText("Busca por Nombre o Apellido paterno");
                    hTitle.setText("Registro de Clientes");
                    break;
                case "Productos":
                    tfSearch.setToolTipText("Busca por Nombre o Descripción");
                    hTitle.setText("Registro de Productos");
                    break;
                case "Ventas":
                case "Pedidos":
                    tfSearch.setToolTipText("Busca por Fecha(YYYY-MM-DD) o ID de cliente");
                    hTitle.setText("Registro de Ventas");
                    break;
                case "Ventas_Diarias":
                    tfSearch.setToolTipText("Busca por Fecha(YYYY-MM-DD) o ID de cliente");
                    hTitle.setText("Registro de Reporte ventas");
                    break;
                case "Detalles_Pedidos":
                    tfSearch.setToolTipText("Busca por ID de producto");
                    hTitle.setText("Registro de Sub-Pedidos");
                    break;
                case "Facturas":
                    tfSearch.setToolTipText("Busca por Metodo de Pago o ID del pedido");
                    hTitle.setText("Registro de Facturas");
                    break;
            }
            applyFilter();
        });
        btnNew.addActionListener(e -> {
            String tabla = (String) cbFiltro.getSelectedItem();
            switch (tabla) {
                case "Clientes":
                    agregarCliente();
                    break;
                case "Productos":
                    agregarProducto();
                        break;
            }
        });
        btnEdit.addActionListener(e -> {
            switch (cbFiltro.getSelectedItem().toString()) {
                case "Productos":
                    actualizarProducto();
                    break;
            }
        });
        btnDelete.addActionListener(e -> {
            switch (cbFiltro.getSelectedItem().toString()) {
                case "Productos":
                    eliminarProducto();
                    break;
            }
        });
        applyFilter();
    }

    private void cargarTabla(String sql) {
        try (Connection cn = conexionBD.connectAdmin();
             Statement ps = cn.createStatement();
             ResultSet rs = ps.executeQuery(sql)) {

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
            table.setModel(modelo);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla(String sql, String query) {
        try (Connection cn = conexionBD.connectAdmin();
             PreparedStatement ps = cn.prepareStatement(sql);) {

            ps.setString(1, query.toLowerCase());
            ps.setString(2, query.toLowerCase());
            ResultSet rs = ps.executeQuery();

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
            table.setModel(modelo);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String tabla = (String) cbFiltro.getSelectedItem();
        String q = tfSearch.getText().trim();
        if(q.isEmpty()){
            cargarTabla("SELECT * FROM " + tabla);
            return;
        }
        q = "%" + q.replace(" ", "%") + "%";
        String dat1 = "", dat2 = "";
        switch (tabla) {
            case "Clientes":
                dat1 = "nombre";
                dat2 = "apePat";
                break;
            case "Productos":
                dat1 = "nombre";
                dat2 = "descripcion";
                break;
            case "Ventas":
            case "Pedidos":
                dat1 = "fecha";
                dat2 = "ID_cliente";
                break;
            case "Ventas_Diarias":
                dat1 = "fecha";
                dat2 = "totalVentas";
                break;
            case "Detalles_Pedidos":
                dat1 = dat2 = "ID_producto";
                break;
            case "Facturas":
                dat1 = "metodoPago";
                dat2 = "ID_pedido";
                break;
        }
        String sql = "SELECT * FROM "+ tabla +" WHERE + "+ dat1 +" LIKE ? OR "+ dat2 +" LIKE ?";
        cargarTabla(sql, q);
    }

    // Formularios

    private void agregarCliente() {
        Connection cn = conexionBD.connectAdmin();
        JTextField tfNombre = new JTextField();
        JTextField tfApePat = new JTextField();
        JTextField tfApeMat = new JTextField();
        String[] opcionesSexo = {"M", "F"};
        JComboBox<String> cbSexo = new JComboBox<>(opcionesSexo);
        JTextField tfFechaNac = new JTextField("AAAA-MM-DD");
        JTextField tfTelefono = new JTextField();

        // Crear el panel del formulario
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Nombre(s):"));
        panel.add(tfNombre);
        panel.add(new JLabel("Apellido Paterno:"));
        panel.add(tfApePat);
        panel.add(new JLabel("Apellido Materno:"));
        panel.add(tfApeMat);
        panel.add(new JLabel("Sexo:"));
        panel.add(cbSexo);
        panel.add(new JLabel("Fecha de Nacimiento:"));
        panel.add(tfFechaNac);
        panel.add(new JLabel("Teléfono:"));
        panel.add(tfTelefono);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Registrar nuevo cliente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            // Validar campos
            if (tfNombre.getText().trim().isEmpty() || tfApePat.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, rellene los campos obligatorios.");
                return;
            }

            String sql = "INSERT INTO clientes (nombre, apePat, apeMat, sexo, fechaNac, telefono) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, tfNombre.getText().trim());
                ps.setString(2, tfApePat.getText().trim());
                ps.setString(3, tfApeMat.getText().trim());
                ps.setString(4, cbSexo.getSelectedItem().toString());
                ps.setString(5, tfFechaNac.getText().trim());
                ps.setString(6, tfTelefono.getText().trim());
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Cliente agregado correctamente.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al insertar: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Operación cancelada.");
        }
    }

    private static void agregarProducto() {
        // Crear los campos de entrada
        JTextField nombreField = new JTextField();
        JTextArea descripcionArea = new JTextArea(3, 15);
        JTextField precioField = new JTextField();
        JTextField stockField = new JTextField();

        // Configurar el área de descripción con scroll
        JScrollPane scrollDescripcion = new JScrollPane(descripcionArea);
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);

        // Crear el panel del formulario
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panel.add(nombreField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panel.add(scrollDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        panel.add(precioField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        panel.add(stockField, gbc);

        // Mostrar el formulario dentro de un JOptionPane
        int resultado = JOptionPane.showConfirmDialog(
                null, panel,
                "Agregar Producto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText().trim();
                String descripcion = descripcionArea.getText().trim();
                double precio = Double.parseDouble(precioField.getText());
                int stock = Integer.parseInt(stockField.getText());

                String sql = "INSERT INTO productos (nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?)";
                Connection cn = conexionBD.connectAdmin();
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, descripcion);
                    ps.setDouble(3, precio);
                    ps.setInt(4, stock);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Cliente agregado correctamente.");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Error al insertar: " + e.getMessage());
                }

                JOptionPane.showMessageDialog(null,
                        "Producto agregado con éxito:\n" + nombre);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Error: El precio o stock no son válidos",
                        "Error de formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void actualizarProducto() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un registro");
            return;
        }
        int ID_producto = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
        // Crear los campos de entrada
        JTextField nombreField = new JTextField();
        JTextArea descripcionArea = new JTextArea(3, 15);
        JTextField precioField = new JTextField();
        JTextField stockField = new JTextField();
        nombreField.setPreferredSize(new Dimension(200, 25));
        precioField.setPreferredSize(new Dimension(100, 25));
        stockField.setPreferredSize(new Dimension(100, 25));

        // Configurar el área de descripción con scroll
        JScrollPane scrollDescripcion = new JScrollPane(descripcionArea);
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);

        // Crear el panel del formulario
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panel.add(nombreField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panel.add(scrollDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        panel.add(precioField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        panel.add(stockField, gbc);

        //agregar texto
        nombreField.setText(table.getValueAt(table.getSelectedRow(), 1).toString());
        descripcionArea.setText(table.getValueAt(table.getSelectedRow(), 2).toString());
        precioField.setText(table.getValueAt(table.getSelectedRow(), 3).toString());
        stockField.setText(table.getValueAt(table.getSelectedRow(), 4).toString());

        panel.setPreferredSize(new Dimension(400, panel.getPreferredSize().height));

        // Mostrar el formulario dentro de un JOptionPane
        int resultado = JOptionPane.showConfirmDialog(
                null, panel,
                "Actualizar Producto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText().trim();
                String descripcion = descripcionArea.getText().trim();
                double precio = Double.parseDouble(precioField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());

                String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ? WHERE ID_producto = ?";
                Connection cn = conexionBD.connectAdmin();
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, descripcion);
                    ps.setDouble(3, precio);
                    ps.setInt(4, stock);
                    ps.setInt(5, ID_producto);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Cliente agregado correctamente.");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Error al insertar: " + e.getMessage());
                }

                JOptionPane.showMessageDialog(null,
                        "Producto actualizado con éxito:\n" + nombre);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Error: El precio o stock no son válidos",
                        "Error de formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void eliminarProducto() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un registro");
            return;
        }
        int ID_producto = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
        String nombre = table.getValueAt(table.getSelectedRow(), 1).toString();
        if (JOptionPane.showConfirmDialog(null, "Seguro que desea eliminar el Producto: " + nombre + "?", "Mensaje de Confirmacion",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            String sql = "DELETE FROM productos WHERE ID_producto = ?";
            Connection cn = conexionBD.connectAdmin();
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, ID_producto);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Cliente agregado correctamente.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al insertar: " + e.getMessage());
            }

            JOptionPane.showMessageDialog(null,
                    "Producto eliminado con éxito:\n" + nombre);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Error: El precio o stock no son válidos",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    // metricas es para que el usuario vea un resumen rapido y/o estadisticas etc toiled
    /*
    private void updateStats() {
        long total = data.size();
        long admins = data.stream().filter(u -> u.role==Role.ADMIN).count();
        long employees = data.stream().filter(u -> u.role==Role.EMPLOYEE).count();
        long active = data.stream().filter(u -> u.status==Status.ACTIVE).count();
        long inactive = total - active;

        scTotal.setValue(total);
        scAdmins.setValue(admins);
        scEmployees.setValue(employees);
        scActive.setValue(active);
        scInactive.setValue(inactive);
    }
    */
    // "botones de eliminar editarIU"
    private static JButton primary(String text){
        JButton b = new JButton(text);
        b.setBackground(ACCENT); b.setForeground(Color.black);
        b.setBorder(new EmptyBorder(10,16,10,16));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private static JButton neutral(String text){
        JButton b = new JButton(text);
        b.setBackground(new Color(0xE5E7EB));

        b.setBorder(new EmptyBorder(10,16,10,16));
        b.setFocusPainted(false);
        return b;
    }
    private static JButton danger(String text){
        JButton b = new JButton(text);
        b.setBackground(new Color(0xEF4444));
        b.setForeground(Color.black);
        b.setBorder(new EmptyBorder(10,16,10,16));
        b.setFocusPainted(false);
        return b;
    }
    private static JButton toolButton(String text){
        JButton b = new JButton(text);
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(6,10,6,10));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private void toast(String msg){ JOptionPane.showMessageDialog(this, msg); }

    //Eventos
    private static class ChipRenderer extends DefaultTableCellRenderer {
        private final Color bg; private final Color fg;
        ChipRenderer(Color bg, Color fg){ this.bg=bg; this.fg=fg; setHorizontalAlignment(SwingConstants.CENTER); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c){
            JLabel lbl=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);
            lbl.setOpaque(true); lbl.setBackground(bg); lbl.setForeground(fg);
            lbl.setBorder(new EmptyBorder(4,10,4,10));
            return lbl;
        }
    }
    private static class StatusChipRenderer extends ChipRenderer {
        StatusChipRenderer(){ super(new Color(0xDCFCE7), new Color(0x166534)); }
        @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
            JLabel lbl=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);
            String val = String.valueOf(v);
            if ("INACTIVO".equalsIgnoreCase(val)) {
                lbl.setBackground(new Color(0xFEF3C7));
                lbl.setForeground(new Color(0x92400E));
            }
            return lbl;
        }
    }


    private static class RoundedPanel extends JPanel {
        private final int radius; private final Color bg;
        RoundedPanel(int radius, Color bg){ this.radius=radius; this.bg=bg; setOpaque(false); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(), radius, radius);
            g2.dispose(); super.paintComponent(g);
        }
    }
    private static class StatCard extends RoundedPanel {
        private final JLabel value = new JLabel("0");
        StatCard(String title, String val, Color accent){
            super(16, CARD);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(16,16,16,16));
            JLabel t = new JLabel(title);
            t.setForeground(new Color(0x0F172A));
            t.setFont(t.getFont().deriveFont(Font.PLAIN, 13f));
            value.setText(val);
            value.setFont(value.getFont().deriveFont(Font.BOLD, 22f));
            value.setForeground(accent);
            add(t, BorderLayout.NORTH);
            add(value, BorderLayout.CENTER);
        }
        void setValue(long v){ value.setText(String.valueOf(v)); }
    }




    private static class SimpleChange implements javax.swing.event.DocumentListener {
        private final Runnable r;
        SimpleChange(Runnable r){ this.r=r; }
        public void insertUpdate(javax.swing.event.DocumentEvent e){ r.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e){ r.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e){ r.run(); }
    }}


