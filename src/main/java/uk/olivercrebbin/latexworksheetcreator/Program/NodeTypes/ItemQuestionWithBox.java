package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

public class ItemQuestionWithBox extends QuestionWithBox{
    public ItemQuestionWithBox(){
        super();
        startCommand = "\\item " + startCommand;
        nodeType = "ItemQuestionWithBox";
    }
}
