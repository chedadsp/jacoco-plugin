package hudson.plugins.jacoco.group;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class PackagePrefix extends AbstractDescribableImpl<PackagePrefix> {

	private String value;
	
	public String getValue() {
		return value;
	}
	
	@DataBoundConstructor
    public PackagePrefix(String value) {
    	this.value = value;
    }
	
	@Extension
    public static class DescriptorImpl extends Descriptor<PackagePrefix> {
        public String getDisplayName() { 
        	return ""; 
        }
    }
}
