package juego.dominio;

public enum Rank {
	/**
	 * Representa los valores o rangos de las cartas en la baraja española de 40 cartas.
	 * Esta enumeración define los números del 1 al 7 y las figuras (Sota, Caballo, Rey),
	 * omitiendo los ochos y nueves según las reglas del juego.
	 * Los valores numéricos asociados se utilizan tanto para verificar la consecutividad
	 * en las escaleras como para el cálculo de puntos al final de la ronda.
	 * @author rgoncar723
	 * @version 1.0
	 */
	ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), SOTA(10), KNIGHT(11), KING(12);

	private final int value;
	/**
     * Constructor del rango.
     * @param value El valor entero asociado a la carta.
     */
	Rank(int value) {
		this.value = value;
	}
	/**
     * Obtiene el valor numérico del rango. 
     * Este valor se utiliza para:
     * 1. Validar escaleras (Ej: 7 seguido de 10)[cite: 42].
     * 2. Sumar puntos de cartas no combinadas al cerrar la ronda.
     * 3. Verificar si la carta de cierre es válida (valor entre 1 y 5).
     * * @return El valor entero de la carta.
     */
	public int getRank() {
		return value;
	}
}
