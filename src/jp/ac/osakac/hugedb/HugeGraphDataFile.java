package jp.ac.osakac.hugedb;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

//import jp.ac.osakac.lgdb.HugeGraph;


public class HugeGraphDataFile extends HugeGraph {

	private static final String CLASS_DIR = "class";
	private static final String INSTANCE_DIR = "instance";
	private static final String LABEL_DIR = "literal";
	private static final String ALTLABEL_DIR = "altlabel";
	private static final String DESCRIPTION_DIR = "description";

	private TripleUtil classUtil;
	private TripleUtil instanceUtil;
	private TripleUtil labelUtil;
	private TripleUtil altlabelUtil;
	private TripleUtil descriptionUtil;

	public HugeGraphDataFile(String dbName, String basePath) {
		super(dbName);
		this.classUtil = new TripleUtil(new File(basePath, CLASS_DIR));
		this.instanceUtil = new TripleUtil(new File(basePath, INSTANCE_DIR));
		this.labelUtil =  new TripleUtil(new File(basePath, LABEL_DIR));
		this.altlabelUtil =  new TripleUtil(new File(basePath, ALTLABEL_DIR));
		this.descriptionUtil =  new TripleUtil(new File(basePath, DESCRIPTION_DIR));
	}

	public HugeGraphDataFile(String server, int port, String dbName, String basePath) {
		super(server, port, dbName);
		this.classUtil = new TripleUtil(new File(basePath, CLASS_DIR));
		this.instanceUtil = new TripleUtil(new File(basePath, INSTANCE_DIR));
		this.labelUtil =  new TripleUtil(new File(basePath, LABEL_DIR));
		this.altlabelUtil =  new TripleUtil(new File(basePath, ALTLABEL_DIR));
		this.descriptionUtil =  new TripleUtil(new File(basePath, DESCRIPTION_DIR));
	}

	public HugeGraphDataFile(String basePath) {
		super();
		this.classUtil = new TripleUtil(new File(basePath, CLASS_DIR));
		this.instanceUtil = new TripleUtil(new File(basePath, INSTANCE_DIR));
		this.labelUtil =  new TripleUtil(new File(basePath, LABEL_DIR));
		this.altlabelUtil =  new TripleUtil(new File(basePath, ALTLABEL_DIR));
		this.descriptionUtil =  new TripleUtil(new File(basePath, DESCRIPTION_DIR));
	}

	/**
	 * 指定概念の上位概念を取得
	 * @param resource	指定概念
	 * @return	指定概念の一段上位の概念のリスト
	 */
	public List<String> findUppers(String resource) {
		resource = "<" + resource + ">";
		List<String> ret = new ArrayList<String>();
		PrefixResource pr = this.classUtil.getPrefixResource(resource);
		if (pr.index == null || pr.value == null) {
			return null;
		}


		List<PrefixResource> reses = this.classUtil.findForwardResource(pr);
		for (PrefixResource res : reses) {
			String uri = this.classUtil.toUri(res);
			if (uri != null) {
				ret.add(uri);
			}
		}

		return ret;

	}

	/**
	 * 指定概念の下位概念を取得
	 * @param resource	指定概念
	 * @return	指定概念の一段下位の概念のリスト
	 */
	public List<String> findLowers(String resource) {
		resource = "<" + resource + ">";
		List<String> ret = new ArrayList<String>();
		PrefixResource pr = this.classUtil.getPrefixResource(resource);
		if (pr.index == null || pr.value == null) {
			return null;
		}


		List<PrefixResource> reses = this.classUtil.findReverseResource(pr);
		for (PrefixResource res : reses) {
			String uri = this.classUtil.toUri(res);
			if (uri != null) {
				ret.add(uri);
			}
		}

		return ret;

	}

	/**
	 * 指定概念のクラス概念を取得
	 * @param resource	指定概念
	 * @return	指定インスタンスのクラス概念のリスト
	 */
	public List<String> findClasses(String resource) {
		resource = "<" + resource + ">";
		List<String> ret = new ArrayList<String>();
		PrefixResource pr = this.instanceUtil.getPrefixResource(resource);
		if (pr.index == null || pr.value == null) {
			return null;
		}


		List<PrefixResource> reses = this.instanceUtil.findForwardResource(pr);
		for (PrefixResource res : reses) {
			String uri = this.classUtil.toUri(res);
			if (uri != null) {
				ret.add(uri);
			}
		}

		return ret;

	}

