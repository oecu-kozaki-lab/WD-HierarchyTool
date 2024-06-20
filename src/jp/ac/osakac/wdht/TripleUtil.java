package jp.ac.osakac.wdht;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class TripleUtil {

	private final String PREFIX_FILE_NAME = "prefix";
	private static final int FILE_HEAD_SIZE = 3;
	private static final String FORWARD_DIR = "spo";
	private static final String REVERSE_DIR = "ops";

	private List<String> prefixes;

	private File prefixFile;
	private File basePath;
	private File fwdPath;
	private File revPath;
	private File literalPath;

	public TripleUtil(File basePath) {
		this.prefixes = new ArrayList<String>();
		this.basePath = basePath;
		this.fwdPath = new File(this.basePath, FORWARD_DIR);
		this.revPath = new File(this.basePath, REVERSE_DIR);
		this.literalPath = this.basePath;
		this.prefixFile = new File(this.basePath, PREFIX_FILE_NAME);
		try {
			init();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}


	private void init() throws FileNotFoundException{
		if (!this.prefixFile.exists()) {
			return;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.prefixFile), StandardCharsets.UTF_8));

		try {
			String line = br.readLine();
			while (line != null) {
				this.prefixes.add(line);

				line = br.readLine();
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * リソースuriをDB格納のために軽量なPrefixResourceに変換する
	 * @param resource
	 * @return
	 */
	public PrefixResource getPrefixResource(String resource) {
		PrefixResource ret = null;
		String[] pre = getPrefix(resource);
		if (pre == null) {
			// PrefixResourceではない
			return new PrefixResource(null, resource);
		}

		int pfxIndex = this.prefixes.indexOf(pre[0]);
		if (pfxIndex >= 0) {
			return new PrefixResource((pfxIndex+1), pre[1]);
		} else {
			// DBに問い合わせ，無ければ登録
			int index = getPrefixIndex(pre[0]);
			ret = new PrefixResource(index, pre[1]);
		}
		return ret;
	}

	/**
	 * リソースvalueからファイル名を取得
	 * @param value
	 * @return
	 */
	public String getHeader(String value) {
		/*
		if (value.length() < FILE_HEAD_SIZE) {
			return value;
		}
		return value.substring(0, FILE_HEAD_SIZE);
		*/
		if (value.length() > (FILE_HEAD_SIZE*2)) {
			value =  value.substring(0, FILE_HEAD_SIZE*2);
		}

		String ret = "";
		int n = FILE_HEAD_SIZE;

		int length = value.length();
		int arrayLength = (int) Math.ceil((double) length / n);
		String[] result = new String[arrayLength];

		for (int i = 0; i < arrayLength; i++) {
			int startIndex = i * n;
			int endIndex = Math.min(startIndex + n, length);
			result[i] = value.substring(startIndex, endIndex);
		}
		ret = String.join(File.separator, result) + ".dat";

		return ret;
	}

	/**
	 * 引数のリソースをSとして，Oとなるリソースの一覧を取得する
	 * @param res
	 * @return
	 */
	public List<PrefixResource> findForwardResource(PrefixResource res){

		return findRelationResource(res, this.fwdPath);
	}

	/**
	 * 引数のリソースをOとして，Sとなるリソースの一覧を取得する
	 * @param res
	 * @return
	 */
	public List<PrefixResource> findReverseResource(PrefixResource res){
		return findRelationResource(res, this.revPath);
	}

	/**
	 * 引数のリソースを見出しとして，参照先となるリソースの一覧を取得する
	 * @param res
	 * @return
	 */
	public List<PrefixResource> findRelationResource(PrefixResource res, File basePath){
		List<PrefixResource> ret = new ArrayList<PrefixResource>();
		String fileName = getHeader(res.value);


		File path = new File(basePath, fileName);
		String key = new StringBuilder().append(res.index).append(":").append(res.value).append(" ").toString();

		if (!path.exists()) {
			// 存在しないことが自明
			return ret;
		}

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e1) {
			// ありえないはず
			e1.printStackTrace();
			return ret;
		}

		try {
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith(key)) {
					String[] reses = line.split(" ");
					if (reses.length == 2) {
						String relation = reses[1].trim();
						String[] tmp = relation.split(":");
						if (tmp.length == 2) {
							ret.add(new PrefixResource(Integer.valueOf(tmp[0]), tmp[1]));
						}
					}
				}

				line = br.readLine();
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 引数のリソースを見出しとして，参照先となるリテラルの一覧を取得する
	 * @param res
	 * @return
	 */
	public List<String> findRelationLiteral(PrefixResource res){
		File basePath = this.literalPath;
		List<String> ret = new ArrayList<String>();
		String dirName = getHeader(res.value);

		File path = new File(basePath, dirName);

		if (!path.exists() || !path.isDirectory()) {
			// 存在しないことが自明
			return ret;
		}

		File[] files = path.listFiles();

		for (File file : files) {
			List<String> literals = findRelationLiteral(res, file.getName());
			ret.addAll(literals);
		}

		return ret;
	}

	public List<String> findRelationLiteral(PrefixResource res, String lang){
		File basePath = this.literalPath;
		List<String> ret = new ArrayList<String>();
		String dirName = getHeader(res.value);

		File file = new File(new File(basePath, dirName), lang);
		String key = new StringBuilder().append(res.index).append(":").append(res.value).append(" ").toString();

		if (!file.exists() || !file.isFile()) {
			// 存在しないことが自明
			return ret;
		}

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e1) {
			// ありえないはず
			e1.printStackTrace();
			return ret;
		}

		try {
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith(key)) {
					//String[] reses = line.split(" ");
					int sp = line.indexOf(" ");
					if(sp>0) {
						String literal = line.substring(sp+1).trim();
						if (lang.isEmpty() || lang.equals("_")) {
							ret.add(new StringBuilder("\"").append(literal).append("\"").toString());
						} else {
							ret.add(new StringBuilder("\"").append(literal).append("\"@").append(lang).toString());
						}
					}
					//以下の処理では「半角スペースを含むリテラル」が取得できないので上記のコードに修正　(2024/6/16)
					/*String[] reses = line.split(" ");
					if (reses.length == 2) {
						String literal = reses[1].trim();
						if (lang.isEmpty() || lang.equals("_")) {
							ret.add(new StringBuilder("\"").append(literal).append("\"").toString());
						} else {
							ret.add(new StringBuilder("\"").append(literal).append("\"@").append(lang).toString());
						}
					}*/
				}

				line = br.readLine();
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 順方向参照データ格納パスを取得
	 * @return
	 */
	public File getForwardPath() {
		return this.fwdPath;
	}

	/**
	 * 逆方向参照データ格納パスを取得
	 * @return
	 */
	public File getReversePath() {
		return this.revPath;
	}

	/**
	 * PrefixResourceをリソースuriに戻す
	 * @param res
	 * @return
	 */
	public String toUri(PrefixResource res) {
		return toUri(res.index, res.value);
	}

	/**
	 * DBの格納データをリソースuriに戻す
	 * @param index
	 * @param value
	 * @return
	 */
	public String toUri(int index, String value) {
		String ret = null;
		if (value != null) {
			if (0 < index && index < (this.prefixes.size()+1)) {
				ret = this.prefixes.get(index-1);
			} else {
				return null; // TODO error
			}
			ret += value;
		}
		return ret;
	}


	public int getPrefixIndex(String prefix) {
		/*
		if (this.prefixMap.containsKey(prefix)) {
			return this.prefixMap.get(prefix);
		}*/
		int pfxIndex = this.prefixes.indexOf(prefix);
		if (pfxIndex >=0) {
			return (pfxIndex+1);
		}

		this.prefixes.add(prefix);
		int index = (this.prefixes.size()+1);

		return index;
	}

	public void registerPrefixes() {
		PrintWriter bw = null;

		if (!this.prefixFile.getParentFile().exists()) {
			this.prefixFile.getParentFile().mkdirs();
		}

		try {
			bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.prefixFile), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		for (String prefix : prefixes) {
			bw.println(prefix);
		}
		bw.close();

	}

	private String[] getPrefix(String uri) {
		String[] ret = null;
		if (!(uri.startsWith("<") && uri.endsWith(">"))) {
			return null;
		}
		uri = uri.substring(1, uri.length()-1);
		if (uri.lastIndexOf("#") > 0) {
			ret = uri.split("#");
			if (ret.length == 2) {
				int index = uri.lastIndexOf("#");
				ret[0] = uri.substring(0, index+1);
				ret[1] = uri.substring(index+1);
				return ret;
			} else {
				return null;
			}
		}
		if (uri.lastIndexOf("/") > 0) {
			ret = new String[2];
			int index = uri.lastIndexOf("/");
			ret[0] = uri.substring(0, index+1);
			ret[1] = uri.substring(index+1);
			return ret;
		}
		return null; // ここは通らないはず
	}

	protected LiteralResource getLiteralResource(String resource) {
		LiteralResource ret = new LiteralResource(resource);
		return ret;
	}
}
