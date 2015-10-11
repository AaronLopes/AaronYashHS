

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

public class SatVocabGame {

	public static void main(String[] args) {
		MyGUI gui = new MyGUI();
		gui.setUpBoard();
	}
}

class MyGUI extends MouseAdapter implements ActionListener  {
	
	JFrame window;
	MyDrawingPanel drawingPanel;
	int numRows;
	int numCols;
	JLabel time;
	Timer timer;
	int timeCounter;
	JLabel player1;
	JLabel player2;
	int[][] revealed;
	String[][] bottom;
	ArrayList<String> d;
	ArrayList<String> selected;
	int prevX;
	int prevY;	
	int playerTurn;
	int playerOneScore;
	int playerTwoScore;
	Timer delay;
	int xSquare, ySquare;
	boolean inProcess;
	boolean gameOver;
	
	public MyGUI(){

		window = new JFrame("Math Memory");
		window.setBounds(100, 100, 640, 700);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		drawingPanel = new MyDrawingPanel();
		drawingPanel.setBounds(20, 20, 600, 540);
		drawingPanel.setBorder(BorderFactory.createEtchedBorder());
		drawingPanel.addMouseListener(this);
		drawingPanel.addMouseMotionListener(this);
				
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		time = new JLabel("120", JLabel.CENTER);
		time.setBounds(120, 580, 100, 50);
		time.setBorder(BorderFactory.createTitledBorder("Time Remaining"));
		
		timer = new Timer(1000, this);
		timer.setActionCommand("Timer");
		
		player1 = new JLabel("0", JLabel.CENTER);
		player1.setBounds(240, 580, 100, 50);
		player1.setBorder(BorderFactory.createTitledBorder("Player 1 Score"));
		
		player2 = new JLabel("0", JLabel.CENTER);
		player2.setBounds(360, 580, 100, 50);
		player2.setBorder(BorderFactory.createTitledBorder("Player 2 Score"));

		JMenuBar bar = new JMenuBar();
		
		JMenu game = new JMenu("Game");
		JMenu help = new JMenu("Help");
		
		JMenuItem newGame = new JMenuItem("New Game");
		JMenuItem exit = new JMenuItem("Exit");
		JMenuItem howToPlay = new JMenuItem("How to Play");
		
		newGame.addActionListener(this);
		exit.addActionListener(this);
		howToPlay.addActionListener(this);

		game.add(newGame);
		game.add(exit);
		help.add(howToPlay);
		
		bar.add(game);
		bar.add(help);
		
		window.setJMenuBar(bar);

		mainPanel.add(drawingPanel);
		mainPanel.add(time);
		mainPanel.add(player1);
		mainPanel.add(player2);
		
		window.getContentPane().add(mainPanel);
		
		window.setVisible(true);
	}
	
	// Class that draws the main part of the user interface
	
	private class MyDrawingPanel extends JPanel {
		
		public void paintComponent(Graphics g){
			
			for(int r = 0; r < this.getWidth(); r += 100){
				for(int c = 0; c < this.getHeight(); c += 90){
					
					if(revealed[r / 100][c / 90] == 0){
						g.setColor(Color.darkGray);
						g.fillRect(r, c, 100, 90);
					} else {
						
						g.setColor(Color.white);
						g.fillRect(r, c, 100, 90);
						g.setColor(Color.black);
						Font f = new Font (Font.SERIF, Font.PLAIN, 12);
						g.setFont(f);
						
						if(bottom[r / 100][c / 90].contains("*")){
							g.drawString(bottom[r / 100][c / 90], r + 10, c + 60);
						} else {
							g.drawString(bottom[r / 100][c / 90], r + 10, c + 60);
						}
					}
				}
			}
			
			g.setColor(Color.black);
			for (int x = 0; x < this.getWidth(); x += 100)
				g.drawLine(x, 0, x, this.getHeight());

			for (int y = 0; y < this.getHeight(); y += 90)
				g.drawLine(0, y, this.getWidth(), y);
		}
	}
	
