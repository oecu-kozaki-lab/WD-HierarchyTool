package jp.ac.osakac.wdht;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class LiteralFileRegister extends TripleFileRegister {
	public LiteralFileRegister(File outDir, String file) {
		super(outDir, file);
	}

	protected boolean outputSPOLine(String[] spo) {
		Concept s = new Concept(spo[0]);
		LiteralResource o = super.getTripleUtil().getLiteralResource(spo[2]);

		if (s.value == null || o.value == null) {
			return false;
		}

		/**
		 * spoを格納するファイル
		 */
		String sfilename = this.getTripleUtil().getHeader(s.value);
		File sout = new File(new File(this.outDir, sfilename), o.lang);
		outputLine(sout, s, o);

		return true;
	}

	private void outputLine(File out, Concept c1, LiteralResource c2) {
		PrintWriter bw = null;
		if (!out.getParentFile().exists()) {
			out.getParentFile().mkdirs();
		}

		if (this.getPWMap().containsKey(out.getAbsolutePath())) {
			bw = this.getPWMap().get(out.getAbsolutePath());
		} else {
			try {
				bw = new PrintWriter(new FileWriter(out, StandardCharsets.UTF_8));
				this.getPWMap().put(out.getAbsolutePath(), bw);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		bw.println(c1.val() + " " + c2.value + " ");
//		bw.flush();
//		bw.close();
	}
}
