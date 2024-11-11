
package escom.carritocomprasservidor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class servidor {
    public static void main(String args[]) throws IOException {
        
        File folder = new File("resources");
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("La carpeta 'resources' no existe o no es un directorio.");
            return;
        }

        // Listar los archivos en la carpeta "resources"
        File[] photoFiles = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
        
        if (photoFiles == null || photoFiles.length == 0) {
            System.out.println("No se encontraron imágenes en la carpeta 'resources'.");
            return;
        }

        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(6040)) {
                System.out.println("Esperando cliente ...");
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Conexión establecida desde " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                    
                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                    // Enviar cada foto al cliente
                    for (File file : photoFiles) {
                        if (file.exists() && file.isFile()) {
                            FileInputStream fileIn = new FileInputStream(file);

                            // Enviar el nombre del archivo y su tamaño
                            output.writeUTF(file.getName());
                            output.writeLong(file.length());

                            // Enviar el archivo en bloques de datos
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = fileIn.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                            fileIn.close();
                            System.out.println("Foto enviada: " + file.getName());
                        }
                    }
                
                    List<Producto> catalogo = cargarCatalogo();

                    // se envia el catálogo al cliente
                    try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
                        out.writeObject(catalogo);
                    }

                    // se cierra el primer socket para finalizar la comunicación de catálogo
                    clientSocket.close();

                    // se espera por una nueva conexión para recibir el catálogo actualizado
                    try (Socket updateSocket = serverSocket.accept()) {
                        System.out.println("Catálogo actualizado desde " + updateSocket.getInetAddress());

                        // se recibe el carrito actualizado y se guarda
                        try (ObjectInputStream in = new ObjectInputStream(updateSocket.getInputStream())) {
                            catalogo = (List<Producto>) in.readObject();
                            guardarCatalogo(catalogo);
                        }
                    }

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
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
