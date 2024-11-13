//package escom.carritocomprasservidor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    public static void main(String args[]) {
        File folder = new File("resources");
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("La carpeta 'resources' no existe o no es un directorio.");
            return;
        }

        File[] photoFiles = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));

        if (photoFiles == null || photoFiles.length == 0) {
            System.out.println("No se encontraron imágenes en la carpeta 'resources'.");
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(6040)) {
            System.out.println("Esperando cliente ...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Conexión establecida desde " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                    // Enviar cada foto al cliente
                    for (File file : photoFiles) {
                        if (file.exists() && file.isFile()) {
                            try (FileInputStream fileIn = new FileInputStream(file)) {
                                output.writeUTF(file.getName());
                                output.writeLong(file.length());

                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = fileIn.read(buffer)) != -1) {
                                    output.write(buffer, 0, bytesRead);
                                }
                                output.flush();
                                System.out.println("Foto enviada: " + file.getName());
                            }
                        }
                    }

                    List<Producto> catalogo = cargarCatalogo();

                    // Enviar el catálogo completo al cliente
                    try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
                        out.writeObject(catalogo);
                        out.flush();
                    }

                    while (true) {
                        // Esperar a recibir el catálogo actualizado si el cliente hace una compra
                        try (Socket updateSocket = serverSocket.accept();
                             ObjectInputStream in = new ObjectInputStream(updateSocket.getInputStream())) {
                            System.out.println("Recibiendo catálogo actualizado desde " + updateSocket.getInetAddress());
                            catalogo = (List<Producto>) in.readObject();
                            guardarCatalogo(catalogo);
                        } catch (EOFException eof) {
                            System.out.println("Cliente desconectado.");
                            break;
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Producto> cargarCatalogo() {
        List<Producto> productos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("catalogo.csv"))) {
            reader.readLine();
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                int id = Integer.parseInt(datos[0]);
                String nombre = datos[1];
                int cantidad = Integer.parseInt(datos[2]);
                double precio = Double.parseDouble(datos[3]);
                boolean enStock = Boolean.parseBoolean(datos[4]);
                float peso = Float.parseFloat(datos[5]);
                char categoria = datos[6].charAt(0);

                productos.add(new Producto(id, nombre, cantidad, precio, enStock, peso, categoria));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productos;
    }

    private static void guardarCatalogo(List<Producto> productos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("catalogo.csv"))) {
            writer.write("id,Nombre,Cantidad,Precio,EnStock,Peso,Categoria");
            writer.newLine();
            for (Producto producto : productos) {
                writer.write(producto.fromCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
