package com.iaic.cortacesped.Heuristicas;

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
 * Heurística Hill-Climbing
 * Continua por el mejor de los hijos basándonos en la distancia de su hijo respecto al nodo objetivo
 * 
 * Inicialmente valoramos los hijos del nodo raíz y los introducimos en la lista, tratamos de conseguir el objetivo
 * a partir del que tenga un valor mejor (es decir, distancia mejor). En caso de que no encuentre el objetivo
 * continuará por el siguiente de la lista.
 * 
 * @author Jeray
 *
 */
//pepe
public class HillClimbing {
	
	private enum Estado	{OCUPADO, 
											 CORTADO,
											 DESCONOCIDO};
	
	private final Point POSICION_INICIAL = new Point(1,1); 
	
	// TODO: Utilizar la interfaz para colocar aquí el nodo final
	private final Point nObjetivo = new Point (11,10);
	private final int MAXIMO_NUMERO_REGRESOS_POSICION_ANTERIOR = 2;
	private Point posicionActual = POSICION_INICIAL;
	private Point posicionAnterior = POSICION_INICIAL;
	private int anchoJardin, largoJardin;
	private int contadorRegresosPosicionAnterior = 0;
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
	public HillClimbing(int anchoJardin, int largoJardin, CortaCesped cortaCesped) {
		this.anchoJardin = anchoJardin;
		this.largoJardin = largoJardin;
		this.cortaCesped = cortaCesped;
		this.jardinRecorrido = new HashMap<Point, Estado>();
		
		for (int i = 1; i <= anchoJardin; i++) {
			for (int j = 1; j <= largoJardin; j++) {
				jardinRecorrido.put(new Point(i, j), Estado.DESCONOCIDO);
			}
		}
	}
	
	
	/**
	 * Corta el cesped según la trayectoria que le indique la heurística Hill-Climbing hasta alcanzar el
	 * nodo objetivo.
	 * 
	 * @return true si se ha finalizado correctamente
	 */
	public boolean cortarCesped() {
		//Mientras no se consiga el objetivo
		while (!isNodoObjetivo()) {
			memorizarPosicionesOcupadas(cortaCesped.getEstadoSensores());  //Captamos la información de los sensores y la memorizamos
			Point posicionSiguiente = getPosicionSiguiente();  //Se obtiene la posición siguiente mediante el método getPosicionSiguiente()
			
																			/**		// se evita entrar en un bucle infinito
																					if (isMaximoNumeroRegresosPosicionAnterior())
																						posicionSiguiente = getPosicionSiguienteAleatoria();
																			**/		
			// No encuentra hacia donde moverse
			if (posicionSiguiente == null)
				return false;
														/**
																	//Contamos si hemos de volver sobre la posición anterior
																	if (isPosicionAnterior(posicionSiguiente)) {
																		contadorRegresosPosicionAnterior++;
																	} else {
																		contadorRegresosPosicionAnterior = 0;
																	}
														**/			
			//Movemos el cortacesped hacia la posición siguiente y actualizamos las variables de posición
			cortaCesped.mover(posicionSiguiente.x, posicionSiguiente.y);
			posicionAnterior = posicionActual;
			posicionActual = posicionSiguiente;
			
			//Cortamos el cesped de la posición actual y actualizamos el estado de dicha posición
			cortaCesped.cortarCesped();
			jardinRecorrido.put(posicionActual, Estado.CORTADO);
		}
		
		return true;
	}

	/*
	 * getPosicionSiguiente
	 * Calcula el siguiente punto en función de la distancia con el punto de destino e introduce en el mapa los distintos hijos
	 * 
	 */
	private Point getPosicionSiguiente() {
		Map<Integer, Point> distanciasAlObjetivo = new HashMap<Integer, Point>();

		if (isPosicionSiguienteValidaAndDesconocida(getPosicionEste()))
			distanciasAlObjetivo.put(getDistanciaObjetivo(getPosicionEste()), getPosicionEste());
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionSur()))
			distanciasAlObjetivo.put(getDistanciaObjetivo(getPosicionSur()), getPosicionSur());
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionOeste()))
			distanciasAlObjetivo.put(getDistanciaObjetivo(getPosicionOeste()), getPosicionOeste());
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionNorte()))
			distanciasAlObjetivo.put(getDistanciaObjetivo(getPosicionNorte()), getPosicionNorte());
		
		
		while (!distanciasAlObjetivo.isEmpty()) {
			int distanciaMinima = Collections.min(distanciasAlObjetivo.keySet());
			
			Point posicionSiguiente = distanciasAlObjetivo.get(distanciaMinima);
			if (isPosicionSiguienteValida(posicionSiguiente))
				return posicionSiguiente;
			else
				distanciasAlObjetivo.remove(distanciaMinima);
		}
		
		return null;
	}
	
	/*
	 * 
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
		return isPosicionFinal();
	}
	
	private boolean isPosicionInicial() {
		return posicionActual.equals(POSICION_INICIAL);
	}	

	private boolean isPosicionFinal() {
		return posicionActual.equals(nObjetivo);
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
