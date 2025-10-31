package control;

import java.sql.*;

public class conexionBD {

    private static String url = "jdbc:mysql://localhost:3306/bd_moykayparty";

    private static String userAdmin = "admin";
    private static String passAdmin = "moyka0314";

    private static String userCajero = "cajero";
    private static String passCajero = "tienda123";

    public static Connection connectAdmin() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(url, userAdmin, passAdmin);
            System.out.println("Conexion Admin establecida!");
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos!");
            e.printStackTrace();
        }
        return conexion;
    }

    public static Connection connectCajero() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(url, userCajero, passCajero);
            System.out.println("Conexion Cajero establecida!");
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos!");
            e.printStackTrace();
        }
        return conexion;
    }

    public static void disconnect(Connection conexion) {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexion cerrada!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
