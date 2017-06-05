package hudson.plugins.jacoco.group;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class PackagePrefixTest {
	
	private PackagePrefix pPrefix;

	@Before
    public void setUp() {
		pPrefix = new PackagePrefix("test.package");
	}
	
	@Test
	public void testConstructor() {
		assertNotNull(pPrefix);
		assertEquals("test.package", pPrefix.getValue());
	}
}
