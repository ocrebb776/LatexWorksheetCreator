package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

import uk.olivercrebbin.latexworksheetcreator.Program.LaTexNode;
import uk.olivercrebbin.latexworksheetcreator.Program.Project;

public class Image extends LaTexNode {
    public Image() {
        super("", "", "Image");
        setQuantity("Relative Width","0.5","number");
        setQuantity("Image Reference","","IMAGE_REFERENCE");
        setQuantity("Center","false","boolean");
    }

    @Override
    public String customString(Project project) {
        if(project.getImage(getQuantityValue("Image Reference")) == null) {
            System.out.println("ERROR");
            return "";
        }
        else{
            String text = "\\includegraphics[width=" + getQuantityValue("Relative Width") + "\\textwidth]{" + getQuantityValue("Image Reference") + ".png}";
            if (getQuantityValue("Center").equals("true")) text = "\\begin{center} " + text + "\\end{center}";
            return text;
        }
    }
}
