set START=%time%

java -cp WD-HierarchyTool.jar jp.ac.osakac.wdht.HugeGraphFileDBRegister settings/loadWDdb_settings_all.txt
echo START %START%
echo END %time%
pause
