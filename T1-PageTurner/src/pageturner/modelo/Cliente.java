package pageturner.modelo;

import java.time.LocalDate;

/**
 * Representa a un cliente de PageTurner (estudiante, docente o investigador).
 * Cubre el requerimiento RF02 (registro de clientes).
 */
public class Cliente {

    // --- Atributos ---
    private int id;
    private String nombre;
    private String dni;
    private String correo;
    private boolean registrado;
    private LocalDate fechaRegistro;

    // --- Constructor ---
    public Cliente(int id, String nombre, String dni, String correo) {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos.");
        }
        if (correo == null || !correo.contains("@")) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.correo = correo;
        this.registrado = false;
    }

    // --- Metodos del diagrama de clases ---

    /** Marca al cliente como registrado en el sistema y guarda la fecha de alta. */
    public void registrar() {
        this.registrado = true;
        this.fechaRegistro = LocalDate.now();
    }

    /** Actualiza los datos de contacto del cliente. */
    public void actualizar(String nuevoNombre, String nuevoCorreo) {
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            this.nombre = nuevoNombre;
        }
        if (nuevoCorreo != null && nuevoCorreo.contains("@")) {
            this.correo = nuevoCorreo;
        }
    }

    /** Devuelve los datos del cliente en formato legible. */
    public String obtenerDatos() {
        return String.format("[%d] %s | DNI: %s | %s | %s",
                id, nombre, dni, correo, registrado ? "registrado el " + fechaRegistro : "sin registrar");
    }

    // --- Getters y setters ---
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    public String getCorreo() {
        return correo;
    }

    public boolean estaRegistrado() {
        return registrado;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    @Override
    public String toString() {
        return obtenerDatos();
    }
}
