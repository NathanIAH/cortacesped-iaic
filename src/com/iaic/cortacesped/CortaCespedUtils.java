package com.iaic.cortacesped;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.iaic.cortacesped.CortaCesped.Sensor;

public class CortaCespedUtils {

	protected enum Estado {OCUPADO, 
						   CORTADO,
						   DESCONOCIDO};

	protected final Point POSICION_INICIAL = new Point(1,1); 
	protected Point posicionActual = POSICION_INICIAL;
	protected int anchoJardin, largoJardin;
	protected CortaCesped cortaCesped;
	protected Map<Point, Estado> jardinRecorrido;	


	protected CortaCespedUtils() {};

	/**
	 * Constructor con las dimensiones del jardín como parámetros, para inicializar la memoria 
	 * del cortacésped con el estado de los puntos recorridos por éste. Además se le debe pasar
	 * el objeto CortaCesped que está en ejecución.
	 * 
	 * @param anchoJardin
	 * @param largoJardin
	 * @param cortaCesped
	 */
	public CortaCespedUtils(int anchoJardin, int largoJardin, CortaCesped cortaCesped) {
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
	
	protected boolean isPosicionInicial() {
		return posicionActual.equals(POSICION_INICIAL);
	}	

	protected boolean isJardinRecorridoCompletamente() {
		for (Entry<Point, Estado> puntoJardin : jardinRecorrido.entrySet()) {
			if (!isPuntoJardinHecho(puntoJardin)) {
				return false;
			}
		}
		
		return true;
	}

	protected boolean isPuntoJardinHecho(Entry<Point, Estado> puntoJardin) {
		return Estado.OCUPADO.equals(puntoJardin.getValue()) ||
			   Estado.CORTADO.equals(puntoJardin.getValue());
	}	

	protected boolean isPosicionSiguienteValidaAndDesconocida(Point posicion) {
		return isPosicionSiguienteValida(posicion) &&
		isPosicionDesconocida(posicion);
	}
	
	protected boolean isPosicionSiguienteValida(Point posicion) {
		return isPosicionDentroJardin(posicion) &&
	   	   	   !isPosicionOcupada(posicion);
	}
	
	protected boolean isPosicionDentroJardin(Point posicion) {
		return posicion.x <= anchoJardin &&
			   posicion.x >= 1 			 &&
			   posicion.y <= largoJardin &&
			   posicion.y >= 1;
	}

	protected boolean isPosicionOcupada(Point posicion) {
		return Estado.OCUPADO.equals(jardinRecorrido.get(posicion));
	}
	
	protected boolean isPosicionDesconocida(Point posicion) {
		return Estado.DESCONOCIDO.equals(jardinRecorrido.get(posicion));
	}

	protected void memorizarPosicionesOcupadas(Map<Sensor, Boolean> estadoSensores) {
		if (estadoSensores.get(Sensor.NORTE))
			jardinRecorrido.put(getPosicionNorte(), Estado.OCUPADO);
		if (estadoSensores.get(Sensor.OESTE))
			jardinRecorrido.put(getPosicionOeste(), Estado.OCUPADO);
		if (estadoSensores.get(Sensor.SUR))
			jardinRecorrido.put(getPosicionSur(), Estado.OCUPADO);
		if (estadoSensores.get(Sensor.ESTE))
			jardinRecorrido.put(getPosicionEste(), Estado.OCUPADO);
	}	
	
	protected Point getPosicionNorte() {
		return new Point(posicionActual.x, posicionActual.y - 1);
	}
	
	protected Point getPosicionOeste() {
		return new Point(posicionActual.x - 1, posicionActual.y);
	}
	
	protected Point getPosicionSur() {
		return new Point(posicionActual.x, posicionActual.y + 1);
	}		

	protected Point getPosicionEste() {
		return new Point(posicionActual.x + 1, posicionActual.y);
	}		
	
}
