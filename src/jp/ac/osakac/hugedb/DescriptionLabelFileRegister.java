package jp.ac.osakac.hugedb;

import java.io.File;

public class DescriptionLabelFileRegister extends LiteralFileRegister {
	public DescriptionLabelFileRegister(String outDir, String file) {
		super(new File(outDir, "description"), file);
	}

}
