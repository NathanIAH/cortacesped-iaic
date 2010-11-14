package com.iaic.cortacesped.Heuristicas;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iaic.cortacesped.CortaCesped;
import com.iaic.cortacesped.CortaCespedUtils;



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

public class HillClimbing extends CortaCespedUtils {
	
	private Point nObjetivo = new Point();

	
	// no se permite utilizar el constructor por defecto
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
		super(anchoJardin, largoJardin, cortaCesped);
		this.nObjetivo = nObjetivo;
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
		List<Integer> distanciasAlObjetivo = new ArrayList<Integer>();
		List<Point> movimientos = new ArrayList<Point>();

		distanciasAlObjetivo.add(getDistanciaObjetivo(getPosicionEste()));
		movimientos.add(getPosicionEste());

		distanciasAlObjetivo.add(getDistanciaObjetivo(getPosicionSur()));
		movimientos.add(getPosicionSur());

		distanciasAlObjetivo.add(getDistanciaObjetivo(getPosicionOeste()));
		movimientos.add(getPosicionOeste());

		distanciasAlObjetivo.add(getDistanciaObjetivo(getPosicionNorte()));
		movimientos.add(getPosicionNorte());
		
		List<Point> posicionesSiguientes = new ArrayList<Point>();
		List<Point> movimientosRestantes = new ArrayList<Point>();
		while (!distanciasAlObjetivo.isEmpty()) {
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
	
	private boolean isNodoObjetivo() {
		return isPosicionFinal();
	}
	
	private boolean isPosicionFinal() {
		return posicionActual.equals(nObjetivo);
	}	

}
