package jp.ac.osakac.hugedb;

import java.io.File;

public class HierarchyFileRegister extends TripleFileRegister {
	public HierarchyFileRegister(String outDir, String file) {
		super(new File(outDir, "class"), file);
	}

}
