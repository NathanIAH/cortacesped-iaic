package com.iaic.cortacesped;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import com.iaic.cortacesped.busquedasCiegas.BusquedaPrimeroAnchura;

// TODO: salvar obst�culos cargados en fichero
// TODO: trazar trayectoria
// TODO: ampliar m�s
public class CortaCesped implements ActionListener {
	
	public static enum Sensor {NORTE, SUR, ESTE, OESTE};
	
	
	@SuppressWarnings("serial")
	private class Lienzo extends Canvas {
		public Lienzo() {
			this.setSize(800, 600);
		}
		public void update(Graphics g) {
			paint(g);
		}
		public void paint(Graphics g) {
			g.setColor(Color.BLACK);
			int posFila = MARGENFILA;
			int posColumna = MARGENCOL;
			// Dibujar la cuadr�cula
			// Dibujar las filas de la cuadr�cula
			for (int i = 0; i <= filas; i++) {
				g.drawLine(posFila, posColumna, posFila+(columnas*CELDAFILA), posColumna);
				posColumna += CELDACOL;
			}
			posColumna = MARGENCOL;
			// Dibujar las columnas de la cuadr�cula
			for (int i = 0; i <= columnas; i++) {
				g.drawLine(posFila, posColumna, posFila, posColumna+(filas*CELDACOL));
				posFila += CELDAFILA;
			}
			// Dibujar las im�genes en las casillas correspondientes de la cuadr�cula
			Image icespedbajo = Toolkit.getDefaultToolkit().getImage("res/imagenes/CespedBajo.jpg");
			Image icespedalto = Toolkit.getDefaultToolkit().getImage("res/imagenes/CespedAlto.jpg");
			Image iobstaculo = Toolkit.getDefaultToolkit().getImage("res/imagenes/Obstaculo.jpg");
			for (int i = 1; i <= filas; i++) {
				for (int j = 1; j <= columnas; j++) {
					if (matriz[i][j].equals("Obst�culo")) {
//						System.out.println("Obst�culo en " + i +"," + j);
						g.drawImage(iobstaculo, MARGENFILA+((j-1)*CELDAFILA), MARGENCOL+((i-1)*CELDACOL), this);
					}
					else if (matriz[i][j].equals("C�sped bajo")) {
//						System.out.println("C�sped bajo en " + i + "," + j);
						g.drawImage(icespedbajo, MARGENFILA+((j-1)*CELDAFILA), MARGENCOL+((i-1)*CELDACOL), this);
					}
					else if (matriz[i][j].equals("C�sped alto")) {
//						System.out.println("C�sped alto en " + i + "," + j);
						g.drawImage(icespedalto, MARGENFILA+((j-1)*CELDAFILA), MARGENCOL+((i-1)*CELDACOL), this);
					}
				}
			}
			Image icortacespedbajo = Toolkit.getDefaultToolkit().getImage("res/imagenes/Cortacesped_Bajo.jpg");
			Image icortacespedalto = Toolkit.getDefaultToolkit().getImage("res/imagenes/Cortacesped_Alto.jpg");
			// Dibujar el cortac�sped
			if (matriz[cortacespedFila][cortacespedColumna].equals("C�sped alto"))
				g.drawImage(icortacespedalto, MARGENFILA+((cortacespedColumna-1)*CELDAFILA), MARGENCOL+((cortacespedFila-1)*CELDACOL), this);
			else if (matriz[cortacespedFila][cortacespedColumna].equals("C�sped bajo"))
				g.drawImage(icortacespedbajo, MARGENFILA+((cortacespedColumna-1)*CELDAFILA), MARGENCOL+((cortacespedFila-1)*CELDACOL), this);
			// Aqu� habr� que contemplar que si se ha dispuesto un obst�culo en la posici�n (1,1) no se mostrar� el cortacesped
		}
	}
	private class VentanaDimensiones implements ActionListener {
		private Frame ventana_dimensiones;
		public VentanaDimensiones() {
			ventana_dimensiones = new Frame("Dimensiones");
			ventana_dimensiones.setSize(300, 100);
			ventana_dimensiones.setResizable(false);
			ventana_dimensiones.setLayout(new GridLayout(3,2));
			ventana_dimensiones.addWindowListener(new WindowListener() {
				public void windowOpened(WindowEvent e) {}
				public void windowActivated(WindowEvent e) {}
				public void windowDeactivated(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void windowClosed(WindowEvent e) {}
				public void windowClosing(WindowEvent e) {
					e.getWindow().setVisible(false);
					e.getWindow().dispose();
				}
			});
			// A�adir los elementos de la ventana Dimensiones
			Label l1 = new Label("N�mero de filas: ");
			TextField tf1 = new TextField();
			Label l2 = new Label("N�mero de columnas: ");
			TextField tf2 = new TextField();
			Button boton = new Button("Guardar");
			boton.addActionListener(this);
    
			ventana_dimensiones.add(l1);
			ventana_dimensiones.add(tf1);
			ventana_dimensiones.add(l2);
			ventana_dimensiones.add(tf2);
			ventana_dimensiones.add(boton);
        
			ventana_dimensiones.setVisible(true);
		}
		
		public void actionPerformed(ActionEvent e) {
			String texto = ((Button)e.getSource()).getLabel();
			System.out.println("Boton presionado: " + texto);
			if (texto.equals("Guardar")) {
				TextField tf_filas = (TextField)ventana_dimensiones.getComponent(1);
				filas = Integer.parseInt(tf_filas.getText());
				TextField tf_columnas = (TextField)ventana_dimensiones.getComponent(3);
				columnas = Integer.parseInt(tf_columnas.getText());
				System.out.println("Filas = " + filas);
				System.out.println("Columnas = " + columnas);
				
				ventana_dimensiones.setVisible(false);
				ventana_dimensiones.dispose();
			}
		}
	}
	private class VentanaObstaculos implements ActionListener {
		private Frame ventana_obstaculos;
		public VentanaObstaculos() {
			ventana_obstaculos = new Frame("Obst�culos");
			ventana_obstaculos.setSize(300, 100);
			ventana_obstaculos.setResizable(false);
			ventana_obstaculos.setLayout(new GridLayout(3,2));
			ventana_obstaculos.addWindowListener(new WindowListener() {
				public void windowOpened(WindowEvent e) {}
				public void windowActivated(WindowEvent e) {}
				public void windowDeactivated(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void windowClosed(WindowEvent e) {}
				public void windowClosing(WindowEvent e) {
					e.getWindow().setVisible(false);
					e.getWindow().dispose();
				}
			});
			// A�adir los elementos de la ventana Dimensiones
			Label l1 = new Label("Obst�culos (fila,columna);(fila',columna'): ");
			TextField tf1 = new TextField();
			Label l2 = new Label("C�sped bajo (fila,columna);(fila',columna'): ");
			TextField tf2 = new TextField();
			Button boton = new Button("Guardar");
			boton.addActionListener(this);
    
			ventana_obstaculos.add(l1);
			ventana_obstaculos.add(tf1);
			ventana_obstaculos.add(l2);
			ventana_obstaculos.add(tf2);
			ventana_obstaculos.add(boton);
        
			ventana_obstaculos.setVisible(true);
		}
		public void actionPerformed(ActionEvent e) {
			String texto = ((Button)e.getSource()).getLabel();
			System.out.println("Boton presionado: " + texto);
			if (texto.equals("Guardar")) {
				TextField tf_obstaculos = (TextField)ventana_obstaculos.getComponent(1);
				String obstaculos = tf_obstaculos.getText();
				System.out.println("Obstaculos: " + obstaculos);
				String vobstaculos[] = obstaculos.split(";");
				for (int i = 0; i < vobstaculos.length; i++) {
					String vcoordenadas[] = vobstaculos[i].split(",");
					matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "Obst�culo";
				}
				TextField tf_cespedbajo = (TextField)ventana_obstaculos.getComponent(3);				
				String cespedbajo = tf_cespedbajo.getText();
				System.out.println("Cesped bajo: " + cespedbajo);
				String vcespedbajo[] = cespedbajo.split(";");
				for (int i = 0; i < vcespedbajo.length; i++) {
					String vcoordenadas[] = vcespedbajo[i].split(",");
					matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "C�sped bajo";
				}
				
				ventana_obstaculos.setVisible(false);
				ventana_obstaculos.dispose();
			}
		}
	}
	private static int filas;
	// Tama�o de la Ventana: 800x600
	// M�ximo N� Filas: 20
	// M�ximo N� Columnas: 20
	private final int CELDAFILA = 36;
	private final int MARGENFILA = 40;
	private static int columnas;
	private final int CELDACOL = 26;
	private final int MARGENCOL = 40;
    private Frame ventana;
    private final int MAFilaFILAS = 21;
    private final int MAFilaCOLS = 21;
    private String matriz[][] = new String[MAFilaFILAS][MAFilaCOLS];
    private int cortacespedFila;
    private int cortacespedColumna;
    private Lienzo lienzo;
    
