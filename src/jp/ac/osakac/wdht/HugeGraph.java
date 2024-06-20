package jp.ac.osakac.wdht;

public class HugeGraph {
//	private MongoDBConnector conn;

	private TripleUtil util;

	/**
	 * ローカルのMongoDBに登録された大規模知識グラフデータへのアクセスモジュールを初期化する
	 * @param dbName	MongoDBのDB名
	 */
	public HugeGraph(String dbName) {
//		this.conn = new MongoDBConnector(dbName);
//		this.util = new TripleUtil(this.conn.getDB());
	}

	/**
	 * サーバとポートを指定して，MongoDBに登録された大規模知識グラフデータへのアクセスモジュールを初期化する
	 * @param server	MongoDBのサーバ(ドメインもしくはIPアドレス)
	 * @param port		MongoDBのポート
	 * @param dbName	MongoDBのDB名
	 */
	public HugeGraph(String server, int port, String dbName) {
//		this.conn = new MongoDBConnector(server, port, dbName);
//		this.util = new TripleUtil(this.conn.getDB());
	}

	public HugeGraph() {
	}


	private static void error() {
		System.out.println("usage:");
		System.out.println("\tデータ登録コマンド");
		System.out.println("\tjava -jar HugeGraphDB.jar register  登録ファイル");
		System.out.println("\t\t登録ファイル\t登録内容の設定ファイル");
		System.out.println();
		System.out.println("\tデータ検索コマンド");
		System.out.println("\tjava -jar HugeGraphDB.jar find 検索種別 検索対象URI/文字列 [言語種別]");
		System.out.println("\t\t検索種別");
		System.out.println("\t\t\tupper\t上位概念を検索");
		System.out.println("\t\t\tlower\t下位概念を検索");
		System.out.println("\t\t\tclass\tクラスを検索");
		System.out.println("\t\t\tinstance\tインスタンスを検索");
		System.out.println("\t\t\tliteral\tラベルを検索");
		System.out.println("\t\t\tfullmatch\t文字列完全一致でリソースを検索");
		System.out.println("\t\t\tpartmatch\t文字列部分一致でリソースを検索");
		System.out.println("\t\t検索対象URI/文字列\t検索の元となるリソースのURI");
		System.out.println("\t\t\t\tfullmatch/partmatchの場合は検索文字列");
		System.out.println("\t\t言語種別\tラベル検索・文字列検索時の言語を指定する");
	}

}
