package fi.tuni.prog3.sisu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for class Course
 * @author tobsu
 */
public class CourseTest {
    
    /**
     * Test getter methods.
     */
    @Test
    public void testGetters() {
    
        Course c = new Course("Ohjelmointi 3", "COMP.CS.140", "gdsjfofj", 5, "Kurssin suoritettuaan opiskelija osaa hakea Kelalta tukia");
        
        String expName = "Ohjelmointi 3";
        String resultName = c.getName();
        String expId = "COMP.CS.140";
        String resultId = c.getId();
        String expGroupid = "gdsjfofj";
        String resultGroupid = c.getGroupId();
        int expMincredits = 5;
        int resultMincredits = c.getMinCredits();
        String expOutcomes = "Kurssin suoritettuaan opiskelija osaa hakea Kelalta tukia";
        String resultOutcomes = c.getOutcomes();
        
        assertEquals(expName, resultName);
        assertEquals(expId, resultId);
        assertEquals(expGroupid, resultGroupid);
        assertEquals(expMincredits, resultMincredits);
        assertEquals(expOutcomes, resultOutcomes);
    }
    
    @Test
    public void testCompareTo() {
        Course c1 = new Course("Ohjelmointi 3", "COMP.CS.140", "gdsjfofj", 5, "Kurssin suoritettuaan opiskelija osaa hakea Kelalta tukia");
        Course c2 = new Course("Laaja fysiikka 1", "FYS.100", "teruuegeigfh", 5, "Kurssin suoritettuaan opiskelija osaa hake opintolainaa");
        
        int result = c1.getName().compareTo(c2.getName());
        assertTrue(0 < result);
    }
}
