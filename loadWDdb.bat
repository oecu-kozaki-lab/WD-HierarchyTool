set START=%time%

java -cp lib\HugeGraphFile.jar jp.ac.osakac.lgdb.HugeGraphRegister loadWDdb_settings_test.txt

echo START %START%
echo END %time%
pause
