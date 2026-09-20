package pageturner.modelo;

import java.time.LocalDate;

/**
 * Representa una venta: relaciona 1 Cliente con 1 Libro en una fecha determinada.
 * Cubre los requerimientos RF03 (registro de ventas) y RF08 (calculo de ingresos).
 */
public class Venta {

    // --- Atributos ---
    private int id;
    private LocalDate fecha;
    private int cantidad;
    private double total;

    // --- Asociaciones (multiplicidad 1 hacia Cliente y 1 hacia Libro) ---
    private Cliente cliente;
    private Libro libro;

    // --- Constructor ---
    public Venta(int id, Cliente cliente, Libro libro, int cantidad, LocalDate fecha) {
        if (cliente == null || libro == null) {
            throw new IllegalArgumentException("Una venta debe estar asociada a un cliente y a un libro.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad vendida debe ser mayor que cero.");
        }
        this.id = id;
        this.cliente = cliente;
        this.libro = libro;
        this.cantidad = cantidad;
        this.fecha = (fecha == null) ? LocalDate.now() : fecha;
        this.total = libro.getPrecio() * cantidad;
    }

    // --- Metodos del diagrama de clases ---

    /** Calcula el importe de la venta: precio del libro por cantidad comprada. */
    public double calcularTotal() {
        this.total = libro.getPrecio() * cantidad;
        return this.total;
    }

    /** Devuelve el detalle de la venta en formato legible. */
    public String getInformacion() {
        return String.format("Venta #%d | %s | %s | Cliente: %s | Cant: %d | Total: S/ %.2f",
                id, fecha, libro.getTitulo(), cliente.getNombre(), cantidad, total);
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getTotal() {
        return total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Libro getLibro() {
        return libro;
    }

    @Override
    public String toString() {
        return getInformacion();
    }
}
