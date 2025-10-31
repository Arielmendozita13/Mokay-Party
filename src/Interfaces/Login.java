package Interfaces;

import control.conexionBD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login extends JFrame implements ActionListener {

    private JPanel panelFondo;

    private JButton butIng;
    private JButton butSalir;
    private JButton butBack;
    private JButton butVisible;

    private JLabel fondoIMG;
    private JLabel labTitu;
    private JLabel labText1;
    private JLabel labText2;
    private JLabel labText3;
    private JLabel labText4;

    private JTextField txtUser;
    private JPasswordField txtPass;

    private boolean estVis = false;
    private ImageIcon img;
    private Image imgScal;

    private Connection conexion;

    public Login() {

        this.setLayout(null);
        this.setBounds(0, 0, 500, 650);
        this.setResizable(false);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);

        this.panelFondo = new JPanel();
        this.panelFondo.setBounds(0, 0, 500, 650);
        this.panelFondo.setBackground(new Color(255, 255, 255, 255));
        this.panelFondo.setLayout(null);
        this.add(this.panelFondo);

        this.fondoIMG = new JLabel();
        this.fondoIMG.setBounds(0, 0, 500, 650);
        this.fondoIMG.setOpaque(true);
        this.fondoIMG.setLayout(null);
        img = new ImageIcon(getClass().getResource("/resources/wallpaper/fondo_registro.png"));
        imgScal = img.getImage().getScaledInstance(500, 650, Image.SCALE_SMOOTH);
        fondoIMG.setIcon(new ImageIcon(imgScal));
        panelFondo.add(fondoIMG);

        butIng = new JButton("INICIAR SESIÓN");
        butIng.setBounds(100, 500, 300, 90);
        butIng.setBackground(new Color(17, 255, 130));
        butIng.setBorder(BorderFactory.createLineBorder(new Color(0, 176, 80), 10, true));
        butIng.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 34));
        butIng.setForeground(Color.WHITE);
        butIng.setFocusPainted(false);
        butIng.setOpaque(true);
        butIng.addActionListener(this);
        fondoIMG.add(butIng);

        butSalir = new JButton("SALIR");
        butSalir.setFocusPainted(false);
        butSalir.setOpaque(false);
        butSalir.addActionListener(this);
        fondoIMG.add(butSalir);

        butVisible = new JButton();
        butVisible.setFocusPainted(false);
        butVisible.setContentAreaFilled(false);
        butVisible.setBorderPainted(false);
        butVisible.setOpaque(false);
        butVisible.addActionListener(this);
        fondoIMG.add(butVisible);

        labTitu = new JLabel();
        labTitu.setHorizontalAlignment(SwingConstants.CENTER);
        labTitu.setVerticalAlignment(SwingConstants.CENTER);
        fondoIMG.add(labTitu);

        labText1 = new JLabel();
        labText1.setHorizontalAlignment(SwingConstants.CENTER);
        labText1.setVerticalAlignment(SwingConstants.CENTER);
        fondoIMG.add(labText1);

        labText2 = new JLabel();
        labText2.setHorizontalAlignment(SwingConstants.CENTER);
        labText2.setVerticalAlignment(SwingConstants.CENTER);
        fondoIMG.add(labText2);

        labText3 = new JLabel();
        labText3.setHorizontalAlignment(SwingConstants.CENTER);
        labText3.setVerticalAlignment(SwingConstants.CENTER);
        fondoIMG.add(labText3);

        labText4 = new JLabel();
        labText4.setHorizontalAlignment(SwingConstants.CENTER);
        labText4.setVerticalAlignment(SwingConstants.CENTER);
        fondoIMG.add(labText4);

        txtUser = new JTextField();
        fondoIMG.add(txtUser);

        txtPass = new JPasswordField();
        fondoIMG.add(txtPass);

        butBack = new JButton();
        butBack.setFocusPainted(false);
        butBack.setOpaque(false);
        fondoIMG.add(butBack);

        generarPanel("Inicio");

    }

    private void generarPanel(String panel) {
        if (panel.equals("Inicio")){

            img = new ImageIcon(getClass().getResource("/resources/wallpaper/fondo_registro.png"));
            imgScal = img.getImage().getScaledInstance(500, 650, Image.SCALE_SMOOTH);
            fondoIMG.setIcon(new ImageIcon(imgScal));

            labTitu.setText("<html><center>TIENDA DE REGALOS MOYKA PARTY</center></html>");
            labTitu.setBounds(50, 30, 400, 150);
            labTitu.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 40));
            labTitu.setForeground(Color.WHITE);

            butSalir.setBounds(438, 2, 60, 25);
            butSalir.setBackground(new Color(255, 255, 255,0));
            butSalir.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 10));
            butSalir.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4, true));
            butSalir.setForeground(Color.WHITE);

            labText1.setText("Por Favor ingrese Usuario y Contraseña");
            labText1.setBounds(50, 185, 400, 30);
            labText1.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4, true));
            labText1.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 16));
            labText1.setForeground(Color.WHITE);

            labText2.setText("USUARIO");
            labText2.setBounds(25, 280, 130, 40);
            labText2.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 23));
            labText2.setBorder(BorderFactory.createLineBorder(new Color(0, 112, 192), 8, true));
            labText2.setForeground(Color.WHITE);

            txtUser.setBounds(165, 280, 310, 40);
            txtUser.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 23));
            txtUser.setBorder(BorderFactory.createLineBorder(new Color(0, 112, 192), 8, true));
            txtUser.setForeground(Color.WHITE);
            txtUser.setOpaque(false);

            labText3.setText("CONTRASEÑA");
            labText3.setBounds(25, 370, 130, 40);
            labText3.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 15));
            labText3.setBorder(BorderFactory.createLineBorder(new Color(0, 176, 80), 8, true));
            labText3.setForeground(Color.WHITE);

            txtPass.setBounds(165, 370, 250, 40);
            txtPass.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 23));
            txtPass.setBorder(BorderFactory.createLineBorder(new Color(0, 165, 74), 8, true));
            txtPass.setForeground(Color.WHITE);
            txtPass.setOpaque(false);

            butVisible.setBounds(425, 370, 40, 40);
            img = new ImageIcon(getClass().getResource("/resources/images/invisible.png"));
            imgScal = img.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            butVisible.setIcon(new ImageIcon(imgScal));

            labText4.setBounds(50, 440, 400, 30);
            labText4.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 16));
            labText4.setForeground(Color.RED);

            butIng.setVisible(true);
            butSalir.setVisible(true);
            butBack.setVisible(false);
            butVisible.setVisible(true);
            labText4.setVisible(false);

        }
        panelFondo.revalidate();
        panelFondo.repaint();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == butSalir) {
            this.dispose();
        }
        if (e.getSource() == butIng) {
            if (txtUser.getText().equals("admin") && txtPass.getText().equals("moyka0314")) {
                conexion = conexionBD.connectAdmin();
                conexionBD.disconnect(conexion);
                CRUD_admin admin = new CRUD_admin();
                admin.setVisible(true);
                this.dispose();
            } else if (txtUser.getText().equals("cajero") && txtPass.getText().equals("tienda123")) {
                conexion = conexionBD.connectCajero();
                conexionBD.disconnect(conexion);
                CRUD_cajero c = new CRUD_cajero();
                c.setVisible(true);
                this.dispose();
            }   else {
                labText4.setText("El Usuario y/o Contraseña estan Mal");
                labText4.setVisible(true);
            }
        }
        if (e.getSource() == butVisible) {
            if (estVis) {
                txtPass.setEchoChar('•');
                img = new ImageIcon(getClass().getResource("/resources/images/invisible.png"));

                estVis = false;
            } else {
                txtPass.setEchoChar((char) 0);
                img = new ImageIcon(getClass().getResource("/resources/images/visible.png"));

                estVis = true;
            }
            imgScal = img.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            butVisible.setIcon(new ImageIcon(imgScal));
            txtPass.requestFocusInWindow();
        }
    }
}