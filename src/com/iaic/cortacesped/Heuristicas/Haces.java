package com.iaic.cortacesped.Heuristicas;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iaic.cortacesped.CortaCesped;
import com.iaic.cortacesped.CortaCespedUtils;



public class Haces extends CortaCespedUtils {
	
	private Point nObjetivo = new Point();

	
	// no se permite utilizar el constructor por defecto
	@SuppressWarnings("unused")
	private Haces() {};

	/**
	 * Constructor con las dimensiones del jardin y el cortacesped.
	 * 
	 * @param anchoJardin
	 * @param largoJardin
	 * @param cortaCesped
	 * @param nObjetivo
	 */
	public Haces(int anchoJardin, int largoJardin, Point nObjetivo, CortaCesped cortaCesped) {
		super(anchoJardin, largoJardin, cortaCesped);
		this.nObjetivo = nObjetivo;
	}
	
	
	/**
	 * Corta el cesped según la trayectoria que le indique la heurística Hill-Climbing hasta alcanzar el
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

			// mover(int x, int y) está implementado en los métodos del cortacesped, 
			// y mover el cortacesped en la dirección (N,O,S,E) relativa a la posición especificada
			cortaCesped.mover(posicionActual.x, posicionActual.y);
			
			// cortarCesped() está implementado en los métodos del cortacesped, 
			// y cortará el césped en caso de que el fotosensor detecte césped lo suficientemente alto
			cortaCesped.cortarCesped();
			
			// memorizar posici�n cortada
			jardinRecorrido.put(posicionActual, Estado.CORTADO);			

			// getEstadoSensores() est� implementado en los m�todos del cortac�sped, 
			// y devolver� si est� ocupado el vecino correspondiente a alguna de las direcciones de 
			// movimiento (SN, SO, SS, SE)
			memorizarPosicionesOcupadas(cortaCesped.getEstadoSensores());

			if (isNodoObjetivo()) {
				nodoObjetivo = true;
			} else {
				if (!movimientosNodos.containsKey(posicionActual))
					movimientosNodos.put(posicionActual, getPosicionesSiguientes(getDistanciaObjetivo(posicionActual)));
				
				if (movimientosNodos.get(posicionActual).isEmpty())
					nodos.remove(0);
				else
					nodos.add(0, movimientosNodos.get(posicionActual).remove(0));
			}
		}
		
		return nodoObjetivo;
	}

	/**
	 * Calcula las posiciones siguientes o hijos del nodo actual. Introduce en el mapa dichos nodos con las distancias
	 * hasta el punto objetivo.
	 * @return mapa con las distancias de cada nodo estudiado hasta el punto objetivo.
	 */
	private List<Point> getPosicionesSiguientes(int umbral) {
		List<Integer> distanciasAlObjetivo = new ArrayList<Integer>();
		List<Point> movimientos = new ArrayList<Point>();
        // Introduce los puntos con su distancia hasta el objetivo en 'distanciasAlObjetivo'
		
		if (getDistanciaObjetivo(getPosicionEste()) <= umbral){
			distanciasAlObjetivo.add(getDistanciaObjetivo(getPosicionEste()));
			movimientos.add(getPosicionEste());
		}

		if (getDistanciaObjetivo(getPosicionSur()) <= umbral){
 		   distanciasAlObjetivo.add(getDistanciaObjetivo(getPosicionSur()));
		   movimientos.add(getPosicionSur());
		}
	
		if (getDistanciaObjetivo(getPosicionOeste()) <= umbral){
		   distanciasAlObjetivo.add(getDistanciaObjetivo(getPosicionOeste()));
		   movimientos.add(getPosicionOeste());
		}
		
		if (getDistanciaObjetivo(getPosicionNorte()) <= umbral){
		   distanciasAlObjetivo.add(getDistanciaObjetivo(getPosicionNorte()));
		   movimientos.add(getPosicionNorte());
		}
		
		List<Point> posicionesSiguientes = new ArrayList<Point>();
		List<Point> movimientosRestantes = new ArrayList<Point>();
		
	
		while (!distanciasAlObjetivo.isEmpty()) {                                           //Mientras haya
			int distanciaMinima = Collections.min(distanciasAlObjetivo);;
			for (int i = 0; i < distanciasAlObjetivo.size(); i++) {
				if (distanciaMinima == distanciasAlObjetivo.get(i)) {
					if (isPosicionSiguienteValidaAndDesconocida(movimientos.get(i)))
						posicionesSiguientes.add(movimientos.remove(i));
					else
						movimientosRestantes.add(movimientos.remove(i));
					distanciasAlObjetivo.remove(i);
					break;
				}
			}
		}

		for (int i = 0; i < movimientosRestantes.size(); i++) {
			if (isPosicionSiguienteValida(movimientosRestantes.get(i)))
				posicionesSiguientes.add(movimientosRestantes.get(i));
		}

		return posicionesSiguientes;
	}
	
	
	/**
	 * Calcula la distancia Euclídea desde el punto dado hasta el destino
	 * @param nHijo punto dado
	 * @return distancia Euclídea
	 */
	private int getDistanciaObjetivo(Point  nHijo) {
			return  (int) Math.sqrt(Math.pow((nHijo.x - nObjetivo.x),2) + Math.pow((nHijo.y-nObjetivo.y),2));

	}
	
	/**
	 * Verdadero si el punto actual es el objetivo
	 * @return
	 */
	private boolean isNodoObjetivo() {
		return isPosicionFinal();
	}
	
	/**
	 * Verdadero si el punto actual es la posición final
	 * @return
	 */
	private boolean isPosicionFinal() {
		return posicionActual.equals(nObjetivo);
	}	

}
