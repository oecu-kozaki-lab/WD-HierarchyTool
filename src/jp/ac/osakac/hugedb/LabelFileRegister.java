package jp.ac.osakac.hugedb;

import java.io.File;

public class LabelFileRegister extends LiteralFileRegister {
	public LabelFileRegister(String outDir, String file) {
		super(new File(outDir, "literal"), file);
	}

}
