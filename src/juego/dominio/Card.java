package juego.dominio;

public class Card implements Comparable<Card> {
	private Rank rank;
	private Suit suit;
	
	public Card(Rank rank, Suit suit) {
		this.rank=rank;
		this.suit=suit;
	}
	public int getPoints() {
		return rank.getRank();
	}
	public String getSuitCharacter() {
		return suit.getDescription();
	}
	public Suit getSuit() {
		return suit;
	}
	public Rank getRank() {
		return rank;
	}
	@Override
	public String toString() {
		return String.format("%d%s", getPoints(),getSuitCharacter());
	}

	@Override
	public int compareTo(Card o) {
		if(suit != o.suit) {
			return suit.compareTo(o.suit);
		}
		return Integer.compare(rank.getRank(), o.rank.getRank());
	}
	

}
