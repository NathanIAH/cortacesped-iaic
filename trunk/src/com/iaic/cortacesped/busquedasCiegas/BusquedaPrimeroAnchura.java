package com.iaic.cortacesped.busquedasCiegas;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
public class BusquedaPrimeroAnchura {
	
	private enum Estado {OCUPADO,	// punto del jard�n ocupado por un objeto, no se puede cortar 
			 			 CORTADO,
						 DESCONOCIDO};
	
	private final Point POSICION_INICIAL = new Point(1,1); 
	private final int MAXIMO_NUMERO_REGRESOS_POSICION_ANTERIOR = 2;
	private Point posicionActual = POSICION_INICIAL;
	private Point posicionAnterior = POSICION_INICIAL;
	private int anchoJardin, largoJardin;
	private int contadorRegresosPosicionAnterior = 0;
	private CortaCesped cortaCesped;
	private Map<Point, Estado> jardinRecorrido;	// mapa con el estado de los puntos del jard�n, 
												// conocidos por el cortac�sped
	

	// no se permite utilizar el constructor por defecto
	@SuppressWarnings("unused")
	private BusquedaPrimeroAnchura() {};

	/**
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
			
			// se evita entrar en un bucle infinito
			if (isMaximoNumeroRegresosPosicionAnterior())
				posicionSiguiente = getPosicionSiguienteAleatoria();
			
			// no tiene hacia d�nde moverse
			if (posicionSiguiente == null)
				return false;

			if (isPosicionAnterior(posicionSiguiente)) {
				contadorRegresosPosicionAnterior++;
			} else {
				contadorRegresosPosicionAnterior = 0;
			}
			
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

	/**	
	 * Si el jard�n se ha recorrido completamente, se vuelve hacia la posici�n inicial. De lo contrario, se avanza
	 * hacia una posici�n v�lida y desconocida.
	 * En caso de que se haya pasado previemente por todas las posiciones v�lidas para el siguiente movimiento, 
	 * se optar� por dirigirse hacia la direcci�n con mayor n�mero de posiciones desconocidas.
	 */
	private Point getPosicionSiguiente() {
		if(isJardinRecorridoCompletamente())
			return getPosicionSiguienteParaVolverPosicionInicial();
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionEste()))
			return getPosicionEste();
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionSur()))
			return getPosicionSur();
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionOeste()))
			return getPosicionOeste();
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionNorte()))
			return getPosicionNorte();				
		
		Map<Integer, Point> numeroPosicionesDesconocidasMap = new HashMap<Integer, Point>();
		numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasNorte(), getPosicionNorte());
		numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasOeste(), getPosicionOeste());
		numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasSur(), getPosicionSur());
		numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasEste(), getPosicionEste());
		
		while (!numeroPosicionesDesconocidasMap.isEmpty()) {
			int maximoNumeroPosicionesDesconocidas = Collections.max(numeroPosicionesDesconocidasMap.keySet());
			
			Point posicionSiguiente = numeroPosicionesDesconocidasMap.get(maximoNumeroPosicionesDesconocidas);
			if (isPosicionSiguienteValida(posicionSiguiente))
				return posicionSiguiente;
			else
				numeroPosicionesDesconocidasMap.remove(maximoNumeroPosicionesDesconocidas);
		}
		
		return null;
	}
	
	/*
	 * Se optar� por avanzar hacia el Noroeste, d�nde se sit�a la posici�n inicial.
	 */
	private Point getPosicionSiguienteParaVolverPosicionInicial() {
		
		if (isPosicionSiguienteValidaAndDistintaPosicionAnterior(getPosicionNorte()))
			return getPosicionNorte();

		if (isPosicionSiguienteValidaAndDistintaPosicionAnterior(getPosicionOeste()))
			return getPosicionOeste();
		
		if (isPosicionSiguienteValidaAndDistintaPosicionAnterior(getPosicionSur()))
			return getPosicionSur();
		
		if (isPosicionSiguienteValidaAndDistintaPosicionAnterior(getPosicionEste()))
			return getPosicionEste();				
		
		return getPosicionSiguienteAleatoria();
	}
	
	private Point getPosicionSiguienteAleatoria() {
		List<Point> posicionSiguienteAleatoriaList = new ArrayList<Point>();
		
		if (isPosicionSiguienteValida(getPosicionNorte()))
			posicionSiguienteAleatoriaList.add(getPosicionNorte());

		if (isPosicionSiguienteValida(getPosicionOeste()))
			posicionSiguienteAleatoriaList.add(getPosicionOeste());
		
		if (isPosicionSiguienteValida(getPosicionSur()))
			posicionSiguienteAleatoriaList.add(getPosicionSur());
		
		if (isPosicionSiguienteValida(getPosicionEste()))
			posicionSiguienteAleatoriaList.add(getPosicionEste());	
		
		
		if (posicionSiguienteAleatoriaList.isEmpty())
			return null;
		else {		
			int numeroAleatorio = new Random().nextInt(posicionSiguienteAleatoriaList.size());
			return posicionSiguienteAleatoriaList.get(numeroAleatorio);
		}
	}
	
	private int getNumeroPosicionesDesconocidasNorte() {
		// si no existen posiciones al Norte
		if(posicionActual.y == 1)
			return 0;

		int numeroPosicionesDesconocidasNorte = 0;
		for (int i = 1; i <= anchoJardin; i++) {
			for (int j = posicionActual.y - 1; j >= 1; j--) {
				if(Estado.DESCONOCIDO.equals(jardinRecorrido.get(new Point(i,j))))
					numeroPosicionesDesconocidasNorte++;
			}
		}
		
		return numeroPosicionesDesconocidasNorte;
	}
	
	private int getNumeroPosicionesDesconocidasOeste() {
		// si no existen posiciones al Oeste
		if(posicionActual.x == 1)
			return 0;

		int numeroPosicionesDesconocidasOeste = 0;
		for (int i = posicionActual.x - 1; i >= 1; i--) {
			for (int j = 1; j <= largoJardin; j++) {
				if(Estado.DESCONOCIDO.equals(jardinRecorrido.get(new Point(i,j))))
					numeroPosicionesDesconocidasOeste++;
			}
		}
		
		return numeroPosicionesDesconocidasOeste;
	}
	
	private int getNumeroPosicionesDesconocidasSur() {
		// si no existen posiciones al Sur
		if(posicionActual.y == largoJardin)
			return 0;

		int numeroPosicionesDesconocidasSur = 0;
		for (int i = 1; i <= anchoJardin; i++) {
			for (int j = posicionActual.y + 1; j <= largoJardin; j++) {
				if(Estado.DESCONOCIDO.equals(jardinRecorrido.get(new Point(i,j))))
					numeroPosicionesDesconocidasSur++;
			}
		}
		
		return numeroPosicionesDesconocidasSur;
	}
	
	private int getNumeroPosicionesDesconocidasEste() {
		// si no existen posiciones al Este
		if(posicionActual.x == anchoJardin)
			return 0;

		int numeroPosicionesDesconocidasEste = 0;
		for (int i = posicionActual.x + 1; i <= anchoJardin; i++) {
			for (int j = 1; j <= largoJardin; j++) {
				if(Estado.DESCONOCIDO.equals(jardinRecorrido.get(new Point(i,j))))
					numeroPosicionesDesconocidasEste++;
			}
		}
		
		return numeroPosicionesDesconocidasEste;
	}
	
	private boolean isPosicionSiguienteValidaAndDesconocida(Point posicion) {
		return isPosicionSiguienteValida(posicion) &&
			   isPosicionDesconocida(posicion);
	}
	
	private boolean isPosicionSiguienteValidaAndDistintaPosicionAnterior(Point posicion) {
		return isPosicionSiguienteValida(posicion) &&
		   	   !isPosicionAnterior(posicion);
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

	private boolean isMaximoNumeroRegresosPosicionAnterior() {
		return contadorRegresosPosicionAnterior >= MAXIMO_NUMERO_REGRESOS_POSICION_ANTERIOR;
	}
	
	private boolean isPosicionAnterior(Point posicion) {
		return posicionAnterior.equals(posicion);
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

	/**
	 * Punto del jard�n ocupado o cortado.
	 */
	private boolean isPuntoJardinHecho(Entry<Point, Estado> puntoJardin) {
		return Estado.OCUPADO.equals(puntoJardin.getValue()) ||
			   Estado.CORTADO.equals(puntoJardin.getValue());
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
