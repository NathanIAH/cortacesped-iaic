package com.iaic.cortacesped.busquedasCiegas;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iaic.cortacesped.CortaCesped;
import com.iaic.cortacesped.CortaCespedUtils;

public class BusquedaPrimeroProfundidad extends CortaCespedUtils {

	// no se permite utilizar el constructor por defecto
	@SuppressWarnings("unused")
	private BusquedaPrimeroProfundidad() {};

	/**
	 * Constructor con las dimensiones del jardín como parámetros, para inicializar la memoria 
	 * del cortacésped con el estado de los puntos recorridos por éste. Además se le debe pasar
	 * el objeto CortaCesped que está en ejecución.
	 * 
	 * @param anchoJardin
	 * @param largoJardin
	 * @param cortaCesped
	 */
	public BusquedaPrimeroProfundidad(int anchoJardin, int largoJardin, CortaCesped cortaCesped) {
		super(anchoJardin, largoJardin, cortaCesped);
	}
	
	
	/**
	 * Corta el césped utilizando la búsqueda primero en profundidad
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
	
	private List<Point> getPosicionesSiguientes() {
		List<Point> posicionesSiguientes = new ArrayList<Point>();

		if (isPosicionSiguienteValida(getPosicionEste()))
			posicionesSiguientes.add(getPosicionEste());

		if (isPosicionSiguienteValida(getPosicionSur()))
			posicionesSiguientes.add(getPosicionSur());

		if (isPosicionSiguienteValida(getPosicionOeste()))
			posicionesSiguientes.add(getPosicionOeste());

		if (isPosicionSiguienteValida(getPosicionNorte()))
			posicionesSiguientes.add(getPosicionNorte());

		return posicionesSiguientes;
	}
	
	private boolean isNodoObjetivo() {
		return isPosicionInicial() &&
			   isJardinRecorridoCompletamente();
	}
	
}
