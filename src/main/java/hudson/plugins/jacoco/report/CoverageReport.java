package hudson.plugins.jacoco.report;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import hudson.model.Run;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.tools.ExecFileLoader;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebMethod;
import org.objectweb.asm.Type;

import hudson.plugins.jacoco.ExecutionFileLoader;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.JacocoHealthReportThresholds;
import hudson.plugins.jacoco.group.Group;
import hudson.plugins.jacoco.group.PackagePrefix;
import hudson.plugins.jacoco.group.utils.Constants;
import hudson.plugins.jacoco.model.Coverage;
import hudson.util.HttpResponses;

/**
 * Root object of the coverage report.
 * 
 * @author Kohsuke Kawaguchi
 * @author Ognjen Bubalo
 */
public final class CoverageReport extends AggregatedReport<CoverageReport/*dummy*/,CoverageReport,PackageReport> {
	private final JacocoBuildAction action;

	private CoverageReport(JacocoBuildAction action) {
		this.action = action;
		setName("Jacoco");
		
		setGroupConfig(action);
		
	}
	
	private JacocoBuildAction getAction() {
		return action;
	}
	
//	private String instructionColor;
//	private String classColor;
//	private String branchColor;
//	private String complexityColor;
//	private String lineColor;
//	private String methodColor;
	public JacocoHealthReportThresholds healthReports;

