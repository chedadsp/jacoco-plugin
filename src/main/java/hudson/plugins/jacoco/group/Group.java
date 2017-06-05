package hudson.plugins.jacoco.group;

import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class Group extends AbstractDescribableImpl<Group> {
	
	private String name;
	private List<PackagePrefix> prefixes;
    
	public String getName() {
		return name;
	}
	
	public List<PackagePrefix> getPrefixes() {
		return prefixes;
	}

	@DataBoundConstructor
    public Group(String name, List<PackagePrefix> prefixes) {
    	this.name = name;
    	this.prefixes = prefixes;
    }
	
	@Extension
    public static class DescriptorImpl extends Descriptor<Group> {
        public String getDisplayName() { 
        	return ""; 
        }
    }
	

}