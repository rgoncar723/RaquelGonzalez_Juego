package juego.app;
import java.util.*;


import juego.dominio.Player;
import juego.dominio.Round;
/**
 * Controlador principal del flujo del juego de Chinchón.
 * * Gestiona el ciclo de vida de la partida, coordinando la creación de rondas,
 * el seguimiento de las puntuaciones acumuladas, la eliminación de jugadores
 * que superan el límite de puntos y la declaración del ganador final.
 * * @author rgoncar723
 * @version 1.0
 */
public class GameController {
	private List<Player> players;
	private int pointLimit;
	private List<Round> rounds;
    private ConsoleInput ci;
    private int numberOfDecks;
    /**
     * Constructor del controlador del juego.
     * * @param pointLimit Límite de puntos establecido para la eliminación.
     * @param numberOfDecks Número de barajas con las que se jugará.
     * @param ci Instancia de ConsoleInput para la interacción.
     */
    public GameController(int pointLimit, int numberOfDecks, ConsoleInput ci) {
        this.players = new ArrayList<>();
        this.rounds = new ArrayList<>();
        this.pointLimit = pointLimit;
        this.numberOfDecks = numberOfDecks;
        this.ci = ci;
    }

    /**
     * Inicia el proceso de bienvenida y arranca el bucle de juego principal.
     */
    public void startGame() {
        ci.write("¡Bienvenidos al Chinchón!\n");
        startGameLoop();
    }
    /**
     * Gestiona el bucle principal de la partida.
     * * Ejecuta rondas de forma sucesiva mediante un bucle do-while. 
     * Tras cada ronda, muestra el marcador, elimina a los jugadores que 
     * exceden el límite y comprueba si se ha cumplido la condición de fin de juego.
     */
    public void startGameLoop() {
    	do {
    		 nextRound();     
    		 ci.write("\nMARCADOR ACUMULADO\n");
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
     * Crea, registra y ejecuta una nueva ronda de juego.
     * Cada ronda regenera el mazo y la pila de descartes de forma independiente.
     */
    public void nextRound() {
        
        Round currentRound = new Round(players, numberOfDecks, pointLimit);
        rounds.add(currentRound);
        
        ci.write("\n--- Iniciando nueva ronda ---\n");
        currentRound.execute(); 
    }

   

    /**
     * Filtra la lista de jugadores utilizando un predicado.
     * Elimina de la colección a aquellos jugadores cuya puntuación acumulada
     * sea igual o superior al límite de puntos establecido.
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
     * Determina si la partida ha finalizado.
     * * @return true si queda uno o ningún jugador en la lista; false si la partida continúa.
     */
    public boolean isGameOver() {
        return players.size() <= 1;
    }

    /**
     * Finaliza la partida y anuncia el resultado.
     * Identifica al último jugador restante como el campeón de la partida.
     * * @return El objeto {@link Player} ganador, o null si no quedan jugadores.
     */
    public Player declareWinner() {
    	Player winner;
        if (players.isEmpty()) {
            ci.write("No hay ganadores, todos han sido eliminados.");
            return null;
        }
        winner = players.get(0); 
        ci.write(String.format("¡EL GANADOR ES  %s !\n", winner.getName().toUpperCase()));
        return winner;
    }
    /**
     * Añade un nuevo jugador a la lista de participantes antes de iniciar la partida.
     * * @param player El jugador (humano o IA) que se une a la partida.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }


	
}
