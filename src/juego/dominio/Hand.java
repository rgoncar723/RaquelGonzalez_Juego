package juego.dominio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Representa la mano de cartas de un jugador.
 * * Gestiona el almacenamiento, ordenación y validación de las combinaciones
 * de cartas (tríos, escaleras y Chinchón). Incluye la lógica necesaria
 * para determinar si un jugador puede cerrar la ronda y para optimizar
 * los descartes en jugadores controlados por la IA.
 * * @author rgoncar723
 * @version 1.0
 */
public class Hand {
	private List<Card> cards;
	private static final int MAX_CARDS_TURN = 8;
	/**
     * Constructor que inicializa una mano vacía.
     */
	public Hand() {
		cards = new ArrayList<>();
	}

	/**
     * Añade una carta a la mano tras el robo.
     * Controla que no se supere el límite de 8 cartas simultáneas.
     * @param drawnCard La carta obtenida del mazo o la pila de descartes.
     */
	public void addCard(Card drawnCard) {
		if (cards.size() < MAX_CARDS_TURN) {
			cards.add(drawnCard);
		} else {
			System.out.println("No puedes agregar una carta a tu mano, debes soltar una");
		}
		 
	}

	/**
     * Elimina una carta específica de la mano (normalmente por descarte o cierre).
     * @param card La carta a remover.
     */

	public void removeCard(Card card) {
	     cards.remove(card);
	    
	}
	/**
     * Ordena las cartas de la mano primero por su palo (Suit) 
     * y secundariamente por su valor numérico.
     */
	public void sortBySuit() {
		cards.sort(Comparator.comparing(Card::getSuit).thenComparingInt(Card::getPoints));
	}

	/**
     * Ordena las cartas de la mano de menor a mayor valor numérico.
     */
	public void sortByRank() {
		cards.sort(Comparator.comparingInt(Card::getPoints));
	}

	/**
     * Verifica si la mano actual cumple las condiciones de Chinchón 
     * (7 cartas consecutivas del mismo palo).
     * @return true si es Chinchón; false en caso contrario.
     */
	public boolean checkChinchon() {
		return CombinationUtils.isChinchon(cards);
	}

	/**
     * Devuelve una copia de la lista de cartas para proteger el encapsulamiento.
     * @return Lista con las cartas actuales del jugador.
     */
	public List<Card> getCards() {
		return new ArrayList<>(cards);
	}

	/**
     * Valida si un jugador puede cerrar la ronda basándose en grupos elegidos manualmente.
     * * @param group1       Primer grupo de cartas combinadas.
     * @param group2       Segundo grupo (puede ser una lista vacía).
     * @param currentScore Puntuación acumulada del jugador.
     * @param pointLimit   Límite de puntos de la partida.
     * @param isFirstTurn  Flag para evitar cierres en el primer turno.
     * @return true si la combinación permite el cierre legal de la ronda.
     */
	public boolean canClose(List<Card> group1, List<Card> group2, int currentScore, int pointLimit, boolean isFirstTurn) {

		boolean validGroup1 = CombinationUtils.isGroup(group1) || CombinationUtils.isSequence(group1);
		boolean validGroup2 = group2.isEmpty()
				|| (CombinationUtils.isGroup(group2) || CombinationUtils.isSequence(group2));
		int totalCombined;
		if (isFirstTurn) {
			return false;
		}

		// Valida que los grupos sean combinaciones reales

		if (!validGroup1 || !validGroup2) {
			return false;
		}

		totalCombined = group1.size() + group2.size();
		
		if (totalCombined != 6 && totalCombined != 7) {
			return false;
		}

		
		int pointsThisRound = calculateClosingPoints(group1, group2, totalCombined);

		
		if (currentScore + pointsThisRound >= pointLimit) {
			return false;
		}

		

		if (totalCombined == 6) {
			Card remaining = findRemainingCard(group1, group2);
			return remaining.getPoints() <= 5;
		}

	
		return true;
	}
	/**
     * Sobrecarga de canClose para la lógica automática de la IA.
     * Valida el cierre basándose en la suma total de puntos no combinados.
     * @param currentScore Puntuación acumulada.
     * @param limitPoints Límite de la partida.
     * @return true si la penalización es de 5 o menos y no implica eliminación.
     */
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
     * Identifica la carta que no ha sido incluida en ninguna combinación.
     * @param group1 Lista de cartas del grupo 1.
     * @param group2 Lista de cartas del grupo 2.
     * @return La carta sobrante.
     * @throws IllegalStateException si no se encuentra ninguna carta fuera de los grupos.
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
     * Calcula los puntos de penalización o bonificación al cerrar la ronda.
     * * @param group1 Grupo 1 de combinaciones.
     * @param group2 Grupo 2 de combinaciones.
     * @param totalCombined Cantidad de cartas combinadas.
     * @return 0/-10/-100 puntos dependiendo del tipo de cierre.
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
     * Suma los puntos de las cartas que no forman parte de los grupos proporcionados.
     * @param chosenGroups Lista de listas con los grupos formados.
     * @return Puntuación total de cartas sueltas.
     */
	public int calculateUncombinedPointsFromGroups(List<List<Card>> chosenGroups) {
		return CombinationUtils.calculateUncombinedPoints(cards, chosenGroups);
	}
	/**
     * Calcula la suma total de puntos de todas las cartas en la mano.
     * @return Puntos totales.
     */
	public int calculateUncombinedPoints() {
		return CombinationUtils.calculateUncombinedPointsAll(cards);
	}
	/**
     * Vacía la mano para comenzar una nueva ronda.
     */
	public void reset() {
		cards.clear();
	}
	/**
     * Analiza la mano de 8 cartas para determinar el descarte óptimo.
     * Utiliza una simulación de "fuerza bruta" evaluando qué carta, al ser eliminada,
     * minimiza la puntuación de cartas no combinadas restante.
     * @return La carta más prescindible para el jugador.
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
