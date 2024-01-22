
package fi.tuni.prog3.sisu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author antti
 */
public class StudentTest {
    @Test
    public void testGetters() {
        String name = "Teemu Teekkari";
        String[] names = name.split(" ");
        String studentNumber = "123";
        Student testStudent = new Student(name, studentNumber);
        
        assertEquals(studentNumber, testStudent.getStudentNumber());
        assertEquals(names[0], testStudent.getFirstName());
        assertEquals(names[1], testStudent.getLastName());
    }
    
    @Test
    public void testSetters() {
        String name = "Teemu Teekkari";
        String studentNumber = "123";
        Student testStudent = new Student(name, studentNumber);
        DegreeProgram testDp = new DegreeProgram("Biotekniikka", "123",
                                          "A1", 0);
        StudyModule testSm = new StudyModule("Laitteet ja järjestelmät", "345", "LJ", 55);
        
        testStudent.setDegreeProgram(testDp);
        testStudent.setStudyModule(testSm);
        assertEquals(testDp, testStudent.getDegreeProgram());
        assertEquals(testSm, testStudent.getStudyModule());
    }
    
    // Test Attainment
    @Test
    public void testAttainment() {
        String name = "Teemu Teekkari";
        String studentNumber = "123";
        Student testStudent = new Student(name, studentNumber);
        
        Course c = new Course("Ohjelmointi 3", "COMP.CS.140", "gdsjfofj", 5, "Kurssin suoritettuaan opiskelija osaa hakea Kelalta tukia");
        Student.Attainment a = testStudent.new Attainment(3, c);
        testStudent.addAttainment(a);
        assertTrue(testStudent.getAttainments().contains(a));
    }
}
