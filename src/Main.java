import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class Main extends Application {
	//GUI dimensions
	private static final int size = 20;
	private static final int scene_height = size * 20 + 100;
	private static final int scene_width = size * 20 + 200;
	private static Label[][] fields;
	static TextArea scoreList;
	private static Scene scene;

	//Media references
	private static Image image_floor;
	private static Image hero_right,hero_left,hero_up,hero_down;

	//Player variables
	static Player me;
	static Player opponent;
	private static List<Player> players = new ArrayList<>();
	private static String[] participants = {"10.24.68.98", "10.24.4.92", "10.24.2.197"};

	//Threads
	private static Requester connectionRequester;

	//Variables for ME, Ricart-Agrawala
	//Constant
	public static final int My_Unique_Number = 0;
	//Integers
	public static int Our_Sequence_Number = 0;
	public static int Outstanding_Reply_Count;
	//Booleans
	public static boolean Requesting_Critical_Section = false;
	public static boolean[] Reply_Deferred = new boolean[2];


	//Board for gameplay initiation
	private static String[] board = {    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	};

	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			//GUI setup
			primaryStage.setTitle("Vores Spil");
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	
			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();
			
			GridPane boardGrid = new GridPane();

			Image image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':					
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);
			
			
			grid.add(mazeLabel,  0, 0); 
			grid.add(scoreLabel, 1, 0); 
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);
						

			scene = new Scene(grid,scene_width,scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

            // Setting up standard players


			fields[me.getXpos()][me.getYpos()].setGraphic(new ImageView(hero_up));

			fields[opponent.getXpos()][opponent.getYpos()].setGraphic(new ImageView(hero_up));

			scoreList.setText(getScoreList());

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	static void playerMoved(Player player, int delta_x, int delta_y, String direction) {
		if (player.equals(me)) {
			try {
				Semaphore Shared_vars = new Semaphore(1);
				Shared_vars.acquire();
				//Sets the state & increaments the clock
				Requesting_Critical_Section = true;
				Our_Sequence_Number += 1;
				Shared_vars.release();
				Outstanding_Reply_Count = 1;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Sends a REQUEST message to opponent
			connectionRequester.addMessageToOutgoingQueue("REQUEST " + Our_Sequence_Number + " " + My_Unique_Number);

			while (Outstanding_Reply_Count > 0);
		}

		//Critical Section
		player.direction = direction;
		int x = player.getXpos(),y = player.getYpos();

		if (board[y+delta_y].charAt(x+delta_x)=='w') {
			player.addPoints(-1);
			connectionRequester.addMessageToOutgoingQueue("POINT " + player.name + " " + (player.point - 1));
		} 
		else {
			Player p = getPlayerAt(x+delta_x,y+delta_y);
			if (p!=null) {
              player.addPoints(10);
              p.addPoints(-10);
              if (player.equals(me)) {
				  connectionRequester.addMessageToOutgoingQueue("POINT " + player.name + " " + (player.point + 10));
				  connectionRequester.addMessageToOutgoingQueue("POINT " + p.name + " " + (p.point - 10));
			  }
			} else {
				player.addPoints(1);
			
				fields[x][y].setGraphic(new ImageView(image_floor));
				x+=delta_x;
				y+=delta_y;

				if (direction.equals("right")) {
					fields[x][y].setGraphic(new ImageView(hero_right));
				}
				if (direction.equals("left")) {
					fields[x][y].setGraphic(new ImageView(hero_left));
				}
				if (direction.equals("up")) {
					fields[x][y].setGraphic(new ImageView(hero_up));
				}
				if (direction.equals("down")) {
					fields[x][y].setGraphic(new ImageView(hero_down));
				}

				player.setXpos(x);
				player.setYpos(y);
				if (player.equals(me)) {
					connectionRequester.addMessageToOutgoingQueue("MOVE " + player.getXpos() + " " + player.getYpos() + " " + player.getDirection());
					connectionRequester.addMessageToOutgoingQueue("POINT " + player.name + " " + player.point);
				}
			}
		}
		scoreList.setText(getScoreList());
		//Critical Section
		if (player.equals(me)) {
			Requesting_Critical_Section = false;
			if (Reply_Deferred[1]) {
				Reply_Deferred[1] = false;
				//Sends a REPLY message, if opponent is requesting access to the critical section
				connectionRequester.addMessageToOutgoingQueue("REPLY 1");
			}
		}
	}

	static String getScoreList() {
		StringBuilder b = new StringBuilder(100);
		for (Player p : players) {
			b.append(p).append("\r\n");
		}
		return b.toString();
	}

	private static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}

	static void connectedAction() {
		//Player controls
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case UP:    playerMoved(me, 0,-1,"up");    break;
				case DOWN:  playerMoved(me, 0,+1,"down");  break;
				case LEFT:  playerMoved(me, -1,0,"left");  break;
				case RIGHT: playerMoved(me, +1,0,"right"); break;
				default: break;
			}
		});


	}

	public static void main(String[] args) {
		me = new Player("Tomas",9,4,"up");
		players.add(me);
		opponent = new Player("Opponent", 14, 15, "up");
		players.add(opponent);
		try {
			connectionRequester = new Requester(participants[0]);
			Receiver connectionReceiver = new Receiver(new ServerSocket(6061), connectionRequester);
			connectionRequester.start();
			connectionReceiver.start();
			launch(args);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

