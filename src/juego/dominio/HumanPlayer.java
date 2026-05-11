package juego.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import juego.app.ConsoleInput;

public class HumanPlayer extends Player {
	private ConsoleInput ci;

	public HumanPlayer(String name, ConsoleInput ci) {
		super(name);
		this.ci = ci;

	}

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

		ci.writeLine("¿Quieres cerrar la ronda? (s/n): ");
		answer = ci.readBooleanUsingChar('s', 'n');

		if (answer) {
			handleClosingSequence(discardPile);
		} else {
			standardDiscard(discardPile);
		}

	}

	private void standardDiscard(DiscardPile discardPile) {
		int discardIndex;
		Card cardToDiscard;
		ci.writeLine("Elige el índice de la carta a descartar (1-8): ");
		discardIndex = ci.readIntInRange(1, 8);
		cardToDiscard = this.getHand().getCards().get(discardIndex - 1);

		this.getHand().removeCard(cardToDiscard);
		discardPile.push(cardToDiscard);
		ci.write(String.format("Has descartado:", cardToDiscard.toString()));
	}

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
		} else {
			ci.writeLine("Combinaciones inválidas o carta de cierre demasiado alta. No puedes cerrar.");
			ci.writeLine("Realizando descarte normal...");
			this.getHand().removeCard(closingDiscard);
			discardPile.push(closingDiscard);
		}
	}

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
