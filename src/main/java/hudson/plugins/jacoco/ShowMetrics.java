package hudson.plugins.jacoco;

public class ShowMetrics {
	
	private boolean showInstruction;
	private boolean showBranch;
	private boolean showComplexity;
	private boolean showLine;
	private boolean showMethod;
	private boolean showClass;

	public ShowMetrics(boolean showInstruction, boolean showBranch, boolean showComplexity, 
					   boolean showLine,        boolean showMethod, boolean showClass) {
		this.showInstruction = showInstruction;
		this.showBranch = showBranch;
		this.showComplexity = showComplexity;
		this.showLine = showLine;
		this.showMethod = showMethod;
		this.showClass = showClass;
	}

	public boolean isShowInstruction() {
		return showInstruction;
	}

	public void setShowInstruction(boolean showInstruction) {
		this.showInstruction = showInstruction;
	}

	public boolean isShowBranch() {
		return showBranch;
	}

	public void setShowBranch(boolean showBranch) {
		this.showBranch = showBranch;
	}

	public boolean isShowComplexity() {
		return showComplexity;
	}

	public void setShowComplexity(boolean showComplexity) {
		this.showComplexity = showComplexity;
	}

	public boolean isShowLine() {
		return showLine;
	}

	public void setShowLine(boolean showLine) {
		this.showLine = showLine;
	}

	public boolean isShowMethod() {
		return showMethod;
	}

	public void setShowMethod(boolean showMethod) {
		this.showMethod = showMethod;
	}

	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}
	
}
