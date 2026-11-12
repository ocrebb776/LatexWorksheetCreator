package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

public class ItemQuestionWithLines extends QuestionWithLines{
    public ItemQuestionWithLines(){
        super();
        startCommand = "\\item "+startCommand;
        nodeType  ="ItemQuestionWithLines";
    }
}
