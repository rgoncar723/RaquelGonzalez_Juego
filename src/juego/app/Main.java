package juego.app;

import java.util.List;

import juego.dominio.Player;

public class Main {
	public void show() {
		ConsoleInput ci = new ConsoleInput();
		int numPlayers, pointLimit, numDecks;
		List<Player> players;
		GameController game;

		ci.writeLine("       BIENVENIDO A CHINCHÓN 2026       ");
		// 1. Configuración inicial de la partida
		// Usamos los métodos de ConsoleInput para validar la entrada
		ci.writeLine("¿Cuántos jugadores participarán? ");
		numPlayers = ci.readInt();
		ci.writeLine("El límitee de puntos es 100");
		pointLimit = 100;
		ci.writeLine("¿Con cuántas barajas jugaran?: ");
		numDecks = ci.readInt();

		// 2. Creación de jugadores mediante el PlayerBuilder
		// Pasamos 'ui' para que el Builder pueda preguntar nombres y dificultades
		ci.writeLine("\n--- CONFIGURACIÓN DE JUGADORES ---");
		players = PlayerBuilder.buildPlayers(numPlayers, ci);

		// 3. Inicialización del GameController
		// Le entregamos el límite, el número de barajas y la interfaz de consola
		game = new GameController(pointLimit, numDecks, ci);

		for (Player p : players) {
			game.addPlayer(p);
		}

		
		
		try {
			game.startGame();
		} catch (Exception e) {
			ci.writeLine("Se ha producido un error crítico: " + e.getMessage());
		}

	}

	public static void main(String[] args) {
		new Main().show();

	}

}
