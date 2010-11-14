package com.iaic.cortacesped.Heuristicas;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iaic.cortacesped.CortaCesped;
import com.iaic.cortacesped.CortaCespedUtils;

public class BusquedaPrimeroProfundidadMejorada extends CortaCespedUtils {

	private Point posicionAnterior = POSICION_INICIAL;

	
	// no se permite utilizar el constructor por defecto
	@SuppressWarnings("unused")
	private BusquedaPrimeroProfundidadMejorada() { };

	/**
	 * Constructor con las dimensiones del jardín como parámetros, para inicializar la memoria 
	 * del cortacésped con el estado de los puntos recorridos por éste. Además se le debe pasar
	 * el objeto CortaCesped que está en ejecución.
	 * 
	 * @param anchoJardin
	 * @param largoJardin
	 * @param cortaCesped
	 */
	public BusquedaPrimeroProfundidadMejorada(int anchoJardin, int largoJardin, CortaCesped cortaCesped) {
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
		Map<Point, List<Point>> movimientosNodosSiguienteDesconocido = new HashMap<Point, List<Point>>();
		Map<Point, List<Point>> movimientosNodosHaciaMaximoDesconocidos = new HashMap<Point, List<Point>>();
		Map<Point, List<Point>> movimientosNodosVolverPosicionInicial = new HashMap<Point, List<Point>>();
		while (!nodos.isEmpty() && !nodoObjetivo) {
			posicionAnterior = posicionActual;
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
				if (isJardinRecorridoCompletamente()) {
					if (!movimientosNodosVolverPosicionInicial.containsKey(posicionActual))
						movimientosNodosVolverPosicionInicial.put(posicionActual, getPosicionesSiguientesParaVolverPosicionInicial());

					if (movimientosNodosVolverPosicionInicial.get(posicionActual).isEmpty())
						nodos.remove(0);
					else
						nodos.add(0, movimientosNodosVolverPosicionInicial.get(posicionActual).remove(0));
				} else { 
					if (!movimientosNodosSiguienteDesconocido.containsKey(posicionActual))
						movimientosNodosSiguienteDesconocido.put(posicionActual, getPosicionesSiguientesDesconocidas());
					else
						movimientosNodosSiguienteDesconocido.put(posicionActual, actualizarPosicionesSiguientesDesconocidas(movimientosNodosSiguienteDesconocido.get(posicionActual)));
					
					if (!movimientosNodosSiguienteDesconocido.get(posicionActual).isEmpty())
						nodos.add(0, movimientosNodosSiguienteDesconocido.get(posicionActual).remove(0));
					else { 
						if (!movimientosNodosHaciaMaximoDesconocidos.containsKey(posicionActual))
							movimientosNodosHaciaMaximoDesconocidos.put(posicionActual, getPosicionesSiguientesHaciaMaximoDesconocidas());
						else
							movimientosNodosHaciaMaximoDesconocidos.put(posicionActual, actualizarPosicionesSiguientesHaciaMaximoDesconocidas(movimientosNodosHaciaMaximoDesconocidos.get(posicionActual)));
						
						if(!movimientosNodosHaciaMaximoDesconocidos.get(posicionActual).isEmpty())
							nodos.add(0, movimientosNodosHaciaMaximoDesconocidos.get(posicionActual).remove(0));
						else
							nodos.remove(0);
					}
				}
			}
		}
		
