package juego.app;

import java.util.ArrayList;
import java.util.List;

import juego.dominio.AiPlayer;
import juego.dominio.HumanPlayer;
import juego.dominio.Player;

public class PlayerBuilder {
	public static List<Player> buildPlayers(int count, ConsoleInput ui) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            players.add(askPlayerType(i + 1, ui));
        }
        return players;
    }
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
