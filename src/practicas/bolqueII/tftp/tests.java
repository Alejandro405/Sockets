package practicas.bolqueII.tftp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class tests {

    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_RESET = "\u001B[0m";

    private static String sFolder = System.getProperty("user.dir");

    public static void main(String[] args) throws IOException {
        /**
         * List<Integer> t1 = List.of(1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 13, 14);
         *
         *         List<Integer> result = new ArrayList<>();
         *
         *         for (int i = 0; i < t1.size() - 1; i++) {
         *             if (t1.get(i) + 1 != t1.get(i + 1)) // Encontramos las dos subsecuencias
         *                 result.addAll( IntStream.rangeClosed(t1.get(i) + 1, t1.get(i + 1) - 1)
         *                         .boxed()
         *                         .toList());
         *         }
         *
         *
         *         System.out.println(new prueba.txt(new int[]{1, 2, 2}).toString());
         *
         *
         *         System.out.println(Arrays.deepToString(result.toArray()));
         *         Short p = 0;
         *         System.out.println("Short 0 = "+ p);
         */

        //String filename = "C:\\Users\\Usuario\\IdeaProjects\\Sockets\\src\\practicas\\bolqueII\\tftp";
        String filename = "prueba.txt";
        /**
         * Testeo ficheros y byte[]
         */
        //Lectura
        //File txt = new File(filename + "\\prueba.txt");
        File txt = new File(System.getProperty("user.dir") + "/prueba.txt");

        byte[] datos = Files.readAllBytes(txt.toPath());
        System.out.println("Tamaño de bites del fichero: " + datos.length);
        System.out.println(ANSI_GREEN+"Contenido del fichero\n"+new String(datos, 0, datos.length, StandardCharsets.UTF_8)+ANSI_RESET);


        File clientFolder = new File(sFolder+"/TFTPserver/client1/");

        if (!clientFolder.exists())
            clientFolder.mkdirs();

        File file = new File(clientFolder, "prueba.txt");
        file.setWritable(true);
        FileOutputStream salida = new FileOutputStream(file);
        BufferedOutputStream out = new BufferedOutputStream(salida);

        out.write(datos);

        out.close();
    }
}
