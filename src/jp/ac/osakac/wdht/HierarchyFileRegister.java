package jp.ac.osakac.wdht;

import java.io.File;

public class HierarchyFileRegister extends TripleFileRegister {
	public HierarchyFileRegister(String outDir, String file) {
		super(new File(outDir, "class"), file);
	}

}