    public CortaCesped() {
    	// Inicializaci�n de algunas variables
    	for (int i = 1; i < MAFilaFILAS; i++) {
			for (int j = 1; j < MAFilaCOLS; j++) {
				matriz[i][j] = "C�sped alto";
			}
    	}
    	cortacespedFila = cortacespedColumna = 1;
    	//------------------------------------
        ventana = new Frame("Cortac�sped v1.0");
        
        MenuBar menu = new MenuBar();
        
        Menu cargar = new Menu("Cargar");
        MenuItem cargar_dimensiones = new MenuItem("Dimensiones");
        MenuItem cargar_obstaculos = new MenuItem("Obst�culos");
 
        cargar.add(cargar_dimensiones);
        //cortacesped.addSeparator();
        cargar.add(cargar_obstaculos);
        
        Menu generar = new Menu("Generar");
        MenuItem generar_instancia = new MenuItem("Instancia");
        
        generar.add(generar_instancia);
        
        Menu ejecutar = new Menu("Ejecutar");
        MenuItem ejecutar_algciego = new MenuItem("Alg. Ciego");
        
        ejecutar.add(ejecutar_algciego);
        
        Menu debug = new Menu("Debug");
        MenuItem debug_debug = new MenuItem ("Debug");
        
        debug.add(debug_debug);
        
        // Agregar un listener para los elementos del men�
        cargar_dimensiones.addActionListener(this);
        cargar_obstaculos.addActionListener(this);
        generar_instancia.addActionListener(this);
        ejecutar_algciego.addActionListener(this);
        debug_debug.addActionListener(this);

        menu.add(cargar);
        menu.add(generar);
        menu.add(ejecutar);
        menu.add(debug);
        
        ventana.setMenuBar(menu);
        ventana.setSize(800, 600);
        ventana.setResizable(false);
        ventana.setLayout(new BorderLayout());
        ventana.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {
                ventana.setVisible(false);
                ventana.dispose();
            }
        });       
        ventana.setVisible(true);
    }
 
