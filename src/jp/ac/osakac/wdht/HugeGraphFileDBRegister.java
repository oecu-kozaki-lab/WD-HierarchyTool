package jp.ac.osakac.wdht;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import jp.ac.osakac.lgdb.AltLabelRegister;
//import jp.ac.osakac.lgdb.DescriptionLabelRegister;
//import jp.ac.osakac.lgdb.HierarchyRegister;
//import jp.ac.osakac.lgdb.InstanceRegister;
//import jp.ac.osakac.lgdb.LabelRegister;
//import jp.ac.osakac.lgdb.TripleRegister;

/**
 * データファイル格納I/Fクラス
 * @author mouse
 *
 */
public class HugeGraphFileDBRegister {
//	private String server;
//	private Integer port;
//	private String dbName;
	private String baseDir;
//	private String hierarchy = "file";
	private List<String[]> data;


	/**
	 * データ格納
	 * @param iniFile
	 * @throws FileNotFoundException
	 */
	public HugeGraphFileDBRegister(String iniFile) throws FileNotFoundException {
		this.data = new ArrayList<String[]>();

		if (!this.init(iniFile)) {
			return;
		}
	}

	private boolean init(String iniFile) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(iniFile), StandardCharsets.UTF_8));
		boolean ret = true;
		String line;

		try {
			line = br.readLine();

			while (line != null) {
				processLine(line);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
/* mongDBの設定が無くても処理できるようにコメントアウ
		if (this.server == null) {
			System.err.println("server not defined.");
			ret = false;
		}
		if (this.port == null) {
			System.err.println("port not defined.");
			ret = false;
		}
		if (this.dbName == null) {
			System.err.println("dbName not defined.");
			ret = false;
		}*/
		if (this.baseDir == null) {
			this.baseDir = "";
		}
		if (this.baseDir.startsWith("\"") && this.baseDir.endsWith("\"")) {
			this.baseDir = this.baseDir.substring(1, this.baseDir.length()-1);
		}
		if (this.baseDir.trim().isEmpty()) {
			this.baseDir = Paths.get("").toAbsolutePath().toString();
		}

System.out.println("basedir:["+this.baseDir+"]");
		return ret;
	}

	public void process() {
		for (String[] colls: this.data) {
			String coll = colls[0];
			String path = colls[1];
			if (path.startsWith("\"") && path.endsWith("\"")) {
				path = path.substring(1, path.length()-1);
			}
			TripleFileRegister fregister = null;
//			TripleRegister register = null;
			try {
				if (coll.equals("class")) {
					System.out.println("Indexing class hierarchies : "+path);
//					if (this.hierarchy.equals("file")) {
						fregister = new HierarchyFileRegister(this.baseDir, path);
//					} else {
//						register = new HierarchyRegister(this.dbName, path);
//					}
				}
				if (coll.equals("instance")) {
					System.out.println("Indexing instances : "+path);
//					if (this.hierarchy.equals("file")) {
						fregister = new InstanceFileRegister(this.baseDir, path);
//					} else {
//						register = new InstanceRegister(this.dbName, path);
//					}
				}

				if (coll.equals("literal") || coll.equals("label")) {
					System.out.println("Indexing literal data (label) : "+path);
//					if (this.hierarchy.equals("file")) {
//						System.out.println(" !リテラル情報をファイルに保存する場合，逆引き検索ができません");
//						System.out.println(" !逆引きを行う場合，DBにも同じデータを格納してください");
						fregister = new LabelFileRegister(this.baseDir, path);
//					} else {
//						register = new LabelRegister(this.dbName, path);
//					}
				}
				if (coll.equals("altlabel")) {
					System.out.println("Indexing literal data (altLabel) : "+path);
//					if (this.hierarchy.equals("file")) {
//						System.out.println(" !リテラル情報をファイルに保存する場合，逆引き検索ができません");
//						System.out.println(" !逆引きを行う場合，DBにも同じデータを格納してください");
						fregister = new AltLabelFileRegister(this.baseDir, path);
//					} else {
//						register = new AltLabelRegister(this.dbName, path);
//					}
				}
				if (coll.equals("description")) {
					System.out.println("Indexing literal data (description) : "+path);
//					if (this.hierarchy.equals("file")) {
//						System.out.println(" !リテラル情報をファイルに保存する場合，逆引き検索ができません");
//						System.out.println(" !逆引きを行う場合，DBにも同じデータを格納してください");
						fregister = new DescriptionLabelFileRegister(this.baseDir, path);
//					} else {
//						register = new DescriptionLabelRegister(this.dbName, path);
//					}
				}

//				if (register != null) {
//					System.out.println("処理開始："+getCurrentTime());
//					register.process();
//					System.out.println("処理完了："+getCurrentTime());
//				}
				if (fregister != null) {
					System.out.println("Start : "+getCurrentTime());
					fregister.process();
					System.out.println("Complete : "+getCurrentTime());
				}
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	private boolean processLine(String line) {
		String[] lines = line.trim().split("\t");
		if (line.trim().startsWith("#")){
			return true;
		}
		if (lines.length == 2) {
			String cmd = lines[0].toLowerCase();
			String val = lines[1].trim();
//			if (cmd.equals("server")) {
//				this.server = val;
//			}
//			if (cmd.equals("port")) {
//				try {
//					this.port = Integer.parseInt(val);
//				} catch(NumberFormatException e) {
//					System.err.println("wrong port :["+val+"]");
//					return false;
//				}
//			}
			if (cmd.equals("basedir")) {
				this.baseDir = val;
			}
//			if (cmd.equals("hierarchy")) {
//				this.hierarchy = val;
//			}
//			if (cmd.equals("dbname")) {
//				this.dbName = lines[1].trim();
//			}
		} else
		if (lines.length == 3) {
			String cmd = lines[0].toLowerCase();
			String coll = lines[1].toLowerCase();
			String val = lines[2].trim();
			if (cmd.equals("data")) {
				String[] arg = new String[] {coll, val};
				this.data.add(arg);
			} else {
				System.err.println("wrong line :["+line+"]");
				return false;
			}
		}
		return true;
	}

	private String getCurrentTime() {
		Date now = new Date();
		return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(now);
	}

	public static void main(String[] args) {
/*
		try {
//			new HugeDBDataFileStorer("D:\\works\\work\\otros\\osu\\202302_2\\data\\WD-ification_6.nt").process(new File("D:\\works\\work\\otros\\osu\\202302_2\\data\\store\\instance"));
//			new HugeDBDataFileStorer("D:\\works\\work\\otros\\osu\\202302_2\\data\\WD-class_6.nt").process(new File("D:\\works\\work\\otros\\osu\\202302_2\\data\\store\\class"));
			new HugeGraphFileDBRegister("D:\\works\\work\\otros\\osu\\202306_01\\data\\settings_store.txt").process();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		*/

		if (args.length != 1) {
			error();
			System.exit(-1);
		}
		try {
			new HugeGraphFileDBRegister(args[0].trim()).process();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void error() {
		System.out.println("usage:");
		System.out.println("\tjava jp.ac.osakac.wdht.HugeGraphFileDBRegister <setting file>");
	}
}
