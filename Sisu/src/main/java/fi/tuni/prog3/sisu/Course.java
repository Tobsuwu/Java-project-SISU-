
package fi.tuni.prog3.sisu;


/**
 * A class defining a single course. Extends the abstract class DegreeModule.
 * @author antti
 */

public class Course extends DegreeModule implements Comparable<Course> {
    private String outcomes;
    
    
    /**
     * Constructor for initializing variables in this class and its base class.
     * @param name Course name
     * @param id Course id
     * @param groupId groupId of the course
     * @param minCredits Course credits
     * @param outcomes Course learning outcomes
     */
    public Course(String name, String id, String groupId, int minCredits, String outcomes) {
        super(name, id, groupId, minCredits);
        this.outcomes = outcomes;
    }
    
    
    /**
     * Getter for learning outcomes of the course.
     * @return Learning outcomes of the course.
     */
    public String getOutcomes() {
        return this.outcomes;
    }
    
    
    /**
     * Enable comparison between courses.
     * @param c Course for comparison.
     * @return result of comparison.
     */
    @Override
    public int compareTo(Course c) {
        return this.getName().compareTo(c.getName());
        
    }
}
