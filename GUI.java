import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeListener;

import javax.swing.JLayeredPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Icon;

public class GUI extends JFrame {
    //Lógica de la aplicación
    private Logica l;

    //Paneles
    private JPanel panelJuego;

    //Celdas
    private JButton [][] celdas;

    //Labels
    private JLabel fondo;

    //Constructor
    public GUI() {
        //Establezco nombre de la ventana
        super("Minesweeper");
        //Establezco icono de la ventana
        String iconPath = "imagenes/iconoJuego.png"; //Ruta relativa al archivo de imagen del icono dentro del proyecto.
        ImageIcon icono = new ImageIcon(getClass().getResource(iconPath)); //El objeto icono (instancia de ImageIcon) contiene la imagen del icono cargada desde el archivo especificado
        setIconImage(icono.getImage()); //El método setIconImage espera un objeto de tipo Image como parámetro
        
        //Establezco configuraciones de la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(800, 800));
        setLocationRelativeTo(null); //Establezco que la ventana aparezca por default en el centro de la pantalla
        /*
        El método setLocationRelativeTo() coloca la ventana en una posición relativa a un componente que le paso como parámetro. 
        Pasándole null como parámetro, coloca a la ventana en el centro de la pantalla.
        */
        setVisible(true); //Hago visible la ventana en la pantalla
        setResizable(false); //Establezco que no se pueda cambiar el tamaño de la ventana
       
        //Inicializo la lógica y la GUI
        l = new Logica();
        inicializarGUI();
    }

