package com.iaic.cortacesped.busquedasCiegas;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.iaic.cortacesped.CortaCesped;
import com.iaic.cortacesped.CortaCesped.Sensor;


/**
 * 
 * Búsqueda primero en anchura: primero se expande el nodo raíz, y luego todos los nodos generados
 * por éste; luego sus sucesores, y así sucesivamente.
 * 
 * @author Tino
 *
 */

//TODO: sólo debe fallar en caso de un recinto cerrado 

public class BusquedaPrimeroAnchura {
	
	public enum Estado {OCUPADO,	// punto del jardín ocupado por un objeto, no se puede cortar 
						CORTADO,
						DESCONOCIDO};
	
	public final Point POSICION_INICIAL = new Point(1,1); 
	private Point posicionActual = POSICION_INICIAL;
	private Point posicionAnterior = POSICION_INICIAL;
	private int anchoJardin, largoJardin;
	private CortaCesped cortaCesped;
	private Map<Point, Estado> jardinRecorrido;	// mapa con el estado de los puntos del jardín, 
												// conocidos por el cortacésped
	

	// no se permite utilizar el constructor por defecto
	@SuppressWarnings("unused")
	private BusquedaPrimeroAnchura() {};

	/*
	 * Constructor con las dimensiones del jardín como parámetros, para inicializar la memoria 
	 * del cortacésped con el estado de los puntos recorridos por éste. Además se le debe pasar
	 * el objeto CortaCesped que está en ejecución.
	 * 
	 * @param anchoJardin
	 * @param largoJardin
	 * @param cortaCesped
	 */
	public BusquedaPrimeroAnchura(int anchoJardin, int largoJardin, CortaCesped cortaCesped) {
		this.anchoJardin = anchoJardin;
		this.largoJardin = largoJardin;
		this.cortaCesped = cortaCesped;
		this.jardinRecorrido = new HashMap<Point, Estado>();
		
		// a priori el cortacésped no conoce el estado de ningún punto del jardín
		for (int i = 1; i <= anchoJardin; i++) {
			for (int j = 1; j <= largoJardin; j++) {
				jardinRecorrido.put(new Point(i, j), Estado.DESCONOCIDO);
			}
		}
	}
	
	
	/**
	 * Corta el césped utilizando la búsqueda primero en anchura
	 * 
	 * @return true si se ha finalizado correctamente
	 */
	public boolean cortarCesped() {
		while (!isNodoObjetivo()) {
			// getEstadoSensores() está implementado en los métodos del cortacésped, 
			// y devolverá si está ocupado el vecino correspondiente a alguna de las direcciones de 
			// movimiento (SN, SO, SS, SE)
			memorizarPosicionesOcupadas(cortaCesped.getEstadoSensores());
			
			// moverse en una dirección que detecte libre y por dónde preferiblemente no haya pasado aún
			Point posicionSiguiente = getPosicionSiguiente();
			
			// no tiene hacia dónde moverse
			if (posicionSiguiente == null)
				return false;
			
			// mover(int x, int y) está implementado en los métodos del cortacésped, 
			// y moverá el cortacésped en la dirección (N,O,S,E) relativa a la posición especificada
			cortaCesped.mover(posicionSiguiente.x, posicionSiguiente.y);
			
			posicionAnterior = posicionActual;
			posicionActual = posicionSiguiente;
			
			// cortarCesped() está implementado en los métodos del cortacésped, 
			// y cortará el césped en caso de que el fotosensor detecte césped lo suficientemente alto
			cortaCesped.cortarCesped();
			
			// memorizar posición cortada
			jardinRecorrido.put(posicionActual, Estado.CORTADO);
		}
		
		return true;
	}

