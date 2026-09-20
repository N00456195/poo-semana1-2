package pageturner.servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pageturner.modelo.Cliente;
import pageturner.modelo.Libro;
import pageturner.modelo.Reserva;
import pageturner.modelo.Venta;

/**
 * Clase gestora del sistema PageTurner. Mantiene las colecciones de objetos
 * del dominio y centraliza las operaciones de registro y consulta (RF01 - RF10).
 * El asistente opera el sistema a traves de esta clase, pero no se modela
 * como entidad del dominio.
 */
public class Libreria {

    private final List<Libro> libros = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Venta> ventas = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();

    // Contadores para generar identificadores automaticos
    private int contadorLibros = 1;
    private int contadorClientes = 1;
    private int contadorVentas = 1;
    private int contadorReservas = 1;

    // ------------------------------------------------------------------
    // RF01 - Registrar libros
    // ------------------------------------------------------------------
    public Libro registrarLibro(String titulo, String autor, String isbn, double precio, int stock) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título del libro es obligatorio.");
        }
        if (buscarLibroPorIsbn(isbn) != null) {
            throw new IllegalArgumentException("Ya existe un libro registrado con el ISBN " + isbn + ".");
        }
        Libro libro = new Libro(contadorLibros++, titulo, autor, isbn, precio, stock);
        libros.add(libro);
        return libro;
    }

    // ------------------------------------------------------------------
    // RF02 - Registrar clientes
    // ------------------------------------------------------------------
    public Cliente registrarCliente(String nombre, String dni, String correo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (buscarClientePorDni(dni) != null) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con el DNI " + dni + ".");
        }
        Cliente cliente = new Cliente(contadorClientes++, nombre, dni, correo);
        cliente.registrar();
        clientes.add(cliente);
        return cliente;
    }

    // ------------------------------------------------------------------
    // RF03, RF04 y RF10 - Registrar venta, descontar stock y validar disponibilidad
    // ------------------------------------------------------------------
    public Venta registrarVenta(int idCliente, int idLibro, int cantidad) {
        Cliente cliente = buscarClientePorId(idCliente);
        Libro libro = buscarLibroPorId(idLibro);

        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con el id " + idCliente + ".");
        }
        if (libro == null) {
            throw new IllegalArgumentException("No existe un libro con el id " + idLibro + ".");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        // RF10: se valida ANTES de crear la venta; el stock nunca queda negativo.
        if (!libro.disminuirStock(cantidad)) {
            throw new IllegalArgumentException("Stock insuficiente para \"" + libro.getTitulo()
                    + "\". Disponible: " + libro.getStock() + ", solicitado: " + cantidad + ".");
        }
        Venta venta = new Venta(contadorVentas++, cliente, libro, cantidad, LocalDate.now());
        ventas.add(venta);
        return venta;
    }

    // ------------------------------------------------------------------
    // RF05 y RF06 - Registrar reserva de un libro sin stock
    // ------------------------------------------------------------------
    public Reserva registrarReserva(int idCliente, int idLibro) {
        Cliente cliente = buscarClientePorId(idCliente);
        Libro libro = buscarLibroPorId(idLibro);

        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con el id " + idCliente + ".");
        }
        if (libro == null) {
            throw new IllegalArgumentException("No existe un libro con el id " + idLibro + ".");
        }
        if (libro.estaDisponible()) {
            throw new IllegalArgumentException("El libro \"" + libro.getTitulo()
                    + "\" tiene stock disponible (" + libro.getStock() + "); corresponde registrar una venta.");
        }
        Reserva reserva = new Reserva(contadorReservas++, cliente, libro, LocalDate.now());
        reservas.add(reserva);
        return reserva;
    }

    // ------------------------------------------------------------------
    // RF07 - Unidades vendidas de un libro
    // ------------------------------------------------------------------
    public int unidadesVendidas(Libro libro) {
        int total = 0;
        for (Venta venta : ventas) {
            if (venta.getLibro().getId() == libro.getId()) {
                total += venta.getCantidad();
            }
        }
        return total;
    }

    // ------------------------------------------------------------------
    // RF08 - Ingresos generados por un libro
    // ------------------------------------------------------------------
    public double ingresosPorLibro(Libro libro) {
        double total = 0;
        for (Venta venta : ventas) {
            if (venta.getLibro().getId() == libro.getId()) {
                total += venta.getTotal();
            }
        }
        return total;
    }

    /** Ingresos totales de la libreria. */
    public double ingresosTotales() {
        double total = 0;
        for (Venta venta : ventas) {
            total += venta.getTotal();
        }
        return total;
    }

    // ------------------------------------------------------------------
    // RF09 - Reservas registradas para un libro
    // ------------------------------------------------------------------
    public List<Reserva> reservasDeLibro(Libro libro) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getLibro().getId() == libro.getId()) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    public List<Reserva> reservasDeCliente(Cliente cliente) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getCliente().getId() == cliente.getId()) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    // ------------------------------------------------------------------
    // Busquedas auxiliares
    // ------------------------------------------------------------------
    public Libro buscarLibroPorId(int id) {
        for (Libro libro : libros) {
            if (libro.getId() == id) {
                return libro;
            }
        }
        return null;
    }

    public Libro buscarLibroPorIsbn(String isbn) {
        for (Libro libro : libros) {
            if (libro.getIsbn() != null && libro.getIsbn().equalsIgnoreCase(isbn)) {
                return libro;
            }
        }
        return null;
    }

    public Cliente buscarClientePorId(int id) {
        for (Cliente cliente : clientes) {
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        return null;
    }

    public Cliente buscarClientePorDni(String dni) {
        for (Cliente cliente : clientes) {
            if (cliente.getDni().equals(dni)) {
                return cliente;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Listados
    // ------------------------------------------------------------------
    public List<Libro> getLibros() {
        return new ArrayList<>(libros);
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes);
    }

    public List<Venta> getVentas() {
        return new ArrayList<>(ventas);
    }

    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }

    /** Carga informacion de ejemplo para poder probar el sistema rapidamente. */
    public void cargarDatosDemo() {
        registrarLibro("Introducción a los Algoritmos", "Cormen", "978-0262033848", 180.00, 5);
        registrarLibro("Clean Code", "Robert C. Martin", "978-0132350884", 145.50, 3);
        registrarLibro("Cálculo de una Variable", "James Stewart", "978-6075267456", 210.00, 0);

        registrarCliente("Ana Torres", "70123456", "ana.torres@upn.pe");
        registrarCliente("Luis Ramírez", "40876512", "luis.ramirez@upn.pe");
    }
}
