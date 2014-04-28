import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileInputStream;
import java.util.*;

public class Question
{
    protected String prompt;
    protected ArrayList < String > answers;
    protected int correct;

    /**
     * create a Question object
     * @param prompt
     * @param answers
     * @param correct
     */
    public Question (String prompt, ArrayList < String > answers, int correct)
    {

	this.prompt = prompt;

	// randomize answer order
	String correctAns = answers.get (correct);
	Collections.shuffle (answers);
	this.answers = answers;

	for (int i = 0 ; i < 4 ; i++) // find the correct answer
	    if (answers.get (i) == correctAns)
		this.correct = i;
    }


    /**
     * accessor for prompt
     * @return prompt
     */
    public String getPrompt ()
    {
	return prompt;
    }


    /**
     * accessor for answers
     * @return four answers including the correct one in random order
     */
    public ArrayList < String > getAnswers ()
    {
	return answers;
    }


    /**
     *
     * @return
     */
    public int getCorrect ()
    {
	return correct;
    }


    /**
     * returns true if correct answer is given
     * @param x answer
     * @return if correct
     */
    public boolean isCorrect (int x)
    {
	return x == correct;
    }


    /**
     * Read from file
     * @param str
     * @return
     */
    public static ArrayList < Question > readQuestionsFromFile (String str)
    {
        // create ArrayList of questions
        ArrayList < Question > ret = new ArrayList < Question > ();

        try
        {
            // create Document object
            SAXReader reader = new SAXReader ();
            Document document = reader.read (new FileInputStream("/home/andrew/WWTBAM/data/" + str));

            // read data
            Element root = document.getRootElement ();
            List questions = root.selectNodes ("/questions/question"); // get list of questions

            // iterate through questions
            for (Iterator q = questions.iterator () ; q.hasNext () ;)
            {
                Element question = (Element) q.next ();

                // get prompt
                String prompt = ((Element) question.selectNodes ("prompt").get (0)).getText ();

                // get answers
                ArrayList < String > answers = new ArrayList < String > ();
                int correct = 0;
                for (Iterator a = question.selectNodes ("answer").iterator () ; a.hasNext () ;)
                {
                    Element answer = (Element) a.next ();
                    answers.add (answer.getText ());

                    // get correct
                    if (answer.attribute ("correct") != null)
                        correct = answers.size () - 1;
                }

                // create question object
                ret.add (new Question (prompt, answers, correct));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
        return ret;
    }
}
