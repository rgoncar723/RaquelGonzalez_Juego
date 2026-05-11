package juego.dominio;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Gestiona el montón de descartes del juego, donde los jugadores colocan las
 * cartas de las que se deshacen al final de su turno[cite: 22, 28]. * Funciona
 * como una estructura LIFO (Last-In, First-Out), permitiendo visualizar o robar
 * únicamente la última carta descartada. * @author rgoncar723
 * 
 * @version 1.0
 */
public class DiscardPile {
	private Deque<Card> cards;

	/**
	 * Inicializa una pila de descarte vacía.
	 */
	public DiscardPile() {
		this.cards = new ArrayDeque<>();
	}

	/**
	 * Añade una carta a la parte superior del montón de descarte[cite: 47].
	 * 
	 * @param card La carta que el jugador decide descartar[cite: 28].
	 */
	public void push(Card card) {
		cards.push(card);
	}

	/**
	 * Extrae y devuelve la carta superior del descarte si existe.
	 * 
	 * @return Un @code Optional con la carta superior, o vacío si no hay cartas.
	 */
	public Optional<Card> pop() {
		return Optional.ofNullable(cards.poll());
	}

	/**
	 * Permite consultar la carta superior sin extraerla del montón.
	 * 
	 * @return Un @code Optional con la carta superior.
	 */
	public Optional<Card> peek() {
		return Optional.ofNullable(cards.peek());
	}

	/**
	 * Verifica si el montón de descarte está vacío.
	 * 
	 * @return @code true si no hay cartas, @code false en caso contrario.
	 */
	public boolean isEmpty() {
		return cards.isEmpty();
	}

	/**
	 * Prepara el reciclaje de cartas para el mazo cuando este se agota. Extrae
	 * todas las cartas excepto la superior, que debe permanecer visible para el
	 * siguiente turno. 
	 * * @return Una lista con las cartas para reponer en el mazo.
	 */
	public List<Card> grabAllButLast() {
		List<Card> rest;
		Card topCard;
		if (cards.isEmpty()) {
			return new ArrayList<>();
		}
		topCard = cards.pop(); 
		rest = new ArrayList<>(cards); 
		cards.clear(); 
		cards.push(topCard); 
		return rest;
	}
}
