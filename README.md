# WD-HierarchyTool
Java library for manipulating the Wikidata class hierarchy using local indexfiles.

# Overview
## 1. Creating "Index Files" from Wikidata RDF dumps (N-Triple format)　　
Generate index files from Wikidata RDF dumps (N-Triple format) to manipulate the Wikidata hierarchy on a local PC with the following data as input. 
  - N-Triples with the predicate wdt:P31(instance-of)
  - N-Triples with the predicate wdt:P279(sub-class-of)
  - N-Triples with the predicate rdfs:label
  - N-Triples with the predicate skos:altLabel  
  - N-Triples with the predicate schema:description  

### How to run the program
\>java java jp.ac.osakac.wdht.HugeGraphFileDBRegister \<setting file\>  

example)  
\>java jp.ac.osakac.wdht.HugeGraphFileDBRegister settings/indexingSetting.txt  
*Set the classpath (-cp) as necessary.

## 2. Extraction of "the class hierarchy" starting from the input Wikidata entity  
Extract the class hierarchy from the generated index file. The following two extraction methods are supported.
  1. Extracts the class hierarchy by specifying the entity as the root(starting point).  
  example of the setting file) extractSetting_root-ex.txt  
  2. Specify entities that are subclasses, obtain the superclasses that they have in common, and extract the class hierarchy starting from them as root.  
  example of the setting file) extractSetting_subclasses-ex.txt  
     
### How to run the program
\>java java jp.ac.osakac.wdht.HugeDBHierarchyFile \<setting file\>  
*Set the classpath (-cp) as necessary.

example)  
\>java jp.ac.osakac.wdht.HugeDBHierarchyFile settings/extractSetting_root-ex.txt  
