package fi.tuni.prog3.sisu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A class implementing iReadAndWriteToFile. Reads and writes json-files where 
 * users' data are stored.
 * @author jaakko
 */

public class ReadAndWriteStudentData implements iReadAndWriteToFile{

    /**
     * Function where saved json-file is read and converted to a Student object.
     * @return List of saved students.
     * @throws IOException
     */
    @Override
    public ArrayList<Student> readFromFile() throws IOException {

        ArrayList<Student> students = new ArrayList<>();
        File dir = new File("studentData");
        File[] dirList = dir.listFiles();
        if (dirList != null) {

            for(File file : dirList) {

                var rdr = new BufferedReader(new FileReader(file));
                String jsonAsString = rdr.readLine();
                JsonObject json = 
                    (JsonObject) JsonParser.parseString(jsonAsString);
                String studentName = json.get("name").getAsString();
                String studentNumber = json.get("studentNumber").getAsString();

                JsonObject studies = json.getAsJsonObject("studies");
                String dpName = studies.get("degreeProgramName").getAsString();
                String dpId = studies.get("id").getAsString();
                String dpGroupId = studies.get("groupId").getAsString();
                Integer dpMinCredits = studies.get("minCredits").getAsInt();

                DegreeProgram dp = new DegreeProgram(dpName, dpId, 
                                                    dpGroupId, dpMinCredits);

                for (JsonElement o : studies.getAsJsonArray("modules")) {
                    JsonObject obj = o.getAsJsonObject();
                    
                        StudyModule module = 
                            new StudyModule(obj.get("moduleName").getAsString(),
                            obj.get("id").getAsString(), 
                            obj.get("groupId").getAsString(),
                            obj.get("minCredits").getAsInt());

                        for (JsonElement c : obj.getAsJsonArray("courses")){
                            JsonObject course = c.getAsJsonObject();
                            module.addCourse(
                              new Course(course.get("courseName").getAsString(),
                                          course.get("id").getAsString(),
                                          course.get("groupId").getAsString(),
                                          course.get("minCredits").getAsInt(),
                                          course.get("outcome").getAsString()));
                        }   
                    dp.addModules(module);
                }

                Student s = new Student(studentName, studentNumber);
                s.setDegreeProgram(dp);

                try {
                    JsonArray attainments = json.get("attainments")
                        .getAsJsonArray();
                        
                    for (JsonElement attainment : attainments) {
                        JsonObject att = attainment.getAsJsonObject();

                        s.addAttainment(
                            s.new Attainment(att.get("grade").getAsInt(),
                            new Course(att.get("courseName").getAsString(),
                                        att.get("id").getAsString(),
                                        att.get("groupId").getAsString(),
                                        att.get("minCredits").getAsInt(),
                                        att.get("outcome").getAsString())));
                    }
                }
                catch (NullPointerException e) {
                    continue;
                }
                students.add(s);
                rdr.close();
            }
        }

        return students;
    }

    /**
     * Writes a student in the form of a json-file to a file named after it's 
     * student number.
     * @param student Student we wish to write to file.
     * @return True if successful, false otherwise.
     */
    @Override
    public boolean writeToFile(Student student) 
        throws IOException {

        JsonObject studentJson = student.createStudentJson();
        try {
            FileWriter file = new FileWriter("studentData/" +
                                        student.getStudentNumber() + ".json");
            file.write(studentJson.toString());
            file.close();
        }
        catch (IOException e) {
            return false;
        }
        return true;
    }
}
