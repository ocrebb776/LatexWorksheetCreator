package uk.olivercrebbin.latexworksheetcreator.GUIController;

import uk.olivercrebbin.latexworksheetcreator.Program.LaTexNode;
import uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes.*;

import java.lang.invoke.SerializedLambda;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DisplayHelper {
    public static String getDisplayNameOfNode(LaTexNode node){
        String name=  node.getNodeType();
        NodeTypes n = getNodeType(name);
        if(n!=null)
            return n.toString();
        else
            return "Node Type Not Found:"+name ;
    }
    public static boolean haveChild(LaTexNode node){
        String name=  node.getNodeType();
        NodeTypes n = getNodeType(name);
        if(n!=null)
        return n.haveChild;
    else return false;
    }
    private static NodeTypes getNodeType(String name){
        return Arrays.stream(NodeTypes.values()).filter(x->x.name().equals(name)).findAny().orElse(null);
    }

    public static List<NodeTypes> possibleChild(LaTexNode node){
        return Arrays.stream(
                NodeTypes.values()
        )
                .filter(x->x.possibleChild(node.getNodeType()))
                .collect(Collectors.toList());
    }


    public enum NodeTypes{
        Enumerate("Ordered List",true ,()-> new Enumerate(), (Arrays.stream(new String[]{"ListItem","Section","SectionStar","SubSection","SubSectionStar","SubSubSection","SubSubSectionStar","MainbDocument"}).toList())),
        Itemize("Unordered List",true,()-> new Itemize(),(Arrays.stream(new String[]{"ListItem","Section","SectionStar","SubSection","SubSectionStar","SubSubSection","SubSubSectionStar","MainbDocument"}).toList())),
        ListItem("ListItem",true,()-> new ListItem(),(Arrays.stream(new String[]{"Enumerate","Itemize"}).toList())),
        QuestionWithBox("Box Question",false,()-> new QuestionWithBox(),(Arrays.stream(new String[]{"ListItem","Section","SectionStar","SubSection","SubSectionStar","SubSubSection","SubSubSectionStar","MainbDocument"}).toList())),
        QuestionWithLines("Lined Question",false,()->new QuestionWithLines(),(Arrays.stream(new String[]{"ListItem","Section","SectionStar","SubSection","SubSectionStar","SubSubSection","SubSubSectionStar","MainbDocument"}).toList())),
        Section("(1) Numbered Section",true,()->new Section(),(Arrays.stream(new String[]{"MainbDocument"}).toList())),
        SectionStar("(1) Section",true,()->new SectionStar(),(Arrays.stream(new String[]{"MainbDocument"}).toList())),
        Text("LaTex text",false,()->new Text(),(Arrays.stream(new String[]{"ListItem","Section","SectionStar","SubSection","SubSectionStar","SubSubSection","SubSubSectionStar","MainbDocument"}).toList())),
        ItemText("Text",false,()-> new ItemText(),(Arrays.stream(new String[]{"Enumerate","Itemize"}).toList())),
        ItemQuestionWithBox("Box Question",false,()-> new ItemQuestionWithBox(),(Arrays.stream(new String[]{"Enumerate","Itemize"}).toList())),
        ItemQuestionWithLines("Lined Question",false,()-> new ItemQuestionWithLines(),(Arrays.stream(new String[]{"Enumerate","Itemize"}).toList())),
        SubSection("(2) Numbered Section",true,()-> new SubSection(),(Arrays.stream(new String[]{"Section","SectionStar"}).toList())),
        SubSectionStar("(2) Section",true,()-> new SubSection(),(Arrays.stream(new String[]{"Section","SectionStar"}).toList())),
        FullHeightEQ("EquationLine",false,()->new FullHeightEQ(),(Arrays.stream(new String[]{"ListItem","Section","SectionStar","SubSection","SubSectionStar","SubSubSection","SubSubSectionStar","MainbDocument"}).toList())),
        Image("Image",false,()->new Image(),(Arrays.stream(new String[]{"ListItem","Section","SectionStar","SubSection","SubSectionStar","SubSubSection","SubSubSectionStar","MainbDocument"}).toList())),

        SubSubSection("(3) Numbered Section",true,()-> new SubSubSection(),(Arrays.stream(new String[]{"SubSection","SubSectionStar"}).toList())),
        SubSubSectionStar("(3) Section",true,()-> new SubSubSectionStar(),(Arrays.stream(new String[]{"SubSection","SubSectionStar"}).toList())),
        EnumerateCont("Enumerate(resumer last)",true,()->new EnumerateCont(), (Arrays.stream(new String[]{"ListItem","Section","SectionStar","SubSection","SubSectionStar","SubSubSection","SubSubSectionStar","MainbDocument"}).toList()))
        ;
        String name;
        boolean haveChild;
        Supplier<LaTexNode> supplier;
        List<String> impossibleChild;

        NodeTypes(String s, boolean b, Supplier<LaTexNode> supplier,List<String> impossibleChild) {
            this.name = s;
            this.haveChild  =b;
            this.supplier = supplier;
            this.impossibleChild = impossibleChild;
        }

        @Override
        public String toString() {
            return name;
        }
        public LaTexNode getItem(){
            return supplier.get();
        }
        public boolean possibleChild(String s){
            if(impossibleChild.stream().anyMatch(x->x.equals(s))) return true;
            return false;
        }
    }
}
