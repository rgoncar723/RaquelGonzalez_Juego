package juego.app;
import java.util.*;

import juego.dominio.Player;
import juego.dominio.Round;
public class GameController {
	private List<Player> players;
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
    		 ci.writeLine("MARCADOR ACUMULADO");
    		 for(Player p: players) {
    			 ci.write(String.format("Jugador: %s\t Puntuaje:%d\n ", p.getName(),p.getScore()));
    		 }
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
     * Filtra la lista de jugadores eliminando a los que perdieron.
     */
    public void eliminatePlayers() {
    	players.removeIf(p -> {
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
    	Player winner;
        if (players.isEmpty()) {
            ci.write("No hay ganadores, todos han sido eliminados.");
            return null;
        }
        winner = players.get(0); // El ganador
        ci.write(String.format("¡EL GANADOR ES  %s !\n", winner.getName().toUpperCase()));
        return winner;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }


	
}
