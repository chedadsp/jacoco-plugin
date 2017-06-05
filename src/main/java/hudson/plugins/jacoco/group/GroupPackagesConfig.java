package hudson.plugins.jacoco.group;

import java.io.Serializable;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

public class GroupPackagesConfig implements Serializable {
	
	private boolean showMismatched;
	private List<Group> groups;
	
	public boolean isShowMismatched() {
		return showMismatched;
	}
	public void setShowMismatched(boolean showMismatched) {
		this.showMismatched = showMismatched;
	}
	public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	@DataBoundConstructor
    public GroupPackagesConfig(boolean showMismatched, List<Group> groups) {
    	this.showMismatched = showMismatched;
    	this.groups = groups;
    }

}
