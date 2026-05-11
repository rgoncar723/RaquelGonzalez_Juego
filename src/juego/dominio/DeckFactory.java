package juego.dominio;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
/**
 * Factoría encargada de la creación y configuración de la baraja española para el juego.
 * Esta clase centraliza la lógica de generación de cartas basándose en los enumerados 
 *  {@link Suit}  y {@link Rank} , permitiendo la creación de mazos simples o dobles 
 * según la configuración de la partida.
 * @author rgoncar723
 * @version 1.0
 */
public class DeckFactory {
	/**
     * Crea un mazo personalizado y barajado de cartas españolas.
     * * El mazo resultante contiene 40 cartas por cada baraja solicitada, 
     * siguiendo la estructura de 1-7 y 10-12 para los cuatro palos.
     * *@param numberOfDecks Cantidad de barajas a incluir.
     * @return Una  List Deque de cartas barajadas lista para ser utilizada en un objeto {@code Deck}.
     */
	public static Deque<Card> createCustomDeck(int numberOfDecks) {
		List<Card> cards= new ArrayList<>();
		for(int i = 0; i < numberOfDecks; i++) {
			cards.addAll(generateDeck());
		}
		Collections.shuffle(cards);
		return new ArrayDeque<>(cards);
	}
	/**
     * Genera una única baraja española de 40 cartas.
     * Recorre todos los palos y rangos definidos en el sistema para construir 
     * el conjunto básico de cartas.
     * * @return Lista con las 40 cartas iniciales sin barajar.
     */
	private static List<Card> generateDeck(){
		List<Card> deck = new ArrayList<>();
		for(Suit s: Suit.values()) {
			for(Rank r: Rank.values()) {
				deck.add(new Card(r,s));
			}
		}
		return deck;
	}
}
