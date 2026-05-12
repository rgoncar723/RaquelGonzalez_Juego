package juego.app;
import java.util.*;

import juego.dominio.Player;
import juego.dominio.Round;
public class GameController {
	private List<Player> players;
	private List<Player> eliminatedPlayers;
	private int pointLimit;
	private List<Round> rounds;
    private ConsoleInput ci;
    private int numberOfDecks;
    
    public GameController(int pointLimit, int numberOfDecks, ConsoleInput ui) {
        this.players = new ArrayList<>();
        this.rounds = new ArrayList<>();
        this.pointLimit = pointLimit;
        this.numberOfDecks = numberOfDecks;
        this.ci = ui;
    }

    /**
     * Punto de entrada principal tras configurar los jugadores.
     */
    public void startGame() {
        ci.write("¡Bienvenidos al Chinchón!\n");
        startGameLoop();
    }
    /**
     * Bucle principal: Una iteración = Una ronda completa.
     */
    public void startGameLoop() {
    	do {
    		 nextRound();      
             updateScores();    
             eliminatePlayers(); 
             
             ci.displayScores(players); 
    	}
        while (!isGameOver()); 
        
        declareWinner();
    }
    /**
     * Crea una nueva instancia de Round y la ejecuta.
     */
    public void nextRound() {
        // En cada ronda el mazo se regenera 
        Round currentRound = new Round(players, numberOfDecks, pointLimit);
        rounds.add(currentRound);
        
        ci.write("--- Iniciando nueva ronda ---\n");
        currentRound.execute(); 
    }

    /**
     * Al finalizar una ronda, se penaliza a los jugadores.
     */
    public void updateScores() {
        for (Player p : players) {
            // Obtenemos los puntos de las cartas que no pudo combinar
            int pointsToAdd = p.getHand().calculateUncombinedPoints();
            p.addPoints(pointsToAdd);
            
            ci.write(String.format("\n%s suma %d %s.\n", p.getName(),pointsToAdd,pointsToAdd>1?"puntos":"punto"));
           
        }
    }

    /**
     * Filtra la lista de jugadores eliminando a los que perdieron.
     */
    public void eliminatePlayers() {
    	eliminatedPlayers = new ArrayList<>(players);
    	eliminatedPlayers.removeIf(p -> {
            boolean eliminated = p.isEliminated(pointLimit);
            if (eliminated) {
                ci.write(String.format("El jugador %s ha sido eliminado.\n", p.getName()));
                
            }
            return eliminated;
        });
    }

    /**
     * El juego termina si solo queda un jugador vivo (o ninguno por empate técnico).
     */
    public boolean isGameOver() {
        return players.size() <= 1;
    }

    /**
     * Muestra quién es el campeón.
     */
    public Player declareWinner() {
        if (players.isEmpty()) {
            ci.write("No hay ganadores, todos han sido eliminados.");
            return null;
        }
        Player winner = players.get(0); // El ganador
        ci.write(String.format("¡EL GANADOR ES  %s !\n", winner.getName().toUpperCase()));
        return winner;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }


	
}
