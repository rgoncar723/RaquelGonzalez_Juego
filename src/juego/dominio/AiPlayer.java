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
		Card toDiscard;
		// 1. Decidir de dónde robar
		System.out.printf("\n TURNO DE: %s", name);
		Optional<Card> topCard = discardPile.peek();
		boolean drawFromDiscard = topCard.isPresent() && shouldDrawFromDiscard(topCard.get());

		if (drawFromDiscard) {
			// Roba del descarte (la carta ya estaba en peek, ahora la saca)
			discardPile.pop().ifPresent(hand::addCard);
		} else {
			// Roba del mazo
			hand.addCard(deck.drawCard(discardPile));
		}

		// 2. Decide que carta descarta
		toDiscard = discardOptimal();

		// 3. Descarta la carta elegida
		hand.removeCard(toDiscard);
		discardPile.push(toDiscard);
		System.out.printf("\nEL JUGADOR %s ha descartado [%s]\n", name, toDiscard.toString());
	}

	private boolean shouldDrawFromDiscard(Card card) {
		List<Card> simulatedHand = new ArrayList<>(hand.getCards());

		simulatedHand.add(card);
		return helpsToFormMeld(card, simulatedHand);
	}

	/**
	 * Comprueba si la carta dada puede ser parte de alguna combinación (grupo o
	 * escalera) dentro del conjunto de cartas simulado.
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
	 * descarta la carta de mayor valor (puntos). En caso de empate, la primera
	 * encontrada.
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
	 * evalúa qué carta es menos útil (no forma parte de ninguna melda o tiene el
	 * valor más alto).
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
	 * Determina si una carta puede ser parte de algún grupo o escalera junto con
	 * otras cartas de la mano (sin ella ya no se forma la melda).
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
	 * Versión 2.0: Lógica de decisión de cierre para la IA.
	 * Este método analiza la mano de 8 cartas, identifica el descarte óptimo
	 * y determina si es legal y conveniente finalizar la ronda.
	 */
	private void tryToClose(DiscardPile discardPile) {
	   
	    Card candidateToDiscard = this.getHand().getBestDiscard();
	    Card finalDiscard;
	    boolean canPhysicallyClose;
	    
	    if (candidateToDiscard == null) {
	        return; 
	    }

	    
	    this.getHand().removeCard(candidateToDiscard);

	   
	    canPhysicallyClose = this.getHand().canClose(this.getScore(), 100);

	    if (canPhysicallyClose) {
	        // --- ACCIÓN DE CIERRE ---
	        // Si el simulador dice que es seguro, consolidamos el cierre.
	        discardPile.push(candidateToDiscard);
	        this.setClosed(true); // Activamos el flag para que Round detenga el bucle
	        
	        System.out.printf("[TORNEO] IA %s ha CERRADO con descarte: %s\n", 
	                          this.getName(), candidateToDiscard);
	    } else {
	       
	        this.getHand().addCard(candidateToDiscard); 
	        
	        
	        finalDiscard = this.getHand().getBestDiscard();
	        this.getHand().removeCard(finalDiscard);
	        discardPile.push(finalDiscard);
	    }
	}
	
	

}
