package fi.tuni.prog3.sisu;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class is responsible for reading the study related information from the
 * Sisu API. 
 * @author tobsu and jaakko
 */
public class ReadAPI implements iAPI{

    private String moduleURLbegin = 
        "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=";
    private String moduleURLend = 
        "&universityId=tuni-university-root-id";
    private String courseURLbegin = 
        "https://sis-tuni.funidata.fi/kori/api"+
        "/course-units/by-group-id?groupId=";
    private String courseURLend  = 
        "&universityId=tuni-university-root-id";

    private DegreeProgram dp;
    private StudyModule sm;
    
    
    /**
     * This method implements the iAPI interface and returns the JSON data from 
     * a given URL as JsonObject. Used to get information about the 
     * DegreePrograms, StudyModules and Courses from the Sisu API.
     * @param urlString The URL of the page we wish to get JSON data from.
     * @return JsonObject json.
     */
    @Override
    public JsonObject getJsonObjectFromApi(String urlString) {

        try {
            var urli = new URL(urlString);
            String data = "";
            
            data = new String(urli.openStream().readAllBytes());
            if (data.charAt(0) == '[' && data.charAt(data.length()-1) == ']') {
                data = data.substring(1, data.length()-1);

            }
            JsonObject json = (JsonObject) JsonParser.parseString(data);
            return json;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    
    /**
     * This method goes through all of the DegreePrograms in the Sisu API, adds
     * them to a TreeSet and returns them to the GUI-class Sisu.java.
     * @return degreePrograms
     * @throws MalformedURLException
     */
    public TreeSet<DegreeProgram> parseDegreePrograms() 
    throws MalformedURLException {
        
        System.out.print("Parsing DegreePrograms... ");

        TreeSet<DegreeProgram> degreePrograms = new TreeSet<>();
        
        JsonObject courseData = new JsonObject();

        courseData = getJsonObjectFromApi("https://sis-tuni."+
        "funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-"+
        "2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme"+
        "&limit=1000");

        var results = courseData.getAsJsonArray("searchResults");
        for (var result : results.getAsJsonArray()) {
            var json = result.getAsJsonObject();
            
            String name = String.valueOf(json.get("name")).replaceAll("\"", "");
            String id = String.valueOf(json.get("id")).replaceAll("\"", "");
            String groupId = String.valueOf(
                json.get("groupId")).replaceAll("\"", "");
            Integer credits = Integer.parseInt(
                                String.valueOf(json.getAsJsonObject
                                ("credits").get("min")).replaceAll("\"", ""));

            degreePrograms.add(new DegreeProgram(name, id, groupId, credits));
            

        }
        System.out.println("Done!");
        return degreePrograms;
    }
    
    
    /**
     * This function takes in the DegreeProgram given by the user and adds 
     * StudyModules and Courses to it recursively from the Sisu API, before 
     * returning the populated DegreeProgram.
     * @param dp DegreeProgram that the user chooses from the GUI
     * @return DegreeProgram populated with StudyModules populated with Courses.
     * @throws MalformedURLException 
     */
    public DegreeProgram parseModules(DegreeProgram dp) 
    throws MalformedURLException {
        
        System.out.print("Fetching StudyModules and Courses from Sisu API... ");

        this.dp = dp;
        String dpGroupId = dp.getGroupId();
        JsonObject data = getJsonObjectFromApi(moduleURLbegin + dpGroupId + 
                                                moduleURLend);
        
        // Sisu API consists of rule or rules, which open submodules. We go 
        // through these recursively until we get to Courses, which do not 
        // contain rule.
        if (data.has("rule")) {
            JsonObject rule = data.getAsJsonObject("rule");
            recursionOverRulesDP(rule); // Recursion start
        }
        
        StudyModule lastModule = this.sm;
        if (lastModule.getSize() > 0) {
            this.dp.addModules(lastModule);
        }
        
        System.out.println("Done!");
        return this.dp;
        
    }
    
    
    /**
     * This is the main recursion function, which calls other sub-recursive 
     * functions depending of the type of the given rule. 
     * @param rule The rule JsonObject
     */
    public void recursionOverRulesDP(JsonObject rule) {
        
        // Check what the type is.
        if (rule.get("type").getAsString().equals("CompositeRule")) {
            recursionCompositeRule(rule);
        } 
        
        else if (rule.get("type").getAsString().equals("ModuleRule")) {
            recursionModuleRule(rule);
        }
        
        else if (rule.get("type").getAsString().equals("CreditsRule")) {
            recursionCreditsRule(rule);
        }
        
        else if (rule.get("type").getAsString().equals("CourseUnitRule")) {
            recursionCourseUnitRule(rule);
        }
            
    }
    
    
    /**
     * This method does the recursion over type CompositeRule. CompositeRule 
     * contains rules as subrules. 
     * @param rule Given rule as JsonObject for the recursion
     */ 
    public void recursionCompositeRule (JsonObject rule) {
        JsonArray rules = rule.getAsJsonArray("rules");
            for (int i = 0; i < rules.size() ; ++i) {
                recursionOverRulesDP(rules.get(i).getAsJsonObject());
            }
    }
    
    
    /**
     * This method does the recursion over type ModuleRule. These are the 
     * StudyModules added to the DegreeProgram. A messy if else structure is 
     * implemented due to inconsistencies in the Sisu API.
     * @param rule Given rule as JsonObject for the recursion
     */
    public void recursionModuleRule (JsonObject rule) {
        
        // Add the StudyModule into DegreeProgram, if it contains Courses.
        if (this.sm != null) {
            if (this.sm.getSize() > 0) {
                StudyModule sm = this.sm;
                this.dp.addModules(sm);
            }
        }
        
        String mgi = rule.get("moduleGroupId").getAsString();
        
        JsonObject moduleData = getJsonObjectFromApi(this.moduleURLbegin + 
            mgi + this.moduleURLend);
        // Recursively call the function again for the ModuleRule
        JsonObject moduleRule = moduleData.getAsJsonObject("rule");
        
        String name = "";
        JsonObject jname = moduleData.getAsJsonObject("name");
        if (jname.has("fi")) {
            name = jname.get("fi").getAsString();
        }
        else {
            name = jname.get("en").getAsString();    
        }
        
        String code = "";
        if (moduleData.get("code").isJsonNull()) {
            code = "";
        }
        else {
            code = moduleData.get("code").getAsString();
        }
        
        String groupId = moduleData.get("groupId").getAsString();
        int minCredits = 0;
        
        if (moduleData.has("targetCredits")) {
            JsonObject jminCredits = 
                moduleData.getAsJsonObject("targetCredits");
      
            if (jminCredits.get("min").isJsonNull()) {
                minCredits = 0;
            }
            else {
                minCredits = jminCredits.get("min").getAsInt();
            }
        
        }
        
        this.sm = new StudyModule(name, code, groupId, minCredits);
        recursionOverRulesDP(moduleRule);
    }
    
    
    /**
     * This method does the recursion over type CreditsRule. We continue past it
     * since it does not contain interesting data.
     * @param rule Given rule as JsonObject for the recursion.
     */
    public void recursionCreditsRule (JsonObject rule) {
        JsonObject newRule = rule.getAsJsonObject("rule");
        recursionOverRulesDP(newRule);
    }
    
    
    /**
     * This method does the recursion over type CourseUnitRule, creates Courses 
     * from the data and adds them to the current StudyModule. Messy if else 
     * structure implemented also here due to the features of the Sisu API. 
     * Recursion ends here.
     * @param rule Given rule as JsonObject for the recursion
     */
    public void recursionCourseUnitRule(JsonObject rule) {

        String cid = rule.get("courseUnitGroupId").getAsString();
        
        JsonObject courseData = getJsonObjectFromApi(this.courseURLbegin + cid + 
                                                        this.courseURLend);
        JsonObject jname = courseData.getAsJsonObject("name");
        String name;
        if (jname.has("fi")) {
            name = jname.get("fi").getAsString();
        }
        else {
            name = jname.get("en").getAsString();
        }
            
        String code;
        if (courseData.get("code").isJsonNull()) {
            code = "";
        }
        else {
            code = courseData.get("code").getAsString();
        }
                
        String groupId = courseData.get("groupId").getAsString();
        JsonObject jminCredits = courseData.getAsJsonObject("credits");
        int minCredits = jminCredits.get("min").getAsInt();
        
        String outcomes;
        if (courseData.get("outcomes").isJsonNull()) {
            outcomes = "";
        }
        else {
            JsonObject joutcomes = courseData.getAsJsonObject("outcomes");
                if (joutcomes.has("fi")) {
                    outcomes = joutcomes.get("fi").getAsString();
                    outcomes = outcomes.replaceAll("\\<.*?\\>", "");
                    outcomes = outcomes.replaceAll(" +", " ");
                    outcomes = outcomes.replaceAll("&#39;", "'");
                    
                }
                else {
                    outcomes = joutcomes.get("en").getAsString();
                    outcomes = outcomes.replaceAll("\\<.*?\\>", "");
                    outcomes = outcomes.replaceAll(" +", " ");
                    outcomes = outcomes.replaceAll("&#39;", "'");
                   
                }
        }
            
        Course course = new Course(name, code, groupId, minCredits, outcomes);
        this.sm.addCourse(course);
    }
}
