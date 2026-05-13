package juego.dominio;

public abstract class Player {
	protected String name;
	protected int score;
	protected Hand hand;
	protected boolean closed;
	
	public Player(String name) {
		this.name=name;
		this.score=0;
		this.hand= new Hand();
	}
	
	public abstract void playTurn(Deck deck, DiscardPile discardPile);

    public void addPoints(int points) {
        this.score += points;
    }
    public String getName() {
    	return name;
    }
    public int getScore() {
    	return score;
    }
    public Hand getHand() {
    	return hand;
    }
    public boolean hasClosed() {
    	return closed;
    }
    public void setClosed(boolean closed) {
    	this.closed=closed;
    }
    public boolean isEliminated(int limit) {
        return score >= limit;
    }
    public void resetForNewRound() {
        this.closed = false;
        this.hand.reset(); 
    }

}