		return nodoObjetivo;
	}
	
	private List<Point> getPosicionesSiguientesDesconocidas() {
		List<Point> posicionesSiguientesDesconocidas = new ArrayList<Point>();
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionEste()))
			posicionesSiguientesDesconocidas.add(getPosicionEste());
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionSur()))
			posicionesSiguientesDesconocidas.add(getPosicionSur());
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionOeste()))
			posicionesSiguientesDesconocidas.add(getPosicionOeste());
		
		if (isPosicionSiguienteValidaAndDesconocida(getPosicionNorte()))
			posicionesSiguientesDesconocidas.add(getPosicionNorte());
		
		return posicionesSiguientesDesconocidas;
	}
	
	private List<Point> actualizarPosicionesSiguientesDesconocidas(List<Point> nodos) {
		List<Point> posicionesSiguientesDesconocidas = new ArrayList<Point>();
		
		for (Point nodo : nodos) {
			if (isPosicionSiguienteValidaAndDesconocida(nodo))
				posicionesSiguientesDesconocidas.add(nodo);
		}
		
		return posicionesSiguientesDesconocidas;
	}	
		
	private List<Point> getPosicionesSiguientesHaciaMaximoDesconocidas() {
		List<Point> posicionesSiguientesHaciaMaximoDesconocidas = new ArrayList<Point>();
		
		Map<Integer, Point> numeroPosicionesDesconocidasMap = new HashMap<Integer, Point>();
		numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasNorte(), getPosicionNorte());
		numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasOeste(), getPosicionOeste());
		numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasSur(), getPosicionSur());
		numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasEste(), getPosicionEste());
		
		while (!numeroPosicionesDesconocidasMap.isEmpty()) {
			int maximoNumeroPosicionesDesconocidas = Collections.max(numeroPosicionesDesconocidasMap.keySet());
			
			Point posicionSiguiente = numeroPosicionesDesconocidasMap.get(maximoNumeroPosicionesDesconocidas);
			if (isPosicionSiguienteValida(posicionSiguiente))
				posicionesSiguientesHaciaMaximoDesconocidas.add(posicionSiguiente);
			
			numeroPosicionesDesconocidasMap.remove(maximoNumeroPosicionesDesconocidas);
		}
		
		// añadir al final de la lista el resto de posiciones válidas
		if (isPosicionSiguienteValida(getPosicionEste()) 
				&& !posicionesSiguientesHaciaMaximoDesconocidas.contains(getPosicionEste()))
			posicionesSiguientesHaciaMaximoDesconocidas.add(getPosicionEste());
		
		if (isPosicionSiguienteValida(getPosicionSur())
				&& !posicionesSiguientesHaciaMaximoDesconocidas.contains(getPosicionSur()))
			posicionesSiguientesHaciaMaximoDesconocidas.add(getPosicionSur());
		
		if (isPosicionSiguienteValida(getPosicionOeste())
				&& !posicionesSiguientesHaciaMaximoDesconocidas.contains(getPosicionOeste()))
			posicionesSiguientesHaciaMaximoDesconocidas.add(getPosicionOeste());
		
		if (isPosicionSiguienteValida(getPosicionNorte())
				&& !posicionesSiguientesHaciaMaximoDesconocidas.contains(getPosicionNorte()))
			posicionesSiguientesHaciaMaximoDesconocidas.add(getPosicionNorte());		
		
		return posicionesSiguientesHaciaMaximoDesconocidas;
	}
	
	private List<Point> actualizarPosicionesSiguientesHaciaMaximoDesconocidas(List<Point> nodos) {
		List<Point> posicionesSiguientesHaciaMaximoDesconocidas = new ArrayList<Point>();
		
		Map<Integer, Point> numeroPosicionesDesconocidasMap = new HashMap<Integer, Point>();
		for(Point nodo : nodos) {
			if (nodo.equals(getPosicionNorte()))
				numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasNorte(), nodo);
			else if (nodo.equals(getPosicionOeste()))
				numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasOeste(), nodo);
			else if (nodo.equals(getPosicionSur()))
				numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasSur(), nodo);
			else if (nodo.equals(getPosicionEste()))
				numeroPosicionesDesconocidasMap.put(getNumeroPosicionesDesconocidasEste(), nodo);
		}
		
		while (!numeroPosicionesDesconocidasMap.isEmpty()) {
			int maximoNumeroPosicionesDesconocidas = Collections.max(numeroPosicionesDesconocidasMap.keySet());
			
			Point posicionSiguiente = numeroPosicionesDesconocidasMap.get(maximoNumeroPosicionesDesconocidas);
			if (isPosicionSiguienteValida(posicionSiguiente))
				posicionesSiguientesHaciaMaximoDesconocidas.add(posicionSiguiente);
			
			numeroPosicionesDesconocidasMap.remove(maximoNumeroPosicionesDesconocidas);
		}
		
		// añadir al final de la lista el resto de posiciones válidas
		for (Point nodo : nodos) {
			if (isPosicionSiguienteValida(nodo)
					&& !posicionesSiguientesHaciaMaximoDesconocidas.contains(nodo))
				posicionesSiguientesHaciaMaximoDesconocidas.add(nodo);
		}
		
		return posicionesSiguientesHaciaMaximoDesconocidas;
	}
	
	private List<Point> getPosicionesSiguientesParaVolverPosicionInicial() {
		List<Point> posicionesSiguientesParaVolverPosicionInicial = new ArrayList<Point>();
		
		if (isPosicionSiguienteValidaAndDistintaPosicionAnterior(getPosicionNorte()))
			posicionesSiguientesParaVolverPosicionInicial.add(getPosicionNorte());

		if (isPosicionSiguienteValidaAndDistintaPosicionAnterior(getPosicionOeste()))
			posicionesSiguientesParaVolverPosicionInicial.add(getPosicionOeste());
		
		if (isPosicionSiguienteValidaAndDistintaPosicionAnterior(getPosicionSur()))
			posicionesSiguientesParaVolverPosicionInicial.add(getPosicionSur());
		
		if (isPosicionSiguienteValidaAndDistintaPosicionAnterior(getPosicionEste()))
			posicionesSiguientesParaVolverPosicionInicial.add(getPosicionEste());
		
		return posicionesSiguientesParaVolverPosicionInicial;
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
	
	private boolean isPosicionSiguienteValidaAndDistintaPosicionAnterior(Point posicion) {
		return isPosicionSiguienteValida(posicion) &&
		   	   !isPosicionAnterior(posicion);
	}

	private boolean isPosicionAnterior(Point posicion) {
		return posicionAnterior.equals(posicion);
	}

	
	private boolean isNodoObjetivo() {
		return isPosicionInicial() &&
			   isJardinRecorridoCompletamente();
	}

}
