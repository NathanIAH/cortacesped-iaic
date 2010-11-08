package com.iaic.cortacesped.Heuristicas;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iaic.cortacesped.CortaCesped;
import com.iaic.cortacesped.CortaCesped.Sensor;



/**
 * 
 * HeurÃ­stica Hill-Climbing
 * Continua por el mejor de los hijos basÃ¡ndonos en la distancia de su hijo respecto al nodo objetivo
 * 
 * Inicialmente valoramos los hijos del nodo raÃ­z y los introducimos en la lista, tratamos de conseguir el objetivo
 * a partir del que tenga un valor mejor (es decir, distancia mejor). En caso de que no encuentre el objetivo
 * continuarÃ¡ por el siguiente de la lista.
 * 
 * @author Jeray
 *
 */

public class HillClimbing {
	
	private enum Estado	{OCUPADO, 
											 CORTADO,
											 DESCONOCIDO};
	
	private final Point POSICION_INICIAL = new Point(1,1); 
	
	private Point nObjetivo = new Point();
	private Point posicionActual = POSICION_INICIAL;
	private int anchoJardin, largoJardin;
	private CortaCesped cortaCesped;
	private Map<Point, Estado> jardinRecorrido;	

	@SuppressWarnings("unused")
	private HillClimbing() {};

	/**
	 * Constructor con las dimensiones del jardin y el cortacesped.
	 * 
	 * @param anchoJardin
	 * @param largoJardin
	 * @param cortaCesped
	 * @param nObjetivo
	 */
	public HillClimbing(int anchoJardin, int largoJardin, Point nObjetivo, CortaCesped cortaCesped) {
		this.anchoJardin = anchoJardin;
		this.largoJardin = largoJardin;
		this.cortaCesped = cortaCesped;
		this.nObjetivo = nObjetivo;
		this.jardinRecorrido = new HashMap<Point, Estado>();
		
		for (int i = 1; i <= anchoJardin; i++) {
			for (int j = 1; j <= largoJardin; j++) {
				jardinRecorrido.put(new Point(i, j), Estado.DESCONOCIDO);
			}
		}
	}
	
	
	/**
	 * Corta el cesped segÃºn la trayectoria que le indique la heurÃ­stica Hill-Climbing hasta alcanzar el
	 * nodo objetivo.
	 * 
	 * @return true si se ha finalizado correctamente
	 */
	public boolean cortarCesped() {
		boolean nodoObjetivo = false;
		List<Point> nodos = new ArrayList<Point>();
		nodos.add(0, POSICION_INICIAL);
		Map<Point, List<Point>> movimientosNodos = new HashMap<Point, List<Point>>();
		while (!nodos.isEmpty() && !nodoObjetivo) {
			posicionActual = nodos.get(0);

			// mover(int x, int y) está implementado en los métodos del cortacésped, 
			// y moverá el cortacésped en la dirección (N,O,S,E) relativa a la posición especificada
			cortaCesped.mover(posicionActual.x, posicionActual.y);
			
			// cortarCesped() está implementado en los métodos del cortacésped, 
			// y cortará el césped en caso de que el fotosensor detecte césped lo suficientemente alto
			cortaCesped.cortarCesped();
			
			// memorizar posición cortada
			jardinRecorrido.put(posicionActual, Estado.CORTADO);			

			// getEstadoSensores() está implementado en los métodos del cortacésped, 
			// y devolverá si está ocupado el vecino correspondiente a alguna de las direcciones de 
			// movimiento (SN, SO, SS, SE)
			memorizarPosicionesOcupadas(cortaCesped.getEstadoSensores());

			if (isNodoObjetivo()) {
				nodoObjetivo = true;
			} else {
				if (!movimientosNodos.containsKey(posicionActual))
					movimientosNodos.put(posicionActual, getPosicionesSiguientes());
				
				if (movimientosNodos.get(posicionActual).isEmpty())
					nodos.remove(0);
				else
					nodos.add(0, movimientosNodos.get(posicionActual).remove(0));
			}
		}
		
		return nodoObjetivo;
	}

