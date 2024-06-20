package jp.ac.osakac.hugedb;

import java.io.File;

public class InstanceFileRegister extends TripleFileRegister {
	public InstanceFileRegister(String outDir, String file) {
		super(new File(outDir, "instance"), file);
	}
}
