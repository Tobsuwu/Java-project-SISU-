
package fi.tuni.prog3.sisu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for class StudyModule
 * @author tobsu
 */
public class StudyModuleTest {

    @Test
    public void testGetters() {
            
        StudyModule m = new StudyModule("Computational physics", "COMP.PHYS", "FYS-123", 25);
 
        String expName = "Computational physics";
        String resultName = m.getName();
        String expId = "COMP.PHYS";
        String resultId = m.getId();
        String expGroupid = "FYS-123";
        String resultGroupid = m.getGroupId();
        int expMincredits = 25;
        int resultMincredits = m.getMinCredits();
        
        assertEquals(expName, resultName);
        assertEquals(expId, resultId);
        assertEquals(expGroupid, resultGroupid);
        assertEquals(expMincredits, resultMincredits);
    }
}