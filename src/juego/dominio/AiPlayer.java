package juego.dominio;

import java.util.ArrayList;

import java.util.List;

import java.util.Optional;

/**
 * Implementación de un jugador controlado por la Inteligencia Artificial. *
 * Esta clase hereda de {@link Player} y define una estrategia basada en la
 * evaluación de utilidad de las cartas. La IA simula jugadas, identifica
 * combinaciones (meldes) y optimiza el descarte para minimizar los puntos no
 * combinados en cada turno. * @author rgoncar723
 * 
 * @version 1.0
 */
public class AiPlayer extends Player {
	/**
	 * Constructor para inicializar el jugador IA con un nombre.
	 * 
	 * @param name Nombre identificativo de la IA.
	 */
	public AiPlayer(String name) {

		super(name);

	}

	/**
	 * Ejecuta el flujo lógico del turno de la IA. 1. Evalúa si conviene robar de la
	 * pila de descartes o del mazo. 2. Realiza el robo (pasa de 7 a 8 cartas). 3.
	 * Ejecuta tryToClose para gestionar el descarte y evaluar el cierre (vuelve a
	 * 7). 4. Muestra la mano resultante por consola. * @param deck Mazo de la
	 * ronda.
	 * 
	 * @param discardPile Pila de descartes.
	 */
	@Override
	public void playTurn(Deck deck, DiscardPile discardPile) {
		System.out.printf("\n TURNO DE: %s", name);

		// 1. Robo (Pasamos de 7 a 8 cartas)
		Optional<Card> topCard = discardPile.peek();
		if (topCard.isPresent() && shouldDrawFromDiscard(topCard.get())) {
			discardPile.pop().ifPresent(hand::addCard);
		} else {
			hand.addCard(deck.drawCard(discardPile));
		}

		tryToClose(discardPile);
		showHand(hand);
	}

	/**
	 * Evalúa si una carta de la pila de descartes es útil para la IA.
	 * 
	 * @param card Carta superior del descarte.
	 * @return true si la carta ayuda a formar o completar una melda.
	 */
	private boolean shouldDrawFromDiscard(Card card) {

		List<Card> simulatedHand = new ArrayList<>(hand.getCards());

		simulatedHand.add(card);

		return helpsToFormMeld(card, simulatedHand);

	}

	/**
	 * Evalúa si una carta externa (normalmente de la pila de descartes)
	 * contribuiría a formar una combinación válida con la mano actual. * Este
	 * método actúa como el "predictor" de la IA para decidir si robar del descarte.
	 * Realiza una búsqueda exhaustiva de todas las combinaciones de 3 cartas
	 * posibles en una mano simulada que incluye la carta candidata. * Lógica de
	 * decisión: 1. Crea ternas virtuales combinando la carta nueva con las
	 * existentes. 2. Si al menos una terna que contenga 'card' es un grupo o
	 * secuencia legal, el método confirma que la carta es útil para el jugador.
	 * * @param card La carta candidata a ser robada (procedente del descarte).
	 * 
	 * @param simulatedHand Una lista temporal que contiene las cartas actuales de
	 *                      la IA más la carta candidata.
	 * @return true si la carta permite completar al menos una combinación; false si
	 *         no aporta valor inmediato a la formación de meldas.
	 */

	private boolean helpsToFormMeld(Card card, List<Card> simulatedHand) {

		List<Card> triple;

		int size = simulatedHand.size();

		for (int i = 0; i < size; i++) {

			for (int j = i + 1; j < size; j++) {

				for (int k = j + 1; k < size; k++) {

					triple = new ArrayList<>();

					triple.add(simulatedHand.get(i));

					triple.add(simulatedHand.get(j));

					triple.add(simulatedHand.get(k));

					if (triple.contains(card)

							&& (CombinationUtils.isGroup(triple) || CombinationUtils.isSequence(triple))) {

						return true;

					}

				}

			}

		}

		return false;

	}

	/**
	 * Criterio de descarte por valor: selecciona la carta con más puntos.
	 * 
	 * @return La carta de mayor peso numérico.
	 */

	private Card discardByPoints() {

		Card worst;

		List<Card> cards = hand.getCards();

		worst = cards.get(0);

		for (Card c : cards) {

			if (c.getPoints() > worst.getPoints()) {

				worst = c;

			}

		}

		return worst;

	}

	/**
	 * Criterio de descarte optimizado. Prioriza descartar cartas que no sirven para
	 * ninguna combinación. Si todas son útiles, descarta la de mayor valor.
	 * 
	 * @return La carta identificada como menos útil para la mano.
	 */

	private Card discardOptimal() {

		Card worst;

		List<Card> currentCards = hand.getCards();

// Identificar cartas que no son necesarias para ninguna combinación

		List<Card> looseCards = new ArrayList<>();

		for (Card c : currentCards) {

			if (!isCardUsefulInAnyMeld(c, currentCards)) {

				looseCards.add(c);

			}

		}

// Si todas las cartas son útiles, descartar la de mayor valor

		if (looseCards.isEmpty()) {

			return discardByPoints();

		}

// Entre las cartas sueltas, descartar la de mayor valor

		worst = looseCards.get(0);

		for (Card c : looseCards) {

			if (c.getPoints() > worst.getPoints()) {

				worst = c;

			}

		}

		return worst;

	}

