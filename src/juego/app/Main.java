package juego.app;

import java.util.List;

import juego.dominio.Player;

/**
 * Clase principal que actúa como punto de entrada para la aplicación Chinchón
 * 2026. * Se encarga de la inicialización de los componentes básicos, la
 * configuración inicial por parte del usuario y el arranque del motor de juego.
 * * @author rgoncar723
 * 
 * @version 1.0
 */
public class Main {
	public void show() {
		ConsoleInput ci = new ConsoleInput();
		int numPlayers, pointLimit, numDecks;
		List<Player> players;
		GameController game;

		ci.writeLine("BIENVENIDO A CHINCHÓN 2026");

		ci.writeLine("¿Cuántos jugadores participarán? ");
		numPlayers = ci.readInt();
		ci.writeLine("El límitee de puntos es 100");
		pointLimit = 100;
		ci.writeLine("¿Con cuántas barajas jugaran?: ");
		numDecks = ci.readInt();

		ci.writeLine("\n--- CONFIGURACIÓN DE JUGADORES ---");
		players = PlayerBuilder.buildPlayers(numPlayers, ci);

		game = new GameController(pointLimit, numDecks, ci);

		for (Player p : players) {
			game.addPlayer(p);
		}

		try {
			game.startGame();
		} catch (Exception e) {
			ci.write(String.format("Se ha producido un error crítico: %s", e.getMessage()));
		}

	}

	public static void main(String[] args) {
		new Main().show();

	}

}
