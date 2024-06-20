package jp.ac.osakac.hugedb;

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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import jp.ac.osakac.lgdb.HugeGraph;


public class HugeDBHierarchyFile {
	private String source;
	private File wordFile;
	private File inUpperFile;
	private File outUpperFile;
	private File stopFile;
	private Boolean isGetLabel;
	private Boolean isGetAltLabel;
	private Boolean isGetDescription;
	private Boolean isGetIns = true; //インスタンスを取得するか？
	private Boolean skipNoLabelEntity = false; //ラベルが無いEntityをスキップする
	private String outType;
	private Boolean isStopDuplication;

	private String server;
	private String dbName;
	private String baseDir;
	private Integer port;
	private Integer threshold;
	private Integer depth;
	private File outFile;


	public static void main(String[] args) {
/*
		try {
			new HugeDBHierarchyFile("D:\\works\\work\\otros\\osu\\202306_01\\data\\settings_read.txt").process();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
*/
		if (args.length != 1) {
			error();
			System.exit(-1);
		}
		try {
			new HugeDBHierarchyFile(args[0].trim()).process();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void error() {
		System.out.println("usage:");
		System.out.println("\tjava -jar HugeDBHierarchyFile.jar {settingFile}");
	}

	public HugeDBHierarchyFile(String iniFile) throws FileNotFoundException {
		if (!this.init(iniFile)) {
			return;
		}
	}


	private boolean init(String iniFile) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(iniFile), StandardCharsets.UTF_8));
		