    //Método para inicializar las componentes
    private void inicializarGUI() {
        //Seteo el contentPane como layeredPane para poder colocar paneles uno por encima del otro
        JLayeredPane layeredPane = new JLayeredPane();
        this.setContentPane(layeredPane);

        //Seteo el fondo del juego
        fondo = new JLabel(new ImageIcon("imagenes/fondoGeneral.png"));
        fondo.setBounds(0, 0, 790, 763); //Lo ajusto al contentPane para que lo cubra completamente 

        //Armado del panel del juego con GridLayout (superpuesto)
        panelJuego = new JPanel(new GridLayout(8, 8));
        panelJuego.setBounds(200, 230, 400, 400);  // Ajusta la ubicación (x,y) y el tamaño (w,h)

        //Seteo los botones
        inicializarBotones();

        //Agregado del fondo y del panelJuego al contentPane
        layeredPane.add(fondo, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(panelJuego, JLayeredPane.PALETTE_LAYER);
    }

    //Método que inicializa los botones del juego (celdas)
    private void inicializarBotones() {
        //Creo la matriz de botones
        celdas = new JButton[8][8]; //El tablero es de 8x8 como el juego clásico

        //Por cada celda
        for (int f = 0; f < celdas.length; f++) {
            for (int c = 0; c < celdas[0].length; c++) {
                //Creo el botón
                celdas[f][c] = new JButton();
                //Establezco su tamaño
                celdas[f][c].setSize(new Dimension(50,50));
                //Seteo el icono del boton
                celdas[f][c].setIcon(new ImageIcon("imagenes/iconoSinJugar.png"));
                //Establezo su oyente
                celdas[f][c].addMouseListener(new OyenteClick(f,c));
                //Agrego el botón al panel del juego
                panelJuego.add(celdas[f][c]);
            }
        }
    }
    
    //Metodo privado que, una vez finalizado el juego, muestra la posición en el tablero de cada bomba 
    private void mostrarBombas() {
        for(int f = 0; f < l.cantidadFilas(); f++) {
            for(int c = 0; c < l.cantidadColumnas(); c++) {
                if (l.obtenerCelda(f,c)) { //Si hay bomba en la celda
                    celdas[f][c].setIcon(new ImageIcon("imagenes/iconoBomba.png"));
                }
            }
        }
    }

    //Función recursiva que libera las celdas que no tienen bombas a su alrededor 
    private void liberarEspacios(int f, int c) {
        //Aviso que la celda fue presionada
        l.presionarCelda(f,c);
        //Cuento las bombas a su alrededor
        switch(contarSeguras(f,c)) {
            //Casos base
            case 0: //Si ninguna casilla es segura, significa que tiene 8 bombas alrededor
                celdas[f][c].setIcon(new ImageIcon("imagenes/icono8Bombas.png"));
                break;
            case 1: //Si solo una casilla es segura, significa que tiene 7 bombas alrededor
                celdas[f][c].setIcon(new ImageIcon("imagenes/icono7Bombas.png"));
                break;
            case 2:
                celdas[f][c].setIcon(new ImageIcon("imagenes/icono6Bombas.png"));
                break;
            case 3:
                celdas[f][c].setIcon(new ImageIcon("imagenes/icono5Bombas.png"));
                break;
            case 4:
                celdas[f][c].setIcon(new ImageIcon("imagenes/icono4Bombas.png"));
                break;
            case 5:
                celdas[f][c].setIcon(new ImageIcon("imagenes/icono3Bombas.png"));
                break;
            case 6:
                celdas[f][c].setIcon(new ImageIcon("imagenes/icono2Bombas.png"));
                break;
            case 7:
                celdas[f][c].setIcon(new ImageIcon("imagenes/icono1Bombas.png"));
                break;
            //Caso recursivo
            case 8:
                //Si las 8 casillas a su alrededor son seguras, puedo liberar la celda y declararla segura
                celdas[f][c].setIcon(new ImageIcon("imagenes/iconoSegura.png"));
                //Reviso las casillas alrededor
                chequearCasillasAlrededor(f,c);
                break;
        }
    }

    //Método privado que cuenta la cantidad de celdas seguras alrededor de una celda de posición (f,c) en la matriz
    private int contarSeguras(int f, int c) {
        int contadorCasillasSeguras = 0;
        //Chequeo las tres casillas de arriba
        if (l.celdaValida(f-1,c-1)) { //Si existe la celda en el tablero
            if (!l.obtenerCelda(f-1,c-1)) {
                contadorCasillasSeguras++;
            }
        } else { //Si no existe esa celda en el tablero, entonces es segura
            contadorCasillasSeguras++;
        }
        if (l.celdaValida(f-1,c)) {
            if (!l.obtenerCelda(f-1,c)) {
                contadorCasillasSeguras++;
            }
        } else {
            contadorCasillasSeguras++;
        }
        if (l.celdaValida(f-1,c+1)) {
            if (!l.obtenerCelda(f-1,c+1)) {
                contadorCasillasSeguras++;
            }
        } else {
            contadorCasillasSeguras++;
        }
        //Chequeo las dos casillas laterales
        if (l.celdaValida(f,c-1)) {
            if (!l.obtenerCelda(f,c-1)) {
                contadorCasillasSeguras++;
            }
        } else {
            contadorCasillasSeguras++;
        }
        if (l.celdaValida(f,c+1)) {
            if (!l.obtenerCelda(f,c+1)) {
                contadorCasillasSeguras++;
            }
        } else {
            contadorCasillasSeguras++;
        }
        //Chequeo las tres casillas de abajo
        if (l.celdaValida(f+1,c-1)) {
            if (!l.obtenerCelda(f+1,c-1)) {
                contadorCasillasSeguras++;
            }
        } else {
            contadorCasillasSeguras++;
        }
        if (l.celdaValida(f+1,c)) {
            if (!l.obtenerCelda(f+1,c)) {
                contadorCasillasSeguras++;
            }
        } else {
            contadorCasillasSeguras++;
        }
        if (l.celdaValida(f+1,c+1)) {
            if (!l.obtenerCelda(f+1,c+1)) {
                contadorCasillasSeguras++;
            }
        } else {
            contadorCasillasSeguras++;
        }

        return contadorCasillasSeguras;
    }

    //Método privado para chequear las celdas alrededor de una celda específica. Corrobora si tiene bomba cerca
    private void chequearCasillasAlrededor(int f, int c) {
        //Chequeo las tres casillas de arriba
        if (l.celdaValida(f-1, c-1) && !l.celdaPresionada(f-1,c-1)) {
            liberarEspacios(f-1, c-1);
        }
        if (l.celdaValida(f-1, c) && !l.celdaPresionada(f-1,c)) {
            liberarEspacios(f-1, c);
        }
        if (l.celdaValida(f-1, c+1) && !l.celdaPresionada(f-1,c+1)) {
            liberarEspacios(f-1, c+1);
        }
        //Chequeo las dos casillas laterales
        if (l.celdaValida(f, c-1) && !l.celdaPresionada(f,c-1)) {
            liberarEspacios(f, c-1);
        }
        if (l.celdaValida(f, c+1) && !l.celdaPresionada(f,c+1)) {
            liberarEspacios(f, c+1);
        }
        //Chequeo las tres casillas de abajo
        if (l.celdaValida(f+1, c-1) && !l.celdaPresionada(f+1,c-1)) {
            liberarEspacios(f+1, c-1);
        }
        if (l.celdaValida(f+1, c) && !l.celdaPresionada(f+1,c)) {
            liberarEspacios(f+1, c);
        }
        if (l.celdaValida(f+1, c+1) && !l.celdaPresionada(f+1,c+1)) {
            liberarEspacios(f+1, c+1);
        }
    }

    //Método privado que permite reiniciar el juego
    private void reiniciarJuego() {
        l = new Logica();
        reiniciarBotones();
    }

    //Método privado que reinicia la apariencia de los botones
    private void reiniciarBotones() {
        for (int f = 0; f < celdas.length; f++) {
            for (int c = 0; c < celdas[0].length; c++) {
                //Seteo el icono inicial
                celdas[f][c].setIcon(new ImageIcon("imagenes/iconoSinJugar.png"));
            }
        }
    }
    
    //Oyente de botones de celdas (tablero) 
    private class OyenteClick extends MouseAdapter {
        //Atributos de instancia
        private int fila;
        private int columna;
        private int botonPresionado;
        private boolean iconoBandera; //booleano que controla cuando se coloca o se quita la bandera

        //Constructor
        public OyenteClick(int fila, int columna) {
            //Guardo la posición del botón
            this.fila = fila;
            this.columna = columna;
            //Establezco el booleano en true para permitir colocar el icono de la bandera
            iconoBandera = true;
        }

        //Sobreescritura de método
        public void mouseClicked(MouseEvent e) {
            //Obtengo el botón presionado (izquierdo, central o derecho)
            botonPresionado = e.getButton();

            //Verifica si se presiono click izquierdo sobre el botón y que el mismo no haya sido presionado previamente
            if (botonPresionado == MouseEvent.BUTTON1 && !l.celdaPresionada(fila,columna)) {
                if (l.obtenerCelda(fila,columna)) {
                    //Significa que el botón que presioné tiene una bomba
                    celdas[fila][columna].setIcon(new ImageIcon("imagenes/iconoBomba.png"));
                    mostrarBombas();
                    //Muestro el mensaje de derrota y reinicio juego
                    JOptionPane.showMessageDialog(null,"¡HAS DADO CON UNA BOMBA!","DERROTA",JOptionPane.INFORMATION_MESSAGE);
                    reiniciarJuego();
                } else {
                    //Significa que el botón que presioné es seguro
                    liberarEspacios(fila, columna);
                }
            } else if (botonPresionado == MouseEvent.BUTTON3 && !l.celdaPresionada(fila,columna)) { //Verifica si se presiono click derecho sobre el botón y que el mismo no haya sido presionado previamente
                // Cambia el icono del botón según el estado de iconoBandera
                if (iconoBandera) { //Si iconoBandera estaba en "true" significa que no habia bandera colocada y, por lo tanto, la coloco
                    celdas[fila][columna].setIcon(new ImageIcon("imagenes/iconoBanderin.png"));
                } else { //Si iconoBandera estaba en "false" significa que no habia bandera colocada y, por lo tanto, la quito
                    celdas[fila][columna].setIcon(new ImageIcon("imagenes/iconoSinJugar.png"));
                }
                // Invierte el estado de iconoBandera
                iconoBandera = !iconoBandera;
            }
            
            //Verifica si tras presionar el botón el jugador ha ganado el juego
            if (l.obtenerCeldasPresionadas() == 55) { //El tablero tiene 64 cuadriculas y 9 bombas, por lo cual para ganar se deben haber presionado 55 celdas
                //Muestro el mensaje de victoria y reinicio juego
                JOptionPane.showMessageDialog(null,"¡ENHORABUENA, HAS GANADO!","VICTORIA",JOptionPane.INFORMATION_MESSAGE);
                reiniciarJuego();
            }
        }
        
        //----Métodos para quitar el efecto visual de presionado una vez el botón ya fue clickeado (funcionan en conjunto)----
    
        //Método para manejar el evento cuando el mouse entra al botón (pasa por encima)
        public void mouseEntered(MouseEvent e) {
            if (l.celdaPresionada(fila,columna)) { //Si la celda ya fue presionada
                celdas[fila][columna].setBorderPainted(false);
                celdas[fila][columna].setFocusPainted(false);
            }
        }
        
        //Método para manejar el evento cuando el mouse sale del botón (lo quito de encima)
        public void mouseExited(MouseEvent e) {
            if (l.celdaPresionada(fila,columna)) { //Si la celda ya fue presionada
                celdas[fila][columna].setBorderPainted(true);
                celdas[fila][columna].setFocusPainted(true);
            }
        }
    }
}