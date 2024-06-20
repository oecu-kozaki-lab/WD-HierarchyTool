set START=%time%

java -cp WD-HierarchyTool.jar jp.ac.osakac.wdht.HugeGraphFileDBRegister loadWDdb_settings_test.txt

echo START %START%
echo END %time%
pause
