package juego.app;

import java.util.ArrayList;
import java.util.List;

import juego.dominio.AiPlayer;
import juego.dominio.HumanPlayer;
import juego.dominio.Player;
/**
 * Clase de utilidad encargada de la instanciación de los jugadores de la partida.
 * *Permite configurar dinámicamente si un jugador será controlado por un usuario 
 * humano o por la inteligencia artificial (IA), cumpliendo con los requisitos 
 * del proyecto de soportar ambos tipos de participantes. 
 * * @author rgoncar723
 * @version 1.0
 */
public class PlayerBuilder {
	/**
     * Construye la lista completa de jugadores que participarán en la partida. 
     * * @param count Número de jugadores a crear (entre 2 y 5 según reglas). 
     * @param ui Instancia de ConsoleInput para la comunicación con el usuario. 
     * @return Lista de objetos Player (HumanPlayer o AiPlayer). 
     */
	public static List<Player> buildPlayers(int count, ConsoleInput ui) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            players.add(askPlayerType(i + 1, ui));
        }
        return players;
    }
	/**
     * Solicita los datos de un jugador individual y devuelve la instancia correspondiente.
     * @param index Índice correlativo del jugador para mostrar en consola.
     * @param ci Instancia de ConsoleInput para la lectura de datos. 
     * @return Una instancia de HumanPlayer o AiPlayer. 
     */
	public static Player askPlayerType(int index, ConsoleInput ci) {
		String name;
		int type;
		ci.write(String.format("Nombre del jugador %d: ", index));
		name = ci.readString();
        ci.writeLine("Selecciona tipo: 1 Humano | 2 IA");
        type = ci.readIntInRange(1, 2);
        if (type == 1) {
            return new HumanPlayer(name, ci);
        } else {
            return new AiPlayer(name);
        }
    }
}
