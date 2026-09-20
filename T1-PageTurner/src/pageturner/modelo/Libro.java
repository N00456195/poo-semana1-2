package pageturner.modelo;

/**
 * Representa un libro del catalogo de la libreria academica PageTurner.
 * Cubre los requerimientos RF01 (registro de libros), RF04 (actualizacion de stock)
 * y RF10 (validacion de disponibilidad).
 */
public class Libro {

    // --- Atributos (encapsulados: solo accesibles mediante metodos publicos) ---
    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private double precio;
    private int stock;

    // --- Constructores ---
    public Libro(int id, String titulo, String autor, String isbn, double precio, int stock) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio del libro no puede ser negativo.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock inicial no puede ser negativo.");
        }
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.precio = precio;
        this.stock = stock;
    }

    // --- Metodos del diagrama de clases ---

    /** Incrementa el stock disponible del libro (reposicion de inventario). */
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor que cero.");
        }
        this.stock += cantidad;
    }

    /**
     * Descuenta unidades del stock. RF10: si la cantidad solicitada supera
     * el stock disponible la operacion se rechaza y el stock nunca queda negativo.
     *
     * @return true si el descuento se realizo, false si no hay stock suficiente.
     */
    public boolean disminuirStock(int cantidad) {
        if (cantidad <= 0) {
            return false;
        }
        if (cantidad > this.stock) {
            return false;
        }
        this.stock -= cantidad;
        return true;
    }

    /** Indica si el libro tiene al menos una unidad disponible para la venta. */
    public boolean estaDisponible() {
        return this.stock > 0;
    }

    /** Devuelve una descripcion legible del libro. */
    public String getInformacion() {
        return String.format("[%d] %s - %s | ISBN: %s | S/ %.2f | Stock: %d",
                id, titulo, autor, isbn, precio, stock);
    }

    // --- Getters y setters ---
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio del libro no puede ser negativo.");
        }
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {
        return getInformacion();
    }
}
