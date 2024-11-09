/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    // Constructor vacío
    public Producto() {}

    // Constructor con parámetros
    public Producto(String nombre, int cantidad, double precio, float peso, char categoria) {
        this.id = ++contadorID; // Incrementar y asignar
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

    public void setid(int id) {
        this.id = id;
    }
    
    public int getid() {
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
        return id + "," + nombre + "," + cantidad + "," + precio + "," + enStock + "," + peso + "," + categoria;
    }
    
    public String toCSV() {
        return id + "," + nombre + "," + cantidad + "," + precio + "," + enStock + "," + peso + "," + categoria;
    }
    
    public static Producto fromCSV(String csvLine) {
        String[] atributos = csvLine.split(",");
        
        int id = Integer.parseInt(atributos[0]);
        String nombre = atributos[1];         // String
        int cantidad = Integer.parseInt(atributos[2]);          // int
        double precio = Double.parseDouble(atributos[3]);        // double
        //boolean enStock = Boolean.parseBoolean(atributos[3]);       // boolean
        float peso = Float.parseFloat(atributos[5]);            // float
        char categoria = atributos[6].charAt(0);;        // char
        
        return new Producto(nombre, cantidad, precio, peso, categoria);
    }
}