    public void actionPerformed(ActionEvent e) {
    	MenuItem item = (MenuItem)e.getSource();
        String texto = item.getLabel();
        System.out.println("Opcion seleccionada: " + texto);
        if (texto.equals("Dimensiones")) {
        	VentanaDimensiones vd = new VentanaDimensiones();
        }
        else if (texto.equals("Obst�culos")) {
        	VentanaObstaculos vo = new VentanaObstaculos();
        }
        else if (texto.equals("Instancia")) {
        	dibujarInstancia();
        }
        else if (texto.equals("Alg. Ciego")) {
        	BusquedaPrimeroAnchura busquedaPrimeroAnchura = new BusquedaPrimeroAnchura(columnas, filas, this);
        	boolean resultado = busquedaPrimeroAnchura.cortarCesped();
        	if (resultado)
        		System.out.println("Objetivo cumplido");
        	else
        		System.out.println("Objetivo NO cumplido");
        	
        }
        else if (texto.equals("Debug")) {		// S�lo para realizar ciertas pruebas
        	mover(5,5);
        }
    }
    
    public void dibujarInstancia() {
    	lienzo = new Lienzo();
    	//ventana.setSize((columnas*CELDACOL)+(2*MARGENCOL), (filas*CELDAFILA)+(2*MARGENFILA));
    	ventana.add(lienzo, BorderLayout.CENTER);
    	ventana.repaint();
    }
    
    /**
     * Devuelve si est� ocupado el vecino correspondiente a alguna de las direcciones de 
 	 * movimiento (SN, SO, SS, SE).
 	 * 
     * Las casillas fuera del jard�n se contemplaran como un obst�culo
 	 * 
     * @return mapa con los sensores (SN, SO, SS, SE) como clave y <code>boolean</code>
     * como valor; siendo �ste <code>true</code> si el vecino correspondiente est� ocupado
     */
    public Map<Sensor, Boolean> getEstadoSensores() {
    	Map<Sensor, Boolean> estadoSensores = new HashMap<Sensor, Boolean>();
    	estadoSensores.put(Sensor.NORTE, false);
    	estadoSensores.put(Sensor.OESTE, false);
    	estadoSensores.put(Sensor.SUR, false);
    	estadoSensores.put(Sensor.ESTE, false);
    		
    	if (((cortacespedFila-1) < 1) || (matriz[cortacespedFila-1][cortacespedColumna].equals("Obst�culo")))
    		estadoSensores.put(Sensor.NORTE, true);  	
    	if (((cortacespedColumna-1) < 1) || (matriz[cortacespedFila][cortacespedColumna-1].equals("Obst�culo")))
    		estadoSensores.put(Sensor.OESTE, true);  	
    	if (((cortacespedFila+1) > filas) || (matriz[cortacespedFila+1][cortacespedColumna].equals("Obst�culo")))
    		estadoSensores.put(Sensor.SUR, true);
    	if (((cortacespedColumna+1) > columnas) || (matriz[cortacespedFila][cortacespedColumna+1].equals("Obst�culo")))
    		estadoSensores.put(Sensor.ESTE, true);	
    	
    	return estadoSensores;
    }
    
    public void mover(int posicionColumna, int posicionFila) {
    	this.cortacespedFila = posicionFila;
    	this.cortacespedColumna = posicionColumna;
    	
    	System.out.println("Mover a (" + posicionFila + "," + posicionColumna + ")");
    	// En este momento se debe actualizar la ventana
    	lienzo.repaint(1000000L);
    }
    
    public void cortarCesped() {
    	// S�lo cortar� el c�sped si el sensor del cortac�sped indica que �ste est� lo suficiente alto
    	if (matriz[cortacespedFila][cortacespedColumna].equals("C�sped alto")) {
    		matriz[cortacespedFila][cortacespedColumna] = "C�sped bajo";
    		System.out.println("Cortar c�sped en (" + cortacespedFila + "," + cortacespedColumna + ")");
    	}
    	// En este momento se debe actualizar la ventana
    	lienzo.repaint(10000000000L);
    }
    
    public static void main(String[] args){
        CortaCesped cc = new CortaCesped();
    }
}