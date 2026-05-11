package juego.dominio;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class Deck {
	private Deque<Card> cards;
	
	public Deck(int numberOfDecks) {
		
		cards = DeckFactory.createCustomDeck(numberOfDecks);
	}
	public void refillFromDiscard(DiscardPile discardPile) {
	    List<Card> newCards = discardPile.grabAllButLast();
	    Collections.shuffle(newCards); 
	    for (Card card : newCards) {
	        cards.push(card);
	    }
	}
	public Card drawCard(DiscardPile discardPile) {
	    if (cards.isEmpty()) {
	        refillFromDiscard(discardPile);
	    }
	    
	   
	    if (cards.isEmpty()) {
	        throw new IllegalStateException("No cards left in Deck or DiscardPile");
	    }
	    
	    return cards.pop();
	}
	public int sizeDeck() {
		return cards.size();
	}
	
}
