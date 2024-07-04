# WD-HierarchyTool
Wikidataのクラス階層を操作するためのJavaライブラリです．

# 使い方の概要
## 1. WikidataのRDFダンプ（N-Triple形式）から
  - 述語がwdt:P31(分類/instance-of)のもの
  - 述語がwdt:P279(上位クラス/sub-class-of)のもの
  - 述語がrdfs:label(ラベル)のもの
  - 述語がskos:altLabel(別名)のもの  
を入力ファイルとして，「処理用のインデックス」 を作成する
## 2. 起点としたいWikidataのエンティティの一覧を入力し，下記の処理で「クラス階層を抽出」 する
  - それらに共通している上位クラス(=共通上位)を取得
  - 共通上位を起点とする下位クラスの階層を取得
     