	// en caso de que se haya pasado previemente por todas las posiciones libres, se optará primero por dirigirse
	// al noroeste (hacia la posición inicial)
	private Point getPosicionSiguiente() {
		Point posicionSiguiente = null;
		
		if (isPosicionSiguienteValida(getPosicionEste(posicionActual))) {
			posicionSiguiente = getPosicionEste(posicionActual);
			if (isPosicionDesconocida(posicionSiguiente))
				return posicionSiguiente;
		}
		
		if (isPosicionSiguienteValida(getPosicionSur(posicionActual))) {
			posicionSiguiente =  getPosicionSur(posicionActual);
			if (isPosicionDesconocida(posicionSiguiente))
				return posicionSiguiente;
		}
		
		if (isPosicionSiguienteValida(getPosicionOeste(posicionActual))) {
			posicionSiguiente =  getPosicionOeste(posicionActual);
			if (isPosicionDesconocida(posicionSiguiente))
				return posicionSiguiente;
		}
		
		if (isPosicionSiguienteValida(getPosicionNorte(posicionActual))) {
			posicionSiguiente = getPosicionNorte(posicionActual);
			if (isPosicionDesconocida(posicionSiguiente))
				return posicionSiguiente;				
		}
		
		return posicionSiguiente;
	}
	
	// la posición siguiente debe estar dentro de los márgenes del jardín,
	// no estar ocupada (según el recorrido memorizado por el cortacésped)
	// y no ser la posición anterior (ya que se entraría en un bucle infinito)	
	private boolean isPosicionSiguienteValida(Point posicion) {
		return isPosicionDentroJardin(posicion) 		&&
		   	   !isPosicionOcupadaConocida(posicion) 	&&
		   	   !isPosicionAnterior(posicion);
	}
	
	private boolean isPosicionDentroJardin(Point posicion) {
		return posicion.x <= anchoJardin &&
			   posicion.x >= 1 			 &&
			   posicion.y <= largoJardin &&
			   posicion.y >= 1;
	}
	
	private boolean isPosicionAnterior(Point posicion) {
		return posicionAnterior.equals(posicion);
	}

	private boolean isPosicionOcupadaConocida(Point posicion) {
		return Estado.OCUPADO.equals(jardinRecorrido.get(posicion));
	}
	
	private boolean isPosicionDesconocida(Point posicion) {
		return Estado.DESCONOCIDO.equals(jardinRecorrido.get(posicion));
	}

	private void memorizarPosicionesOcupadas(Map<Sensor, Boolean> estadoSensores) {
		if (estadoSensores.get(Sensor.NORTE))
			jardinRecorrido.put(getPosicionNorte(posicionActual), Estado.OCUPADO);
		if (estadoSensores.get(Sensor.OESTE))
			jardinRecorrido.put(getPosicionOeste(posicionActual), Estado.OCUPADO);
		if (estadoSensores.get(Sensor.SUR))
			jardinRecorrido.put(getPosicionSur(posicionActual), Estado.OCUPADO);
		if (estadoSensores.get(Sensor.ESTE))
			jardinRecorrido.put(getPosicionEste(posicionActual), Estado.OCUPADO);
	}
	
	private boolean isNodoObjetivo() {
		return isPosicionInicial() &&
			   isJardinRecorridoCompletamente();
	}
	
	private boolean isPosicionInicial() {
		return posicionActual.equals(POSICION_INICIAL);
	}	

	private boolean isJardinRecorridoCompletamente() {
		for (Entry<Point, Estado> puntoJardin : jardinRecorrido.entrySet()) {
			if (!isPuntoJardinHecho(puntoJardin)) {
				return false;
			}
		}
		
		return true;
	}

	// punto del jardín ocupado o cortado
	private boolean isPuntoJardinHecho(Entry<Point, Estado> puntoJardin) {
		return Estado.OCUPADO.equals(puntoJardin.getValue()) ||
			   Estado.CORTADO.equals(puntoJardin.getValue());
	}
	
	public Point getPosicionNorte(Point punto) {
		return new Point(punto.x, punto.y - 1);
	}
	
	public Point getPosicionOeste(Point punto) {
		return new Point(punto.x - 1, punto.y);
	}
	
	public Point getPosicionSur(Point punto) {
		return new Point(punto.x, punto.y + 1);
	}		

	public Point getPosicionEste(Point punto) {
		return new Point(punto.x + 1, punto.y);
	}	

}
