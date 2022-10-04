package practicas.bolqueII.tftp;

public class prueba {
    private int id;
    private int a;
    private int p;

    public prueba(int[] input){
        create(this, input);
    }

    private static void create(prueba prueba, int[] input) {
        prueba.a = input[0];
        prueba.id = input[1];
        prueba.p = input[2];
    }

    @Override
    public String toString() {
        return "prueba.txt{" +
                "id=" + id +
                ", a=" + a +
                ", p=" + p +
                '}';
    }
}
