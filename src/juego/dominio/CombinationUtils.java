package juego.dominio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Clase de utilidad que contiene la lógica de validación de reglas del Chinchón.
 * * Proporciona métodos estáticos para verificar combinaciones de cartas (grupos y secuencias),
 * calcular puntuaciones de cartas no combinadas y validar la legalidad del cierre de una ronda.
 * * @author rgoncar723
 * @version 1.0
 */
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
     * Comprueba si una lista de cartas forma una "Secuencia" o escalera.
     * Una secuencia válida requiere al menos 3 cartas del mismo palo y con valores consecutivos.
     *  @param cards Lista de cartas a validar.
     * @return true si forman una escalera válida del mismo palo.
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
     * Traduce el valor nominal de la carta a su posición lógica en la baraja española.
     * Gestiona el salto entre el 7 y la Sota (10).
     * * @param card Carta a evaluar.
     * @return Índice corregido (0-9).
     */
	private static int getSpanishOrder(Card card) {
		int value = card.getPoints();
		if (value >= 1 && value <= 7) {
			return value - 1;
		}
		
		return value - 3; 
	}

	/**
     * Calcula la suma de puntos de las cartas que no pertenecen a ninguna combinación elegida.
     * * @param allCards Lista completa de cartas del jugador.
     * @param chosenGroups Lista que contiene las listas de cartas agrupadas.
     * @return Suma total de los puntos de las cartas sueltas.
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
     * Verifica si la mano constituye un Chinchón.
     * Se define como una secuencia completa de 7 cartas del mismo palo.
     * * @param cards Lista de 7 cartas.
     * @return true si es Chinchón.
     */
	public static boolean isChinchon(List<Card> cards) {
		if (cards.size() == NORMAL_HAND_SIZE && isSequence(cards)) {
			return true;
		}
		return false;
	}
	/**
     * Valida si el intento de cierre de un jugador cumple con las reglas oficiales.
     * * Casos permitidos:
     * 1. Chinchón.
     * 2. 7 cartas combinadas.
     * 3. 6 cartas combinadas y carta de cierre (sobrante) menor o igual a 5.
     * @param g1 Primer grupo de cartas.
     * @param g2 Segundo grupo de cartas (puede ser lista vacía).
     * @param closingDiscard Carta que el jugador descarta para cerrar.
     * @return true si el cierre es legal.
     */
	public static boolean validateClosing(List<Card> g1, List<Card> g2, Card closingDiscard) {
       
        
        boolean g1Valid = isValidGroup(g1);
        boolean g2Valid = g2.isEmpty() || isValidGroup(g2); // El segundo grupo puede estar vacío

        int combinedCount = (g1Valid ? g1.size() : 0) + (g2Valid ? g2.size() : 0);

        // Caso 1: Chinchón (7 cartas consecutivas del mismo palo) 
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
     * Comprueba si un subconjunto de cartas es un grupo o una secuencia válida.
     * * @param group Lista de cartas.
     * @return true si cumple con alguna de las dos combinaciones.
     */
    public static boolean isValidGroup(List<Card> group) {
        if (group.size() < 3) return false; // Mínimo 3 cartas [cite: 32, 34]
        return isGroup(group) || isSequence(group);
    }
    /**
     * Calcula la suma total de puntos de una lista de cartas sin considerar combinaciones.
     * * @param cards Lista de cartas.
     * @return Suma de los puntos nominales.
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
