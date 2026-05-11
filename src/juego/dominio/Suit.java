package juego.dominio;
/**
 * Representa los cuatro palos de la baraja española utilizados en el juego del Chinchón.
 * 
 * Esta enumeración define los palos: Oros (Gold), Copas (Cups), Espadas (Swords) y Bastos (Clubs).
 * Cada constante incluye una representación visual mediante emojis para mejorar la interfaz de 
 * usuario en la consola. 
 * @author rgoncar723
 * @version 1.0
 */
public enum Suit {
GOLD("🪙"),CUPS("🍷"),SWORDS("⚔️"),CLUBS("🦯");
	
	private final String description;
	/**
     * Constructor privado del enumerado.
     * * @param description El emoji o cadena representativa del palo.
     */
	Suit(String description) {
		this.description=description;
	}
	/**
     * Obtiene la representación visual del palo para su uso en {@code ConsoleInput}. 
     * * @return Una cadena con el icono representativo.
     */
	public String getDescription() {
		return description;
	}
}
