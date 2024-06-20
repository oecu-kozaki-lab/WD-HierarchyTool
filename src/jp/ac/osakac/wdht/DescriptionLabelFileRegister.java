package jp.ac.osakac.wdht;

import java.io.File;

public class DescriptionLabelFileRegister extends LiteralFileRegister {
	public DescriptionLabelFileRegister(String outDir, String file) {
		super(new File(outDir, "description"), file);
	}

}
