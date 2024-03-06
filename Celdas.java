public class Celdas {
    //Atributos de instancia
    private boolean [][] celdasJugadas;
    private int cuadriculasPresionadas;

    //Constructor
    public Celdas() {
        celdasJugadas = new boolean [8][8]; //Se corresponde con el tablero clásico del juego, que de 8x8
        cuadriculasPresionadas = 0;
        //Establezco todas las celdas en falso representando que ninguna celda fue presionada
        for (int f = 0; f < celdasJugadas.length; f++) {
            for (int c = 0; c < celdasJugadas[0].length; c++) {
                celdasJugadas[f][c] = false;
            }
        }
    }

    //Consultas
    public boolean celdaJugada(int f, int c) {
        //Si retorna "true" significa que la celda ya fue presionada
        return celdasJugadas[f][c];
    }

    public int celdasPresionadas() {
        return cuadriculasPresionadas;
    }

    //Metodos
    public void jugarCelda(int f, int c) {
        //Una vez presionada la celda, la misma no puede volver a su estado inicial
        celdasJugadas[f][c] = true;
        cuadriculasPresionadas++;
    }
}