	// Method is invoked when some kind of action is performed and responds based on the action
	
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("New Game")){
			timer.stop();
			time.setText("120");
			timeCounter = 0;
			player1.setText("0");
			player2.setText("0");
			setUpBoard();
			drawingPanel.repaint();
			
		} else if (arg0.getActionCommand().equals("Exit")){
			window.dispose();
			
		} else if (arg0.getActionCommand().equals("Timer")){
			timeCounter++;
			time.setText((120 - timeCounter) + "");
			
			if(time.getText().equals("0")){
				gameOver = true;
				gameOver();
			}
			
		} else if (arg0.getActionCommand().equals("Delay")){
			revealed[xSquare][ySquare] = 0;
			revealed[prevX][prevY] = 0;
			playerTurn++;
			selected.clear();
			drawingPanel.repaint();
			inProcess = false;
			delay.stop();
		}  else if (arg0.getActionCommand().equals("How to Play")){
			
			JEditorPane helpContent = null;
			ClassLoader cl = this.getClass().getClassLoader();
			
			try {
				helpContent = new JEditorPane(cl.getResource("help.html"));
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			
			JScrollPane helpPane = new JScrollPane(helpContent);
			JOptionPane.showMessageDialog(null, helpPane, "How To Play", JOptionPane.PLAIN_MESSAGE, null);
			
		} 
	}
	
	// Method called on mouse click and responds based on what is clicked
	
	public void mouseClicked(MouseEvent e){
		
		if(e.getButton() == 1 && !inProcess && !gameOver){
			
			timer.start();
			
			if(revealed[e.getX() / 100][e.getY() / 90] == 0){
				xSquare = e.getX() / 100;
				ySquare = e.getY() / 90;
			}
			
			
			if(selected.size() < 2){
				selected.add(bottom[xSquare][ySquare]);
				revealed[xSquare][ySquare] = 1;
				drawingPanel.repaint();
			}
			
			if(selected.size() == 1){
				prevX = xSquare;
				prevY = ySquare;
			}
			
			if(selected.size() == 2 && !checkForMatch()){
				
				delay = new Timer(2000, this);
				delay.setInitialDelay(2000);
				delay.setActionCommand("Delay");
				inProcess = true;
				delay.start();
				
			} else if (selected.size() == 2 && checkForMatch() && playerTurn % 2 == 1){
				playerOneScore += 10;
				selected.clear();
				player1.setText(playerOneScore + "");
				playerTurn++;
			} else if (selected.size() == 2 && checkForMatch() && playerTurn % 2 == 0){
				playerTwoScore += 10;
				selected.clear();
				player2.setText(playerTwoScore + "");
				playerTurn++;
			}
			
			gameOver = true;
			
			for(int r = 0; r < revealed.length; r++){
				for(int c = 0; c < revealed[0].length; c++){
					if(revealed[r][c] == 0){
						gameOver = false;
					}
				}
			}
			
			if(gameOver){
				gameOver();
			}
			
		}
		
	}
	
	// Helper method to set up game board
	
	public void setUpBoard(){
		
		selected = new ArrayList<String>();
		playerTurn = 1;
		revealed = new int[6][6];
		d = new ArrayList<String>();
		playerOneScore = 0;
		playerTwoScore = 0;
		inProcess = false;
		gameOver = false;
		
		for(int r = 0; r < revealed.length; r++){
			for(int c = 0; c < revealed[0].length; c++){
				revealed[r][c] = 0;
			}
		}
		
		bottom = new String[6][6];
		
		d.add("*abbreviate");
		d.add("to shorten");
		d.add("*abstinence");
		d.add("refraining from");
		d.add("*adversity");
		d.add("misfortune");
		d.add("*anecdote");
		d.add("personal account");
		d.add("*antagonist");
		d.add("adversary");
		d.add("*collaborate");
		d.add("work together");
		d.add("*conditional");
		d.add("separating");
		d.add("dependent on");
		d.add("*divergent");
		d.add("*deleterious");
		d.add("scorn");
		d.add("harmful");
		d.add("*disdain");
		d.add("*digression");
		d.add("hard-working");
		d.add("straying from");
		d.add("*diligent");
		
		d.add("*provocative");
		d.add("provoke");
		d.add("*rancorous");
		d.add("bitter");
		d.add("*reconciliation");
		d.add("agreement");
		d.add("*renovation");
		d.add("repair");
		d.add("*restrained");
		d.add("controlled");
		d.add("*reverence");
		d.add("worship");
		

		
			
		for(int r = 0; r < bottom.length; r++){
			for(int c = 0; c < bottom[0].length; c++){
				
				Random rand = new Random();
				int a = rand.nextInt(d.size());
				
				bottom[r][c] = d.remove(a);
			}
		}
			
		//printArray(bottom);

	}
	
	// Helper method to print array for debugging purposes
	
	public void printArray (String[][] arr){
		
		for(int row = 0; row < arr.length; row++){
			for(int col = 0; col < arr[0].length; col++){
				System.out.print(arr[col][row] + " ");
			}
			
			System.out.println();
		}
		
		System.out.println();
	}
	
	// Helper method to check if two tiles are matches
	
	public boolean checkForMatch(){
		
		String s1 = selected.get(0);
		String s2 = selected.get(1);
		
		int a, b, c;
		
		if((!s1.contains("*") && !s2.contains("*")) || (s1.contains("*") && s2.contains("*"))){
			return false;
		}
		
		if(s1.contains("*")){
		    String eeerr= getD(s1);
			if (s2.equals(eeerr)){
			    return true;
			 }else{
			     return false;
			 }
		} else {
			String eeerr= getD(s2);
			if (s1.equals(eeerr)){
			    return true;
			 }else{
			     return false;
			 }
		}
		
		
		//return false;
	}
	public String getD(String x){
	    if (x.equals("*abbreviate")){
	      return  "to shorten"  ; 
	    }
	    if (x.equals("*abstinence")){
	      return  "refraining from"  ; 
	    }
	    if (x.equals("*adversity")){
	      return  "misfortune"  ; 
	    }
	    if (x.equals("*anecdote")){
	      return   "personal account" ; 
	    }
	    if (x.equals("*antagonist")){
	      return  "adversary"  ; 
	    }
	    if (x.equals("*collaborate")){
	      return   "work together" ; 
	    }
	    if (x.equals("*conditional")){
	      return   "dependent on" ; 
	    }
	    if (x.equals("*divergent")){
	      return  "separating"  ; 
	    }
	    if (x.equals("*deleterious")){
	      return  "harmful"  ; 
	    }
	    if (x.equals("*disdain")){
	      return  "scorn"  ; 
	    }
	    if (x.equals("*digression")){
	      return    "straying from"; 
	    }
	    if (x.equals("*diligent")){
	      return  "hard-working"  ; 
	    }
	    if (x.equals("*provocative")){
	      return  "provoke"  ; 
	    }
	    if (x.equals("*rancorous")){
	      return   "bitter" ; 
	    }
	    if (x.equals("*reconciliation")){
	      return   "agreement" ; 
	    }
	    if (x.equals("*renovation")){
	      return   "repair" ; 
	    }
	    if (x.equals("*restrained")){
	      return   "controlled" ; 
	    }
	    if (x.equals("*reverence")){
	      return   "worship" ; 
	    }
	    return "deez";
	}
	// Helper method to deal with end of game
	
	public void gameOver(){
		JOptionPane.showMessageDialog(window, "Game Over! Click New game to restart");
		timer.stop();
	}

}
