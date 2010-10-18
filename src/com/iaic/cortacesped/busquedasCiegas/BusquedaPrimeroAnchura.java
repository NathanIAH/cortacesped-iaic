package com.iaic.cortacesped.busquedasCiegas;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.iaic.cortacesped.CortaCesped;
import com.iaic.cortacesped.CortaCesped.Sensor;


/**
 * 
 * B�squeda primero en anchura: primero se expande el nodo ra�z, y luego todos los nodos generados
 * por �ste; luego sus sucesores, y as� sucesivamente.
 * 
 * @author Tino
 *
 */

//TODO: s�lo debe fallar en caso de un recinto cerrado 

public class BusquedaPrimeroAnchura {
	
	public enum Estado {OCUPADO,	// punto del jard�n ocupado por un objeto, no se puede cortar 
						CORTADO,
						DESCONOCIDO};
	
	public final Point POSICION_INICIAL = new Point(1,1); 
	private Point posicionActual = POSICION_INICIAL;
	private Point posicionAnterior = POSICION_INICIAL;
	private int anchoJardin, largoJardin;
	private CortaCesped cortaCesped;
	private Map<Point, Estado> jardinRecorrido;	// mapa con el estado de los puntos del jard�n, 
												// conocidos por el cortac�sped
	

	// no se permite utilizar el constructor por defecto
	@SuppressWarnings("unused")
	private BusquedaPrimeroAnchura() {};

	/*
	 * Constructor con las dimensiones del jard�n como par�metros, para inicializar la memoria 
	 * del cortac�sped con el estado de los puntos recorridos por �ste. Adem�s se le debe pasar
	 * el objeto CortaCesped que est� en ejecuci�n.
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
		
		// a priori el cortac�sped no conoce el estado de ning�n punto del jard�n
		for (int i = 1; i <= anchoJardin; i++) {
			for (int j = 1; j <= largoJardin; j++) {
				jardinRecorrido.put(new Point(i, j), Estado.DESCONOCIDO);
			}
		}
	}
	
	
	/**
	 * Corta el c�sped utilizando la b�squeda primero en anchura
	 * 
	 * @return true si se ha finalizado correctamente
	 */
	public boolean cortarCesped() {
		while (!isNodoObjetivo()) {
			// getEstadoSensores() est� implementado en los m�todos del cortac�sped, 
			// y devolver� si est� ocupado el vecino correspondiente a alguna de las direcciones de 
			// movimiento (SN, SO, SS, SE)
			memorizarPosicionesOcupadas(cortaCesped.getEstadoSensores());
			
			// moverse en una direcci�n que detecte libre y por d�nde preferiblemente no haya pasado a�n
			Point posicionSiguiente = getPosicionSiguiente();
			
			// no tiene hacia d�nde moverse
			if (posicionSiguiente == null)
				return false;
			
			// mover(int x, int y) est� implementado en los m�todos del cortac�sped, 
			// y mover� el cortac�sped en la direcci�n (N,O,S,E) relativa a la posici�n especificada
			cortaCesped.mover(posicionSiguiente.x, posicionSiguiente.y);
			
			posicionAnterior = posicionActual;
			posicionActual = posicionSiguiente;
			
			// cortarCesped() est� implementado en los m�todos del cortac�sped, 
			// y cortar� el c�sped en caso de que el fotosensor detecte c�sped lo suficientemente alto
			cortaCesped.cortarCesped();
			
			// memorizar posici�n cortada
			jardinRecorrido.put(posicionActual, Estado.CORTADO);
		}
		
		return true;
	}

	// en caso de que se haya pasado previemente por todas las posiciones libres, se optar� primero por dirigirse
	// al noroeste (hacia la posici�n inicial)
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
	
	// la posici�n siguiente debe estar dentro de los m�rgenes del jard�n,
	// no estar ocupada (seg�n el recorrido memorizado por el cortac�sped)
	// y no ser la posici�n anterior (ya que se entrar�a en un bucle infinito)	
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

	// punto del jard�n ocupado o cortado
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
