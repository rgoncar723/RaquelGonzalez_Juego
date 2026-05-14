package juego.pruebas;
import static org.junit.jupiter.api.Assertions.*;
import juego.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;
public class CombinationUtilsTest {
	private Card sieteOros, sotaOros, caballoOros, reyOros;
    private Card sieteCopas, sieteEspadas;

    @BeforeEach
    void setUp() {
       
        sieteOros = new Card(Rank.SEVEN, Suit.GOLD);     
        sotaOros = new Card(Rank.SOTA, Suit.GOLD);       
        caballoOros = new Card(Rank.KNIGHT, Suit.GOLD); 
        reyOros = new Card(Rank.KING, Suit.GOLD);         
        
        sieteCopas = new Card(Rank.SEVEN, Suit.CUPS);
        sieteEspadas = new Card(Rank.SEVEN, Suit.SWORDS);
    }

    @Test
 
    void testIsGroup() {
        List<Card> grupoValido = Arrays.asList(sieteOros, sieteCopas, sieteEspadas);
        List<Card> grupoInvalido = Arrays.asList(sieteOros, sieteCopas, sotaOros);
        List<Card> grupoPocoTamanyo = Arrays.asList(sieteOros, sieteCopas);

        assertTrue(CombinationUtils.isGroup(grupoValido), "Debería ser un grupo válido de 7s");
        assertFalse(CombinationUtils.isGroup(grupoInvalido), "No debería ser grupo (valores distintos)");
        assertFalse(CombinationUtils.isGroup(grupoPocoTamanyo), "No debería ser grupo (menos de 3 cartas)");
    }
    @Test
   
    void testIsSequenceSpanishOrder() {
        
        List<Card> escaleraOros = Arrays.asList(sieteOros, sotaOros, caballoOros);
        
        assertTrue(CombinationUtils.isSequence(escaleraOros), 
            "Debería reconocer 7-Sota-Caballo como consecutivos en orden español");
    }
    @Test
    
    void testValidateClosingScenarios() {
        List<Card> g1 = Arrays.asList(sieteOros, sotaOros, caballoOros); 
        List<Card> g2 = Arrays.asList(sieteCopas, sieteEspadas, new Card(Rank.SEVEN, Suit.CLUBS)); 
        Card cierreValido = new Card(Rank.FIVE, Suit.GOLD); 
        Card cierreInvalido = new Card(Rank.SEVEN, Suit.GOLD);

       
        assertTrue(CombinationUtils.validateClosing(g1, g2, cierreValido), 
            "Cierre legal con 6 cartas combinadas y un 5");

        
        assertFalse(CombinationUtils.validateClosing(g1, g2, cierreInvalido), 
            "Cierre ilegal: la carta de descarte es mayor a 5");
    }
    @Test
   
    void testIsChinchon() {
        List<Card> manoChinchon = Arrays.asList(
            new Card(Rank.ACE, Suit.GOLD),
            new Card(Rank.TWO, Suit.GOLD),
            new Card(Rank.THREE, Suit.GOLD),
            new Card(Rank.FOUR, Suit.GOLD),
            new Card(Rank.FIVE, Suit.GOLD),
            new Card(Rank.SIX, Suit.GOLD),
            new Card(Rank.SEVEN, Suit.GOLD)
        );
        
        assertTrue(CombinationUtils.isChinchon(manoChinchon), "Debería ser Chinchón");
    }
}
