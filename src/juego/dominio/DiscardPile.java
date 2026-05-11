package juego.dominio;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class DiscardPile {
	private Deque<Card> cards;
	
	public DiscardPile() {
		this.cards= new ArrayDeque<>();
	}
	public void push(Card card) {
		cards.push(card);
	}
	public Optional<Card> pop() {
		return Optional.ofNullable(cards.poll());
	}
	public Optional<Card> peek() {
        return Optional.ofNullable(cards.peek());
    }
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	public List<Card> grabAllButLast(){
		Card topCard = cards.pop();
		List<Card> temporaryCards = new ArrayList<>(cards);
		
		if(cards.size()<2) {
			return temporaryCards;
		}
		cards.clear();
		cards.push(topCard);
		return temporaryCards;
	}
}
