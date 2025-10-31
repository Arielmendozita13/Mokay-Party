# 🎁 Tienda de Regalos - Sistema Automatizado

Este proyecto es una **aplicación de escritorio en Java (Swing)** que gestiona una tienda de regalos con conexión a base de datos **MySQL**, permitiendo automatizar los procesos de **ventas, control de productos, usuarios y administración general**.

---

## 🧩 Descripción General

La aplicación cuenta con una **interfaz gráfica intuitiva**, diseñada en **IntelliJ IDEA / NetBeans**, que facilita la interacción con los datos almacenados en la base de datos.  
Su objetivo principal es **automatizar las tareas repetitivas** y mantener un control eficiente del inventario y de los movimientos de venta en la tienda.

---

## ⚙️ Funcionalidades Principales

- 🛍️ **Gestión de productos:**  
  Agregar, editar, eliminar y visualizar los productos disponibles en la tienda (por ejemplo, peluches, flores, tazas, etc.).

- 👤 **Control de usuarios:**  
  Inicio de sesión con distintos roles:
  - **Administrador:** controla el inventario y usuarios.  
  - **Cajero:** realiza ventas y genera notas automáticas.

- 💾 **Conexión a base de datos MySQL:**  
  Se establece mediante el conector `mysql-connector-java`, utilizando credenciales configuradas en la clase `conexionBD.java`.

- 🧮 **Registro automatizado:**  
  Cada venta genera automáticamente una **nota de venta** con detalles del producto, cantidad, total y fecha.

- 🧰 **Interfaz modular:**  
  Separación por paquetes:
  - `control` → conexión y lógica de la base de datos  
  - `Interfaces` → ventanas y menús de usuario  
  - `driver_conector` → librerías del conector JDBC

---

## 🗃️ Estructura de la Base de Datos

**Nombre de la BD:** `bd_moykayparty`  
Tablas principales:
- `usuarios`  
- `productos`  
- `ventas`  
- `detalles_venta`

Ejemplo de conexión en `conexionBD.java`:
```java
private static String url = "jdbc:mysql://localhost:3306/bd_moykayparty";
private static String userAdmin = "admin";
private static String passAdmin = "moyka0314";