	/**
	 * 指定概念のインスタンス概念を取得
	 * @param resource	指定概念
	 * @return	指定概念のインスタンスのリスト
	 */
	public List<String> findInstances(String resource) {
		resource = "<" + resource + ">";
		List<String> ret = new ArrayList<String>();
		PrefixResource pr = this.instanceUtil.getPrefixResource(resource);
		if (pr.index == null || pr.value == null) {
			return null;
		}


		List<PrefixResource> reses = this.instanceUtil.findReverseResource(pr);
		for (PrefixResource res : reses) {
			String uri = this.classUtil.toUri(res);
			if (uri != null) {
				ret.add(uri);
			}
		}

		return ret;

	}

	/**
	 * 指定概念のラベルを取得
	 * @param resource	指定概念
	 * @param lang		言語
	 * @return	指定概念のラベルの一覧(「"{ラベル}"@{言語}」の形で返す)
	 */
	public List<String> findLabel(String resource) {
		return findLiteral(resource, this.labelUtil);
	}

	/**
	 * 言語を指定して概念のラベルを取得
	 * @param resource	指定概念
	 * @param lang		言語
	 * @return	指定概念の指定言語でのラベルの一覧
	 */
	public List<String> findLabel(String resource, String lang) {
		return findLiteral(resource, this.labelUtil, lang);
	}

	/**
	 * 指定概念のaltラベルを取得
	 * @param resource	指定概念
	 * @param lang		言語
	 * @return	指定概念のaltラベルの一覧(「"{ラベル}"@{言語}」の形で返す)
	 */
	public List<String> findAltLabel(String resource) {
		return findLiteral(resource, this.altlabelUtil);
	}

	/**
	 * 言語を指定して概念のaltラベルを取得
	 * @param resource	指定概念
	 * @param lang		言語
	 * @return	指定概念の指定言語でのaltラベルの一覧
	 */
	public List<String> findAltLabel(String resource, String lang) {
		return findLiteral(resource, this.altlabelUtil, lang);
	}

	/**
	 * 指定概念のdescriptionを取得
	 * @param resource	指定概念
	 * @param lang		言語
	 * @return	指定概念のdescriptionの一覧(「"{ラベル}"@{言語}」の形で返す)
	 */
	public List<String> findDescription(String resource) {
		return findLiteral(resource, this.descriptionUtil);
	}

	/**
	 * 言語を指定して概念のdescriptionを取得
	 * @param resource	指定概念
	 * @param lang		言語
	 * @return	指定概念の指定言語でのdescriptionの一覧
	 */
	public List<String> findDescription(String resource, String lang) {
		return findLiteral(resource, this.descriptionUtil, lang);
	}

	/**
	 * 指定概念のリテラルを取得
	 * @param resource	指定概念
	 * @param util リテラル種別に応じたutil
	 * @return	指定概念のラベルの一覧(「"{ラベル}"@{言語}」の形で返す)
	 */
	public List<String> findLiteral(String resource, TripleUtil util) {
		resource = "<" + resource + ">";
		List<String> ret = new ArrayList<String>();
		PrefixResource pr = util.getPrefixResource(resource);
		if (pr.index == null || pr.value == null) {
			return null;
		}
		List<String> reses = util.findRelationLiteral(pr);
		for (String res : reses) {
			ret.add(res);
		}

		return ret;
	}

	/**
	 * 言語を指定して概念のラベルを取得
	 * @param resource	指定概念
	 * @param util リテラル種別に応じたutil
	 * @param lang		言語
	 * @return	指定概念の指定言語でのラベルの一覧
	 */
	public List<String> findLiteral(String resource, TripleUtil util, String lang) {
		resource = "<" + resource + ">";
		List<String> ret = new ArrayList<String>();
		PrefixResource pr = util.getPrefixResource(resource);
		if (pr.index == null || pr.value == null) {
			return null;
		}
		List<String> reses = util.findRelationLiteral(pr, lang);
		for (String res : reses) {
			ret.add(res);
		}

		return ret;
	}

/*
	public static void main(String[] args) {
		HugeGraphDataFile test = new HugeGraphDataFile(null, "D:\\works\\work\\otros\\osu\\202302_2\\data\\store");

		List<String> uppers = test.findUppers("http://www.wikidata.org/entity/Q35120");
		List<String> lowers = test.findLowers("http://www.wikidata.org/entity/Q35120");
		List<String> classes = test.findClasses("http://www.wikidata.org/entity/Q35120");
		List<String> instances = test.findInstances("http://www.wikidata.org/entity/Q35120");

		System.out.println("uppers");
		for (String res : uppers) {
			System.out.println("*["+res+"]");
		}
		System.out.println("lowers");
		for (String res : lowers) {
			System.out.println("*["+res+"]");
		}
		System.out.println("classes");
		for (String res : classes) {
			System.out.println("*["+res+"]");
		}
		System.out.println("instances");
		for (String res : instances) {
			System.out.println("*["+res+"]");
		}
	}
*/

