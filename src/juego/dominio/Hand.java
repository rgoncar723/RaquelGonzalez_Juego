package juego.dominio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Hand {
	private List<Card> cards;
	private static final int MAX_CARDS_TURN = 8;

	public Hand() {
		cards = new ArrayList<>();
	}

	/**
	 * 
	 * @param drawnCard
	 */
	public void addCard(Card drawnCard) {
		if (cards.size() < MAX_CARDS_TURN) {
			cards.add(drawnCard);
		} else {
			System.out.println("No puedes agregar una carta a tu mano, debes soltar una");
		}
	}

	/**
	 * 
	 * @param card
	 */
	public void removeCard(Card card) {
		cards.remove(card);
	}

	/**
	 * 
	 */
	public void sortBySuit() {
		cards.sort(Comparator.comparing(Card::getSuit).thenComparingInt(Card::getPoints));
	}

	/**
	 * 
	 */
	public void sortByRank() {
		cards.sort(Comparator.comparingInt(Card::getPoints));
	}

	/**
	 * 
	 * @return
	 */
	public boolean checkChinchon() {
		return CombinationUtils.isChinchon(cards);
	}

	/**
	 * 
	 * @return
	 */
	public List<Card> getCards() {
		return new ArrayList<>(cards);
	}

	/**
	 * Verifica si el jugador puede cerrar con los grupos que ha elegido.
	 * 
	 * @param group1       Primer grupo (obligatorio, mínimo 3 cartas)
	 * @param group2       Segundo grupo (puede estar vacío)
	 * @param currentScore Puntuación actual del jugador antes de cerrar
	 * @param pointLimit   Límite de puntos para quedar eliminado (ej. 100)
	 * @param isFirstTurn  Indica si es el primer turno de la ronda (no se puede
	 *                     cerrar)
	 * @return true si el cierre es válido según las reglas
	 */
	public boolean canClose(List<Card> group1, List<Card> group2, int currentScore, int pointLimit, boolean isFirstTurn) {

		boolean validGroup1 = CombinationUtils.isGroup(group1) || CombinationUtils.isSequence(group1);
		boolean validGroup2 = group2.isEmpty()
				|| (CombinationUtils.isGroup(group2) || CombinationUtils.isSequence(group2));
		int totalCombined;
		if (isFirstTurn) {
			return false;
		}

		// Validar que los grupos sean combinaciones reales

		if (!validGroup1 || !validGroup2) {
			return false;
		}

		totalCombined = group1.size() + group2.size();
		// Debe ser 6 o 7 cartas combinadas
		if (totalCombined != 6 && totalCombined != 7) {
			return false;
		}

		// Calcular los puntos que sumaría esta ronda (usando los grupos indicados)
		int pointsThisRound = calculateClosingPoints(group1, group2, totalCombined);

		// No se puede cerrar si se alcanza o supera el límite de puntos
		if (currentScore + pointsThisRound >= pointLimit) {
			return false;
		}

		// Si son 6 combinadas, la carta suelta debe valer ≤ 5
		if (totalCombined == 6) {
			Card remaining = findRemainingCard(group1, group2);
			return remaining.getPoints() <= 5;
		}

		// totalCombined == 7 -> siempre se puede cerrar (ya validamos el límite de
		// puntos)
		return true;
	}
	public boolean canClose(int currentScore, int limitPoints) {
		boolean canPhysicallyClose, safeFromElimination;
        int penalty = CombinationUtils.calculateUncombinedPointsAll(cards);

        // 1. Condición: O todas combinadas (0 pts) o una suelta de 5 o menos.
        canPhysicallyClose = (penalty <= 5);

        // 2. Condición : No cerrar si te eliminas a ti mismo.
        safeFromElimination = (currentScore + penalty) < limitPoints;

        return canPhysicallyClose && safeFromElimination;
	}
	/**
	 * 
	 * @param group1
	 * @param group2
	 * @return
	 */
	private Card findRemainingCard(List<Card> group1, List<Card> group2) {

		return cards
				.stream()
				.filter(card -> !group1.contains(card))
				.filter(card -> !group2.contains(card))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No remaining card found."));
	}

	/**
	 * Calcula los puntos que se añaden al marcador del jugador que cierra.
	 * 
	 * @param group1        Grupo 1
	 * @param group2        Grupo 2
	 * @param totalCombined Nº total de cartas combinadas (6 o 7)
	 * @return Puntos a añadir (pueden ser negativos si hay bonus)
	 */
	public int calculateClosingPoints(List<Card> group1, List<Card> group2, int totalCombined) {
		if (totalCombined == 7 && CombinationUtils.isChinchon(cards)) {
			return -100; // Chinchón: victoria inmediata (valor especial)
		}
		if (totalCombined == 7) {
			return -10; // Bonus por cerrar con 7 cartas combinadas
		}
		if (totalCombined == 6) {
			Card remaining = findRemainingCard(group1, group2);
			return remaining.getPoints();
		}
		// Fallback (no debería ocurrir)
		return calculateUncombinedPointsFromGroups(List.of(group1, group2));
	}

	/**
	 * Calcula la suma de puntos de las cartas NO incluidas en los grupos
	 * proporcionados.
	 */
	public int calculateUncombinedPointsFromGroups(List<List<Card>> chosenGroups) {
		return CombinationUtils.calculateUncombinedPoints(cards, chosenGroups);
	}
	public int calculateUncombinedPoints() {
		return CombinationUtils.calculateUncombinedPointsAll(cards);
	}
	public void reset() {
		cards.clear();
	}
	/**
	 * Analiza las 8 cartas actuales y determina cuál es la mejor para descartar.
	 * La "mejor" es aquella que, al ser eliminada, deja la menor cantidad de puntos
	 * en cartas no combinadas.
	 * * @return La carta que más conviene descartar.
	 */
	public Card getBestDiscard() {
	    Card bestCard = null;
	    int minPoints = Integer.MAX_VALUE;
	    List<Card> remainingCards;
	    int currentPoints;

	    
	    for (Card candidate : cards) {
	      
	        remainingCards = new ArrayList<>(this.cards);
	        remainingCards.remove(candidate);

	        // Calculamos cuántos puntos quedarían "sueltos" con esta opción
	        currentPoints = CombinationUtils.calculateUncombinedPointsAll(remainingCards);

	        // Si esta opción es mejor que la anterior, la guardamos
	        if (currentPoints < minPoints) {
	            minPoints = currentPoints;
	            bestCard = candidate;
	        }
	    }
	    return bestCard;
	}
}
