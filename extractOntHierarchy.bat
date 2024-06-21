set START=%time%

java -cp WD-HierarchyTool.jar jp.ac.osakac.wdht.HugeDBHierarchyFile settings/extractOntHierarchy_test.txt

echo START %START%
echo END %time%
pause
