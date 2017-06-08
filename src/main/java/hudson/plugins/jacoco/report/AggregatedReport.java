package hudson.plugins.jacoco.report;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.plugins.jacoco.group.Group;

import java.io.IOException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Reports that have children.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AggregatedReport<PARENT extends AggregatedReport<?,PARENT,?>,
    SELF extends AggregatedReport<PARENT,SELF,CHILD>,
    CHILD extends AbstractReport<SELF,CHILD>> extends AbstractReport<PARENT,SELF> {

    private final Map<String, CHILD> children = new TreeMap<>();
    
    private Set<Group> groups; 

    public void add(CHILD child) {
        children.put(child.getName(),child);
        this.hasClassCoverage();
    }

    public Map<String,CHILD> getChildren() {
        return children;
    }

    @Override
    protected void setParent(PARENT p) {
        super.setParent(p);
        for (CHILD c : children.values())
            c.setParent((SELF)this);
    }

    public CHILD getDynamic(String token, StaplerRequest req, StaplerResponse rsp ) throws IOException {
        return getChildren().get(token);
    }
    
    @Override
    public void setFailed() {
        super.setFailed();

        if (getParent() != null)
            getParent().setFailed();
    }
    
    public boolean hasChildren() {
    	return getChildren().size() > 0;
    }

    public boolean hasChildrenLineCoverage() {
    	for (CHILD child : getChildren().values()){
    		if (child.hasLineCoverage()) {
    			return true;
    		}
    	}
        return false;
    }

    public boolean hasChildrenClassCoverage() {
    	for (CHILD child : getChildren().values()){
    		if (child.hasClassCoverage()) {
    			return true;
    		}
    	}
        return false;
    }

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = new LinkedHashSet<Group>();
		for (Group g : groups) {
			this.groups.add(g);
		}
	}
	
}
