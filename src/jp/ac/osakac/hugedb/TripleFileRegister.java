package jp.ac.osakac.hugedb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class TripleFileRegister {
	private File inPath;
	protected File outDir;
	private Map<String, PrintWriter> pwMap;
	private TripleUtil util;

	public TripleFileRegister(File outDir, String filePath) {
		this.inPath = new File(filePath);
		this.pwMap = new HashMap<String, PrintWriter>();
		this.outDir = outDir;
		this.util = new TripleUtil(this.outDir);
	}

	/**
	 * 一行一データのファイルを読み込みリストに格納
	 * @param wordFile
	 * @return
	 * @throws FileNotFoundException
	 */
	public void process() throws FileNotFoundException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.inPath), StandardCharsets.UTF_8));
		String line="";
		long i=0;
		
		try {
			line = br.readLine();
			while (line != null) {
				if (!line.trim().isEmpty()) {
					processLine(line);
				}

				//途中経過の表示処理
				i++;
				if(i % 10000 == 0) {
					System.out.print(".");
				}
				if(i % 1000000 == 0) {
					System.out.println(i);
				}
				
				line = br.readLine();

			}
			this.util.registerPrefixes();

			br.close();
			for (PrintWriter pw : this.pwMap.values()) {
				pw.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		} 

		return;
	}

	private boolean processLine(String line) {
		//末尾の「.」を最初に削除しておく
		if (line.trim().endsWith(".")) {
			line = line.trim();
			line = line.substring(0, line.length()-1);
		}
		
		String[] spo = line.split(" ");

		if (spo.length < 3) {
			return false;
		}

		//目的語に空白が含まれていた時のエラー回避処理
		if (spo.length > 3) {
			spo[2] = line.substring(line.indexOf(spo[2],spo[0].length())+spo[2].length());
		}
		
		if (spo[2].trim().endsWith(".")) {
			spo[2] = spo[2].trim();
			spo[2] = spo[2].substring(0, spo[2].length()-1);
		}


		if (!outputSPOLine(spo)) {
			System.err.println("error:["+line+"]");
			return false;
		}

		return true;
	}

	protected boolean outputSPOLine(String[] spo) {
		Concept s = new Concept(spo[0]);
		Concept o = new Concept(spo[2]);

		if (s.value == null || o.value == null) {
			return false;
		}

		/**
		 * spoを格納するファイル
		 */
		String sfilename = this.util.getHeader(s.value);
		File sout = new File(new File(this.outDir, "spo"), sfilename);
		outputLine(sout, s, o);

		String ofilename = this.util.getHeader(o.value);
		File oout = new File(new File(this.outDir, "ops"), ofilename);
		outputLine(oout, o, s);

		return true;
	}

	private void outputLine(File out, Concept c1, Concept c2) {
		PrintWriter bw = null;
		if (!out.getParentFile().exists()) {
			out.getParentFile().mkdirs();
		}

		if (this.pwMap.containsKey(out.getAbsolutePath())) {
			bw = this.pwMap.get(out.getAbsolutePath());
		} else {
			try {
				bw = new PrintWriter(new FileWriter(out, StandardCharsets.UTF_8));
				this.pwMap.put(out.getAbsolutePath(), bw);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		bw.println(c1.val() + " " + c2.val() + " ");
//		bw.flush();
//		bw.close();
	}

	TripleUtil getTripleUtil() {
		return this.util;
	}

	Map<String, PrintWriter> getPWMap() {
		return this.pwMap;
	}

	protected class Concept {
		public Integer index;
		public String value;
		public String con;
		protected Concept(String concept) {
			/**
			 * <http://..../xxxx> の形で入ってくるので
			 * http://..../　をprefix，xxxxとvalueとして返す
			 */
			concept = concept.trim();
			this.con = concept;

			if (!concept.startsWith("<") || !concept.endsWith(">")) {
				return;
			}

			int pindex = concept.lastIndexOf("#");
			if (pindex < 0) {
				pindex = concept.lastIndexOf("/");
			}
			if (pindex < 0) {
				return;
			}

			String prefix = concept.substring(1, pindex+1);
			/*
			if (!prefixes.contains(prefix)) {
				prefixes.add(prefix);
			}*/

//			this.index = prefixes.indexOf(prefix) + 1;
			this.index = util.getPrefixIndex(prefix);
			this.value = concept.substring(pindex+1, concept.length()-1);
		}
		public String val() {
			if (index == null) {
				return this.con;
			}
			return index.toString() + ":" + value;
		}
	}
}
