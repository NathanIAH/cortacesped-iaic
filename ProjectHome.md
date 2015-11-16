El entorno del agente cortacésped autónomo se puede suponer rectangular de dimensiones M x
N y constituido por celdas libres y ocupadas, donde se desenvuelve un robot cortacésped que
puede efectuar acciones de movimiento, una cada vez, desde la casilla actual a una de las 4-
vecinas (Norte, Sur, Este u Oeste) que no se encuentre ocupada. El robot posee un vector de
percepción de componentes binarias constituido por:

• El estado de un sensor de proximidad (SN, SO, SS, SE) por cada una de las direcciones de
movimiento, que detecta si el vecino correspondiente está ocupado por algún obstáculo
(Si=1).

• Un fotosensor F que detecta si el espacio situado debajo del robot cortacésped contiene
césped lo suficientemente alto, que debe ser cortado (F = 1) o no (F = 0).

• Un sensor infrarrojo I que detecta cuando ha alcanzado su posición de reposo (I = 1).
Las casillas ocupadas pueden corresponder a obstáculos (macetas, personas, árboles, piscina,…)
o a césped demasiado alto. Las casillas libres corresponden con celdas libres de obstáculos y
césped bajo.
Se pretende:

1. Cortar el césped de todo el jardín: Partiendo de la posición de reposo P (p.e. la posición
de recarga de baterías del robot cortacésped), recorrer todo el jardín, cortando el césped en
aquellos lugares en que esté demasiado alto y no parar hasta alcanzar de nuevo la posición
de reposo. El robot cortacésped no tiene acceso a un mapa topológico que contenga una
descripción del entorno con los elementos que aparecen y su posición, como los obstáculos
o la altura del césped.

2. Cortar el césped de una zona determinada del jardín: Partiendo de una celda inicial S, se
desea cortar el césped en todas aquellas celdas que se encuentren en la trayectoria mínima
hasta alcanzar la celda Z. Para la resolución de este problema, se utilizarán funciones
heurísticas, asimismo, se puede resolver el problema usando un mapa topológico. Hay que
2 /2
justificar la decisión tomada tanto en la elección de la función heurística, como en la
elección de la utilización ó no, de un mapa topológico, en la resolución del problema.

3. Realización de estudio experimental: con el fin de determinar el rendimiento de las
estrategias empleadas, se ha de realizar un estudio experimental que refleje el número de
movimientos por parte del cortacésped, utilizando diferentes tamaños de escenarios de
pruebas (un mínimo de tres tamaños diferentes), así como la utilización de diferentes
heurísticas (un mínimo de tres heurísticas diferentes) para cada escenario de prueba.
Asimismo, se mostrará un ejemplo del resultado de la trayectoria realizada por el
cortacésped para el mejor y peor caso de cada heurística empleada.