package juego.dominio;

import java.util.List;

public class Round {
	private List<Player> players;
	private Deck deck;
	private DiscardPile pile;
	private int currentPlayerIndex;
	private boolean isFinished;
	private static final int NORMAL_HAND_SIZE = 7;
	private int pointLimits;

	public Round(List<Player> players, int numDecks, int pointLimits) {
		this.players = players;
		this.deck = new Deck(numDecks);
		this.pile = new DiscardPile();
		this.currentPlayerIndex = 0;
		this.isFinished = false;
		this.pointLimits = pointLimits;
	}

	public void dealCards() {
		for (Player p : players) {

			p.getHand().reset();
		}

		for (int i = 0; i < NORMAL_HAND_SIZE; i++) {
			for (Player player : players) {
				player.getHand().addCard(deck.drawCard(pile));
			}
		}
		pile.push(deck.drawCard(pile));
	}

	public void start() {
		Player currentPlayer = players.get(currentPlayerIndex);

		currentPlayer.playTurn(deck, pile);

		if (currentPlayer.getHand().canClose(currentPlayer.getScore(), pointLimits)) {
			this.isFinished = true;
		}
	}

	public void nextTurn() {
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
	}

	public boolean isRoundOver() {
		return isFinished;
	}

	public Deck getDeck() {
		return deck;
	}

	public DiscardPile getPile() {
		return pile;
	}

	public void execute() {
		dealCards();
		do {
			start();
			if (!isFinished) {
				nextTurn();
			}
		} while (!isRoundOver());
	}

}