	public static void main(String[] args) {
		if (args.length > 0) {
			String cmd = args[0].trim();
			if (cmd.equals("register")) {
				if (args.length == 2) {
					String file = args[1];
					try {
						new HugeGraphFileDBRegister(file).process();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						System.exit(0);
					}
				}
			} else if (cmd.equals("find")) {
				if (args.length >= 3) {
					String type = args[1];
					String uri = args[2];
					String lang = null;
					if (args.length >= 4) {
						lang = args[3];
					}
					List<String> ret = null;
					HugeGraphDataFile hg = new HugeGraphDataFile("vocab", ".\\");
					if (type.contentEquals("upper")) {
						ret = hg.findUppers(uri);
					} else
					if (type.contentEquals("lower")) {
						ret = hg.findLowers(uri);
					} else
					if (type.contentEquals("class")) {
						ret = hg.findClasses(uri);
					} else
					if (type.contentEquals("instance")) {
						ret = hg.findInstances(uri);
					} else
					if (type.contentEquals("literal") || type.contentEquals("label")) {
						if (lang == null) {
							ret = hg.findLabel(uri);
						} else {
							ret = hg.findLabel(uri, lang);
						}
					} else
					if (type.contentEquals("altlabel")) {
						if (lang == null) {
							ret = hg.findAltLabel(uri);
						} else {
							ret = hg.findAltLabel(uri, lang);
						}
					} else
					if (type.contentEquals("description")) {
						if (lang == null) {
							ret = hg.findDescription(uri);
						} else {
							ret = hg.findDescription(uri, lang);
						}
					} 
//					else
//					if (type.contentEquals("fullmatch")) {
//						if (lang == null) {
//							ret = hg.findByLabel(uri, true);
//						} else {
//							ret = hg.findByLabel(uri, lang, true);
//						}
//					} else
//					if (type.contentEquals("partmatch")) {
//						if (lang == null) {
//							ret = hg.findByLabel(uri, false);
//						} else {
//							ret = hg.findByLabel(uri, lang, false);
//						}
//					}
					if (ret!= null) {
						for (String r : ret) {
							System.out.println(r);
						}
						System.exit(0);
					}
				}

			}
		}

		error();
	}

	private static void error() {
		System.out.println("usage:");
		System.out.println("\tデータ登録コマンド");
		System.out.println("\tjava -jar HugeGraphFile.jar register  登録ファイル");
		System.out.println("\t\t登録ファイル\t登録内容の設定ファイル");
		System.out.println();
		System.out.println("\tデータ検索コマンド");
		System.out.println("\tjava -jar HugeGraphFile.jar find 検索種別 検索対象URI/文字列 [言語種別]");
		System.out.println("\t\t検索種別");
		System.out.println("\t\t\tupper\t上位概念を検索");
		System.out.println("\t\t\tlower\t下位概念を検索");
		System.out.println("\t\t\tclass\tクラスを検索");
		System.out.println("\t\t\tinstance\tインスタンスを検索");
		System.out.println("\t\t\tlabel\tラベルを検索");
		System.out.println("\t\t\taltlabel\taltラベルを検索");
		System.out.println("\t\t\tdescription\tdescriptionを検索");
		System.out.println("\t\t\tfullmatch\t文字列完全一致でリソースを検索");
		System.out.println("\t\t\tpartmatch\t文字列部分一致でリソースを検索");
		System.out.println("\t\t検索対象URI/文字列\t検索の元となるリソースのURI");
		System.out.println("\t\t\t\tfullmatch/partmatchの場合は検索文字列");
		System.out.println("\t\t言語種別\tラベル検索・文字列検索時の言語を指定する");
	}

}
