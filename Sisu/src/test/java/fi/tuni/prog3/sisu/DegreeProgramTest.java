package fi.tuni.prog3.sisu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for class DegreeProgram
 * @author tobsu
 */
public class DegreeProgramTest {
    
    /**
     * Test getter methods
     */
   @Test
   public void testGetters() {
       
       DegreeProgram d = new DegreeProgram("Biolääketieteen kandidaattiohjelma", "BIO", "BIO_group", 180);
       
       String expName = "Biolääketieteen kandidaattiohjelma";
       String resultName = d.getName();
       String expId = "BIO";
       String resultId = d.getId();
       String expGroupId = "BIO_group";
       String resultGroupId = d.getGroupId();
       int expMinCredits = 180;
       int resultMinCredits = d.getMinCredits();
       
       assertEquals(expName, resultName);
       assertEquals(expId, resultId);
       assertEquals(expGroupId, resultGroupId);
       assertEquals(expMinCredits, resultMinCredits);
       
   }
}