	/**
	 * Loads the exec files using JaCoCo API. Creates the reporting objects and the report tree.
	 * 
	 * @param action
	 * @param executionFileLoader
	 */
	public CoverageReport(JacocoBuildAction action, ExecutionFileLoader executionFileLoader ) {
		this(action);
		try {

			action.getLogger().println("[JaCoCo plugin] Loading packages..");
			
			Map<String, Map<String, PackageReport>> groupPackageReports = new HashMap<>();

			if (executionFileLoader.getBundleCoverage() !=null ) {
				setAllCovTypes(this, executionFileLoader.getBundleCoverage(), false);
				
				ArrayList<IPackageCoverage> packageList = new ArrayList<>(executionFileLoader.getBundleCoverage().getPackages());
				for (IPackageCoverage packageCov: packageList) {
					boolean match = false;
					
					PackageReport packageReport = new PackageReport();
					packageReport.setName(packageCov.getName());
					
					if (this.getGroups() != null && this.getGroups().size() > 0 ) {
						for (Group group : action.getGroups()) {
							if (groupPackageReports.get(group.getName()) == null) {
								groupPackageReports.put(group.getName(), new HashMap<String, PackageReport>());
							}
							if (!group.getName().equals(Constants.MISMATCHED_PACKAGES_GROUP_NAME) && group.getPrefixes() != null) {
								for (PackagePrefix prefix : group.getPrefixes()) {
									if (prefix.getValue() != null && !prefix.getValue().isEmpty() && packageCov.getName().replaceAll("/", ".").startsWith(prefix.getValue())) {
										Map<String, PackageReport> packagePrefixReport = groupPackageReports.get(group.getName());
										if (packagePrefixReport.get(prefix.getValue().replaceAll("\\.", "/")) == null) {
											packageReport.setGroupName(group.getName());
											this.setCoverage(packageReport, packageCov, false);
											packageReport.setName(prefix.getValue());
											packagePrefixReport.put(prefix.getValue().replaceAll("\\.", "/"), packageReport);
										} else {
											packageReport =  packagePrefixReport.get(prefix.getValue().replaceAll("\\.", "/"));
											this.setCoverage(packageReport, packageCov, true);
										}
										match = true;
										packageReport.setGroupName(group.getName());
										break;
									}
								}
							}
						}
					}
					
					if (this.getGroups() == null || this.getGroups().size() == 0) {
						packageReport.setParent(this);
						this.setCoverage(packageReport, packageCov, false);
					} else {
						//Add package in mismatched group
						if (!match && action.showMismatchedPackages()) {
							packageReport.setParent(this);
							this.setCoverage(packageReport, packageCov, false);
							packageReport.setGroupName(Constants.MISMATCHED_PACKAGES_GROUP_NAME);
						}
					}

					ArrayList<IClassCoverage> classList = new ArrayList<>(packageCov.getClasses());
					for (IClassCoverage classCov: classList) {
						ClassReport classReport = new ClassReport();
						classReport.setName(classCov.getName());
						classReport.setParent(packageReport);
                        classReport.setSrcFileInfo(classCov, executionFileLoader.getSrcDir() + "/" + packageCov.getName() + "/" + classCov.getSourceFileName());

						packageReport.setCoverage(classReport, classCov, false);

						ArrayList<IMethodCoverage> methodList = new ArrayList<>(classCov.getMethods());
						for (IMethodCoverage methodCov: methodList) {
							MethodReport methodReport = new MethodReport();
							methodReport.setName(getMethodName(classCov, methodCov));
							methodReport.setParent(classReport);
							classReport.setCoverage(methodReport, methodCov, false);
							methodReport.setSrcFileInfo(methodCov);

							classReport.add(methodReport);
						}

						packageReport.add(classReport);
					}

					if (!match) {
						this.add(packageReport);
					} 
				}
				
				if (this.getGroups() != null && this.getGroups().size() > 0) {
					for (Map<String, PackageReport> mpr : groupPackageReports.values()) {
						for (PackageReport pr : mpr.values()) {
							pr.setParent(this);
							this.add(pr);
						}
					}
				}
			}
			action.getLogger().println("[JaCoCo plugin] Done.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * From Jacoco: Checks if a class name is anonymous or not.
     * 
     * @param vmname
     * @return
     */
    private boolean isAnonymous(final String vmname) {
        final int dollarPosition = vmname.lastIndexOf('$');
        if (dollarPosition == -1) {
            return false;
        }
        final int internalPosition = dollarPosition + 1;
        if (internalPosition == vmname.length()) {
            // shouldn't happen for classes compiled from Java source
            return false;
        }
        // assume non-identifier start character for anonymous classes
        final char start = vmname.charAt(internalPosition);
        return !Character.isJavaIdentifierStart(start);
    }

    /**
     * Returns a method name for the method, including possible parameter names.
     * 
     * @param classCov
     *            Coverage Information about the Class
     * @param methodCov
     *            Coverage Information about the Method
     * @return method name
     */
    private String getMethodName(IClassCoverage classCov, IMethodCoverage methodCov) {
        if ("<clinit>".equals(methodCov.getName()))
            return "static {...}";

        StringBuilder sb = new StringBuilder();
        if ("<init>".equals(methodCov.getName())) {
            if (isAnonymous(classCov.getName())) {
                return "{...}";
            }
            
            int pos = classCov.getName().lastIndexOf('/');
            String name = pos == -1 ? classCov.getName() : classCov.getName().substring(pos + 1);
            sb.append(name.replace('$', '.'));
        } else {
            sb.append(methodCov.getName());
        }
        
        sb.append('(');
        final Type[] arguments = Type.getArgumentTypes(methodCov.getDesc());
        boolean comma = false;
        for(final Type arg : arguments) {
            if(comma) {
                sb.append(", ");
            } else {
                comma = true;
            }
            
            String name = arg.getClassName();
            int pos = name.lastIndexOf('.');
            String shortname = pos == -1 ? name : name.substring(pos + 1);
            sb.append(shortname.replace('$', '.'));
        }
        sb.append(')');

        return sb.toString();
    }

    static final NumberFormat dataFormat = new DecimalFormat("000.00", new DecimalFormatSymbols(Locale.US));
    static final NumberFormat percentFormat = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
	
	@Override
	protected void printRatioCell(boolean failed, Coverage ratio, StringBuilder buf) {
		if (ratio != null && ratio.isInitialized()) {
			String bgColor = "#FFFFFF";
			
			if (JacocoHealthReportThresholds.RESULT.BETWEENMINMAX == healthReports.getResultByTypeAndRatio(ratio)) {
				bgColor = "#FF8000";
			} else if (JacocoHealthReportThresholds.RESULT.BELOWMINIMUM == healthReports.getResultByTypeAndRatio(ratio)) {
				bgColor = "#FF0000";
			}
			buf.append("<td bgcolor='").append(bgColor).append("'");
			buf.append(" data='").append(dataFormat.format(ratio.getPercentageFloat()));
			buf.append("'>\n");
			printRatioTable(ratio, buf);
			buf.append("</td>\n");
		}
	}
	
	@Override
	protected void printRatioTable(Coverage ratio, StringBuilder buf){
		buf.append("<table class='percentgraph' cellpadding='0' cellspacing='0'><tr class='percentgraph'>")
		.append("<td style='width:40px' class='data'>").append(ratio.getPercentage()).append("%</td>")
		.append("<td class='percentgraph'>")
		.append("<div class='percentgraph' style='width:100px'>")
		.append("<div class='redbar' style='width:")
		.append(100 - ratio.getPercentage()).append("px'>")
		.append("</div></div></td></tr><tr><td colspan='2'>")
		.append("<span class='text'><b>M:</b> ").append(ratio.getMissed())
		.append(" <b>C:</b> ").append(ratio.getCovered()).append("</span></td></tr></table>\n");
	}

	@Override
	public CoverageReport getPreviousResult() {
		JacocoBuildAction prev = action.getPreviousResult();
		if(prev!=null) {
			return prev.getResult();
		}
		
		return null;
	}

	@Override
	public Run<?,?> getBuild() {
		return action.getOwner();
	}

    /**
     * Serves a single jacoco.exec file that merges all that have been recorded.
     */
    @WebMethod(name="jacoco.exec")
    public HttpResponse doJacocoExec() throws IOException {
        final List<File> files = action.getJacocoReport().getExecFiles();

        switch (files.size()) {
        case 0:
            return HttpResponses.error(404, "No jacoco.exec file recorded");
        case 1:
            return HttpResponses.staticResource(files.get(0));
        default:
            // TODO: perhaps we want to cache the merged result?
            return new HttpResponse() {
                public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
                    ExecFileLoader loader = new ExecFileLoader();
                    for (File exec : files) {
                        loader.load(exec);
                    }
                    rsp.setContentType("application/octet-stream");
                    final ExecutionDataWriter dataWriter = new ExecutionDataWriter(rsp.getOutputStream());
                    loader.getSessionInfoStore().accept(dataWriter);
                    loader.getExecutionDataStore().accept(dataWriter);
                }
            };
        }
    }


	public void setThresholds(JacocoHealthReportThresholds healthReports) {
		this.healthReports = healthReports;
		/*if (healthReports.getMaxBranch() < branch.getPercentage()) {
			branchColor = "#000000";
		} else if (healthReports.getMinBranch() < branch.getPercentage()) {
			branchColor = "#FF8000";
		} else {
			branchColor = "#FF0000";
		}
		*/
	}
	
	private void setGroupConfig(JacocoBuildAction action) {
		if (action.getGroups().size() > 0) {
			this.setGroups(action.getGroups());
			if (action.showMismatchedPackages()) {
				this.getGroups().add(new Group(Constants.MISMATCHED_PACKAGES_GROUP_NAME, new LinkedList<PackagePrefix>()));
			}
		}
	}


}
