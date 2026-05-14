package juego.app;

import java.util.List;
import java.util.Scanner;

import juego.dominio.Card;
import juego.dominio.DiscardPile;
import juego.dominio.Hand;
import juego.dominio.Player;

public class ConsoleInput {
	private final Scanner kb;

	public ConsoleInput() {
		kb = new Scanner(System.in);
	}

	void cleanInput() {// Limpiar el buffer

		kb.nextLine();

	}

	public int readInt() {
		int numberInt = 0;
		boolean error = true;
		do {
			try {
				numberInt = kb.nextInt();
				error = false;
				kb.nextLine(); 
			} catch (NumberFormatException e) {
				System.out.printf("Error: El valor debe ser un número entero entre %d y %d.\n", Integer.MIN_VALUE,
						Integer.MAX_VALUE);
			}
		} while (error);
		return numberInt;
	}

	public int readIntInRange(int lowerBound, int upperBound) {
		int numberInt;
		do {
			
			numberInt = readInt();
			if (numberInt < lowerBound || numberInt > upperBound) {
				System.out.printf("Error: %d no está en el rango [%d, %d].\n", numberInt, lowerBound, upperBound);
			}
		} while (numberInt < lowerBound || numberInt > upperBound);
		return numberInt;
	}

	public String readString() {
		return kb.nextLine();
	}

	public String readStringNotEmpty() {
		String text;
		do {
			text = kb.nextLine();

			if (text.trim().isEmpty()) {
				System.out.println("No puedes ingresar una cadena vacía.");
			}
		} while (text.trim().isEmpty());
		return text;
	}

	public String readString(int maxLength) {
		String text;
		do {
			System.out.printf("Máximo %d caracteres: ", maxLength);
			text = kb.nextLine();

			if (text.length() > maxLength) {
				System.out.println("La cadena supera el máximo permitido.");
			}
		} while (text.length() > maxLength);
		return text;
	}
	public char readChar() {
		String text;

		do {
			text = kb.nextLine().trim();

			if (text.length() != 1) {
				System.out.println("Por favor, introduce un solo carácter.");
			}
		} while (text.length() != 1);
		return text.charAt(0);
	}

	public boolean readBooleanUsingChar(char affirmativeValue, char negativeValue) {
		char input;
		boolean result = false;
		boolean error = true;

		char upperAffirmative = Character.toUpperCase(affirmativeValue);
		char upperNegative = Character.toUpperCase(negativeValue);

		do {
			input = Character.toUpperCase(readChar());

			if (input == upperAffirmative) {
				result = true;
				error = false;
			} else if (input == upperNegative) {
				result = false;
				error = false;
			} else {
				System.out.println("Carácter inválido");
			}
		} while (error);

		return result;
	}
	public String getUserName(Player player) {
		return String.format("Nombre: %s", player.toString().toUpperCase());
	}

	// Verificar
	public String getPlayerType(Player player) {
		return String.format("Tipo de jugador: %s", player.getClass().toString());
	}

	public void displayHand(Hand hand) {
		writeLine("TU MANO:");
		List<Card> cards = hand.getCards();

		
		for (int i = 0; i <= cards.size()-1; i++) {
			write(String.format("(%d)\t", i+1));

		}
		writeLine("");

		
		for (Card card : cards) {
			write(String.format("[%s]\t", card.toString()));
		}
		writeLine("\n" + "-".repeat(40));
	}

	public void displayBoard(Player player, DiscardPile pile) {

		writeLine("=".repeat(40));

		write(String.format("TURNO ACTUAL: %s\n ", player.getName().toUpperCase()));

		// Mostrar la carta superior de la pila de descarte
		writeLine("CARTA EN EL DESCARTE: ");
		if (pile.isEmpty()) {
			writeLine("[ Vacía ]");
		} else {
			write(String.format("[%s]\n ",pile.peek().get().toString()));

		}

		// Mostrar la mano del jugador
		displayHand(player.getHand());
	}

	public int getDiscardIndex(int handSize) {
		return readIntInRange(1, handSize) - 1;
	}

	public void displayScores(List<Player> players) {
		System.out.printf("\tTABLA DE PUNTUACIONES\t\n%-15s | %-10s\n----------------------------------------\n",
				"JUGADOR", "PUNTOS");
		for (Player p : players) {
			System.out.printf("%-15s | %-10d\n", p.getName(), p.getScore());
		}
	}
	public Card askDiscardCard(Hand hand) {
		int handSize, index;
	    displayHand(hand);
	    handSize = hand.getCards().size();
	    index = readIntInRange(1, handSize)-1;
	    return hand.getCards().get(index);
	}
	public int getDrawChoice(boolean isDiscardEmpty) {
		int choice = 0;
			
			write("¿Robar de [1] MAZO" + (isDiscardEmpty ? "" : " o [2] DESCARTE") + "?: ");
			
			choice = readIntInRange(1, 2);
			if (isDiscardEmpty && choice == 2) {
				writeLine("La pila de descarte está vacía. Debes robar del mazo.");
				choice = 0;
			}


		return choice;
	}

	public void write(String texto) {
		System.out.print(texto);
	}

	public void writeLine(String texto) {
		System.out.println(texto);
	}

}
