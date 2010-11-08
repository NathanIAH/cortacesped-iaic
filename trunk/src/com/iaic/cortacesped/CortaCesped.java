package com.iaic.cortacesped;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.iaic.cortacesped.Heuristicas.BusquedaPrimeroProfundidadMejorada;
import com.iaic.cortacesped.Heuristicas.HillClimbing;
import com.iaic.cortacesped.busquedasCiegas.BusquedaPrimeroProfundidad;

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
			// Dibujar la cuadrícula
			// Dibujar las filas de la cuadrícula
			for (int i = 0; i <= filas; i++) {
				g.drawLine(posFila, posColumna, posFila+(columnas*CELDAFILA), posColumna);
				posColumna += CELDACOL;
			}
			posColumna = MARGENCOL;
			// Dibujar las columnas de la cuadrícula
			for (int i = 0; i <= columnas; i++) {
				g.drawLine(posFila, posColumna, posFila, posColumna+(filas*CELDACOL));
				posFila += CELDAFILA;
			}
			// Dibujar las imágenes en las casillas correspondientes de la cuadrícula
			Image icespedbajo = Toolkit.getDefaultToolkit().getImage("res/imagenes/CespedBajo.jpg");
			Image icespedalto = Toolkit.getDefaultToolkit().getImage("res/imagenes/CespedAlto.jpg");
			Image iobstaculo = Toolkit.getDefaultToolkit().getImage("res/imagenes/Obstaculo.jpg");
			for (int i = 1; i <= filas; i++) {
				for (int j = 1; j <= columnas; j++) {
					if (matriz[i][j].equals("Obstáculo")) {
//						System.out.println("Obstáculo en " + i +"," + j);
						g.drawImage(iobstaculo, MARGENFILA+((j-1)*CELDAFILA), MARGENCOL+((i-1)*CELDACOL), this);
					}
					else if (matriz[i][j].equals("Césped bajo")) {
//						System.out.println("Césped bajo en " + i + "," + j);
						g.drawImage(icespedbajo, MARGENFILA+((j-1)*CELDAFILA), MARGENCOL+((i-1)*CELDACOL), this);
					}
					else if (matriz[i][j].equals("Césped alto")) {
//						System.out.println("Césped alto en " + i + "," + j);
						g.drawImage(icespedalto, MARGENFILA+((j-1)*CELDAFILA), MARGENCOL+((i-1)*CELDACOL), this);
					}
				}
			}
			Image icortacespedbajo = Toolkit.getDefaultToolkit().getImage("res/imagenes/Cortacesped_Bajo.jpg");
			Image icortacespedalto = Toolkit.getDefaultToolkit().getImage("res/imagenes/Cortacesped_Alto.jpg");
			// Dibujar el cortacésped
			if (matriz[cortacespedFila][cortacespedColumna].equals("Césped alto"))
				g.drawImage(icortacespedalto, MARGENFILA+((cortacespedColumna-1)*CELDAFILA), MARGENCOL+((cortacespedFila-1)*CELDACOL), this);
			else if (matriz[cortacespedFila][cortacespedColumna].equals("Césped bajo"))
				g.drawImage(icortacespedbajo, MARGENFILA+((cortacespedColumna-1)*CELDAFILA), MARGENCOL+((cortacespedFila-1)*CELDACOL), this);
			// Aquí habrá que contemplar que si se ha dispuesto un obstáculo en la posición (1,1) no se mostrará el cortacesped
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
			// Añadir los elementos de la ventana Dimensiones
			Label l1 = new Label("Número de filas: ");
			TextField tf1 = new TextField();
			Label l2 = new Label("Número de columnas: ");
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
	private class VentanaFinal implements ActionListener {
		private Frame ventana_final;
		public VentanaFinal() {
			ventana_final = new Frame("Punto final");
			ventana_final.setSize(300, 100);
			ventana_final.setResizable(false);
			ventana_final.setLayout(new GridLayout(3,2));
			ventana_final.addWindowListener(new WindowListener() {
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
			// Añadir los elementos de la ventana Dimensiones
			Label l1 = new Label("Punto final (x): ");
			TextField tf1 = new TextField();
			Label l2 = new Label("Punto final (y): ");
			TextField tf2 = new TextField();			
			Button boton = new Button("Guardar");
			boton.addActionListener(this);
    
			ventana_final.add(l1);
			ventana_final.add(tf1);
			ventana_final.add(l2);
			ventana_final.add(tf2);			
			ventana_final.add(boton);
        
			ventana_final.setVisible(true);
		}
		
		public void actionPerformed(ActionEvent e) {
			String texto = ((Button)e.getSource()).getLabel();
			System.out.println("Boton presionado: " + texto);
			if (texto.equals("Guardar")) {
				TextField tf_filas = (TextField)ventana_final.getComponent(1);
				int x = Integer.parseInt(tf_filas.getText());
				TextField tf_columnas = (TextField)ventana_final.getComponent(3);
				int y = Integer.parseInt(tf_columnas.getText());
				puntoFinal = new Point(x,y);
				
				ventana_final.setVisible(false);
				ventana_final.dispose();
				
	        	ejecutarHillClimbing();				
			}
		}
	}	
	private class VentanaObstaculos implements ActionListener {
		private Frame ventana_obstaculos;
		public VentanaObstaculos() {
			ventana_obstaculos = new Frame("Obstáculos");
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
			// Añadir los elementos de la ventana Dimensiones
			Label l1 = new Label("Obstáculos (fila,columna);(fila',columna'): ");
			TextField tf1 = new TextField();
			Label l2 = new Label("Césped bajo (fila,columna);(fila',columna'): ");
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
				obstaculos = tf_obstaculos.getText();
				System.out.println("Obstaculos: " + obstaculos);
				String vobstaculos[] = obstaculos.split(";");
				for (int i = 0; i < vobstaculos.length; i++) {
					String vcoordenadas[] = vobstaculos[i].split(",");
					matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "Obstáculo";
				}
				TextField tf_cespedbajo = (TextField)ventana_obstaculos.getComponent(3);				
				cespedbajo = tf_cespedbajo.getText();
				System.out.println("Cesped bajo: " + cespedbajo);
				String vcespedbajo[] = cespedbajo.split(";");
				for (int i = 0; i < vcespedbajo.length; i++) {
					String vcoordenadas[] = vcespedbajo[i].split(",");
					matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "Césped bajo";
				}
				
				ventana_obstaculos.setVisible(false);
				ventana_obstaculos.dispose();
			}
		}
	}
	private class VentanaMensaje implements ActionListener  {
		private Frame ventana_mensaje;
		public VentanaMensaje(final String mensaje) {
			ventana_mensaje = new Frame("Cortacésped v1.0 - Mensaje");
			ventana_mensaje.setSize(300, 75);
			ventana_mensaje.setResizable(false);
			ventana_mensaje.setLayout(new BorderLayout());
			ventana_mensaje.addWindowListener(new WindowListener() {
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
			// Añadir los elementos de la ventana Mensaje
			Label l1 = new Label(mensaje);	
			Button boton = new Button("Aceptar");
			ventana_mensaje.add(l1, BorderLayout.CENTER);
			ventana_mensaje.add(boton, BorderLayout.SOUTH);
			boton.addActionListener(this);
			
			ventana_mensaje.setVisible(true);
		}
		public void actionPerformed(ActionEvent e) {
			String texto = ((Button)e.getSource()).getLabel();
			System.out.println("Boton presionado: " + texto);
			if (texto.equals("Aceptar")) {
				ventana_mensaje.setVisible(false);
				ventana_mensaje.dispose();
			}
		}
	}	
	private String obstaculos;
	private String cespedbajo;	
	// Tamaño de la Ventana: 800x600
	// Máximo Nº Filas: 20
	// Máximo Nº Columnas: 20
	private final int CELDAFILA = 36;
	private final int MARGENFILA = 40;
	private int filas;
	private int columnas;
	private Point puntoFinal = new Point();
	private String matriz[][];
	private final int CELDACOL = 26;
	private final int MARGENCOL = 40;
    private Frame ventana;
    private int cortacespedFila;
    private int cortacespedColumna;
    private Lienzo lienzo;
    
    public CortaCesped() {
    	// Inicialización de algunas variables
    	cortacespedFila = cortacespedColumna = 1;
    	//------------------------------------
        ventana = new Frame("Cortacésped v1.0");
        
        MenuBar menu = new MenuBar();
        
        Menu archivo = new Menu("Archivo");
        MenuItem archivo_cargar = new MenuItem("Cargar");
        MenuItem archivo_guardar = new MenuItem("Guardar");
        
        archivo.add(archivo_cargar);
        archivo.add(archivo_guardar);        
        
        Menu cargar = new Menu("Cargar");
        MenuItem cargar_dimensiones = new MenuItem("Dimensiones");
        MenuItem cargar_obstaculos = new MenuItem("Obstáculos");
 
        cargar.add(cargar_dimensiones);
        cargar.add(cargar_obstaculos);
        
        Menu generar = new Menu("Generar");
        MenuItem generar_instancia = new MenuItem("Instancia");
        
        generar.add(generar_instancia);
        
        Menu ejecutar = new Menu("Ejecutar");
        MenuItem ejecutar_algciego = new MenuItem("Alg. Ciego");
        MenuItem ejecutar_heutodo = new MenuItem("Heu. Cortar Todo");
        MenuItem ejecutar_heuzona = new MenuItem("Heu. Cortar Zona");
        
        
        ejecutar.add(ejecutar_algciego);
        ejecutar.add(ejecutar_heutodo);
        ejecutar.add(ejecutar_heuzona);
        
        Menu debug = new Menu("Debug");
        MenuItem debug_debug = new MenuItem ("Debug");
        
        debug.add(debug_debug);
        
        // Agregar un listener para los elementos del menú
        archivo_cargar.addActionListener(this);
        archivo_guardar.addActionListener(this);
        cargar_dimensiones.addActionListener(this);
        cargar_obstaculos.addActionListener(this);
        generar_instancia.addActionListener(this);
        ejecutar_algciego.addActionListener(this);
        ejecutar_heutodo.addActionListener(this);
        ejecutar_heuzona.addActionListener(this);
        debug_debug.addActionListener(this);

        menu.add(archivo);
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
 
    private void inicializarMatriz() {
    	for (int i = 1; i < (filas + 1); i++) {
			for (int j = 1; j < (columnas + 1); j++) {
				matriz[i][j] = "Césped alto";
			}
    	}
    }    
    
    public void actionPerformed(ActionEvent e) {
    	MenuItem item = (MenuItem)e.getSource();
        String texto = item.getLabel();
        System.out.println("Opcion seleccionada: " + texto);
        if (texto.equals("Cargar")) {
        	FileDialog fd = new FileDialog(ventana, "Cargar configuración");
        	fd.show();
        	String nombre_archivo = fd.getDirectory() + fd.getFile();
        	try {
        		FileReader fr = new FileReader(nombre_archivo);
        		BufferedReader br = new BufferedReader(fr);
        		String dimensiones = br.readLine();
        		obstaculos = br.readLine();
        		cespedbajo = br.readLine();
        		fr.close();
        	
        		String vdimensiones[] = dimensiones.split(" ");
        		filas = Integer.parseInt(vdimensiones[0]);
        		columnas = Integer.parseInt(vdimensiones[1]);
        		
        		matriz = new String[filas + 1][columnas + 1];
            	inicializarMatriz();
        		
        		String vobstaculos[] = obstaculos.split(";");
        		for (int i = 0; i < vobstaculos.length; i++) {
        			String vcoordenadas[] = vobstaculos[i].split(",");
        			matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "Obstáculo";
        		}
        		
        		String vcespedbajo[] = cespedbajo.split(";");
        		for (int i = 0; i < vcespedbajo.length; i++) {
        			String vcoordenadas[] = vcespedbajo[i].split(",");
        			matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "Césped bajo";
        		}
        	} catch (IOException ioe) {
        		// ERROR
        		ioe.printStackTrace();
        	} 
        }
        else if (texto.equals("Guardar")) {
        	Calendar  c = Calendar.getInstance();
        	String dia = Integer.toString(c.get(Calendar.DATE));
        	String mes = Integer.toString(c.get(Calendar.MONTH + 1));
        	String anio = Integer.toString(c.get(Calendar.YEAR));
        	String hora = Integer.toString(c.get(Calendar.HOUR));
        	String minuto = Integer.toString(c.get(Calendar.MINUTE)); 
        	String nombre_archivo = "datos_" + dia + mes + anio + "_" + hora + minuto + ".dat";
        	// Prepara el fichero de salida:
        	// La primera línea contiene las dimensiones (filas y columnas) separadas por un espacio
        	String guardar = this.filas + " " + this.columnas + "\n";
        	// La segunda línea contiene los obstáculos
        	guardar += obstaculos + "\n";
        	// La tercera línea contiene dónde está el césped bajo
        	guardar += cespedbajo;
			try {		
				FileWriter fw = new FileWriter("C:\\" + nombre_archivo);
				PrintWriter pw = new PrintWriter(fw);
				pw.print(guardar);
				fw.close();
			} catch (IOException ioe) {
				// ERROR
				ioe.printStackTrace();
			}
        	
        }
        else if (texto.equals("Dimensiones")) {
        	VentanaDimensiones vd = new VentanaDimensiones();
        }
        else if (texto.equals("Obstáculos")) {
        	matriz = new String[filas + 1][columnas + 1];
        	inicializarMatriz();
        	
        	VentanaObstaculos vo = new VentanaObstaculos();
        }
        else if (texto.equals("Instancia")) {
        	dibujarInstancia();
        }
        else if (texto.equals("Alg. Ciego")) {
        	final BusquedaPrimeroProfundidad busquedaPrimeroProfundidad = new BusquedaPrimeroProfundidad(columnas, filas, this);

        	// ejecutamos en un nuevo hilo para permitir el repintado del canvas
        	Runnable miRunnable = new Runnable()
        	{
        		public void run() {
        			boolean resultado = busquedaPrimeroProfundidad.cortarCesped();
        			if (resultado)
        				System.out.println("Objetivo cumplido");
        			else
        				System.out.println("Objetivo NO cumplido");
        		}
        	};
        	Thread hilo = new Thread (miRunnable);
        	hilo.start();
        }
        else if (texto.equals("Heu. Cortar Todo")) {
        	final BusquedaPrimeroProfundidadMejorada busquedaPrimeroProfundidad = new BusquedaPrimeroProfundidadMejorada(columnas, filas, this);

        	// ejecutamos en un nuevo hilo para permitir el repintado del canvas
        	Runnable miRunnable = new Runnable()
        	{
        		public void run() {
        			boolean resultado = busquedaPrimeroProfundidad.cortarCesped();
        			if (resultado)
        				System.out.println("Objetivo cumplido");
        			else
        				System.out.println("Objetivo NO cumplido");
        		}
        	};
        	Thread hilo = new Thread (miRunnable);
        	hilo.start();
        }
        else if (texto.equals("Heu. Cortar Zona")) {
        	VentanaFinal vf = new VentanaFinal();
        }
        else if (texto.equals("Debug")) {		// Sólo para realizar ciertas pruebas
        	mover(5,5);
        }
    }

	private void ejecutarHillClimbing() {
		final HillClimbing hillClimbing = new HillClimbing(columnas, filas, puntoFinal, this);

		// ejecutamos en un nuevo hilo para permitir el repintado del canvas
		Runnable miRunnable = new Runnable()
		{
			public void run() {
				boolean resultado = hillClimbing.cortarCesped();
				if (resultado)
					System.out.println("Objetivo cumplido");
				else
					System.out.println("Objetivo NO cumplido");
			}
		};
		Thread hilo = new Thread (miRunnable);
		hilo.start();
	}
    
    public void dibujarInstancia() {
    	lienzo = new Lienzo();
    	//ventana.setSize((columnas*CELDACOL)+(2*MARGENCOL), (filas*CELDAFILA)+(2*MARGENFILA));
    	if ((filas <= 20) && (columnas <= 20)) { 
    		ventana.add(lienzo, BorderLayout.CENTER);
    	}
    	else {
			// MENSAJE: EL ALGORITMO SE EJECUTARÁ SIN VISUALIZAR
    		VentanaMensaje vm = new VentanaMensaje("El algoritmo se ejecutará sin visualización");
    	}
    	ventana.repaint();
    }
    
    /**
     * Devuelve si está ocupado el vecino correspondiente a alguna de las direcciones de 
 	 * movimiento (SN, SO, SS, SE).
 	 * 
     * Las casillas fuera del jardín se contemplaran como un obstáculo
 	 * 
     * @return mapa con los sensores (SN, SO, SS, SE) como clave y <code>boolean</code>
     * como valor; siendo éste <code>true</code> si el vecino correspondiente está ocupado
     */
    public Map<Sensor, Boolean> getEstadoSensores() {
    	Map<Sensor, Boolean> estadoSensores = new HashMap<Sensor, Boolean>();
    	estadoSensores.put(Sensor.NORTE, false);
    	estadoSensores.put(Sensor.OESTE, false);
    	estadoSensores.put(Sensor.SUR, false);
    	estadoSensores.put(Sensor.ESTE, false);
    		
    	if (((cortacespedFila-1) < 1) || (matriz[cortacespedFila-1][cortacespedColumna].equals("Obstáculo")))
    		estadoSensores.put(Sensor.NORTE, true);  	
    	if (((cortacespedColumna-1) < 1) || (matriz[cortacespedFila][cortacespedColumna-1].equals("Obstáculo")))
    		estadoSensores.put(Sensor.OESTE, true);  	
    	if (((cortacespedFila+1) > filas) || (matriz[cortacespedFila+1][cortacespedColumna].equals("Obstáculo")))
    		estadoSensores.put(Sensor.SUR, true);
    	if (((cortacespedColumna+1) > columnas) || (matriz[cortacespedFila][cortacespedColumna+1].equals("Obstáculo")))
    		estadoSensores.put(Sensor.ESTE, true);	
    	
    	return estadoSensores;
    }
    
    public void mover(int posicionColumna, int posicionFila) {
    	this.cortacespedFila = posicionFila;
    	this.cortacespedColumna = posicionColumna;
    	
    	System.out.println("Mover a (" + posicionFila + "," + posicionColumna + ")");
    	
    	try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
    	// En este momento se debe actualizar la ventana
    	lienzo.repaint();
    }
    
    public void cortarCesped() {
    	// Sólo cortará el césped si el sensor del cortacésped indica que éste está lo suficiente alto
    	if (matriz[cortacespedFila][cortacespedColumna].equals("Césped alto")) {
    		matriz[cortacespedFila][cortacespedColumna] = "Césped bajo";
    		System.out.println("Cortar césped en (" + cortacespedFila + "," + cortacespedColumna + ")");

    		try {
    			Thread.sleep(50);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}

    		// En este momento se debe actualizar la ventana
    		lienzo.repaint();
    	}
    }
    
    public static void main(String[] args){
        CortaCesped cc = new CortaCesped();
    }
    
}