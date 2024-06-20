package jp.ac.osakac.hugedb;

import java.io.File;

public class AltLabelFileRegister extends LiteralFileRegister {
	public AltLabelFileRegister(String outDir, String file) {
		super(new File(outDir, "altlabel"), file);
	}

}