	/**
	 * Evalúa si una carta específica tiene utilidad táctica dentro de la mano
	 * actual. * El método utiliza un algoritmo de búsqueda por fuerza bruta para
	 * encontrar al menos una combinación de tres cartas (terna) que incluya la
	 * carta consultada y que forme un grupo (mismo valor) o una secuencia (escalera
	 * del mismo palo). * Lógica de ejecución: 1. Recorre todas las combinaciones
	 * posibles de 3 cartas en la mano (Complejidad O(n³)). 2. Filtra aquellas
	 * ternas que contienen la carta 'card' pasada por parámetro. 3. Valida la terna
	 * mediante {@link CombinationUtils}.
	 * 
	 * @param card      La carta cuya utilidad se desea evaluar.
	 * @param handCards La lista completa de cartas que el jugador tiene en la mano
	 *                  (8 cartas).
	 * @return true si la carta forma parte de al menos una combinación válida;
	 *         false si es una carta "suelta" o no combinable.
	 */
	private boolean isCardUsefulInAnyMeld(Card card, List<Card> handCards) {
		Card a, b, c;

		List<Card> triple;

		int size = handCards.size();

		for (int i = 0; i < size; i++) {

			for (int j = i + 1; j < size; j++) {

				for (int k = j + 1; k < size; k++) {

					a = handCards.get(i);

					b = handCards.get(j);

					c = handCards.get(k);

					if (a == card || b == card || c == card) {

						triple = new ArrayList<>();

						triple.add(a);

						triple.add(b);

						triple.add(c);

						if (CombinationUtils.isGroup(triple) || CombinationUtils.isSequence(triple)) {

							return true;

						}

					}

				}

			}

		}

		return false;

	}

	/**
	 * Gestiona el cierre o descarte final del turno. Selecciona el descarte óptimo,
	 * simula la mano resultante de 7 cartas e identifica si es posible y
	 * conveniente cerrar la ronda según {@link CombinationUtils}. * @param
	 * discardPile Pila donde se entregará la carta descartada.
	 */

	private void tryToClose(DiscardPile discardPile) {
		// Buscamos la carta que más nos sobra (tenemos 8)
		Card candidate = discardOptimal();
		List<Card> remainingCards, g1, g2;
		if (candidate != null) {
			// A. SIMULACIÓN: Obtenemos las 7 cartas que quedarían
			remainingCards = new ArrayList<>(this.getHand().getCards());
			remainingCards.remove(candidate);

			// B. AGRUPACIÓN: La IA intenta formar sus dos grupos (G1 y G2)
			// Buscamos combinaciones automáticamente en la mano restante
			g1 = new ArrayList<>();
			g2 = new ArrayList<>();
			identifyGroups(remainingCards, g1, g2);

			// C. VALIDACIÓN: Usamos el método de CombinationUtils
			if (CombinationUtils.validateClosing(g1, g2, candidate)) {
				// CIERRE VÁLIDO: Ejecutamos físicamente
				this.getHand().removeCard(candidate);
				discardPile.push(candidate);
				this.setClosed(true); // <--- Notifica a la clase Round
				System.out.printf("\n[CIERRE] %s cierra con [%s]\n", name, candidate);
			} else {
				// DESCARTE NORMAL: No se pudo cerrar, pero hay que soltar carta
				this.getHand().removeCard(candidate);
				discardPile.push(candidate);
				System.out.printf("\n[TURNO] %s descarta [%s]\n", name, candidate);
			}
		}
	}

	/**
	 * Analiza una lista de cartas para extraer automáticamente combinaciones
	 * válidas. Llena las listas g1 y g2 con las meldes encontradas.
	 * 
	 * @param cards Lista de 7 cartas a evaluar.
	 * @param g1    Primera combinación detectada.
	 * @param g2    Segunda combinación detectada.
	 */
	private void identifyGroups(List<Card> cards, List<Card> g1, List<Card> g2) {
		List<Card> copy = new ArrayList<>(cards);
		List<Card> trio;

		
		for (int i = 0; i < copy.size(); i++) {
			for (int j = i + 1; j < copy.size(); j++) {
				for (int k = j + 1; k < copy.size(); k++) {
					trio = List.of(copy.get(i), copy.get(j), copy.get(k));
					if (CombinationUtils.isValidGroup(trio)) {
						if (g1.isEmpty()) {
							g1.addAll(trio);
						} else if (g2.isEmpty() && !g1.containsAll(trio)) {
							g2.addAll(trio);
						}
					}
				}
			}
		}
	}

	/**
	 * Imprime por consola la representación visual de la mano actual del jugador.
	 * 
	 * @param hand Objeto mano que contiene las cartas a mostrar.
	 */
	private void showHand(Hand hand) {

		List<Card> cards = hand.getCards();

		System.out.println("Mano del jugador:");

		for (int i = 0; i < cards.size(); i++) {

			System.out.printf("(%d)\t", i + 1);

		}

		System.out.println();

		for (Card c : cards) {

			System.out.printf("[%s]\t", c.toString());

		}

		System.out.println();

		System.out.println("-".repeat(40));

	}

}