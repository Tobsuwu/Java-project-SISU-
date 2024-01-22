package fi.tuni.prog3.sisu;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


/**
 * A class for representing Degree Program. Extends the abstract class 
 * DegreeModule.
 * @author tobsu
 */
public class DegreeProgram extends DegreeModule implements Comparable<DegreeProgram> {
    
    private TreeSet<StudyModule> modules;
    
    
    /**
     * Constructor. Data is read from SISU API.
     * @param name Name of the DegreeProgram.
     * @param id Id of the DegreeProgram.
     * @param groupId groupingId of the DegreeProgram.
     * @param minCredits minimum credits required to complete the DegreeProgram.
     */
    public DegreeProgram (String name, String id, String groupId, int minCredits) {
        super(name, id, groupId, minCredits);
        this.modules = new TreeSet<>();
    }
    
    
    /**
     * Copying constructor where the modules is separate from the one in
     * copiedDp.
     * @param copiedDp 
     */
    public DegreeProgram (DegreeProgram copiedDp) {
        super(copiedDp.name, copiedDp.id, copiedDp.groupId,
                copiedDp.minCredits);
        this.modules = new TreeSet<>();
    }
    
    
    /**
     * Getter for DegreeProgram StudyModule.
     * @return the StudyModules of the DegreeProgram.
     */
    public TreeSet<StudyModule> getModules() {
        return this.modules;
    }
    
    public void addModules(TreeSet<StudyModule> modules) {
        for (var module : modules) {
            this.modules.add(module);
        }
    }
    
    public void removeModules(List<String> ids) {
        this.modules = this.modules.stream()
                .filter((m) -> !ids.contains(m.getGroupId()))
                .collect(Collectors.toCollection(TreeSet::new));
        
    }
    
    
    /**
     * Adds a given StudyModule to the TreeSet of StudyModules.
     * @param sm StudyModule to be added.
     */
    public void addModules(StudyModule sm) {
        this.modules.add(sm);
    }
    
    public StudyModule getModule(String name) {
        for (StudyModule sm : modules) {
            if (sm.getName().equals(name)) {
                return sm;
            }
        }
        return null;
    }
    
    
    /**
     * Enable comparison between DegreePrograms.
     * @param dp DegreeProgram
     * @return result of comparison.
     */
    @Override
    public int compareTo(DegreeProgram dp) {
        return this.getName().compareTo(dp.getName());
        
    }
}
