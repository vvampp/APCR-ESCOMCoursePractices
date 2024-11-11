
package escom.carritocomprasservidor;

import java.io.Serializable;

/**
 *
 * @author braul
 */
public class Producto implements Serializable{

    // Atributos de la clase
    private int id;
    private String nombre;         // String
    private int cantidad;          // int
    private double precio;         // double
    private boolean enStock;       // boolean
    private float peso;            // float
    private char categoria;        // char

    private static int contadorID = 0;


    // Constructor con parámetros
    public Producto(int id, String nombre, int cantidad, double precio,boolean enStock, float peso, char categoria) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;

        if(cantidad==0){
            this.enStock = false;
        }
        else{
            this.enStock = true;
        }

        this.peso = peso;
        this.categoria = categoria;
    }

    // Métodos get y set para cada atributo

    public String getNombre() {
        return nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isEnStock() {
        return enStock;
    }

    public void setEnStock(boolean enStock) {
        this.enStock = enStock;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public char getCategoria() {
        return categoria;
    }

    public void setCategoria(char categoria) {
        this.categoria = categoria;
    }

    public String toString() {
        return id + "\t" + nombre + "\t\t" + cantidad + "\t" + precio + "\t" + enStock + "\t" + peso + "\t" + categoria;

    }

    public String fromCsv() {
        return id + "," + nombre + "," + cantidad + "," + precio + "," + enStock + "," + peso + "," + categoria;
    }
}
