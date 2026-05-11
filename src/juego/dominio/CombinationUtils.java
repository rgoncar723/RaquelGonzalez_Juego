package juego.dominio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinationUtils {
	private static final int NORMAL_HAND_SIZE = 7;

	/**
	 * Comprueba si hay un grupo de cartas (mismo número).
	 * 
	 * @param card Lista de cartas a validar
	 * @return true si hay al menos 3 cartas y todas tienen el mismo valor.
	 */
	public static boolean isGroup(List<Card> cards) {
		int firstValue = cards.get(0).getPoints();
		if (cards == null || cards.size() < 3) {
			return false;
		}
		for (Card c : cards) {
			if (c.getPoints() != firstValue) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Comprueba si un grupo de cartas forma una sequencia (escalera del mismo palo)
	 * 
	 * @param cards Lista de cartas a validar.
	 * @return true si hay al menos 3 cartas consecutivas del mismo palo.
	 */

	public static boolean isSequence(List<Card> cards) {
		Suit firstSuit;
		List<Card> sorted = new ArrayList<>(cards);

		sorted.sort(Comparator.comparingInt(CombinationUtils::getSpanishOrder));
		if (cards == null || cards.size() < 3) {
			return false;
		}
		firstSuit = cards.get(0).getSuit();

		for (Card c : sorted) {
			if (c.getSuit() != firstSuit) {
				return false;
			}
		}

	
		for (int i = 0; i < sorted.size() - 1; i++) {
			int currentOrder = getSpanishOrder(sorted.get(i));
			int nextOrder = getSpanishOrder(sorted.get(i + 1));

			if (nextOrder != currentOrder + 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Devuelve un índice que respeta el orden de la baraja española: 1→0, 2→1, 3→2,
	 * 4→3, 5→4, 6→5, 7→6, 10→7, 11→8, 12→9.
	 */
	private static int getSpanishOrder(Card card) {
		int value = card.getPoints();
		if (value >= 1 && value <= 7) {
			return value - 1;
		}
		
		return value - 3; 
	}

	 /**
     * Calcula la suma de puntos de las cartas NO incluidas en los grupos elegidos.
     * @param allCards Todas las cartas de la mano (normalmente 7 u 8)
     * @param chosenGroups Lista de grupos (cada grupo es una lista de cartas)
     * @return Puntos totales de las cartas sueltas
     */
    public static int calculateUncombinedPoints(List<Card> allCards, List<List<Card>> chosenGroups) {
        Set<Card> used = new HashSet<>();
        for (List<Card> group : chosenGroups) {
            used.addAll(group);
        }
        int total = 0;
        for (Card c : allCards) {
            if (!used.contains(c)) {
                total += c.getPoints();
            }
        }
        return total;
    }

	/**
	 * Verifica si el jugador tiene un Chinchón (7 cartas consecutivas del mismo
	 * palo)
	 * 
	 * @param cards Lista de cartas a verificar.
	 * @return true si se cumple la condición y false si no.
	 */
	public static boolean isChinchon(List<Card> cards) {
		if (cards.size() == NORMAL_HAND_SIZE && isSequence(cards)) {
			return true;
		}
		return false;
	}
	public static boolean validateClosing(List<Card> g1, List<Card> g2, Card closingDiscard) {
        // Regla: La carta de cierre debe valer entre 1 y 5 si no todas están combinadas. 
        // Si el jugador combina las 7 cartas, el valor de la carta de cierre no importa (es solo un descarte). 
        
        boolean g1Valid = isValidGroup(g1);
        boolean g2Valid = g2.isEmpty() || isValidGroup(g2); // El segundo grupo puede estar vacío

        int combinedCount = (g1Valid ? g1.size() : 0) + (g2Valid ? g2.size() : 0);

        // Caso 1: Chinchón (7 cartas consecutivas del mismo palo) [cite: 38, 39, 45]
        if (isChinchon(g1)) {
            return true; 
        }

        // Caso 2: 7 cartas combinadas en total 
        if (combinedCount == 7) {
            return true;
        }

        // Caso 3: 6 cartas combinadas y la sobrante vale entre 1 y 5 
        if (combinedCount == 6 && closingDiscard.getPoints() <= 5) {
            return true;
        }

        return false;
    }

    /**
     * Verifica si una lista de cartas forma un grupo válido (Set o Run). [cite: 30]
     */
    public static boolean isValidGroup(List<Card> group) {
        if (group.size() < 3) return false; // Mínimo 3 cartas [cite: 32, 34]
        return isGroup(group) || isSequence(group);
    }
    /**
     * 
     * @param cards
     * @return
     */
    public static int calculateUncombinedPointsAll(List<Card>cards) {
		int sum;
		sum = cards
				.stream()
				.mapToInt(Card::getPoints)
				.sum();
		return sum;
	}
}
