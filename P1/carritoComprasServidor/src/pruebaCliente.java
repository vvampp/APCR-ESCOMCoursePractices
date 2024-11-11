import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class PruebaCliente {
    public static void main(String args[]) {

        // Solicitar IP y puerto al usuario
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la IP del servidor: ");
        String host = scanner.nextLine();
        System.out.print("Ingrese el puerto del servidor: ");
        int puerto = scanner.nextInt();

        // Crear la carpeta para guardar el archivo recibido
        File carpeta = new File("Cliente");
        if (!carpeta.exists()) {
            boolean creada = carpeta.mkdirs();
            if (creada) {
                System.out.println("Carpeta creada: " + carpeta.getAbsolutePath());
            } else {
                System.out.println("Error al crear la carpeta.");
                return;
            }
        } else {
            System.out.println("La carpeta ya existe: " + carpeta.getAbsolutePath());
        }

        try {
            Socket cl = new Socket(host, puerto);
            System.out.println("Conexión establecida con el servidor en " + host + ":" + puerto);

            try (InputStream inputStream = cl.getInputStream();
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                 BufferedWriter writer = new BufferedWriter(new FileWriter("Cliente/catalogo.csv"))) {

                // Recibir y deserializar el catálogo
                List<Producto> productos = (List<Producto>) objectInputStream.readObject();

                // Guardar en el archivo CSV y mostrar en consola
                writer.write("id,Nombre,Cantidad,Precio,EnStock,Peso,Categoria");
                writer.newLine();
                for (Producto producto : productos) {
                    String linea = producto.toString();
                    System.out.println(linea);
                    writer.write(linea);
                    writer.newLine();
                }
                System.out.println("Archivo recibido y guardado como Cliente/catalogo.csv");

            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al deserializar o guardar el catálogo: " + e.getMessage());
            }

            cl.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
