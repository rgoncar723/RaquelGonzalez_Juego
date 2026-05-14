package juego.dominio;


import java.util.List;
/**
 * Representa una ronda individual dentro de una partida de Chinchón.
 * Gestiona el mazo, la pila de descartes, el reparto de cartas, 
 * el flujo de turnos y el recuento final de puntuaciones.
 * * @author rgoncar723
 * @version 1.0
 */
public class Round {
	private List<Player> players;
	private Deck deck;
	private DiscardPile pile;
	private int currentPlayerIndex;
	private boolean isFinished;
	private static final int NORMAL_HAND_SIZE = 7;
	private int pointLimits;
	/**
     * Constructor de la ronda.
     * @param players Lista de jugadores que participan en la ronda.
     * @param numDecks Número de barajas a utilizar.
     * @param pointLimits Límite de puntos para el control de eliminación.
     */
	public Round(List<Player> players, int numDecks, int pointLimits) {
		this.players = players;
		this.deck = new Deck(numDecks);
		this.pile = new DiscardPile();
		this.currentPlayerIndex = 0;
		this.isFinished = false;
		this.pointLimits = pointLimits;
	}
	/**
     * Realiza el reparto inicial de cartas.
     * Limpia el estado de los jugadores y reparte 7 cartas a cada uno.
     * Al finalizar, sitúa la primera carta del mazo en la pila de descartes.
     */
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
	/**
     * Ejecuta el turno del jugador actual.
     * Si tras jugar su turno el jugador activa su flag de cierre, 
     * la ronda se marca como finalizada.
     */
	public void start() {
		Player currentPlayer = players.get(currentPlayerIndex);

		currentPlayer.playTurn(deck, pile);

	    
	    if (currentPlayer.hasClosed()) {
	        this.isFinished = true;
	    }
	}
	/**
     * Incrementa el índice para pasar el turno al siguiente jugador 
     * de forma circular.
     */

	public void nextTurn() {
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
	}
	/**
     * Indica si la ronda ha concluido.
     * @return true si alguien ha cerrado con éxito; false en caso contrario.
     */
	public boolean isRoundOver() {
		return isFinished;
	}
	/**
     * Obtiene el mazo de cartas actual de la ronda.
     * Útil para consultar el estado de las cartas restantes o realizar
     * operaciones de depuración sobre la baraja.
     * * @return El objeto {@link Deck} que se está utilizando en esta ronda.
     */
	public Deck getDeck() {
		return deck;
	}
	/**
     * Obtiene la pila de descartes de la ronda.
     * Permite acceder a la carta superior visible o gestionar el historial
     * de cartas lanzadas por los jugadores.
     * * @return El objeto {@link DiscardPile} asociado a la mesa de juego.
     */
	public DiscardPile getPile() {
		return pile;
	}
	/**
     * Orquestador de la ronda.
     * Controla el flujo completo: reparto, bucle de turnos hasta el cierre 
     * y actualización final de puntuaciones.
     */
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
     * Finaliza la ronda calculando y asignando las penalizaciones.
     * Aplica las reglas del Chinchón:
     * - El que cierra con 7 cartas combinadas resta 10 puntos.
     * - El que cierra con 6 cartas combinadas suma 0 puntos.
     * - Los demás jugadores suman los puntos de sus cartas no combinadas.
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
