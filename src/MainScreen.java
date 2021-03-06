import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainScreen extends JFrame implements ActionListener {
    //Confirmation
    Object[] options = {"YES", "NO"};
    Object[] options2 = {"CONTINUE", "WALKAWAY"};
    int confirmation, decision;

    // objects for GUI
    private BufferedImage mainScreen, toolBar;
    private JPanel mainMenu = new JPanel();
    private ScorePanel moneyTree = new ScorePanel();
    private JButton startBtn = new JButton("Start");
    private JButton instructionsBtn = new JButton("Instructions");
    private JButton playAgainBtn = new JButton("Play Again");
    private JButton quitBtn = new JButton("Quit");

    //Menus
    private JMenuBar menuBar = new JMenuBar();
    private JMenu GameMENU = new JMenu("Game");
    private JMenuItem newGameITEM = new JMenuItem("New Game");
    private JMenu instructionsMENU = new JMenu("Information");
    private JMenuItem instructionsITEM = new JMenuItem("Instructions");

    // objects for gameplay and confirmations
    private ArrayList<Question> questions;
    private Gameplay currentGameplay;
    private boolean fiftyFiftyUsed1 = false;
    private boolean fiftyFiftyUsed2 = false;
    private boolean audiencePollUsed1 = false;
    private boolean audiencePollUsed2 = false;

    private Sound introMUSIC;
    private Sound gameMUSIC;
    private Sound winMUSIC;
    private Sound loseMUSIC;
    private Sound finalMUSIC;

    //
    private boolean answered;


    public MainScreen() {

        //Sound
        introMUSIC = new Sound("sound/intro.wav");
        introMUSIC.start();

        gameMUSIC = new Sound("sound/game_sound.wav");
        finalMUSIC = new Sound("sound/final_victory.wav");

        //Menus
        menuBar.add(GameMENU);
        menuBar.add(instructionsMENU);
        GameMENU.add(newGameITEM);
        instructionsMENU.add(instructionsITEM);
        newGameITEM.addActionListener(this);
        instructionsITEM.addActionListener(this);
        this.setJMenuBar(menuBar);
        // read from file
        URL url = getClass().getClassLoader().getResource("data/questions.xml"); // create url object
        questions = Question.readQuestionsFromFile(url);

        // SETUP GUI
        setTitle("Who Wants to Be a Millionaire?");
        setSize(908, 658);
        setDefaultLookAndFeelDecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);


        try { //Imports image for the screen
            mainScreen = ImageIO.read(this.getClass().getResource("images/main_screen.jpg"));
            toolBar = ImageIO.read(this.getClass().getResource("images/icon.jpg"));
        } catch (IOException e) {
        }

        this.setIconImage(toolBar);


        // add ActionListeners
        quitBtn.addActionListener(this);
        startBtn.addActionListener(this);
        instructionsBtn.addActionListener(this);

        //customize buttons

        startBtn.setBackground(Color.WHITE);
        instructionsBtn.setBackground(Color.WHITE);
        quitBtn.setBackground(Color.WHITE);
        playAgainBtn.setBackground(Color.WHITE);

        // set up title image
        JLabel title = new JLabel(new ImageIcon(mainScreen));
        title.setPreferredSize(new Dimension(530, 600));
        mainMenu.add(title, BorderLayout.WEST);
        mainMenu.setBackground(Color.WHITE);

        startBtn.setPreferredSize(new Dimension(100, 50));
        instructionsBtn.setPreferredSize(new Dimension(125, 50));
        quitBtn.setPreferredSize(new Dimension(100, 50));


        // populate JPanel
        mainMenu.add(startBtn);
        mainMenu.add(instructionsBtn);
        mainMenu.add(quitBtn);

        this.setContentPane(mainMenu);
    }


    protected void nextScreen() {
        // if there are no more questions, end game
        if (questions.size() == 0) {
            dispose();
            return;
        }

        int qInd, ceil, floor;
        Question currentQuestion = null;
        do {
            qInd = (int) (Math.random() * questions.size()); // generate random index
            // create min&max difficulties based on question number
            ceil = (int) ((float) moneyTree.getIndex() * 10 / 15) + 4;
            floor = ceil - 5;
            // if difficulty within range
            if (questions.get(qInd).getDifficulty() >= floor && questions.get(qInd).getDifficulty() <= ceil)
                currentQuestion = questions.remove(qInd); // set current question
        } while (currentQuestion == null); // continue until suitable question is found

        // create gameplay object
        currentGameplay = new Gameplay(currentQuestion, this, fiftyFiftyUsed1, fiftyFiftyUsed2, audiencePollUsed1, audiencePollUsed2);

        // manage layout of screen
        setContentPane(currentGameplay);
        add(moneyTree);
        Insets insets = getInsets();
        moneyTree.setBounds(899 + insets.left, 1, 250, 650); // place money tree
        pack();
        setSize(900 + 250, 675); //dimensions needed for the template picture of the questions/answers/money-tree
    }

    protected void displayEndScreen(boolean wrong) { //end screen message once the user wins or loses
        // create JComponents
        int finalScore = (wrong) ? moneyTree.getCheckpointScore() : moneyTree.getScore();
        JPanel end = new JPanel();
        JLabel message = new JLabel("Game Over! You Have Earned ");
        JLabel money = new JLabel(String.format("$%d", finalScore));

        // format
        end.setBackground(Color.BLACK);
        message.setFont(new Font("Arial", Font.ROMAN_BASELINE, 40));
        money.setFont(new Font("Arial", Font.ROMAN_BASELINE, 110));
        money.setForeground(Color.WHITE);
        money.setHorizontalAlignment(SwingConstants.CENTER);
        message.setForeground(Color.WHITE);
        money.setForeground(Color.YELLOW);

        end.setLayout(null);


        //adds buttons to the screen
        end.add(message);
        end.add(money);
        end.add(playAgainBtn);
        end.add(quitBtn);

        Insets insets = this.getInsets();

        message.setBounds(175 + insets.left, 1 + insets.top, 600, 50);
        money.setBounds(220 + insets.left, 190 + insets.top, 500, 200);
        playAgainBtn.setBounds(265 + insets.left, 450 + insets.top, 100, 50);
        quitBtn.setBounds(550 + insets.left, 450 + insets.top, 100, 50);

        // add ActionListeners
        playAgainBtn.addActionListener(this);
        quitBtn.addActionListener(this);

        // display
        setContentPane(end);
        this.setSize(950, 675);
    }

    protected void reset() { //resets the score and screen
        moneyTree.resetScore(); // reset score
        mainMenu.add(quitBtn); // re-add button to mainMenu
        setContentPane(mainMenu); // return to main menu
        URL url = getClass().getClassLoader().getResource("data/questions.xml"); // create url object
        questions = Question.readQuestionsFromFile(url); // reload questions
        this.setSize(908, 675);

        //resets all lifelines
        fiftyFiftyUsed1 = false;
        fiftyFiftyUsed2 = false;
        audiencePollUsed1 = false;
        audiencePollUsed2 = false;
    }

    public void lifeLinePressed() //which life line has been used
    {
        //Checks which lifeline has been used and calls the specfic method and repaints the screen to display it being used
        if (currentGameplay.fiftyFiftyOnePressed()) {
            fiftyFiftyUsed1 = true;
            currentGameplay.fiftyFiftyLifeline(1);
            currentGameplay.repaint();
        }
        if (currentGameplay.fiftyFiftyTwoPressed()) {
            fiftyFiftyUsed2 = true;
            currentGameplay.fiftyFiftyLifeline(2);
            currentGameplay.repaint();
        }
        if (currentGameplay.audiencePollOnePressed()) {
            audiencePollUsed1 = true;
            currentGameplay.audiencePollLifeLine(1);
            currentGameplay.repaint();
        }
        if (currentGameplay.audiencePollTwoPressed()) {
            audiencePollUsed2 = true;
            currentGameplay.audiencePollLifeLine(2);
            currentGameplay.repaint();
        }
    }


    protected void showInstructions() {

        try {
            // create JComponents
            JPanel p = new JPanel();
            URL u = getClass().getClassLoader().getResource("data/instructions.html"); // get URL of instructions file
            JEditorPane ep = new JEditorPane(u); // read instructions from file
            ep.setSize(908, 600);
            p.add(ep);
            p.setBackground(Color.WHITE);
            setContentPane(p);
            setSize(908, 675);
        } catch (IOException e) {

        }

    }

    // For ActionListener interface
    public void actionPerformed(ActionEvent e) { //most important method of the game
        if (e.getSource().equals(quitBtn)) { // quit button
            {
                dispose();
                gameMUSIC.stop();
                finalMUSIC.stop();
            }
        } else if (e.getSource().equals(startBtn)) { // start button
            nextScreen();
            introMUSIC.stop();
            gameMUSIC.start();
            gameMUSIC.loop(100);
        } else if (e.getSource().equals(instructionsBtn) || e.getSource().equals(instructionsITEM))
            showInstructions();
        else if (e.getSource().equals(newGameITEM) || e.getSource().equals(playAgainBtn)) {
            reset();
        } else {
            if (!currentGameplay.isAnswered()) { // if something else is pressed

                lifeLinePressed();

            } else if (currentGameplay.isAnswered()) { //else if an answer has been selected

                if (currentGameplay.isCorrect()) { // if correct

                    winMUSIC = new Sound("sound/win.wav");
                    winMUSIC.start();

                    moneyTree.incrementScore(); // increase score
                    moneyTree.repaint();

                    //decision pop up window
                    decision = JOptionPane.showOptionDialog(this, "CONGRATULATIONS! YOU HAVE WON $" +
                            moneyTree.getScore() + "\n\n   Walk away with $" +
                            moneyTree.getScore() + " or continue?", "CONGRATULATIONS!",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options2, options2[1]);

                    if (decision == JOptionPane.YES_OPTION) { // if continue with game

                        winMUSIC.stop();
                        nextScreen(); // display next question


                        if (moneyTree.getScore() == 1000000) //if user wins
                        {
                            finalMUSIC.start();

                            displayEndScreen(false);
                        }

                    } else {
                        displayEndScreen(false);
                    }

                } else { // if incorrect

                    loseMUSIC = new Sound("sound/lose.wav");
                    loseMUSIC.start();

                    displayEndScreen(true);
                }

            }
        }
    }
}


