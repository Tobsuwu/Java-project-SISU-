package fi.tuni.prog3.sisu;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * Class defining a student. Has an inner class Attainment.
 * @author tobsu
 */
public class Student {

    private String studentNumber;
    private String firstName;
    private String lastName;
    private DegreeProgram degreeProgram;
    private StudyModule studyModule;
    private ArrayList<Attainment> attainments = new ArrayList<>();
    
    
    /**
     * Constructor for initializing variables.
     * @param name Name of the student.
     * @param studentNumber Student number of the student.
     */
    public Student(String name, String studentNumber) {
        String[] names = name.split(" ");
        this.firstName = names[0];
        this.lastName = names[1];
        this.studentNumber = studentNumber;
    }
    
    
    /**
     * Class defining an attainment. Has upper class Student.
     */
    public class Attainment implements Comparable<Attainment> {
        private int grade;
        private Course course;
        
        
        /**
         * Constructor for initializing variables.
         * @param grade grade of the Attainment.
         * @param course course the Attainment is for.
         */
        public Attainment(int grade, Course course) {
            this.grade = grade;
            this.course = course;
        }
        
        
        /**
         * Getter for grade of the Attainment.
         * @return grade of the Attainment.
         */
        public int getGrade() {
            return this.grade;
        }
        
        
        /**
         * Getter for Course of the Attainment.
         * @return Course the Attainment is for.
         */
        public Course getCourse() {
            return this.course;
        }
        
        
        /**
         * Setter for the grade of the Attainment.
         * @param grade grade to be set for the Attainment.
         */
        public void setGrade(int grade) {
            this.grade = grade;
        }
        
        
        /**
         * Enable comparison between Attainments. First by name, then by grades.
         * @param a Attainment
         * @return result of comparison.
         */
        @Override
        public int compareTo(Attainment a) {
            
            int c = this.getCourse().getName().compareTo(
                a.getCourse().getName());
            if (c == 0) {return Integer.compare(this.getGrade(), a.getGrade());}

            return c;
        }
    }

    
    /**
     * Getter for the student number of the Student.
     * @return Student number of the Student.
     */
    public String getStudentNumber() {
        return this.studentNumber;
    }

    
    /**
     * Getter for first name of the Student.
     * @return First name of the Student.
     */
    public String getFirstName() {
        return this.firstName;
    }

    
    /**
     * Getter for last name of the Student.
     * @return Last name of the Student.
     */
    public String getLastName() {
        return this.lastName;
    }
    
    
    /**
     * Getter for the DegreeProgram of the Student.
     * @return DegreeProgram of the Student.
     */
    public DegreeProgram getDegreeProgram() {
        return this.degreeProgram;
    }
    
    
    /**
     * Getter for the StudyModule of the Student.
     * @return StudyModule of the Student.
     */
    public StudyModule getStudyModule() {
        return this.studyModule;
    }
    
    
    /**
     * Setter for the DegreeProgram of the Student.
     * @param degreeProgram DegreeProgram we want the Student to have.
     */
    public void setDegreeProgram(DegreeProgram degreeProgram) {
        this.degreeProgram = degreeProgram;
    }
    
    
    /**
     * Setter for the StudyModule of the Student.
     * @param studyModule StudyModule we want the Student to have.
     */
    public void setStudyModule(StudyModule studyModule) {
        this.studyModule = studyModule;
    }
    
    
    /**
     * Getter for the Attainments the Student has.
     * @return Attainments of the Student.
     */
    public ArrayList<Attainment> getAttainments() {
        return this.attainments;
    }
    
    
    /**
     * Add Attainment to the Student. 
     * @param a Attainment to be added.
     */
    public void addAttainment(Attainment a) {
        attainments.add(a);
    }
    
    
    /**
     * Getter for a specific Attainment of Student.
     * @param id id of the Attainment to be searched.
     * @return a specific Attainment.
     */
    public Attainment getAttainment(String id) {
        return attainments.stream().filter(a -> 
            id.equals(a.getCourse().getId())).findFirst().orElse(null);
    }
    
    
    /**
     * Getter for total credits of the Student from the Attainments.
     * @return Total credits of the Student.
     */
    public int getTotalCredits() {
        return attainments.stream().filter((a) -> 
            a.getGrade() > 0).mapToInt((a) -> a.course.getMinCredits()).sum();
    }

    /**
     * This method creates a json object from the given Student object. 
     * @return created json object.
     */
    public JsonObject createStudentJson() {

        JsonObject jsonS = new JsonObject();
        jsonS.addProperty("studentNumber", this.studentNumber);
        jsonS.addProperty("name", this.firstName + " " + this.lastName);

        JsonArray attainments = new JsonArray();

        if (this.attainments.size() != 0) {

            for (Attainment at : this.attainments) {
                JsonObject attainment = new JsonObject();
                attainment.addProperty("grade", at.getGrade());
                attainment.addProperty("groupId", at.getCourse().getGroupId());
                attainment.addProperty("courseName", at.getCourse().getName());
                attainment.addProperty("id", at.getCourse().getId());
                attainment.addProperty("minCredits", 
                    at.getCourse().getMinCredits());
                attainment.addProperty("outcome", at.getCourse().getOutcomes());

                attainments.add(attainment);
            }
        }

        jsonS.add("attainments", attainments);


        JsonObject dp = new JsonObject();

        dp.addProperty("degreeProgramName", this.getDegreeProgram().getName());
        dp.addProperty("id", this.getDegreeProgram().getId());
        dp.addProperty("groupId", this.getDegreeProgram().getGroupId());
        dp.addProperty("minCredits", this.getDegreeProgram().getMinCredits());

        JsonArray modules = new JsonArray();

        for (StudyModule sm : this.degreeProgram.getModules()) {
            JsonObject module = new JsonObject();

            module.addProperty("moduleName", sm.getName());
            module.addProperty("groupId", sm.getGroupId());
            module.addProperty("id", sm.getId());
            module.addProperty("minCredits", sm.getMinCredits());
            
            JsonArray courses = new JsonArray();
            for (Course course : sm.getCourses()) {
                JsonObject c = new JsonObject();
                c.addProperty("courseName", course.getName());
                c.addProperty("id", course.getId());
                c.addProperty("groupId", course.getGroupId());
                c.addProperty("minCredits", course.getMinCredits());
                c.addProperty("outcome", course.getOutcomes());
                
                courses.add(c);
            }

            module.add("courses", courses);
            modules.add(module);
        }

        dp.add("modules", modules);

        jsonS.add("studies", dp);

        return jsonS;
    }
}
