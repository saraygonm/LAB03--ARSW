
## Escuela Colombiana de Ingeniería
### Arquitecturas de Software – ARSW
#### AUTORES:
- [Saray Alieth Mendivelso](https://github.com/saraygonm)
- [Milton Andres Gutierrez](https://github.com/MiltonGutierrez)


#### Ejercicio – programación concurrente, condiciones de carrera y sincronización de hilos. EJERCICIO INDIVIDUAL O EN PAREJAS.

##### Parte I – Antes de terminar la clase.

Control de hilos con wait/notify. Productor/consumidor.

1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este consumo?, cual es la clase responsable?

##### Consumo del CPU cuando inicia el programa:
- El consumo  de CPU es relativamente bajo, alrededor del 9.7%. Desde una perspectiva general ésto puede deberse a varis razones como la naturaleza de la tarea que está ejecutando el programa, el estado de los hilos de ejecución o la eficiencia del código.

<p align="center">
<img src="img/prodcons/parte1/consumoCPU1.png" alt="Hilo CountThread" width="700px">
</p>

##### Consumo del CPU cuando finaliza el programa:

- El consumo de CPU sigue siendo constante, alrededor del 9.4%.
<p align="center">
<img src="img/prodcons/parte1/finalConsumoCPU1.png" alt="Hilo CountThread" width="700px">
</p>

##### Salida en consola:

<p align="center">
<img src="img/prodcons/parte1/salidaterminalCPU1.png" alt="Hilo CountThread" width="700px">
</p>

##### ¿A qué se debe este consumo?:

- Este consumo se debe a que en la clase `Consumer`; el método `run` contiene un bucle `while` que se ejecuta continuamente, incluso cuando la lista está vacía. Esta espera activa causa un aumento innecesario en el consumo de CPU,  afectando negativamente el rendimiento del programa.

<p align="center">
<img src="img/prodcons/parte1/clase.png" alt="clase" width="700px">
</p>

2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU, teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido. Verifique con JVisualVM que el consumo de CPU se reduzca.

Para poder corregur lo mencionado anteriormente, simplemente se sincroniza la parte del codigo quel consumer utilizando como objeto la cola, de modo que si esta vacia, se pide que el Thread que la este usando espere (en este caso seria el mismo Thread de **consumer**) esto para que se espere al que  *producer*, siga llenando la cola.
<p align="center">
<img src="img/prodcons/parte1/solucion1.png" alt="sol1" width="700px">
</p>

En el caso de la ejecución del *Producer* podemos observar como se permite que este añada elementos a la cola, y despues de esto notifica a todos los Threads esperando para que puedan acceder a la cola.
<p align="center">
<img src="img/prodcons/parte1/solucion2.png" alt="sol2" width="700px">
</p>

Con estas modificaciones podemos observar el cambio en el uso del CPU:
<p align="center">
<img src="img/prodcons/parte1/CPUimplementacionMejorada.png" alt="CPUIMEJOR" width="700px">
</p>

3. Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento. Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos debería tener, a lo sumo en la cola), haga que dicho límite se respete. Revise el API de la colección usada como cola para ver cómo garantizar que dicho límite no se supere. Verifique que, al poner un límite pequeño para el 'stock', no haya consumo alto de CPU ni errores.

Usando la colección LinkedBlockingQueue, se permitia establecer un limite al que está podía llegar, simplemente pasandole este como parametro al momento de instanciarla. Además se puso unos limites de tiempo en los que tanto el productor podia producir y el consumidor consumir.

<p align="center">
<img src="img/prodcons/parte1/solucion1.3.1.png" alt="CPUIMEJOR" width="700px">
</p>

<p align="center">
<img src="img/prodcons/parte1/solucion1.3.2.png" alt="CPUIMEJOR" width="700px">
</p>

##### Parte II. – Antes de terminar la clase.

Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. Teniendo esto en cuenta, haga que:

- La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias requerido que determina si un host es confiable o no (_BLACK_LIST_ALARM_COUNT_).
- Lo anterior, garantizando que no se den condiciones de carrera.

Para poder realizar esto, una solución bastante simple y eficiente, fue añadir un AtomicInteger que nos sirve como contador de las veces que se encuentra la lista como negra, de manera que se este objeto se comparta con cada uno de los *BlackListThreads* y simplemente se estableza la condición en el for que si este objeto llega a obtener un valor igual a 5 se termine la ejecución de cada uno de los Threads.

<p align="center">
<img src="img/prodcons/parte2/atomicInteger.png" alt="CPUIMEJOR" width="700px">
</p>

Aquí se muestra el cambio en el metodo *run()* de la clase BlackListThreads:

<p align="center">
<img src="img/prodcons/parte2/atomicInteger2.png" alt="CPUIMEJOR" width="700px">
</p>

Aquí se muestra los resultados (como no se verifican todas las listas existentes):

<p align="center">
<img src="img/prodcons/parte2/atomicInteger3.png" alt="CPUIMEJOR" width="700px">
</p>

##### Parte III. – Avance para el martes, antes de clase.

Sincronización y Dead-Locks.

![](http://files.explosm.net/comics/Matt/Bummed-forever.png)

1. Revise el programa “highlander-simulator”, dispuesto en el paquete edu.eci.arsw.highlandersim. Este es un juego en el que:

	* Se tienen N jugadores inmortales.
	* Cada jugador conoce a los N-1 jugador restantes.
	* Cada jugador, permanentemente, ataca a algún otro inmortal. El que primero ataca le resta M puntos de vida a su contrincante, y aumenta en esta misma cantidad sus propios puntos de vida.
	* El juego podría nunca tener un único ganador. Lo más probable es que al final sólo queden dos, peleando indefinidamente quitando y sumando puntos de vida.

2. Revise el código e identifique cómo se implemento la funcionalidad antes indicada. Dada la intención del juego, un invariante debería ser que la sumatoria de los puntos de vida de todos los jugadores siempre sea el mismo(claro está, en un instante de tiempo en el que no esté en proceso una operación de incremento/reducción de tiempo). Para este caso, para N jugadores, cual debería ser este valor?.

Teniendo en cuenta el valor estandar de vida DEFAULT_IMMORTAL_HEALTH, el valor de la sumatoria de los puntos de vida de todos los jugadores debería de ser de 100*N, en la implementación notamos que instancian un total de 3 inmortales, por lo que sumatoria en teoria deberia de ser de 300 puntos.

<p align="center">
<img src="img/prodcons/parte3/3.2.1.png" alt="Cantidad de inmortales" width="700px">
</p>

<p align="center">
<img src="img/prodcons/parte3/3.2.2.png" alt="Vida inmortales" width="700px">
</p>

3. Ejecute la aplicación y verifique cómo funcionan las opción ‘pause and check’. Se cumple el invariante?.

La opción *pause and check* retorna la cantidad de inmortales junto a su vida, además de la sumatoria de la vida de todos los inmortales (esto sin parar la ejecución de los inmortales). Al utilizar varias veces esta opción, notamos que el invariante no se cumple, es más hasta se encuentra que en cada momento hay una sumatoria con un valor diferente como se muestra en las capturas:

<p align="center">
<img src="img/prodcons/parte3/3.3.1.png" alt="Vida inmortales" width="700px">
</p>

<p align="center">
<img src="img/prodcons/parte3/3.3.2.png" alt="Vida inmortales" width="700px">
</p>


4. Una primera hipótesis para que se presente la condición de carrera para dicha función (pause and check), es que el programa consulta la lista cuyos valores va a imprimir, a la vez que otros hilos modifican sus valores. Para corregir esto, haga lo que sea necesario para que efectivamente, antes de imprimir los resultados actuales, se pausen todos los demás hilos. Adicionalmente, implemente la opción ‘resume’.

Para poder hacer esto, en el método *run()* de cada uno de la clase *Immortal*, se valida la variable booleana *fighting* para que si esta es true, se ejecute normal, sino con el uso de la lista como objeto lock, se ponga a esperar el *Immortal* esto con la finalidad de poder pausar y continuar su ejecución con los métodos *stopImmorta()* y *resumeImmortal()*.

**run()**

<p align="center">
<img src="img/prodcons/parte3/3.4.2.png" alt="run" width="700px">
</p>

**Funcionalidad bóton pause and check**

<p align="center">
<img src="img/prodcons/parte3/3.4.1.png" alt="pause" width="700px">
</p>

**Funcionalidad bóton resume**

<p align="center">
<img src="img/prodcons/parte3/3.4.3.png" alt="Vida inmortales" width="700px">
</p>


5. Verifique nuevamente el funcionamiento (haga clic muchas veces en el botón). Se cumple o no el invariante?.

Al pausar y resumir constantemete, notamos que el invariante seguía sin cumplirse.

<p align="center">
<img src="img/prodcons/parte3/3.5.1.png" alt="!invariante1" width="700px">
</p>

<p align="center">
<img src="img/prodcons/parte3/3.5.2.png" alt="!invariante2" width="700px">
</p>

<p align="center">
<img src="img/prodcons/parte3/3.5.3.png" alt="!invariante3" width="700px">
</p>

6. Identifique posibles regiones críticas en lo que respecta a la pelea de los inmortales. Implemente una estrategia de bloqueo que evite las condiciones de carrera. Recuerde que si usted requiere usar dos o más ‘locks’ simultáneamente, puede usar bloques sincronizados anidados:

	```java
	synchronized(locka){
		synchronized(lockb){
			…
		}
	}
	```

7. Tras implementar su estrategia, ponga a correr su programa, y ponga atención a si éste se llega a detener. Si es así, use los programas jps y jstack para identificar por qué el programa se detuvo.

8. Plantee una estrategia para corregir el problema antes identificado (puede revisar de nuevo las páginas 206 y 207 de _Java Concurrency in Practice_).

9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.

10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto:
	* Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.
	* Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.

11. Para finalizar, implemente la opción STOP.

<!--
### Criterios de evaluación

1. Parte I.
	* Funcional: La simulación de producción/consumidor se ejecuta eficientemente (sin esperas activas).

2. Parte II. (Retomando el laboratorio 1)
	* Se modificó el ejercicio anterior para que los hilos llevaran conjuntamente (compartido) el número de ocurrencias encontradas, y se finalizaran y retornaran el valor en cuanto dicho número de ocurrencias fuera el esperado.
	* Se garantiza que no se den condiciones de carrera modificando el acceso concurrente al valor compartido (número de ocurrencias).


2. Parte III.
	* Diseño:
		- Coordinación de hilos:
			* Para pausar la pelea, se debe lograr que el hilo principal induzca a los otros a que se suspendan a sí mismos. Se debe también tener en cuenta que sólo se debe mostrar la sumatoria de los puntos de vida cuando se asegure que todos los hilos han sido suspendidos.
			* Si para lo anterior se recorre a todo el conjunto de hilos para ver su estado, se evalúa como R, por ser muy ineficiente.
			* Si para lo anterior los hilos manipulan un contador concurrentemente, pero lo hacen sin tener en cuenta que el incremento de un contador no es una operación atómica -es decir, que puede causar una condición de carrera- , se evalúa como R. En este caso se debería sincronizar el acceso, o usar tipos atómicos como AtomicInteger).

		- Consistencia ante la concurrencia
			* Para garantizar la consistencia en la pelea entre dos inmortales, se debe sincronizar el acceso a cualquier otra pelea que involucre a uno, al otro, o a los dos simultáneamente:
			* En los bloques anidados de sincronización requeridos para lo anterior, se debe garantizar que si los mismos locks son usados en dos peleas simultánemante, éstos será usados en el mismo orden para evitar deadlocks.
			* En caso de sincronizar el acceso a la pelea con un LOCK común, se evaluará como M, pues esto hace secuencial todas las peleas.
			* La lista de inmortales debe reducirse en la medida que éstos mueran, pero esta operación debe realizarse SIN sincronización, sino haciendo uso de una colección concurrente (no bloqueante).

	

	* Funcionalidad:
		* Se cumple con el invariante al usar la aplicación con 10, 100 o 1000 hilos.
		* La aplicación puede reanudar y finalizar(stop) su ejecución.
		
		-->

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software del programa de Ingeniería de Sistemas de la Escuela Colombiana de Ingeniería, y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.
