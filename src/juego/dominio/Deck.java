package juego.dominio;

import java.util.Collections;
import java.util.Deque;
import java.util.List;
/**
 * Gestiona el mazo de cartas principal del juego Chinchón. 
 * * Esta clase se encarga de administrar las cartas disponibles para robar, 
 * permitiendo la configuración de una o dos barajas españolas y gestionando 
 * el reciclaje de cartas desde el montón de descarte cuando el mazo se agota. 
 * *@author rgoncar723
 * @version 1.0
 */
public class Deck {
	private Deque<Card> cards;
	/**
     * Construye un mazo nuevo utilizando la factoría de cartas. 
     *  @param numberOfDecks Número de barajas de 40 cartas a incluir (1 o 2). 
     */
	public Deck(int numberOfDecks) {
		
		cards = DeckFactory.createCustomDeck(numberOfDecks);
	}
	/**
     * Recarga el mazo principal utilizando las cartas acumuladas en el descarte.
     * * Este proceso extrae todas las cartas del descarte (excepto la superior),
     * las baraja nuevamente para evitar que sean predecibles y las añade al mazo.
     * * @param discardPile El montón de descarte del cual se extraerán las cartas. 
     */
    public void refillFromDiscard(DiscardPile discardPile) {
        List<Card> newCards = discardPile.grabAllButLast();
        Collections.shuffle(newCards); 
        for (Card card : newCards) {
            cards.push(card);
        }
    }

    /**
     * Extrae la carta superior del mazo. 
     * Si el mazo está vacío, intenta recargarse automáticamente desde el descarte. 
     * @param discardPile Referencia necesaria para recargar el mazo si fuera necesario.
     * @return La carta robada del mazo. 
     * @throws IllegalStateException Si no quedan cartas ni en el mazo ni en el descarte.
     */
    public Card drawCard(DiscardPile discardPile) {
    	removeCard();
    	
        if (cards.isEmpty()) {
            refillFromDiscard(discardPile);
        }
        
        if (cards.isEmpty()) {
            throw new IllegalStateException("No hay cartas disponibles ni en el mazo ni en el descarte.");
        }
        
        return cards.pop();
    }

    /**
     * Obtiene el número actual de cartas en el mazo.
     * * @return Cantidad de cartas restantes.
     */
    public int sizeDeck() {
        return cards.size();
    }
    public boolean removeCard() {
    
		if (cards.isEmpty()) {
            throw new IllegalStateException("No quedan cartas en el mazo");
        }else {
        	return cards.remove(cards.getLast());
        }
		
		
		
	}
	
}
