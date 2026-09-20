package pageturner;

import java.util.List;
import java.util.Scanner;

import pageturner.modelo.Cliente;
import pageturner.modelo.Libro;
import pageturner.modelo.Reserva;
import pageturner.modelo.Venta;
import pageturner.servicio.Libreria;

/**
 * Punto de entrada del sistema PageTurner.
 * Presenta un menú de consola operado por el asistente de la librería (RNF01).
 *
 * Ejecución:
 *   java -cp bin pageturner.Main           -> menú interactivo
 *   java -cp bin pageturner.Main --demo    -> demostración automática del flujo completo
 */
public class Main {

    private static final Libreria libreria = new Libreria();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--demo")) {
            ejecutarDemo();
            return;
        }

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String opcion = sc.nextLine().trim();
            System.out.println();
            try {
                switch (opcion) {
                    case "1": registrarLibro(); break;
                    case "2": registrarCliente(); break;
                    case "3": registrarVenta(); break;
                    case "4": registrarReserva(); break;
                    case "5": listarLibros(); break;
                    case "6": listarClientes(); break;
                    case "7": reporteVentas(); break;
                    case "8": consultarReservas(); break;
                    case "9":
                        libreria.cargarDatosDemo();
                        System.out.println("Datos de ejemplo cargados correctamente.");
                        break;
                    case "0":
                        salir = true;
                        System.out.println("Sistema finalizado. ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
            System.out.println();
        }
    }

    // ------------------------------------------------------------------
    // Menú
    // ------------------------------------------------------------------
    private static void mostrarMenu() {
        System.out.println("==================================================");
        System.out.println("      LIBRERÍA ACADÉMICA PAGETURNER");
        System.out.println("==================================================");
        System.out.println(" 1. Registrar libro");
        System.out.println(" 2. Registrar cliente");
        System.out.println(" 3. Registrar venta");
        System.out.println(" 4. Registrar reserva");
        System.out.println(" 5. Listar libros");
        System.out.println(" 6. Listar clientes");
        System.out.println(" 7. Reporte de ventas e ingresos por libro");
        System.out.println(" 8. Consultar reservas de un libro");
        System.out.println(" 9. Cargar datos de ejemplo");
        System.out.println(" 0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    // ------------------------------------------------------------------
    // Opciones del menú
    // ------------------------------------------------------------------
    private static void registrarLibro() {
        String titulo = leerTexto("Título: ");
        String autor = leerTexto("Autor: ");
        String isbn = leerTexto("ISBN: ");
        double precio = leerDecimal("Precio (S/): ");
        int stock = leerEntero("Stock inicial: ");

        Libro libro = libreria.registrarLibro(titulo, autor, isbn, precio, stock);
        System.out.println("Libro registrado -> " + libro.getInformacion());
    }

    private static void registrarCliente() {
        String nombre = leerTexto("Nombre: ");
        String dni = leerTexto("DNI (8 dígitos): ");
        String correo = leerTexto("Correo: ");

        Cliente cliente = libreria.registrarCliente(nombre, dni, correo);
        System.out.println("Cliente registrado -> " + cliente.obtenerDatos());
    }

    private static void registrarVenta() {
        if (!hayDatos()) {
            return;
        }
        listarClientes();
        int idCliente = leerEntero("Id del cliente: ");
        listarLibros();
        int idLibro = leerEntero("Id del libro: ");
        int cantidad = leerEntero("Cantidad: ");

        Venta venta = libreria.registrarVenta(idCliente, idLibro, cantidad);
        System.out.println("Venta registrada -> " + venta.getInformacion());
        System.out.println("Stock actualizado -> " + venta.getLibro().getInformacion());
    }

    private static void registrarReserva() {
        if (!hayDatos()) {
            return;
        }
        listarClientes();
        int idCliente = leerEntero("Id del cliente: ");
        listarLibros();
        int idLibro = leerEntero("Id del libro: ");

        Reserva reserva = libreria.registrarReserva(idCliente, idLibro);
        System.out.println("Reserva registrada -> " + reserva.getInformacion());
    }

    private static void listarLibros() {
        List<Libro> libros = libreria.getLibros();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        System.out.println("--- Catálogo de libros ---");
        for (Libro libro : libros) {
            System.out.println("  " + libro.getInformacion());
        }
    }

    private static void listarClientes() {
        List<Cliente> clientes = libreria.getClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("--- Clientes registrados ---");
        for (Cliente cliente : clientes) {
            System.out.println("  " + cliente.obtenerDatos());
        }
    }

    private static void reporteVentas() {
        List<Libro> libros = libreria.getLibros();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        System.out.println("--- Ventas e ingresos por libro ---");
        System.out.printf("%-38s %10s %14s%n", "LIBRO", "UNIDADES", "INGRESOS");
        for (Libro libro : libros) {
            System.out.printf("%-38s %10d %14s%n",
                    recortar(libro.getTitulo(), 38),
                    libreria.unidadesVendidas(libro),
                    String.format("S/ %.2f", libreria.ingresosPorLibro(libro)));
        }
        System.out.printf("%nIngresos totales: S/ %.2f%n", libreria.ingresosTotales());
    }

    private static void consultarReservas() {
        if (libreria.getLibros().isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        listarLibros();
        int idLibro = leerEntero("Id del libro: ");
        Libro libro = libreria.buscarLibroPorId(idLibro);
        if (libro == null) {
            System.out.println("ERROR: no existe un libro con ese id.");
            return;
        }
        List<Reserva> reservas = libreria.reservasDeLibro(libro);
        if (reservas.isEmpty()) {
            System.out.println("El libro \"" + libro.getTitulo() + "\" no tiene reservas registradas.");
            return;
        }
        System.out.println("--- Reservas de \"" + libro.getTitulo() + "\" ---");
        for (Reserva reserva : reservas) {
            System.out.println("  " + reserva.getInformacion());
        }
    }

    // ------------------------------------------------------------------
    // Demostración automática (permite validar el modelo sin interacción)
    // ------------------------------------------------------------------
    private static void ejecutarDemo() {
        System.out.println("===== DEMOSTRACIÓN DEL SISTEMA PAGETURNER =====\n");
        libreria.cargarDatosDemo();

        System.out.println("1) Catálogo y clientes iniciales");
        listarLibros();
        listarClientes();

        System.out.println("\n2) HU01: venta con stock disponible (RF03 + RF04)");
        Venta v1 = libreria.registrarVenta(1, 1, 2);
        System.out.println("  " + v1.getInformacion());
        System.out.println("  " + v1.getLibro().getInformacion());

        Venta v2 = libreria.registrarVenta(2, 2, 1);
        System.out.println("  " + v2.getInformacion());

        System.out.println("\n3) RF10: venta rechazada por stock insuficiente");
        try {
            libreria.registrarVenta(1, 2, 99);
        } catch (IllegalArgumentException e) {
            System.out.println("  Rechazada correctamente -> " + e.getMessage());
            System.out.println("  " + libreria.buscarLibroPorId(2).getInformacion() + "  (stock intacto)");
        }

        System.out.println("\n4) HU02: reserva de un libro sin stock (RF05 + RF06)");
        Reserva r1 = libreria.registrarReserva(1, 3);
        Reserva r2 = libreria.registrarReserva(2, 3);
        System.out.println("  " + r1.getInformacion());
        System.out.println("  " + r2.getInformacion());

        System.out.println("\n5) RF09: reservas registradas del libro 3");
        for (Reserva reserva : libreria.reservasDeLibro(libreria.buscarLibroPorId(3))) {
            System.out.println("  " + reserva.getInformacion());
        }

        System.out.println("\n6) Reposición de stock y confirmación de la reserva");
        libreria.buscarLibroPorId(3).aumentarStock(4);
        r1.confirmar();
        System.out.println("  " + libreria.buscarLibroPorId(3).getInformacion());
        System.out.println("  " + r1.getInformacion());

        System.out.println("\n7) HU03: reporte de ventas e ingresos (RF07 + RF08)");
        reporteVentas();

        System.out.println("\n===== FIN DE LA DEMOSTRACIÓN =====");
    }

    // ------------------------------------------------------------------
    // Utilidades de entrada
    // ------------------------------------------------------------------
    private static boolean hayDatos() {
        if (libreria.getClientes().isEmpty() || libreria.getLibros().isEmpty()) {
            System.out.println("Debe registrar al menos un cliente y un libro (opción 9 carga datos de ejemplo).");
            return false;
        }
        return true;
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(sc.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    private static String recortar(String texto, int largo) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= largo ? texto : texto.substring(0, largo - 3) + "...";
    }
}
