package fi.tuni.prog3.sisu;

import java.util.TreeSet;


/**
 * A class for representing StudyModule, extends the abstract class 
 * DegreeModule
 * @author tobsu
 */
public class StudyModule extends DegreeModule implements Comparable<StudyModule> {
    
    
    private TreeSet<Course> courses = new TreeSet<>();
    
    
    /**
     * Constructor. Data is read from SISU API.
     * @param name Name of the StudyModule
     * @param id Id of the StudyModule
     * @param groupId groupId of the StudyModule
     * @param minCredits minimum credits required to complete the StudyModule
     */
    public StudyModule(String name, String id, String groupId, int minCredits) {
        super(name, id, groupId, minCredits);
        this.courses = new TreeSet<>();
    }
    

    /**
     * Copying constructor where the courses are separate from the one in
     * copiedSm.
     * @param copiedSm
     */
    public StudyModule(StudyModule copiedSm) {
        super(copiedSm.name, copiedSm.id, copiedSm.groupId,
        copiedSm.minCredits);
        this.courses = new TreeSet<>();
    }
    
    
    /**
     * Getter for StudyModule courses.
     * @return the courses of the StudyModule.
     */
    public TreeSet<Course> getCourses() {
        return courses;
    }
    
    
    /**
     * Sets a given TreeSet of courses to the StudyModule as courses
     * @param courses TreeSet of courses.
     */
    public void setCourses(TreeSet<Course> courses) {
        this.courses = courses;
    }
    
    
    /**
     * Adds a course to the StudyModule
     * @param c course to be added.
     */
    public void addCourse(Course c) {
        this.courses.add(c);
    }
    
    
    /**
     * Gets the size of the StudyModules i.e he number of Courses it contains.
     * @return the number of courses in the StudyModule.
     */
    public int getSize() {
        return this.courses.size();
    }
    
    
    /**
     * Enable comparison between StudyModules
     * @param sm StudyModule
     * @return result of comparison.
     */
    @Override
    public int compareTo(StudyModule sm) {
        return this.getName().compareTo(sm.getName());
    }
    
}
