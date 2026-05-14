package juego.dominio;
/**
 * Clase abstracta que define la estructura y el comportamiento base de un jugador
 * en el juego de Chinchón.
 * Proporciona la gestión del nombre, la puntuación acumulada, la mano de cartas
 * y el estado de cierre de la ronda. Al ser abstracta, obliga a las subclases 
 * (Humano/IA) a implementar su propia lógica de juego en cada turno.
 * * @author rgoncar723
 * @version 1.0
 */
public abstract class Player {
	protected String name;
	protected int score;
	protected Hand hand;
	protected boolean closed;
	/**
     * Constructor para inicializar un jugador con su nombre.
     * La puntuación comienza en 0 y se genera una mano vacía.
     * @param name Nombre del jugador.
     */
	public Player(String name) {
		this.name = name;
		this.score = 0;
		this.hand = new Hand();
		this.closed = false;
	}
	/**
     * Método abstracto que define la estrategia de juego del jugador.
     * Debe ser implementado por las subclases para gestionar cómo el jugador
     * roba y descarta cartas.
     * @param deck El mazo de la ronda actual.
     * @param discardPile La pila de descartes de la ronda actual.
     */
	public abstract void playTurn(Deck deck, DiscardPile discardPile);

	/**
     * Suma una cantidad de puntos a la puntuación acumulada del jugador.
     * @param points Puntos a añadir (pueden ser negativos si hay bonificación).
     */
    public void addPoints(int points) {
        this.score += points;
    }

    /**
     * Obtiene el nombre del jugador.
     * @return El nombre del jugador.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene la puntuación acumulada.
     * @return Puntos totales del jugador.
     */
    public int getScore() {
        return score;
    }

    /**
     * Obtiene el objeto Hand que representa las cartas del jugador.
     * @return La mano del jugador.
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Indica si el jugador ha ejecutado una acción de cierre en la ronda.
     * @return true si el jugador ha cerrado; false en caso contrario.
     */
    public boolean hasClosed() {
        return closed;
    }

    /**
     * Cambia el estado de cierre del jugador.
     * Este método es crucial para que la clase Round detecte el fin de la ronda.
     * @param closed Nuevo estado de cierre.
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Comprueba si el jugador debe ser eliminado de la partida.
     * @param limit El límite de puntos establecido para la partida.
     * @return true si el jugador ha alcanzado o superado el límite; false si sigue activo.
     */
    public boolean isEliminated(int limit) {
        return score >= limit;
    }

    /**
     * Prepara al jugador para una nueva ronda.
     * Restablece el flag de cierre y vacía la mano de cartas.
     */
    public void resetForNewRound() {
        this.closed = false;
        hand.reset();
    }
}
