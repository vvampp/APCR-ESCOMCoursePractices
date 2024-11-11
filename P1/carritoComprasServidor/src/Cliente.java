import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la IP del servidor: ");
        String host = scanner.nextLine();
        System.out.print("Ingrese el puerto del servidor: ");
        int puerto = scanner.nextInt();

        File carpeta = new File("Catálogo");
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            System.out.println("Error al crear la carpeta.");
            return;
        }

        try (Socket cl = new Socket(host, puerto)) {
            System.out.println("Conexión establecida con el servidor en " + host + ":" + puerto);

            List<Producto> catalogo;
            List<Producto> carrito = new ArrayList<>();

            try (ObjectInputStream objectInputStream = new ObjectInputStream(cl.getInputStream())) {
                catalogo = (List<Producto>) objectInputStream.readObject();
            }

            guardarCatalogo(catalogo);
            mostrarCatalogo(catalogo);

            while (true) {
                System.out.println("\n=== Menú de Carrito ===");
                System.out.println("[1] Agregar un producto al carrito");
                System.out.println("[2] Eliminar producto del carrito");
                System.out.println("[3] Ver carrito");
                System.out.print("Seleccione una opción: ");
                int opcion = scanner.nextInt();

                switch (opcion) {
                    case 1:
                        agregarProductoCarrito(scanner, catalogo, carrito);
                        break;
                    case 2:
                        eliminarProductoCarrito(scanner,catalogo, carrito);
                        break;
                    case 3:
                        verCarrito(scanner, catalogo, carrito, cl);
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static void guardarCatalogo(List<Producto> catalogo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Catálogo/catalogo.csv"))) {
            writer.write("id,Nombre,Cantidad,Precio,EnStock,Peso,Categoria");
            writer.newLine();
            for (Producto producto : catalogo) {
                writer.write(producto.fromCsv());
                writer.newLine();
            }
        }
    }

    private static void mostrarCatalogo(List<Producto> catalogo) {
        System.out.println("id\tNombre\tCantidad\tPrecio\tEnStock\tPeso\tCategoria");
        for (Producto producto : catalogo) {
            System.out.println(producto);
        }
    }

    private static void agregarProductoCarrito(Scanner scanner, List<Producto> catalogo, List<Producto> carrito) {
        System.out.print("Ingrese el ID del producto a agregar: ");
        int id = scanner.nextInt();
        System.out.print("Ingrese la cantidad a agregar: ");
        int cantidad = scanner.nextInt();

        // instancia del producto en el catálogo
        Optional<Producto> productoCatalogo = catalogo.stream().filter(p -> p.getId() == id).findFirst();
        // si el producto está en el catalogo y alcanza el stock
        if (productoCatalogo.isPresent() && productoCatalogo.get().getCantidad() >= cantidad) {
            // isntancia del producto en el carrito
            Optional<Producto> productoCarrito = carrito.stream().filter(p -> p.getId() == id).findFirst();
            // si el producto ya está en el carrito
            if (productoCarrito.isPresent()) {
                // se suma la cantidad anterior con la nueva
                productoCarrito.get().setCantidad(productoCarrito.get().getCantidad() + cantidad);
            } else {
                // se agrega al carrito el nuevo producto
                carrito.add(new Producto(id, productoCatalogo.get().getNombre(), cantidad, productoCatalogo.get().getPrecio(),
                        true, productoCatalogo.get().getPeso(), productoCatalogo.get().getCategoria()));
            }
            // se actualiza la cantidad en el catálogo
            productoCatalogo.get().setCantidad(productoCatalogo.get().getCantidad() - cantidad);
            try {
                guardarCatalogo(catalogo);
                mostrarCatalogo(catalogo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Producto no disponible o cantidad insuficiente en el catálogo.");
        }
    }

    private static void eliminarProductoCarrito(Scanner scanner,List<Producto>catalogo, List<Producto> carrito) {
        System.out.print("Ingrese el ID del producto a eliminar: ");
        int id = scanner.nextInt();
        System.out.print("Ingrese la cantidad a eliminar: ");
        int cantidad = scanner.nextInt();
        // isntancia del producto en el catálogo
        Optional<Producto> productoCatalogo = catalogo.stream().filter(p -> p.getId() == id).findFirst();
        // instancia del producto en el carrito
        Optional<Producto> productoCarrito = carrito.stream().filter(p -> p.getId() == id).findFirst();
        // si el producto está en el carrito y la cantida a eliminar no es mayor a la agregada antes
        if (productoCarrito.isPresent() && productoCarrito.get().getCantidad() >= cantidad) {
            // se disminuye cantidad en el carrito
            productoCarrito.get().setCantidad(productoCarrito.get().getCantidad() - cantidad);
            // si se queda en ceros
            if (productoCarrito.get().getCantidad() == 0) {
                // se remueve el producto del carrito
                carrito.remove(productoCarrito.get());
            }
            productoCatalogo.get().setCantidad(productoCatalogo.get().getCantidad() + cantidad);
            try {
                guardarCatalogo(catalogo);
                mostrarCatalogo(catalogo);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Cantidad no válida o producto no encontrado en el carrito.");
        }
    }

    private static void verCarrito(Scanner scanner, List<Producto> catalogo, List<Producto> carrito, Socket cl) {
        double totalCarrito = 0.0;
        System.out.println("id\tNombre\tCantidad\tPrecio Unitario\tTotal");
        for (Producto producto : carrito) {
            double totalProducto = producto.getCantidad() * producto.getPrecio();
            totalCarrito += totalProducto;
            System.out.println(producto.getId() + "\t" + producto.getNombre() + "\t" + producto.getCantidad() + "\t" +
                    producto.getPrecio() + "\t" + totalProducto);
        }
        System.out.println("Total del carrito: " + totalCarrito);

        System.out.println("\n[1] Regresar al CRUD");
        System.out.println("[2] Realizar compra");
        int opcion = scanner.nextInt();

        if (opcion == 2) {
            realizarCompra(catalogo, cl);
            finalizarCliente(cl);
        }
    }

    private static void realizarCompra(List<Producto> catalogo, Socket cl) {
        try (Socket updateSocket = new Socket(cl.getInetAddress(), cl.getPort())) {
            try (ObjectOutputStream out = new ObjectOutputStream(updateSocket.getOutputStream())) {
                out.writeObject(catalogo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void finalizarCliente(Socket cl) {
        try {
            cl.close();
            System.out.println("Gracias por su compra.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