	/*
	 * getPosicionSiguiente
	 * Calcula el siguiente punto en funciÃ³n de la distancia con el punto de destino e introduce en el mapa los distintos hijos
	 * 
	 */
	private List<Point> getPosicionesSiguientes() {
		Map<Integer, Point> distanciasAlObjetivo = new HashMap<Integer, Point>();
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionEste()))
			distanciasAlObjetivo.put(getDistanciaObjetivo(getPosicionEste()), getPosicionEste());
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionSur()))
			distanciasAlObjetivo.put(getDistanciaObjetivo(getPosicionSur()), getPosicionSur());
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionOeste()))
			distanciasAlObjetivo.put(getDistanciaObjetivo(getPosicionOeste()), getPosicionOeste());
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionNorte()))
			distanciasAlObjetivo.put(getDistanciaObjetivo(getPosicionNorte()), getPosicionNorte());
		
		
		List<Point> posicionesSiguientes = new ArrayList<Point>();
		while (!distanciasAlObjetivo.isEmpty()) {
			int distanciaMinima = Collections.min(distanciasAlObjetivo.keySet());
			
			Point posicionSiguiente = distanciasAlObjetivo.get(distanciaMinima);
			if (isPosicionSiguienteValida(posicionSiguiente))
				posicionesSiguientes.add(posicionSiguiente);

			distanciasAlObjetivo.remove(distanciaMinima);
		}
		
		return posicionesSiguientes;
	}
	
	private int getDistanciaObjetivo(Point  nHijo) {
		int distancia = 0;
		//Si la fila actual es mayor que la fila objetivo
		if(nHijo.x > nObjetivo.x)
			distancia = nHijo.x - nObjetivo.x;
		else
			distancia = nObjetivo.x - nHijo.x;
		
		if(nHijo.y > nObjetivo.y)
			distancia += nHijo.y - nObjetivo.y;
		else
			distancia += nObjetivo.y - nHijo.y;
		return distancia;
	}
	
	private boolean isPosicionSiguienteValidaAndDesconocida(Point posicion) {
		return isPosicionSiguienteValida(posicion) &&
			   isPosicionDesconocida(posicion);
	}
	
	private boolean isPosicionSiguienteValida(Point posicion) {
		return isPosicionDentroJardin(posicion) &&
	   	   	   !isPosicionOcupada(posicion);
	}
	
	private boolean isPosicionDentroJardin(Point posicion) {
		return posicion.x <= anchoJardin &&
			   posicion.x >= 1 			 &&
			   posicion.y <= largoJardin &&
			   posicion.y >= 1;
	}

	private boolean isPosicionOcupada(Point posicion) {
		return Estado.OCUPADO.equals(jardinRecorrido.get(posicion));
	}
	
	private boolean isPosicionDesconocida(Point posicion) {
		return Estado.DESCONOCIDO.equals(jardinRecorrido.get(posicion));
	}

	private void memorizarPosicionesOcupadas(Map<Sensor, Boolean> estadoSensores) {
		if (estadoSensores.get(Sensor.NORTE))
			jardinRecorrido.put(getPosicionNorte(), Estado.OCUPADO);
		if (estadoSensores.get(Sensor.OESTE))
			jardinRecorrido.put(getPosicionOeste(), Estado.OCUPADO);
		if (estadoSensores.get(Sensor.SUR))
			jardinRecorrido.put(getPosicionSur(), Estado.OCUPADO);
		if (estadoSensores.get(Sensor.ESTE))
			jardinRecorrido.put(getPosicionEste(), Estado.OCUPADO);
	}
	
	private boolean isNodoObjetivo() {
		return isPosicionFinal();
	}
	
	private boolean isPosicionFinal() {
		return posicionActual.equals(nObjetivo);
	}	
	
	private Point getPosicionNorte() {
		return new Point(posicionActual.x, posicionActual.y - 1);
	}
	
	private Point getPosicionOeste() {
		return new Point(posicionActual.x - 1, posicionActual.y);
	}
	
	private Point getPosicionSur() {
		return new Point(posicionActual.x, posicionActual.y + 1);
	}		

	private Point getPosicionEste() {
		return new Point(posicionActual.x + 1, posicionActual.y);
	}	

}
