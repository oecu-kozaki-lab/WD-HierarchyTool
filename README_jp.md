# WD-HierarchyTool
ローカルに生成したインデックスファイルを用いて，Wikidataのクラス階層を操作するためのJavaライブラリです．

# 使い方の概要
## 1. WikidataのRDFダンプ（N-Triple形式）から「インデックスファイルの作成」　　
WikidataのRDFダンプ（N-Triple形式）から，ローカルPCでWikidataの階層を操作するためのインデックスファイルを生成する  
  - 述語がwdt:P31(分類/instance-of)のもの
  - 述語がwdt:P279(上位クラス/sub-class-of)のもの
  - 述語がrdfs:label(ラベル)のもの
  - 述語がskos:altLabel(別名)のもの  
  - 述語がschema:description(概要説明)のもの  

を入力ファイルとして，「処理用のインデックス」 を作成する．  
### 実行方法
\>java java jp.ac.osakac.wdht.HugeGraphFileDBRegister \<設定ファイル\>  
※必要の応じてクラスパス(-cp)の設定を行う．

例）  
\>java jp.ac.osakac.wdht.HugeGraphFileDBRegister settings/indexingSetting.txt  



## 2. 入力したWikidataのエンティティを起点した「クラス階層の抽出」  
生成したインデックスファイルから，クラス階層を抽出する．抽出方法は以下の2通りに対応している．
  1. 起点とするエンティティを指定してクラス階層を抽出する．  
  設定ファイルの例）extractSetting_root-ex.txt  
  2. サブクラスとなるエンティティを指定し，それらに共通している上位クラス(=共通上位)を取得し，それらを起点とするクラス階層を抽出する．  
  設定ファイルの例）extractSetting_subclasses-ex.txt  
     
### 実行方法
\>java java jp.ac.osakac.wdht.HugeDBHierarchyFile \<設定ファイル\>  
※必要の応じてクラスパス(-cp)の設定を行う．

例）  
\>java jp.ac.osakac.wdht.HugeDBHierarchyFile settings/extractSetting_root-ex.txt  

