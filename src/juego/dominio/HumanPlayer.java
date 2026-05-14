package juego.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import juego.app.ConsoleInput;
/**
 * Representa a un jugador humano en la partida de Chinchón.
 * * Esta clase extiende de {@link Player} e implementa la lógica necesaria para
 * interactuar con el usuario a través de la consola, permitiéndole tomar decisiones
 * sobre el robo, descarte y el cierre de la ronda.
 * * @author rgoncar723
 * @version 1.0
 */
public class HumanPlayer extends Player {
	private ConsoleInput ci;
	/**
     * Construye un nuevo jugador humano.
     * @param name Nombre del jugador.
     * @param ci Instancia de ConsoleInput para gestionar la interacción.
     */
	public HumanPlayer(String name, ConsoleInput ci) {
		super(name);
		this.ci = ci;

	}
	/**
     * Ejecuta el flujo completo del turno de un humano.
     * 1. Muestra el estado del tablero.
     * 2. Gestiona el robo (mazo o descarte).
     * 3. Gestiona el descarte obligatorio.
     * 4. Consulta y procesa el intento de cierre de ronda.
     * * @param deck Mazo de la ronda.
     * @param discardPile Pila de descartes.
     */
	@Override
	public void playTurn(Deck deck, DiscardPile discardPile) {
		boolean answer = false;
		int drawChoice;
		Card drawnCard;
		Optional<Card> optCard;

		ci.displayBoard(this, discardPile);
		ci.writeLine("¿Robar de (1) Mazo o (2) Descarte?: ");
		drawChoice = ci.readIntInRange(1, 2);

		if (drawChoice == 1) {
			drawnCard = deck.drawCard(discardPile);
		} else {

			optCard = discardPile.pop();

			if (optCard.isPresent()) {
				drawnCard = optCard.get();
			} else {
				ci.writeLine("Pila vacía. Robando del mazo...");
				drawnCard = deck.drawCard(discardPile);
			}
		}

		// Añade la carta a la mano si logramos obtener una
		if (drawnCard != null) {
			hand.addCard(drawnCard);
		}

		// 3. Muestra la mano actualizada (ahora tiene 8 cartas)
		ci.displayHand(hand);
		standardDiscard(discardPile);

		ci.writeLine("¿Quieres cerrar la ronda? (s/n): ");
		answer = ci.readBooleanUsingChar('s', 'n');

		if (answer) {
			handleClosingSequence(discardPile);
		}

	}
	/**
     * Gestiona la selección y ejecución del descarte ordinario.
     * @param discardPile Pila donde se depositará la carta.
     */
	private void standardDiscard(DiscardPile discardPile) {
		int discardIndex;
		Card cardToDiscard;
		ci.writeLine("Elige el índice de la carta a descartar (1-8): ");
		discardIndex = ci.readIntInRange(1, 8);
		cardToDiscard = this.getHand().getCards().get(discardIndex - 1);

		this.getHand().removeCard(cardToDiscard);
		discardPile.push(cardToDiscard);
		
	}
	/**
     * Inicia la secuencia compleja para finalizar la ronda.
     * Solicita al usuario la carta de cierre y la organización de los grupos
     * para validar si cumple con las reglas del Chinchón.
     * * @param discardPile Pila de descarte para la carta de cierre.
     */
	private void handleClosingSequence(DiscardPile discardPile) {
		Card closingDiscard;
		List<Card> cardsToCombine, group1, group2;
		int discardIndex;
		// Al cerrar, el jugador debe elegir su carta de descarte y organizar el resto
		ci.writeLine("Para cerrar, primero elige tu carta de descarte (la que NO formará parte de tus combinaciones):");
		discardIndex = ci.readIntInRange(1, 8);
		closingDiscard = this.getHand().getCards().get(discardIndex - 1);

		// Creamos una copia temporal de la mano sin la carta de cierre para validar
		cardsToCombine = new ArrayList<>(this.getHand().getCards());
		cardsToCombine.remove(closingDiscard);

		// El usuario debe indicar qué cartas forman sus grupos
		group1 = askForGroup("primer", 1, cardsToCombine);
		group2 = askForGroup("segundo", 2, cardsToCombine);

		// Lógica de validación (delegada a CombinationUtils)
		if (CombinationUtils.validateClosing(group1, group2, closingDiscard)) {
			ci.writeLine("¡Cierre válido! La ronda ha terminado.");
			this.getHand().removeCard(closingDiscard);
			discardPile.push(closingDiscard);
		
			this.setClosed(true);
			
		} else {
			ci.writeLine("Combinaciones inválidas o carta de cierre demasiado alta. No puedes cerrar.");
			ci.writeLine("Realizando descarte normal...");
			this.getHand().removeCard(closingDiscard);
			discardPile.push(closingDiscard);
		}
	}
	/**
     * Solicita al usuario que seleccione cartas para formar un grupo.
     * @param order Texto descriptivo ("primer", "segundo").
     * @param num Número de grupo.
     * @param availableCards Lista de cartas disponibles para seleccionar.
     * @return Lista de cartas seleccionadas para el grupo.
     */
	private List<Card> askForGroup(String order, int num, List<Card> availableCards) {
		List<Card> group = new ArrayList<>();
		String[] indices;
		ci.writeLine(String.format("Formando el %s grupo. Cartas disponibles: ", order));
		for (int i = 0; i < availableCards.size(); i++) {
			ci.write(String.format("[%d]%s ", i + 1, availableCards.get(i)));
		}
		ci.writeLine("\nIntroduce índices separados por comas (ej: 1,2,3) o 'skip':");

		String input = ci.readStringNotEmpty();
		if (input.equalsIgnoreCase("skip")) {
			return group;
		}

		indices = input.split(",");
		for (String s : indices) {
			try {
				int idx = Integer.parseInt(s.trim()) - 1;
				if (idx >= 0 && idx < availableCards.size()) {
					group.add(availableCards.get(idx));
				}
			} catch (NumberFormatException e) {
				ci.writeLine("Índice inválido omitido.");
			}
		}
		return group;
	}
}
