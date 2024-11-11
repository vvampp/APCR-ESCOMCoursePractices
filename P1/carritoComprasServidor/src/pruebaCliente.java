/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.*;
import java.io.*;

/**
 *
 * @author braul
 */

public class pruebaCliente {
    public static void main(String args[]){

        final String host = "127.0.0.1";
        final int pto = 6040;

        // Crea la carpeta
        File carpeta = new File("Cliente");
        if (!carpeta.exists()) {
            boolean creada = carpeta.mkdirs(); // mkdirs() crea la carpeta y cualquier carpeta padre necesaria
            if (creada) {
                System.out.println("Carpeta creada: " + carpeta.getAbsolutePath());
            } else {
                System.out.println("Error al crear la carpeta.");
                return;
            }
        } else {
            System.out.println("La carpeta ya existe: " + carpeta.getAbsolutePath());
        }

        try{
            Socket cl = new Socket(host,pto);
            for(;;){
                InputStream inputStream = cl.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream("Cliente/productoSerializadoCliente.csv");

                // Recibir el archivo del cliente
                byte[] buffer = new byte[1024]; // Tamaño del buffer
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead); // Guardar datos en el archivo
                }
                System.out.println("Archivo recibido y guardado como productoSerializadoCliente.csv");


                try (FileInputStream fileInputStream = new FileInputStream("Cliente/productoSerializadoCliente.csv");
                     ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("Cliente/productoDeserializadoCliente.csv"))) {

                        while (true) {
                            try {
                                Object objeto = objectInputStream.readObject();
                                if (objeto != null) {
                                    String archDes;
                                    archDes = objeto.toString();
                                    System.out.println(archDes);
                                    writer.write(archDes);
                                    writer.newLine();
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
                cl.close();


            }//for
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}