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

			p.resetForNewRound();
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

	    
	    if (currentPlayer.hasClosed()) {
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
	    // Este bucle se repetirá hasta que isFinished sea true
	    while (!isFinished) { 
	        start();
	        if (!isFinished) {
	            nextTurn();
	        }
	    }
	    updateScores();
		
	}
	 /**
     * Al finalizar una ronda, se penaliza a los jugadores.
     */
	public void updateScores() {
	    int pointsToAdd;
	    String details;
	    System.out.println("\n--- RESUMEN DE LA RONDA ---");

	    for (Player p : players) {
	        // Obtenemos cuántas cartas le quedan tras el descarte/cierre
	        int remainingCards = p.getHand().getCards().size();

	        if (p.hasClosed()) {
	            // Caso A: El jugador ha cerrado la ronda
	            // Si le quedan 0 cartas es porque combinó las 7 (7 - 1 del cierre = 0)
	            if (remainingCards == 0) {
	                pointsToAdd = -10;
	                details = "¡CIERRE CON 7! (Bonificación -10)";
	            } else {
	                // Si le quedan cartas (normalmente 6 combinadas), suma 0
	                pointsToAdd = 0;
	                details = "Cierre con 6 combinadas (Suma 0)";
	            }
	        } else {
	            // Caso B: El resto de jugadores suman sus puntos no combinados
	            pointsToAdd = p.getHand().calculateUncombinedPoints();
	            details = String.format("Cartas sueltas: %s", p.getHand().getCards());
	        }

	        // Actualizamos el marcador global del jugador
	        p.addPoints(pointsToAdd);

	        // Imprimimos la línea de formato (añadido \n al final para evitar amontonamiento)
	        System.out.printf("%-15s | Suma: %+3d | %s\n", p.getName(), pointsToAdd, details);
	    }
	    System.out.println("---------------------------\n");
	}

}
