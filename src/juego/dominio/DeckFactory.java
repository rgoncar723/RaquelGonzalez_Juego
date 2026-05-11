package juego.dominio;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class DeckFactory {
	public static Deque<Card> createCustomDeck(int numberOfDecks) {
		List<Card> cards= new ArrayList<>();
		for(int i = 0; i < numberOfDecks; i++) {
			cards.addAll(generateDeck());
		}
		Collections.shuffle(cards);
		return new ArrayDeque<>(cards);
	}
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
