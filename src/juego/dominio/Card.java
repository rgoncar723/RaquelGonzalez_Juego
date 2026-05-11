package juego.dominio;
/**
 * Representa una carta individual de la baraja española utilizada en el Chinchón[cite: 2, 6].
 * * Esta clase es inmutable y proporciona métodos para obtener la puntuación de la carta,
 * su representación visual y lógica de comparación para facilitar la ordenación de la mano.
 * @author rgoncar723
 * @version 1.0
 */
public class Card implements Comparable<Card> {
	private Rank rank;
	private Suit suit;
	/**
     * Crea una nueva carta con el rango y palo especificados.
     * @param rank El valor de la carta.
     * @param suit El palo de la carta.
     */
	public Card(Rank rank, Suit suit) {
		this.rank=rank;
		this.suit=suit;
	}
	/**
     * Obtiene los puntos que otorga la carta si no está combinada al final de la ronda[cite: 49].
     * Según las reglas, los puntos coinciden con el valor de la carta.
     * * @return El valor numérico de la carta (Ej: Rey -> 12).
     */
	public int getPoints() {
		return rank.getRank();
	}
	/**
     * Obtiene la representación visual (emoji) del palo de la carta.
     * * @return Cadena con el icono descriptivo del palo.
     */
	public String getSuitCharacter() {
		return suit.getDescription();
	}
	/** 
	 * @return El objeto Suit asociado a la carta. 
	 * */
	public Suit getSuit() {
		return suit;
	}
	/** 
	 * @return El objeto Rank asociado a la carta. 
	 * */
	public Rank getRank() {
		return rank;
	}
	/**
     * Devuelve una representación textual de la carta apta para la consola.
     * Ejemplo: "7⚔️" o "12🪙".
     * * @return La carta formateada como String.
     */
	@Override
	public String toString() {
		return String.format("%d%s", getPoints(),getSuitCharacter());
	}
	/**
     * Define el orden natural de las cartas para facilitar la búsqueda de combinaciones[cite: 32, 34].
     * El criterio de ordenación es:
     * 1. Por palo (según el orden definido en el enum Suit).
     * 2. Por rango (de menor a mayor valor numérico).
     * * @param o La otra carta con la que comparar.
     * @return Un entero negativo, cero o positivo según esta carta sea menor, igual o mayor.
     */
	@Override
	public int compareTo(Card o) {
		if(suit != o.suit) {
			return suit.compareTo(o.suit);
		}
		return Integer.compare(rank.getRank(), o.rank.getRank());
	}
	

}
