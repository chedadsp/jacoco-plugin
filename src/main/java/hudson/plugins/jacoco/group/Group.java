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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Group)) {
			return false;
		}
		Group other = (Group) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
