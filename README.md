# Chinchón 2026 - Java Edition
## 🎴 Objetivo del Juego
El objetivo principal es ser el último jugador en la partida sin superar el límite de puntos establecido (100 puntos). Los jugadores deben formar combinaciones de cartas en su mano para reducir su puntuación al mínimo posible en cada ronda.

## Cómo se juega
- Inicio: Se reparten 7 cartas a cada jugador y se coloca una carta inicial en la pila de descartes.

- El Turno: En cada turno, el jugador (humano o IA) debe:

1. Robar: Elegir entre la carta oculta del mazo o la carta visible de la pila de descarte.

2. Descartar: Tras tener 8 cartas en mano, el jugador debe elegir una para soltarla en la pila, volviendo a tener 7 cartas.

- Cierre: Si un jugador tiene sus cartas combinadas según las reglas, puede optar por "cerrar" la ronda en lugar de hacer un descarte normal.

## Reglas y Validación
La lógica de validación se centraliza en la clase CombinationUtils y sigue estos criterios oficiales:

- Combinaciones Válidas: Grupos de 3 o más cartas del mismo valor o secuencias (escaleras) de 3 o más cartas del mismo palo.

- Chinchón: Una escalera completa de 7 cartas del mismo palo. Otorga una bonificación de -100 puntos.

1. Cierre con 7: Todas las cartas combinadas en la mano. Otorga -10 puntos.

2. Cierre con 6: Seis cartas combinadas y una carta sobrante (de cierre) con un valor de 5 o menos.

- Puntuación: Los jugadores que no cierran suman los puntos de sus cartas no combinadas. Las figuras valen: Sota (10), Caballo (11) y Rey (12).

## 🏗️ Arquitectura del Sistema (Clases por Paquetes)
### Paquete juego.dominio
Contiene la lógica de negocio y las entidades del juego.

- Card: Representa la unidad básica de juego.

- Rank y Suit suit.md:  Enumerados que restringen los valores de las cartas a la baraja española (1-7, 10-12) y gestionan la representación visual mediante emojis.

- Deck y DeckFactory: Deck gestiona el mazo como una estructura de datos Deque. DeckFactory implementa la lógica de creación de barajas (simples o dobles) y su barajado inicial.

- DiscardPile: Gestiona el montón de cartas descartadas (LIFO). Incluye el método grabAllButLast() para permitir el reciclaje del mazo cuando este se agota.

- Hand: Almacena las cartas del jugador. Incluye métodos de ordenación y algoritmos de optimización, como getBestDiscard(), que simula qué carta eliminar para minimizar puntos.

- CombinationUtils: Clase de utilidad que valida si una lista de cartas es un grupo o secuencia, gestiona el salto numérico entre el 7 y la Sota (10) y verifica la legalidad del cierre.

- Player (Abstracta): Define la estructura común para todos los jugadores (nombre, score, hand). Define el método abstracto playTurn() que permite el comportamiento polimórfico.

- HumanPlayer: Implementa la interacción con el usuario. Solicita manualmente qué carta robar, descartar o cómo agrupar las cartas para cerrar.

- AiPlayer: Implementa una IA que toma decisiones basadas en la utilidad. Evalúa si una carta del descarte le ayuda a formar una combinación mediante búsqueda exhaustiva antes de recogerla.

- Round: Gestiona el ciclo de vida de una ronda individual, desde el reparto hasta el cálculo final de puntos tras un cierre.

### 📦 Paquete juego.app
Contiene las clases de control y la interfaz de usuario.

- Main: Punto de entrada de la aplicación que inicializa la configuración de la partida.

- GameController: Orquestador principal de la partida. Controla la sucesión de rondas, elimina a jugadores que superan los 100 puntos y declara al ganador.

- ConsoleInput: Encapsula el uso de Scanner. Provee métodos validados para leer enteros en rangos, caracteres y cadenas, evitando errores de ejecución por entradas inválidas.

- PlayerBuilder: Facilita la creación dinámica de la lista de jugadores, permitiendo elegir entre humanos o IAs.

### Patrones de Diseño Incluidos
#### Factory (DeckFactory): 
Se utiliza para centralizar y ocultar la complejidad de crear una baraja española personalizada (40 cartas por mazo), separando la creación del objeto de su uso en la clase Round.Además, permite configurar fácilmente si la partida usará una o más barajas según el número de jugadores, sin que la clase Deck o Round necesiten conocer los detalles de instanciación. Internamente, utiliza un método privado generateDeck() que recorre los enumerados Suit y Rank para construir las cartas base.
La "factoría" se encarga no solo de crear los objetos Card, sino de devolverlos ya barajados (Collections.shuffle) y estructurados en una colección ArrayDeque lista para su uso.
#### Builder (PLayerBuilder):
La creación de jugadores se gestiona mediante el patrón Builder (en una variante de construcción dinámica). La configuración de una partida de Chinchón puede variar en número de participantes (2-5) y en la naturaleza de estos (Humanos o IAs).La clase PlayerBuilder encapsula la complejidad de instanciar diferentes subtipos de Player. En lugar de que el GameController gestione la entrada de datos y la instanciación, el Builder centraliza la lógica de "fabricación" de la lista de jugadores, asegurando que cada objeto (HumanPlayer o AiPlayer) se configure correctamente con sus dependencias (como ConsoleInput) antes de ser añadido a la partida.


## Pruebas y Validaciones
