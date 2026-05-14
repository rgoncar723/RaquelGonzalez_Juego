package juego.dominio;

import java.util.ArrayList;

import java.util.List;

import java.util.Optional;

public class AiPlayer extends Player {

	public AiPlayer(String name) {

		super(name);

	}


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
	    
	    // 2. ÚNICO PROCESO DE SALIDA: tryToClose se encarga de dejar la mano en 7
	    tryToClose(discardPile);
	    showHand(hand);
	}

	private boolean shouldDrawFromDiscard(Card card) {

		List<Card> simulatedHand = new ArrayList<>(hand.getCards());

		simulatedHand.add(card);

		return helpsToFormMeld(card, simulatedHand);

	}

	/**
	 * 
	 * Comprueba si la carta dada puede ser parte de alguna combinación (grupo o
	 * 
	 * escalera) dentro del conjunto de cartas simulado.
	 * 
	 */

	private boolean helpsToFormMeld(Card card, List<Card> simulatedHand) {

// Buscar cualquier trío (subconjunto de 3 cartas) que incluya 'card'

// y sea grupo o escalera.

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
	 * 
	 * descarta la carta de mayor valor (puntos). En caso de empate, la primera
	 * 
	 * encontrada.
	 * 
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
	 * 
	 * evalúa qué carta es menos útil (no forma parte de ninguna melda o tiene el
	 * 
	 * valor más alto).
	 * 
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
	 * 
	 * Determina si una carta puede ser parte de algún grupo o escalera junto con
	 * 
	 * otras cartas de la mano (sin ella ya no se forma la melda).
	 * 
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

// Solo considerar si la terna incluye la carta candidata

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
	 * 
	 * Versión 2.0: Lógica de decisión de cierre para la IA.
	 * 
	 * Este método analiza la mano de 8 cartas, identifica el descarte óptimo
	 * 
	 * y determina si es legal y conveniente finalizar la ronda.
	 * 
	 */

	private void tryToClose(DiscardPile discardPile) {
	    // Buscamos la carta que más nos sobra (tenemos 8)
	    Card candidate = discardOptimal();
	    List<Card> remainingCards,g1,g2;
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
	 * Método para que la IA identifique qué cartas tiene combinadas.
	 * Llena g1 y g2 con las mejores combinaciones encontradas.
	 */
	private void identifyGroups(List<Card> cards, List<Card> g1, List<Card> g2) {
	    List<Card> copy = new ArrayList<>(cards);
	    List<Card> trio;
	    
	    // Buscamos tríos o escaleras de 3 o 4 cartas
	    for (int i = 0; i < copy.size(); i++) {
	        for (int j = i + 1; j < copy.size(); j++) {
	            for (int k = j + 1; k < copy.size(); k++) {
	                trio = List.of(copy.get(i), copy.get(j), copy.get(k));
	                if (CombinationUtils.isValidGroup(trio)) {
	                    if (g1.isEmpty()) {
	                    	g1.addAll(trio);
	                    }
	                    else if (g2.isEmpty() && !g1.containsAll(trio)) {
	                    	g2.addAll(trio);
	                    }
	                }
	            }
	        }
	    }
	}
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