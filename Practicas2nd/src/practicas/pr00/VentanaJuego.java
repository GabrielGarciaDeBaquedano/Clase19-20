package practicas.pr00;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VentanaJuego extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel pPrincipal;
    CocheJuego miCoche;
    MiRunnable miHilo;
	
	public VentanaJuego() {
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Operacion para cerrar la ventana.
		this.pPrincipal = new JPanel(); //	Creacion de paneles.
		final JPanel pBotonera = new JPanel();
		final JButton bAcelerar = new JButton("Acelera"); //Creacion de botones
        final JButton bFrenar = new JButton("Frena");
        final JButton bGiraIzq = new JButton("Gira Izq.");
        final JButton bGiraDer = new JButton("Gira Der.");
        this.pPrincipal.setLayout(null); // Layout nulo.
        this.pPrincipal.setBackground(Color.white);
        this.add(this.pPrincipal, "Center"); // Añadimos el panel principal blanco al centro.
        pBotonera.add(bAcelerar); // Añadimos los botones al panel de botonera.
        pBotonera.add(bFrenar);
        pBotonera.add(bGiraIzq);
        pBotonera.add(bGiraDer);
        this.add(pBotonera, "South"); // Colocamos el panel de botonera al sur.
        this.setSize(700, 500);
        bAcelerar.addActionListener(new ActionListener() { // Listeners de todos los botones de la botonera.
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (VentanaJuego.this.miCoche.getMiVelocidad() == 0.0) {
                    VentanaJuego.this.miCoche.acelera(5.0);
                }
                else {
                    VentanaJuego.this.miCoche.acelera(5.0);
                }
                System.out.println("Nueva velocidad de coche: " + VentanaJuego.this.miCoche.getMiVelocidad());
            }
        });
        bFrenar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                VentanaJuego.this.miCoche.acelera(-5.0);
                System.out.println("Nueva velocidad de coche: " + VentanaJuego.this.miCoche.getMiVelocidad());
            }
        });
        bGiraIzq.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                VentanaJuego.this.miCoche.gira(10.0);
                System.out.println("Nueva direccion de coche: " + VentanaJuego.this.miCoche.getMiDireccionActual());
            }
        });
        bGiraDer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                VentanaJuego.this.miCoche.gira(-10.0);
                System.out.println("Nueva direccion de coche: " + VentanaJuego.this.miCoche.getMiDireccionActual());
            }
        });
        this.pPrincipal.addKeyListener(new KeyAdapter() { // Añado todas las acciones a las flechas del teclado.
            @Override
            public void keyPressed(final KeyEvent e) {
                if(e.getKeyCode() == 38) { // Flecha arriba (su codigo es 38) para acelerar.
                	VentanaJuego.this.miCoche.acelera(5.0);
                	System.out.println("Nueva velocidad de coche: " + VentanaJuego.this.miCoche.getMiVelocidad());
                    }
                else if (e.getKeyCode() == 40) { // Flecha abajo (su codigo es 40) para frenar.
                	VentanaJuego.this.miCoche.acelera(-5.0);
                	System.out.println("Nueva velocidad de coche: " + VentanaJuego.this.miCoche.getMiVelocidad());
				}
                else if (e.getKeyCode() == 37) {
                	VentanaJuego.this.miCoche.gira(10.0); // Flecha izquierda (su codigo es 37) para girara hacia la izquierda.
                	System.out.println("Nueva direccion de coche: " + VentanaJuego.this.miCoche.getMiDireccionActual());
                    }
                else if (e.getKeyCode() == 39) { // Flecha derecha (su codigo es 39) para girara hacia la derecha.
                	VentanaJuego.this.miCoche.gira(-10.0);
                	System.out.println("Nueva direccion de coche: " + VentanaJuego.this.miCoche.getMiDireccionActual());
                    }
            }
        });
		
        this.pPrincipal.setFocusable(true);
        this.pPrincipal.requestFocus();
        this.pPrincipal.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                VentanaJuego.this.pPrincipal.requestFocus();
            }
        });
        
        this.addWindowListener(new WindowAdapter() { // Listener para matar el hilo cuando se cierra la ventana.
            @Override
            public void windowClosing(final WindowEvent e) { 
                if (VentanaJuego.this.miHilo != null) {
                    VentanaJuego.this.miHilo.acaba();
                }
            }
        });
    }
	
	public void creaCoche(final int posX, final int posY) {
		(this.miCoche = new CocheJuego()).setPosX(posX);
		(this.miCoche = new CocheJuego()).setPosX(posY);
        this.pPrincipal.add(this.miCoche.getGrafico());
    }
	
	public static void main(final String[] args) {
        final VentanaJuego miVentana = new VentanaJuego();
        miVentana.creaCoche(150, 100);
        miVentana.setVisible(true);
        miVentana.miCoche.setPiloto("Gabri");
        miVentana.miHilo = miVentana.new MiRunnable();
        final Thread nuevoHilo = new Thread(miVentana.miHilo);
        nuevoHilo.start();
    }
	
	class MiRunnable implements Runnable
    {
        boolean sigo;
        
        MiRunnable() {
            this.sigo = true;
        }
        
        @Override
        public void run() {
            while (this.sigo) {
                VentanaJuego.this.miCoche.mueve(0.04);
                if (VentanaJuego.this.miCoche.getPosX() < -50.0 ) { //Deteccion de choches con las pared izquierda.
                    System.out.println("Choca -X");
                    double dir = VentanaJuego.this.miCoche.getMiDireccionActual();
                    dir = 180.0 - dir;
                    if (dir < 0.0) {
                        dir += 360.0;
                    }
                    VentanaJuego.this.miCoche.setMiDireccionActual(dir);
                }
                if (VentanaJuego.this.miCoche.getPosX() > VentanaJuego.this.pPrincipal.getWidth() - 50 ) { //Deteccion de choches con las pared derecha.
                    System.out.println("Choca X");
                    double dir = VentanaJuego.this.miCoche.getMiDireccionActual();
                    dir = 180.0 - dir;
                    if (dir < 0.0) {
                        dir += 360.0;
                    }
                    VentanaJuego.this.miCoche.setMiDireccionActual(dir);
                }
                if (VentanaJuego.this.miCoche.getPosY() < -50.0 ) { //Deteccion de choches con las pared superior.
                    System.out.println("Choca Y");
                    double dir = VentanaJuego.this.miCoche.getMiDireccionActual();
                    dir = 360.0 - dir;
                    VentanaJuego.this.miCoche.setMiDireccionActual(dir);
                }
                if (VentanaJuego.this.miCoche.getPosY() > VentanaJuego.this.pPrincipal.getHeight() - 50) { //Deteccion de choches con las pared inferior.
                    System.out.println("Choca -Y");
                    double dir = VentanaJuego.this.miCoche.getMiDireccionActual();
                    dir = 360.0 - dir;
                    VentanaJuego.this.miCoche.setMiDireccionActual(dir);
                }
                try {
                    Thread.sleep(40L);
                }
                catch (Exception ex) {}
            }
        }
        
        public void acaba() {
            this.sigo = false;
        }
    }

}
