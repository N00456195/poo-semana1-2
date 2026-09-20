package pageturner.modelo;

import java.time.LocalDate;

/**
 * Representa la reserva de un libro sin stock disponible.
 * Relaciona 1 Cliente con 1 Libro y registra la fecha de la solicitud.
 * Cubre los requerimientos RF05, RF06 y RF09.
 */
public class Reserva {

    // --- Estados posibles de una reserva ---
    public static final String ACTIVA = "ACTIVA";
    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String CANCELADA = "CANCELADA";

    // --- Atributos ---
    private int id;
    private LocalDate fecha;
    private String estado;

    // --- Asociaciones (multiplicidad 1 hacia Cliente y 1 hacia Libro) ---
    private Cliente cliente;
    private Libro libro;

    // --- Constructor ---
    public Reserva(int id, Cliente cliente, Libro libro, LocalDate fecha) {
        if (cliente == null || libro == null) {
            throw new IllegalArgumentException("Una reserva debe estar asociada a un cliente y a un libro.");
        }
        this.id = id;
        this.cliente = cliente;
        this.libro = libro;
        this.fecha = (fecha == null) ? LocalDate.now() : fecha;
        this.estado = ACTIVA;
    }

    // --- Metodos del diagrama de clases ---

    /** Confirma la reserva cuando el libro vuelve a tener stock. */
    public void confirmar() {
        if (CANCELADA.equals(estado)) {
            throw new IllegalStateException("No se puede confirmar una reserva cancelada.");
        }
        this.estado = CONFIRMADA;
    }

    /** Cancela la reserva a solicitud del cliente. */
    public void cancelar() {
        if (CONFIRMADA.equals(estado)) {
            throw new IllegalStateException("No se puede cancelar una reserva ya confirmada.");
        }
        this.estado = CANCELADA;
    }

    /** Devuelve el detalle de la reserva en formato legible. */
    public String getInformacion() {
        return String.format("Reserva #%d | %s | %s | Cliente: %s | Estado: %s",
                id, fecha, libro.getTitulo(), cliente.getNombre(), estado);
    }

    /** Indica si la reserva sigue pendiente de atencion. */
    public boolean estaActiva() {
        return ACTIVA.equals(estado);
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
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
