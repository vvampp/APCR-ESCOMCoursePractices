/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package escom.carritocomprasservidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author braul
 */
public class servidor {
    public static void main(String args[]){
        Producto[] producto = new Producto[5];
        
        producto[0] = new Producto("Laptop", 10, 1500.99, 1.5f, 'A');
        producto[1] = new Producto("Bocina", 0, 399.99, 5f, 'B');
        producto[2] = new Producto("Telefono", 6, 699.99, .2f, 'B');
        producto[3] = new Producto("Television", 0, 249.99, 1.5f, 'B');
        producto[4] = new Producto("Audifonos", 4, 99.99, .2f, 'C');
        
        /*
        for(int x=0;x<5;x++){
            System.out.println("\nProducto "+ (x+1) +":");
            System.out.println("Nombre: " + producto[x].getNombre());
            System.out.println("Cantidad: " + producto[x].getCantidad());
            System.out.println("Precio: " + producto[x].getPrecio());
            System.out.println("En Stock: " + producto[x].isEnStock());
            System.out.println("Peso: " + producto[x].getPeso());
            System.out.println("Categoría: " + producto[x].getCategoria());
            //System.out.println("\n");
        }
        */
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("productoAtributos.csv"))) {
            
            writer.write("id,Nombre,Cantidad,Precio,EnStock,Peso,Categoria");
            for (Producto produc : producto) {
                writer.newLine();
                writer.write(produc.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (FileOutputStream fileOut = new FileOutputStream("productoSerializado.csv");
            ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            // Escribir la cabecera del CSV
            out.writeObject("id,Nombre,Cantidad,Precio,EnStock,Peso,Categoria");
            for (Producto produc : producto) {
                out.writeObject(produc.toCSV());
            }
            System.out.println("Objeto serializado en productoSerializado.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try{
            ServerSocket s = new ServerSocket(6040);
            System.out.println("Esperando cliente ...");
            for(;;){
                
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde"+cl.getInetAddress()+":"+cl.getPort());
                
                try (
                    OutputStream outputStream = cl.getOutputStream();
                    FileInputStream fileInputStream = new FileInputStream("productoSerializado.csv")) {

                    // Enviar el archivo al servidor
                    byte[] buffer = new byte[1024]; // Tamaño del buffer
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead); // Enviar datos
                    }
                    System.out.println("Archivo enviado al Cliente.");
                    
                    
                cl.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                }//for
        }catch(Exception e){
            e.printStackTrace();
        }//catch
        
        /*
        try (FileInputStream fileInputStream = new FileInputStream("productoSerializado.csv");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("productoDeserializado.txt"))) {
                
                while (true) {
                    try {
                        Object objeto = objectInputStream.readObject();
                        if (objeto != null) {
                            String archDes;
                            archDes = objeto.toString();
                            System.out.println(archDes);
                            writer.write(archDes);
                            writer.write("\n");
                        }
                    } catch (EOFException e) {
                        // Se alcanza el final del archivo, se rompe el bucle
                        break;
                    }
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            System.out.println("Objeto deserializado correctamente.\n");
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al deserializar el objeto: " + e.getMessage());
        }
        */
        
    }//main
}
