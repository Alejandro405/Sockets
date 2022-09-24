package practicas.bolqueII.tftp;

import practicas.bolqueII.tftp.datagram.headers.Header;
import practicas.bolqueII.tftp.datagram.headers.HeaderFactory;
import practicas.bolqueII.tftp.tools.UnsupportedTFTPOperation;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class tests {
    public static void main(String[] args) {
        List<Integer> t1 = List.of(1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 13, 14);

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < t1.size() - 1; i++) {
            if (t1.get(i) + 1 != t1.get(i + 1)) // Encontramos las dos subsecuencias
                result.addAll( IntStream.rangeClosed(t1.get(i) + 1, t1.get(i + 1) - 1)
                        .boxed()
                        .toList());
        }


        System.out.println(new prueba(new int[]{1, 2, 2}).toString());


        System.out.println(Arrays.deepToString(result.toArray()));
        Short p = 0;
        System.out.println("Short 0 = "+ p);
    }
}
