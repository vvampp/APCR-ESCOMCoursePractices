import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    public static void main(String args[]) {

        // Cargar el catálogo desde el archivo CSV
        List<Producto> productos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("productoAtributos.csv"))) {
            String linea;
            reader.readLine(); // Saltar la primera línea (cabecera)
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                int id = Integer.parseInt(datos[0].trim());
                String nombre = datos[1].trim();
                int cantidad = Integer.parseInt(datos[2].trim());
                double precio = Double.parseDouble(datos[3].trim());
                boolean enStock = Boolean.parseBoolean(datos[4].trim());
                float peso = Float.parseFloat(datos[5].trim());
                char categoria = datos[6].trim().charAt(0);

                productos.add(new Producto(id, nombre, cantidad, precio, enStock, peso, categoria));
            }
            System.out.println("Catálogo cargado desde productoAtributos.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Iniciar el servidor y esperar una conexión de cliente
        try {
            ServerSocket s = new ServerSocket(6040);
            System.out.println("Esperando cliente ...");

            while (true) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde " + cl.getInetAddress() + ":" + cl.getPort());

                try (OutputStream outputStream = cl.getOutputStream();
                     ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {

                    // Enviar la lista de productos serializada al cliente
                    objectOutputStream.writeObject(productos);
                    System.out.println("Catálogo enviado al cliente.");

                    cl.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


