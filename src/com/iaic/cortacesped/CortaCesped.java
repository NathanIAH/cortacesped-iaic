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
import com.iaic.cortacesped.Heuristicas.Haces;
import com.iaic.cortacesped.Heuristicas.HillClimbing;
import com.iaic.cortacesped.Heuristicas.HillClimbingEuclides;
import com.iaic.cortacesped.busquedasCiegas.BusquedaPrimeroProfundidad;

public class CortaCesped implements ActionListener {
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
						//System.out.println("Obstáculo en " + i +"," + j);
						g.drawImage(iobstaculo, MARGENFILA+((j-1)*CELDAFILA), MARGENCOL+((i-1)*CELDACOL), this);
					}
					else if (matriz[i][j].equals("Césped bajo")) {
						//System.out.println("Césped bajo en " + i + "," + j);
						g.drawImage(icespedbajo, MARGENFILA+((j-1)*CELDAFILA), MARGENCOL+((i-1)*CELDACOL), this);
					}
					else if (matriz[i][j].equals("Césped alto")) {
						//System.out.println("Césped alto en " + i + "," + j);
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
			//System.out.println("Boton presionado: " + texto);
			if (texto.equals("Guardar")) { 
				ventana_dimensiones.setVisible(false);
				ventana_dimensiones.dispose();
				
				TextField tf_filas = (TextField)ventana_dimensiones.getComponent(1);
				TextField tf_columnas = (TextField)ventana_dimensiones.getComponent(3);
				if ((!tf_filas.getText().equals("")) && (!tf_columnas.getText().equals(""))) {
					filas = Integer.parseInt(tf_filas.getText());
					columnas = Integer.parseInt(tf_columnas.getText());
					//System.out.println("Filas = " + filas);
					//System.out.println("Columnas = " + columnas);
				}
				else {
					VentanaMensaje vm = new VentanaMensaje("No se ha especificado alguna dimensi—n");
				}
			}
		}
	}
	private class VentanaObstaculos implements ActionListener {
		private Frame ventana_obstaculos;
		public VentanaObstaculos() {
			ventana_obstaculos = new Frame("Obstáculos");
			ventana_obstaculos.setSize(550, 100);
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
			//System.out.println("Boton presionado: " + texto);
			if (texto.equals("Guardar")) {
				TextField tf_obstaculos = (TextField)ventana_obstaculos.getComponent(1);
				obstaculos = tf_obstaculos.getText();
				//System.out.println("Obstaculos: " + obstaculos);
				if (!obstaculos.equals("")) {
					String vobstaculos[] = obstaculos.split(";");
					for (int i = 0; i < vobstaculos.length; i++) {
						String vcoordenadas[] = vobstaculos[i].split(",");
						matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "Obstáculo";
					}
				}
				TextField tf_cespedbajo = (TextField)ventana_obstaculos.getComponent(3);
				cespedbajo = tf_cespedbajo.getText();
				//System.out.println("Cesped bajo: " + cespedbajo);
				if (!cespedbajo.equals("")) {
					String vcespedbajo[] = cespedbajo.split(";");
					for (int i = 0; i < vcespedbajo.length; i++) {
						String vcoordenadas[] = vcespedbajo[i].split(",");
						matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "Césped bajo";
					}
				}
				guardarMatriz();
				
				ventana_obstaculos.setVisible(false);
				ventana_obstaculos.dispose();
			}
		}
	}
	private class VentanaObstaculos2 implements ActionListener {
		private Frame ventana_obstaculos;
		public VentanaObstaculos2() {
			ventana_obstaculos = new Frame("Obstáculos");
			ventana_obstaculos.setSize(400, 100);
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
			Label l1 = new Label("Número de Obstáculos");
			TextField tf1 = new TextField();
			Label l2 = new Label("Número de Césped bajo");
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
		
		public int generarAleatorio(int min, int max) {
			return (int)(Math.random()*(max-min))+min;
		}
		
		public void actionPerformed(ActionEvent e) {
			String texto = ((Button)e.getSource()).getLabel();
			//System.out.println("Boton presionado: " + texto);
			if (texto.equals("Guardar")) {
				TextField tf_obstaculos = (TextField)ventana_obstaculos.getComponent(1);
				int nobstaculos = Integer.parseInt(tf_obstaculos.getText());
				for (int i = 0; i < nobstaculos; i++) {
					int fila, columna;
					do {
						fila = generarAleatorio(1, filas);
						columna = generarAleatorio(1, columnas);
					} while (((fila == 1) && (columna == 1)) || (matriz[fila][columna].equals("Obstáculo")));
					matriz[fila][columna] = "Obstáculo";
					obstaculos += fila + "," + columna + ";";
				}
				
				TextField tf_cespedbajo = (TextField)ventana_obstaculos.getComponent(3);				
				int ncespedbajo = Integer.parseInt(tf_cespedbajo.getText());
				for (int i = 0; i < ncespedbajo; i++) {
					int fila, columna;
					do {
						fila = generarAleatorio(1, filas);
						columna = generarAleatorio(1, columnas);
					} while ((matriz[fila][columna].equals("Obstáculo")) || 
							(matriz[fila][columna].equals("Césped bajo")));
					matriz[fila][columna] = "Césped bajo";
					cespedbajo += fila + "," + columna + ";";
				}
				guardarMatriz();
				
				ventana_obstaculos.setVisible(false);
				ventana_obstaculos.dispose();
			}
		}
	}
	private class VentanaFinal implements ActionListener {
		private Frame ventana_final;
		private String algoritmo;
		public VentanaFinal(String algoritmo) {
			this.algoritmo = algoritmo;
			
			ventana_final = new Frame("Punto final");
			ventana_final.setSize(350, 75);
			ventana_final.setResizable(false);
			ventana_final.setLayout(new GridLayout(2,1));
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
			Label l1 = new Label("Punto final (fila, columna): ");
			TextField tf1 = new TextField();			
			Button boton = new Button("Guardar");
			boton.addActionListener(this);
    
			ventana_final.add(l1);
			ventana_final.add(tf1);		
			ventana_final.add(boton);
        
			ventana_final.setVisible(true);
		}
		
		public void actionPerformed(ActionEvent e) {
			String texto = ((Button)e.getSource()).getLabel();
			//System.out.println("Boton presionado: " + texto);
			if (texto.equals("Guardar")) {
				TextField tf_puntofinal = (TextField)ventana_final.getComponent(1);
				String puntofinal = tf_puntofinal.getText();
				String vpuntofinal[] = puntofinal.split(",");
				int x = Integer.parseInt(vpuntofinal[0]);
				int y = Integer.parseInt(vpuntofinal[1]);
				
				ventana_final.setVisible(false);
				ventana_final.dispose();
				
				puntoFinal = new Point(x,y);
				
				
	        if ("Heu. HillClimbing".equals(algoritmo))
	        	ejecutarHillClimbing();			
	        else if ("Heu. Haces".equals(algoritmo))
	        	ejecutarHaces();	
	        else if ("Heu. Euclides".equals(algoritmo))
	        	ejecutarHillClimbingEuclides();	
					
			}
		}
	}	
	private class VentanaMensaje implements ActionListener  {
		private Frame ventana_mensaje;
		public VentanaMensaje(final String mensaje) {
			ventana_mensaje = new Frame("Cortacésped v1.0 - Mensaje");
			ventana_mensaje.setSize(400, 75);
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
			//System.out.println("Boton presionado: " + texto);
			if (texto.equals("Aceptar")) {
				ventana_mensaje.setVisible(false);
				ventana_mensaje.dispose();
			}
		}
	}	
	private String obstaculos;
	private String cespedbajo;	
	// Tamaño de la Ventana: 800x600
	// Máximo Nº Filas visualizables: 20
	// Máximo Nº Columnas visualizables: 20
	public static enum Sensor {NORTE, SUR, ESTE, OESTE};
	private final int CELDAFILA = 36;
	private final int MARGENFILA = 40;
	private int filas;
	private int columnas;
	private int movimientos;
	private Point puntoFinal = new Point();
	private String matriz[][];
	private String matrizRestaurar[][]; 
	private final int CELDACOL = 26;
	private final int MARGENCOL = 40;
    private Frame ventana;
    private int cortacespedFila;
    private int cortacespedColumna;
    private Lienzo lienzo;
    private boolean generado;
    
    public CortaCesped() {
    	// Inicialización de algunas variables
    	filas = columnas = 0;
    	obstaculos = cespedbajo = "";
    	generado = false;
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
        
        Menu cargar_obstaculos = new Menu("Obstáculos");
        MenuItem cargar_obstaculos_manual = new MenuItem("Manual");
        MenuItem cargar_obstaculos_automatico = new MenuItem("Automático");
        cargar_obstaculos.add(cargar_obstaculos_manual);
        cargar_obstaculos.add(cargar_obstaculos_automatico);
        
        cargar.add(cargar_dimensiones);
        cargar.add(cargar_obstaculos);
        
        Menu instancia = new Menu("Instancia");
        MenuItem instancia_generar = new MenuItem("Generar");
        MenuItem instancia_restaurar = new MenuItem("Restaurar");
        
        instancia.add(instancia_generar);
        instancia.add(instancia_restaurar);
        
        Menu ejecutar = new Menu("Ejecutar");
        MenuItem ejecutar_algciego = new MenuItem("Alg. Ciego");
        MenuItem ejecutar_heutodo = new MenuItem("Heu. Cortar Todo");
        MenuItem ejecutar_heuzona = new MenuItem("Heu. HillClimbing");
        MenuItem ejecutar_heuzona2 = new MenuItem("Heu. Haces");
        MenuItem ejecutar_heuzona3 = new MenuItem("Heu. Euclides");
        
        
        ejecutar.add(ejecutar_algciego);
        ejecutar.add(ejecutar_heutodo);
        ejecutar.add(ejecutar_heuzona);
        ejecutar.add(ejecutar_heuzona2);
        ejecutar.add(ejecutar_heuzona3); 
        
        //Menu debug = new Menu("Debug");
        //MenuItem debug_debug = new MenuItem ("Debug");
        
        //debug.add(debug_debug);
        
        // Agregar un listener para los elementos del menú
        archivo_cargar.addActionListener(this);
        archivo_guardar.addActionListener(this);
        cargar_dimensiones.addActionListener(this);
        cargar_obstaculos_manual.addActionListener(this);
        cargar_obstaculos_automatico.addActionListener(this);
        instancia_generar.addActionListener(this);
        instancia_restaurar.addActionListener(this);
        ejecutar_algciego.addActionListener(this);
        ejecutar_heutodo.addActionListener(this);
        ejecutar_heuzona.addActionListener(this);
        ejecutar_heuzona2.addActionListener(this);
        ejecutar_heuzona3.addActionListener(this); 
        //debug_debug.addActionListener(this);

        menu.add(archivo);
        menu.add(cargar);
        menu.add(instancia);
        menu.add(ejecutar);
        //menu.add(debug);
        
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
    	for (int i = 1; i <= filas; i++) {
			for (int j = 1; j <= columnas; j++) {
				matriz[i][j] = "Césped alto";
			}
    	}
    }    
    
    public void actionPerformed(ActionEvent e) {
    	MenuItem item = (MenuItem)e.getSource();
        String texto = item.getLabel();
        //System.out.println("Opcion seleccionada: " + texto);
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
        		matrizRestaurar = new String[filas + 1][columnas + 1]; 
            	inicializarMatriz();
        		
        		String vobstaculos[] = obstaculos.split(";");
        		for (int i = 0; i < vobstaculos.length; i++) {
        			String vcoordenadas[] = vobstaculos[i].split(",");
        			matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "Obstáculo";
        		}
        		
        		if (cespedbajo != null) {
        			String vcespedbajo[] = cespedbajo.split(";");
        			for (int i = 0; i < vcespedbajo.length; i++) {
        				String vcoordenadas[] = vcespedbajo[i].split(",");
        				matriz[Integer.parseInt(vcoordenadas[0])][Integer.parseInt(vcoordenadas[1])] = "Césped bajo";
        			}
        		}
        		guardarMatriz();
        	} catch (IOException ioe) {
        		// No imprimimos la traza por si el usuario cancela la carga del archivo de datos
        		//ioe.printStackTrace();
        		VentanaMensaje vm = new VentanaMensaje("No se han cargado los datos");
        	} 
        }
        else if (texto.equals("Guardar")) {
        	if (!obstaculos.equals("")) {	
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
        			FileWriter fw = new FileWriter(nombre_archivo);
        			PrintWriter pw = new PrintWriter(fw);
					pw.print(guardar);
					fw.close();
        		} catch (IOException ioe) {
        			ioe.printStackTrace();
        		}
        	}
        	else { 
        		VentanaMensaje vm = new VentanaMensaje("No existen datos para su guardado");
        	}
        }
        else if (texto.equals("Dimensiones")) {
        	VentanaDimensiones vd = new VentanaDimensiones();
        	matriz = new String[filas + 1][columnas + 1];
        	matrizRestaurar = new String[filas + 1][columnas + 1];
        	inicializarMatriz();
        }
        else if (texto.equals("Manual")) {
        	if ((filas > 0) && (columnas > 0)) {
        		matriz = new String[filas + 1][columnas + 1];
        		matrizRestaurar = new String[filas + 1][columnas + 1];
        		inicializarMatriz();
        	
        		VentanaObstaculos vo = new VentanaObstaculos();
        	}
        	else {
        		VentanaMensaje vm = new VentanaMensaje("Introduzca primero las dimensiones");
        	}
        }
        else if (texto.equals("Automático")) {
        	if ((filas > 0) && (columnas > 0)) {
        		matriz = new String[filas + 1][columnas + 1];
        		matrizRestaurar = new String[filas + 1][columnas + 1];
        		inicializarMatriz();
        	
        		VentanaObstaculos2 vo2 = new VentanaObstaculos2();
        	}
        	else {
        		VentanaMensaje vm = new VentanaMensaje("Introduzca primero las dimensiones");
        	}
        }
        else if (texto.equals("Generar")) {
        	// S—lo se generar‡ si se pusieron nœmero de filas y columnas y algœn obst‡culo
        	if ((filas > 0) && (columnas > 0) && (!obstaculos.equals(""))) {
        		dibujarInstancia();
        		generado = true;
        	}
        	else {
        		VentanaMensaje vm = new VentanaMensaje("Introduzca primero las dimensiones y obst‡culos"); 
        	}
        }
        else if (texto.equals("Restaurar")) {
        	if (generado) {
        		restaurarMatriz();
        		dibujarInstancia();
        	}
        	else {
        		VentanaMensaje vm = new VentanaMensaje("Esta opci—n requiere que se genere primero");
        	}
        }
        else if (texto.equals("Alg. Ciego")) {
        	if (generado) {
        		final BusquedaPrimeroProfundidad busquedaPrimeroProfundidad = new BusquedaPrimeroProfundidad(columnas, filas, this);

        		// ejecutamos en un nuevo hilo para permitir el repintado del canvas
        		Runnable miRunnable = new Runnable()
        		{
        			public void run() {
        				long t0 = System.currentTimeMillis();
        				movimientos = 0;
        				boolean resultado = busquedaPrimeroProfundidad.cortarCesped();
        				if (resultado)
        					System.out.println("Objetivo cumplido (Algoritmo Ciego)");
        				else
        					System.out.println("Objetivo NO cumplido");
        				long tf = System.currentTimeMillis();
        				System.out.println("Tiempo: " + ((tf-t0)/1000) + "s");
        				System.out.println("Movimientos: " + movimientos);
        			}
        		};
        		Thread hilo = new Thread (miRunnable);
        		hilo.start();
        	} 
        	else {
        		VentanaMensaje vm = new VentanaMensaje("Se debe generar la instancia antes de ejecutar el algoritmo");
        	}
        }
        else if (texto.equals("Heu. Cortar Todo")) {
        	if (generado) {
        		final BusquedaPrimeroProfundidadMejorada busquedaPrimeroProfundidad = new BusquedaPrimeroProfundidadMejorada(columnas, filas, this);
        	
        		// ejecutamos en un nuevo hilo para permitir el repintado del canvas
        		Runnable miRunnable = new Runnable()
        		{
        			public void run() {
        				long t0 = System.currentTimeMillis();
        				movimientos = 0;
        				boolean resultado = busquedaPrimeroProfundidad.cortarCesped();
        				if (resultado)
        					System.out.println("Objetivo cumplido (Heurística Cortar Todo)");
        				else
        					System.out.println("Objetivo NO cumplido");
        				long tf = System.currentTimeMillis();
        				System.out.println("Tiempo: " + ((tf-t0)/1000) + "s");
        				System.out.println("Movimientos: " + movimientos);
        			}
        		};
        		Thread hilo = new Thread (miRunnable);
        		hilo.start();
        	}
        	else {
        		VentanaMensaje vm = new VentanaMensaje("Se debe generar la instancia antes de ejecutar el algoritmo");
        	}
        }
        else if (texto.equals("Heu. HillClimbing")) {
        	if (generado) {
        		VentanaFinal vf = new VentanaFinal("Heu. HillClimbing");
        	}
        	else {
        		VentanaMensaje vm = new VentanaMensaje("Se debe generar la instancia antes de ejecutar el algoritmo");
        	}
        }
        else if (texto.equals("Heu. Haces")) {
        	if (generado) {
        		VentanaFinal vf = new VentanaFinal("Heu. Haces");
        	}
        	else {
        		VentanaMensaje vm = new VentanaMensaje("Se debe generar la instancia antes de ejecutar el algoritmo");
        	}
        }
        else if (texto.equals("Heu. Euclides")) {
        	if (generado) {
        		VentanaFinal vf = new VentanaFinal("Heu. Euclides");
        	}
        	else {
        		VentanaMensaje vm = new VentanaMensaje("Se debe generar la instancia antes de ejecutar el algoritmo");
        	}
        }     
        //else if (texto.equals("Debug")) {		// Sólo para realizar ciertas pruebas
        	//mover(5,5);
        //}
    }

	private void ejecutarHillClimbing() {
		final HillClimbing hillClimbing = new HillClimbing(columnas, filas, puntoFinal, this);

		// Ejecutamos en un nuevo hilo para permitir el repintado del canvas
		Runnable miRunnable = new Runnable()
		{
			public void run() {
				long t0 = System.currentTimeMillis();
				movimientos = 0;
				boolean resultado = hillClimbing.cortarCesped();
				if (resultado)
					System.out.println("Objetivo cumplido (Hill Climbing)");
				else
					System.out.println("Objetivo NO cumplido");
				long tf = System.currentTimeMillis();
				System.out.println("Tiempo: " + ((tf-t0)/1000) + "s");
				System.out.println("Movimientos: " + movimientos);
			}
		};
		Thread hilo = new Thread (miRunnable);
		hilo.start();
	}
	
	private void ejecutarHillClimbingEuclides() {
		final HillClimbingEuclides hillClimbingEuclides = new HillClimbingEuclides(columnas, filas, puntoFinal, this);

		// Ejecutamos en un nuevo hilo para permitir el repintado del canvas
		Runnable miRunnable = new Runnable()
		{
			public void run() {
				long t0 = System.currentTimeMillis();
				movimientos = 0;
				boolean resultado = hillClimbingEuclides.cortarCesped();
				if (resultado)
					System.out.println("Objetivo cumplido (Euclides)");
				else
					System.out.println("Objetivo NO cumplido");
				long tf = System.currentTimeMillis();
				System.out.println("Tiempo: " + ((tf-t0)/1000) + "s");
				System.out.println("Movimientos: " + movimientos);
			}
		};
		Thread hilo = new Thread (miRunnable);
		hilo.start();
	}
	
	private void ejecutarHaces() {
		final Haces haces = new Haces(columnas, filas, puntoFinal, this);

		// Ejecutamos en un nuevo hilo para permitir el repintado del canvas
		Runnable miRunnable = new Runnable()
		{
			public void run() {
				long t0 = System.currentTimeMillis();
				movimientos = 0;
				boolean resultado = haces.cortarCesped();
				if (resultado)
					System.out.println("Objetivo cumplido (Haces)");
				else
					System.out.println("Objetivo NO cumplido");
				long tf = System.currentTimeMillis();
				System.out.println("Tiempo: " + ((tf-t0)/1000) + "s");
				System.out.println("Movimientos: " + movimientos);
			}
		};
		Thread hilo = new Thread (miRunnable);
		hilo.start();
	}	
	
	public void guardarMatriz() {
		for (int i = 1; i <= filas; i++)
			for (int j = 1; j <= columnas; j++)
				matrizRestaurar[i][j] = matriz[i][j];
	}
	
	public void restaurarMatriz() {
		for (int i = 1; i <= filas; i++)
			for (int j = 1; j <= columnas; j++)
				matriz[i][j] = matrizRestaurar[i][j];
	}
    
    public void dibujarInstancia() {
    	if (lienzo != null)
    		ventana.remove(lienzo);
    	
    	lienzo = new Lienzo();
    	//ventana.setSize((columnas*CELDACOL)+(2*MARGENCOL), (filas*CELDAFILA)+(2*MARGENFILA));
    	if ((filas <= 20) && (columnas <= 20)) { 
    		ventana.add(lienzo, BorderLayout.CENTER);
    	}
    	else {
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
    	movimientos++;
    	
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