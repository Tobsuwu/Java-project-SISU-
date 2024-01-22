    
package fi.tuni.prog3.sisu;

/**
 * An abstract class for DegreeProgram, StudyModule, and Course.
 * @author antti
 */
public abstract class DegreeModule {
    public String name;
    public String id;
    public String groupId;
    public int minCredits;
    
    /**
     * Common constructor for the subclasses
     * @param name Name of the module
     * @param id Id of the module
     * @param groupId Id of the group the module belongs to
     * @param minCredits Minimum number of credits in the module
     */
    public DegreeModule(String name, String id, String groupId, 
            int minCredits) {
        this.name = name;
        this.id = id;
        this.groupId = groupId;
        this.minCredits = minCredits;
    }
    
    /**
     * Returns module name.
     * @return name of the module.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns module id.
     * @return id of the module.
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * Returns the group id of the module.
     * @return group id of the module.
     */
    public String getGroupId() {
        return this.groupId;
    }
    
    /**
     * Returns the minimum credits of the module.
     * @return minimum credits of the module.
     */
    public int getMinCredits() {
        return this.minCredits;
    }
}