		//設定が無くても動くように，デフォルト値を入れておく
		this.server = "localhost";
		this.port = 27017;
		this.dbName = "vocab";
		
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
		}
		if (this.baseDir == null || this.baseDir.trim().isEmpty()) {
			this.baseDir = Paths.get("").toAbsolutePath().toString();
		}
		if (this.baseDir.startsWith("\"") && this.baseDir.endsWith("\"")) {
			this.baseDir = this.baseDir.substring(1, this.baseDir.length()-1);
		}
		if (this.source == null) {
			System.err.println("source not defined.");
			ret = false;
		} else if (!(this.source.equals("word") || this.source.equals("file"))) {
			System.err.println("wrong source ["+this.source+"]");
			ret = false;
		}

		if (this.source != null) {
			if (this.source.equals("word")) {
				if (this.wordFile == null) {
					System.err.println("word list file not defined.");
					ret = false;
				}
				if (this.outUpperFile == null) {
					System.err.println("output upper list file not defined.");
					ret = false;
				}
			}
			if (this.source.equals("file")) {
				if (this.inUpperFile == null) {
					System.err.println("input upper list file not defined.");
					ret = false;
				}
			}
		}
		if (this.stopFile == null) {
			System.err.println("stop list file not defined.");
			ret = false;
		}
		if (this.outFile == null) {
			System.err.println("output file not defined.");
			ret = false;
		}
		if (this.threshold == null) {
			this.threshold = 2;
			System.out.println("threshold [2].");
		}
		if (this.depth == null) {
			this.depth = 3;
			System.out.println("depth [3].");
		}
		if (this.isGetLabel == null) {
			this.isGetLabel = false;
			System.out.println("getLabel [no]");
		}
		if (this.isGetAltLabel == null) {
			this.isGetAltLabel = false;
			System.out.println("getAltLabel [no]");
		}
		if (this.isGetDescription == null) {
			this.isGetDescription = false;
			System.out.println("getDescription [no]");
		}
		if (this.isStopDuplication == null) {
			this.isStopDuplication = true;
			System.out.println("stopDuplication [yes]");
		}
		if (this.outType == null) {
			this.outType = "stream";
			System.out.println("outType [stream]");
		}
		return ret;
	}

	private boolean processLine(String line) {
		String[] lines = line.trim().split("\t");
		if (line.trim().startsWith("#")){
			return true;
		}
		if (lines.length == 2) {
			String cmd = lines[0].toLowerCase();
			String val = lines[1].trim();
			if (cmd.equals("server")) {
				this.server = val;
			}
			if (cmd.equals("port")) {
				try {
					this.port = Integer.parseInt(val);
				} catch(NumberFormatException e) {
					System.err.println("wrong port :["+val+"]");
					return false;
				}
			}
			if (cmd.equals("dbname")) {
				this.dbName = lines[1].trim();
			}
			if (cmd.equals("basedir")) {
				val = val.trim();
				if (val.startsWith("\"") && val.endsWith("\"")) {
					val = val.substring(1, val.length()-1);
				}
				this.baseDir = val;
			}

			if (cmd.equals("source")) {
				this.source = val.toLowerCase();
			}
			if (cmd.equals("wordlist")) {
				val = val.trim();
				if (val.startsWith("\"") && val.endsWith("\"")) {
					val = val.substring(1, val.length()-1);
				}
				this.wordFile = new File(val.trim());
			}
			if (cmd.equals("outputupperlist")) {
				val = val.trim();
				if (val.startsWith("\"") && val.endsWith("\"")) {
					val = val.substring(1, val.length()-1);
				}
				this.outUpperFile = new File(val.trim());
			}
			if (cmd.equals("inputupperlist")) {
				val = val.trim();
				if (val.startsWith("\"") && val.endsWith("\"")) {
					val = val.substring(1, val.length()-1);
				}
				this.inUpperFile = new File(val.trim());
			}
			if (cmd.equals("stoplist")) {
				val = val.trim();
				if (val.startsWith("\"") && val.endsWith("\"")) {
					val = val.substring(1, val.length()-1);
				}

				this.stopFile = new File(val.trim());
			}
			if (cmd.equals("outfile")) {
				val = val.trim();
				if (val.startsWith("\"") && val.endsWith("\"")) {
					val = val.substring(1, val.length()-1);
				}
				this.outFile = new File(val.trim());
			}
			if (cmd.equals("getlabel")) {
				this.isGetLabel = val.trim().toLowerCase().equals("yes");
			}
			if (cmd.equals("getaltlabel")) {
				this.isGetAltLabel = val.trim().toLowerCase().equals("yes");
			}
			if (cmd.equals("getdescription")) {
				this.isGetDescription = val.trim().toLowerCase().equals("yes");
			}
			if (cmd.equals("stopduplication")) {
				this.isStopDuplication = val.trim().toLowerCase().equals("yes");
			}
			if (cmd.equals("threshold")) {
				try {
					this.threshold = Integer.parseInt(val);
				} catch(NumberFormatException e) {
					System.err.println("wrong threshold :["+val+"]");
					return false;
				}
			}
			if (cmd.equals("getinstance")) {
				this.isGetIns = val.trim().toLowerCase().equals("yes");
				System.out.println("getInstance = "+this.isGetIns);
			}
			if (cmd.equals("skipnolabelentity")) {
				this.skipNoLabelEntity = val.trim().toLowerCase().equals("yes");
				System.out.println("skipNoLabelEntity = "+this.skipNoLabelEntity);
			}
			
			if (cmd.equals("depth")) {
				try {
					this.depth = Integer.parseInt(val);
				} catch(NumberFormatException e) {
					System.err.println("wrong depth :["+val+"]");
					return false;
				}
			}
			if (cmd.equals("outputtype")) {
				this.outType = val.toLowerCase();
			}

		}
		return true;
	}

	public void process() {
		HugeGraph hg = new HugeGraph(this.server, this.port, this.dbName);
		HugeGraphDataFile hgf = new HugeGraphDataFile(this.server, this.port, this.dbName, this.baseDir);

		PrintWriter bw = null;
		try {
			bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.outFile), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		if (bw == null) {
			return;
		}

		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> wordList = null;
		List<String> stopList = null;

		// キーワードを取得
		try {
			wordList = getLineList(this.wordFile);
			stopList = getLineList(this.stopFile);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}

		if (wordList == null) {
			System.err.println("File Error:["+this.wordFile+"]");
		}

		List<String> globalConceptList = null;

		if (this.source.equals("word")) {
			// キーワードに対応する概念と，その上位階層を取得
			for (String word : wordList) {
				getUpperMap(hg, hgf, word, map);
			}

			for (String key : map.keySet()) {
				System.out.println("key:["+key+"]");
				List<String> uppers = map.get(key);
				for (String upper : uppers) {
					System.out.println(" -["+upper+"]");
				}
			}

			// 共通上位をファイルに吐き出す

			// 一定数以上の概念の上位となっている「共通上位」を取得
			globalConceptList = collectGlobalConcept(map, this.threshold, this.outUpperFile);
		} else {
			try {
				globalConceptList = readGlobalConcept(this.inUpperFile, this.threshold);
			} catch(Exception e) {
				e.printStackTrace();
				return;
			}
		}

		System.out.println("globals");
		for (String global : globalConceptList) {
			System.out.println("["+global+"]");
		}

/*
		// テスト用
		List<String> globalConceptList = new ArrayList<String>();
		globalConceptList.add("http://www.wikidata.org/entity/Q7275");
*/

		Map<String, List<String>> labelMap = new HashMap<String, List<String>>();
		Map<String, List<String>> altLabelMap = new HashMap<String, List<String>>();
		Map<String, List<String>> descriptionMap = new HashMap<String, List<String>>();

		// 共通上位の下位概念・インスタンス概念を再帰取得
		if (this.outType.equals("mem")) {
			List<Map<String, List<String>>> lowerMaps = getLowerConcepts(hgf, globalConceptList, stopList, this.depth);
			Map<String, List<String>> lowerMap = lowerMaps.get(0);
			Map<String, List<String>> instanceMap = lowerMaps.get(1);


			for (String key : lowerMap.keySet()) {
//				System.out.println("lower key:["+key+"]");

				getLabel(labelMap, hgf, key);
				getAltLabel(labelMap, hgf, key);
				getDescription(descriptionMap, hgf, key);

				List<String> lowers = lowerMap.get(key);
				for (String lower : lowers) {
					String line = "<" + lower + "> <http://www.wikidata.org/prop/direct/P279> <" + key + "> .\n";
					bw.write(line);
//					System.out.println(" lower:["+lower+"]");	
					
					getLabel(labelMap, hgf, lower);
					getAltLabel(altLabelMap, hgf, lower);
					getDescription(descriptionMap, hgf, lower);
				}
			}

			for (String key : instanceMap.keySet()) {
//				System.out.println("instance key:["+key+"]");

				getLabel(labelMap, hgf, key);
				getAltLabel(altLabelMap, hgf, key);
				getDescription(labelMap, hgf, key);

				List<String> instances = instanceMap.get(key);
				for (String instance : instances) {
					String line = "<" + instance + "> <http://www.wikidata.org/prop/direct/P31> <" + key + "> .\n";
					bw.write(line);
//					System.out.println(" instance:["+instance+"]");

					getLabel(labelMap, hgf, instance);
					getAltLabel(altLabelMap, hgf, instance);
					getDescription(descriptionMap, hgf, instance);
				}
			}
			
		} else {
			getLowerConcepts(hgf, globalConceptList, stopList, this.depth, bw);
		}

		for (String key : labelMap.keySet()) {
			System.out.println("label key:["+key+"]");
			List<String> literals = labelMap.get(key);
			for (String literal : literals) {
				String line = "<" + key + "> <http://www.w3.org/2000/01/rdf-schema#label> " + literal + " .\n";
				bw.write(line);
				System.out.println(" label:["+literal+"]");
			}
		}
		for (String key : altLabelMap.keySet()) {
			System.out.println("altlabel key:["+key+"]");
			List<String> literals = altLabelMap.get(key);
			for (String literal : literals) {
				String line = "<" + key + "> <http://www.w3.org/2004/02/skos/core#altLabel> " + literal + " .\n";
				bw.write(line);
				System.out.println(" altlabel:["+literal+"]");
			}
		}
		for (String key : descriptionMap.keySet()) {
			System.out.println("description key:["+key+"]");
			List<String> literals = descriptionMap.get(key);
			for (String literal : literals) {
				String line = "<" + key + "> <https://schema.org/description> " + literal + " .\n";
				bw.write(line);
				System.out.println(" description:["+literal+"]");
			}
		}


		bw.close();


	}
	
	//ラベルが見つかったどうかを返すように変更
	private boolean getLabel(Map<String, List<String>> literalMap, HugeGraphDataFile hg, String key) {
		boolean isExistLabel = false;
		if (this.isGetLabel) {
			if (!literalMap.containsKey(key)) {
				List<String> literals = hg.findLabel(key);
				literalMap.put(key, literals);
				isExistLabel = true;
			}
		}
		
		return isExistLabel;
	}

	private List<String> getLabel(HugeGraphDataFile hg, String key) {
		List<String> literals = null;
		if (this.isGetLabel) {
			literals = hg.findLabel(key);
		}
		return literals;
	}

	private void getAltLabel(Map<String, List<String>> literalMap, HugeGraphDataFile hg, String key) {
		if (this.isGetAltLabel) {
			if (!literalMap.containsKey(key)) {
				List<String> literals = hg.findAltLabel(key);
				literalMap.put(key, literals);
			}
		}
	}

	private List<String> getAltLabel(HugeGraphDataFile hg, String key) {
		List<String> literals = null;
		if (this.isGetAltLabel) {
			literals = hg.findAltLabel(key);
		}
		return literals;
	}

	private void getDescription(Map<String, List<String>> literalMap, HugeGraphDataFile hg, String key) {
		if (this.isGetDescription) {
			if (!literalMap.containsKey(key)) {
				List<String> literals = hg.findDescription(key);
				literalMap.put(key, literals);
			}
		}
	}

	private List<String> getDescription(HugeGraphDataFile hg, String key) {
		List<String> literals = null;
		if (this.isGetDescription) {
			literals = hg.findDescription(key);
		}
		return literals;
	}

	/**
	 * 一行一データのファイルを読み込みリストに格納
	 * @param wordFile
	 * @return
	 * @throws FileNotFoundException
	 */
	private List<String> getLineList(File wordFile) throws FileNotFoundException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(wordFile), StandardCharsets.UTF_8));

		List<String> wordList = new ArrayList<String>();

		try {
			String line = br.readLine();
			while (line != null) {
				if (!line.trim().isEmpty()) {
					wordList.add(line.trim());
				}

				line = br.readLine();
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}


		return wordList;
	}

	/**
	 * 上位概念，クラス概念を取得
	 * @param hg
	 * @param word
	 * @param map
	 * @return
	 */
	public boolean getUpperMap(HugeGraph hg, HugeGraphDataFile hgf, String word, Map<String, List<String>> map) {

		List<String> reses = null;

		if (word.startsWith("http://")) {
			// リソース
			reses = new ArrayList<String>();
			reses.add(word);
		} 
//		else {
//			reses = hg.findByLabel(word, "ja", true);
//		}
		List<String> stopList = new ArrayList<String>();

		System.out.println("word:["+word+"]");
		for (String res : reses) {
			// 直接の下位概念 - 上位概念リスト のマップとする場合
//			getUpperMapRecurse(hgf, map, res, stopList);

			// 大元の概念res - 上位概念リスト のマップを作成
			Map<String, List<String>> tmpMap = new HashMap<String, List<String>>();
			getUpperMapRecurse(hgf, tmpMap, res, stopList);
			List<String> mapList = new ArrayList<String>();
			for (List<String> list : tmpMap.values()) {
				for (String upper : list) {
					if (!mapList.contains(upper)) {
						mapList.add(upper);
					}
				}
			}
			map.put(res, mapList);

		}
		return true;
	}

	private void getUpperMapRecurse(HugeGraphDataFile hg, Map<String, List<String>> upperMap, String concept, List<String> stopList) {

//			HugeGraph hg, String word, List<String> map) {
		if (upperMap.containsKey(concept)) {
			// 追加済みであればそのまま返す
			return;
		}
		List<String> ret = new ArrayList<String>();
System.out.println("*find upper start:["+concept+"]");
		List<String> uppers = hg.findUppers(concept);
		if (uppers != null && uppers.size() > 0) {
			// uppersを回して，既に追加済みかどうかを判断
			// まだであれば再帰探索
			upperMap.put(concept, uppers);
			for (String upper : uppers) {
System.out.println("*upper of:["+concept+"]->["+upper+"]");
				ret.add(upper);
				if (stopList.contains(upper)) {
					// 既に存在するので再帰探索はしない
System.out.println(" *skip");
				} else {
					stopList.add(upper);
					getUpperMapRecurse(hg, upperMap, upper, stopList);
				}
			}
		}
System.out.println("*find class start:["+concept+"]");
		List<String> classes = hg.findClasses(concept);
		if (classes != null && classes.size() > 0) {
//			lowerMap.put(concept, instances);
			for (String clazz : classes) {
System.out.println("*class of:["+concept+"]->["+clazz+"]");
				ret.add(clazz);
				if (stopList.contains(clazz)) {
					// 既に存在するので再帰探索はしない
System.out.println(" *skip");
				} else {
					stopList.add(clazz);
					getUpperMapRecurse(hg, upperMap, clazz, stopList);
				}
			}
		}
		if (ret.size() > 0) {
			upperMap.put(concept, ret);
		}
	}

	/**
	 *　閾値以上登場する概念を共通上位として抽出
	 * @param map
	 * @param threshold
	 * @param outFile
	 * @return
	 */
	public List<String> collectGlobalConcept(Map<String, List<String>> map, int threshold, File outFile) {
		PrintWriter bw = null;
		try {
			if (outFile != null) {
				bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8));
			}
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		List<String> globalList = new ArrayList<String>();
		Map<String, Integer> countMap = new HashMap<String, Integer>();

		for (List<String> list : map.values()) {
			for (String res : list) {
				if (countMap.containsKey(res)) {
					Integer count = countMap.get(res);
					count = (count.intValue() + 1);
					countMap.put(res, count);
				} else {
					countMap.put(res, 1);
				}
			}
		}

		for (Entry<String, Integer> set : countMap.entrySet()) {
			System.out.println(" ["+set.getKey()+"] -> "+set.getValue());
			if (bw != null) {
				bw.println(set.getKey() + ", "+set.getValue());
			}
			if (set.getValue() >= threshold) {
				globalList.add(set.getKey());
			}
		}
		if (bw != null) {
			bw.close();
		}

		return globalList;
	}

	public List<String> readGlobalConcept(File file, int threshold) throws FileNotFoundException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		List<String> ret = new ArrayList<String>();
		String line;

		try {
			line = br.readLine();

			while (line != null) {
				line = line.trim();
				String[] vals = line.split(",");
				if (vals.length == 2) {
					int count = Integer.parseInt(vals[1].trim());
					if (count >= threshold) {
						ret.add(vals[0].trim());
					}
				}

				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 *
	 * @param hg
	 * @param concepts
	 * @param stopList 再帰概念のリスト
	 * @param depth 探索深さ
	 * @return
	 */
	public List<Map<String, List<String>>> getLowerConcepts(HugeGraphDataFile hg, List<String> concepts, List<String> stopList, int depth){
		Map<String, List<String>> lowerMap = new HashMap<String, List<String>>();
		Map<String, List<String>> instanceMap = new HashMap<String, List<String>>();

		int lp=0;
		for (String concept : concepts) {
			System.out.print((lp++)+"\t, get lower start:"+concept+"\t");
			long time = new Date().getTime();

			int count = getLowerConceptsRecurse(hg, lowerMap, instanceMap, concept, stopList, 0, depth);

			System.out.println(", time:" + (new Date().getTime()-time) + " ms,  count:"+count);
		}

		List<Map<String, List<String>>> ret = new ArrayList<Map<String, List<String>>>();
		ret.add(lowerMap);
		ret.add(instanceMap);

		return ret;
	}

	private int getLowerConceptsRecurse(HugeGraphDataFile hg, Map<String, List<String>> lowerMap, Map<String, List<String>> instanceMap, String concept, List<String> stopList, int currentDepth, int depth) {
		int count = 0;
		if (currentDepth >= depth) {
			// 深さが指定数以上になった場合は打ち切る
			return count;
		}
		// ストップリストに追加されていれば下位は取得しない
		if (stopList.contains(concept)) {
			return count;
		}
		List<String> lowerList = new ArrayList<String>();
		List<String> instanceList = new ArrayList<String>();
//		stopList.add(concept);

		if (!(lowerMap.containsKey(concept) && this.isStopDuplication)) {
			List<String> lowers = hg.findLowers(concept);
			if (lowers != null && lowers.size() > 0) {
				// lowersを回して，既に追加済みかどうかを判断
				// まだであれば再帰探索
				lowerMap.put(concept, lowers);// この時点では仮登録
				for (String lower : lowers) {
					//if((!this.skipNoLabelEntity)||(hg.findLabel(lower)!=null)) {
						lowerList.add(lower);
						count++;
						count += getLowerConceptsRecurse(hg, lowerMap, instanceMap, lower, stopList, currentDepth+1, depth);
					//}
				}
			}
		}
		if (!(instanceMap.containsKey(concept) && this.isStopDuplication)) {
			List<String> instances = hg.findInstances(concept);
			if (instances != null && instances.size() > 0) {
				instanceMap.put(concept, instances);// この時点では仮登録
				count++;
				for (String instance : instances) {
					//if((!this.skipNoLabelEntity)||(hg.findLabel(instance)!=null)) {
						instanceList.add(instance);
						count += getLowerConceptsRecurse(hg, lowerMap, instanceMap, instance, stopList, currentDepth+1, depth);
					//}
				}
			}
		}
		if (lowerList.size() > 0) {
			lowerMap.put(concept, lowerList);
		}
		if (instanceList.size() > 0) {
			instanceMap.put(concept, instanceList);
		}

		return count;

	}

	/**
	 *
	 * @param hg
	 * @param concepts
	 * @param stopList 再帰概念のリスト
	 * @param depth 探索深さ
	 * @return
	 */
	public boolean getLowerConcepts(HugeGraphDataFile hg, List<String> concepts, List<String> stopList, int depth, PrintWriter pw){
		List<String> lowerMap = new ArrayList<String>();
		List<String> instanceMap = new ArrayList<String>();

		int lp = 0;
		for (String concept : concepts) {
			System.out.print((lp++)+"\t, get lower start:"+concept+"\t");

			long time = new Date().getTime();
			findLiteral(hg, concept, pw);
			int count = getLowerConceptsRecurse(hg, lowerMap, instanceMap, concept, stopList, 0, depth, pw);
			System.out.println(", time:" + (new Date().getTime()-time) + " ms,  count:"+count);
		}


		return true;
	}

	private int getLowerConceptsRecurse(HugeGraphDataFile hg, List<String> lowerMap, List<String> instanceMap, String concept, List<String> stopList, int currentDepth, int depth, PrintWriter pw) {
		int count = 0;
		if (currentDepth >= depth) {
			// 深さが指定数以上になった場合は打ち切る
			return count;
		}
		// ストップリストに追加されていれば下位は取得しない
		if (stopList.contains(concept)) {
			return count;
		}

		if (!(lowerMap.contains(concept) && this.isStopDuplication)) {
			List<String> lowers = hg.findLowers(concept);
			if (lowers != null && lowers.size() > 0) {
				// lowersを回して，既に追加済みかどうかを判断
				// まだであれば再帰探索
				for (String lower : lowers) {
					boolean lbl = findLiteral(hg, lower, pw);
					if(!skipNoLabelEntity||lbl) {
						String line = "<" + lower + "> <http://www.wikidata.org/prop/direct/P279> <" + concept + "> .";
						pw.println(line);		
						count ++ ;
						count += getLowerConceptsRecurse(hg, lowerMap, instanceMap, lower, stopList, currentDepth+1, depth, pw);
					}
				}
				/*再帰処理を上「if文内」に移動
				for (String lower : lowers) {
					count ++ ;
					count += getLowerConceptsRecurse(hg, lowerMap, instanceMap, lower, stopList, currentDepth+1, depth, pw);
				}*/
				lowerMap.add(concept);// この時点では仮登録
			}
		}
		
		if(isGetIns) {//インスタンスを取得するか否かを選択できるように
			if (!(instanceMap.contains(concept) && this.isStopDuplication)) {
				List<String> instances = hg.findInstances(concept);
				if (instances != null && instances.size() > 0) {
					for (String instance : instances) {
						boolean lbl = findLiteral(hg, instance, pw);
						if(!skipNoLabelEntity||lbl) {
							String line = "<" + instance + "> <http://www.wikidata.org/prop/direct/P31> <" + concept + "> .";
							pw.println(line);
							count ++;							
							count += getLowerConceptsRecurse(hg, lowerMap, instanceMap, instance, stopList, currentDepth+1, depth, pw);
						}
					}
					/*再帰処理を上「if文内」に移動
					for (String instance : instances) {
						count += getLowerConceptsRecurse(hg, lowerMap, instanceMap, instance, stopList, currentDepth+1, depth, pw);
					}*/
					instanceMap.add(concept);// この時点では仮登録
				}
			}
		}

		return count;
	}

	//ラベルが見つかったどうかを返すように変更
	private boolean findLiteral(HugeGraphDataFile hg, String concept, PrintWriter pw) {
		boolean isExistLabel = false;

		List<String> labelList = getLabel(hg, concept);
		if (labelList != null) {
			for (String label : labelList) {
				isExistLabel = true;
				String line = "<" + concept + "> <http://www.w3.org/2000/01/rdf-schema#label> " + label + " .";
				pw.println(line);
			}
		}
		labelList = getAltLabel(hg, concept);
		if (labelList != null) {
//			isExistLabel = true;
			for (String label : labelList) {
				String line = "<" + concept + "> <http://www.w3.org/2004/02/skos/core#altLabel> " + label + " .";
				pw.println(line);
			}
		}
		labelList = getDescription(hg, concept);
		if (labelList != null) {
//			isExistLabel = true;
			for (String label : labelList) {
				String line = "<" + concept + "> <https://schema.org/description> " + label + " .";
				pw.println(line);
			}
		}

		return isExistLabel;
	}

}
