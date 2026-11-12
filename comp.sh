 jpackage \
--name "Latex Worksheet Creator" \
--input out/artifacts/LatexWorksheetCreator_jar \
--main-jar LatexWorksheetCreator.jar \
--type app-image \
--vendor "uk.olivercrebbin" \
--dest dist/ \
--icon "icon.icns" \
--file-associations fa.properties \
--java-options "-Dapple.laf.useScreenMenuBar=true"
