import java.util.Random;

public class Logica {
    //Atributos de instancia
    private Celdas celdasJuego;

    //Atributos de clase
    private static boolean casillas[][];
    private static final int bombas = 9; //Es una constante porque en todos los juegos va a haber la misma cantidad de bombas

    //Constructor
    public Logica() {
        //Creo una instancia de Celdas que me va a permitir saber si una celda fue presionada o no
        celdasJuego = new Celdas();
        //Inicializo mi matriz que me va a permitir saber si una celda contiene bomba o no
        inicializarMatriz();
    }

    //Consultas
    public int cantidadFilas() {
        return casillas.length;
    }

    public int cantidadColumnas() {
        return casillas[0].length;
    }

    public int obtenerBombas() {
        return bombas;
    }

    public boolean obtenerCelda(int f, int c) {
        //Requiere f y c válidos (que estén dentro del rango de la matriz)
        //Retorna true si hay una bomba en esa celda
        return casillas[f][c];
    }

    public boolean esSegura(int f, int c) {
        //Si casillas[f][c] es "true" significa que la casilla contiene una bomba y por lo tanto NO es segura
        return !casillas[f][c];
    }

    public boolean celdaPresionada(int f, int c) {
        return celdasJuego.celdaJugada(f,c);
    }

    public boolean celdaValida(int f, int c) {
        return f >= 0 && c >= 0 &&
               f < cantidadFilas() &&
               c < cantidadColumnas();
    }

    public int obtenerCeldasPresionadas() {
        return celdasJuego.celdasPresionadas();
    }

    //Métodos
    public void presionarCelda(int f, int c) {
        celdasJuego.jugarCelda(f,c);
    }

    private static void inicializarMatriz() {
        int bombasColocadas = 0;
        int posFila = 0;
        int posColumna = 0;

        casillas = new boolean[8][8]; //Se corresponde con el tablero clásico del juego, que de 8x8
        Random r = new Random();

        //Inicializo el tablero con todas las posiciones Falsas
        for (int f = 0; f < casillas.length; f++) {
            for (int c = 0; c < casillas[0].length; c++) {
                casillas[f][c] = false;
            }
        }

        //Coloco las bombas en el tablero
        while (bombasColocadas < bombas) {
            posFila = r.nextInt(8); //Genera un número pseudoaleatorio entre [0,7]
            posColumna = r.nextInt(8);

            if (!casillas[posFila][posColumna]) { //Si no había una bomba previamente colocada en la casilla, entonces coloco bomba
                casillas[posFila][posColumna] = true;
                bombasColocadas++;
            }
        }
    }
}