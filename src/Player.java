public class Player {
	String name;
	int xpos;
	int ypos;
	int point;
	String direction;

	Player(String name, int xpos, int ypos, String direction) {
		this.name = name;
		this.xpos = xpos;
		this.ypos = ypos;
		this.direction = direction;
		this.point = 0;
	}

	int getXpos() {
		return xpos;
	}
	void setXpos(int xpos) {
		this.xpos = xpos;
	}
	int getYpos() {
		return ypos;
	}
	void setYpos(int ypos) {
		this.ypos = ypos;
	}
	String getDirection() {
		return direction;
	}
	void setDirection(String direction) {
		this.direction = direction;
	}
	void addPoints(int p) {
		point+=p;
	}
	void setPoint(int p) {point = p;}
	public String toString() {
		return name+":   "+point;
	}

	void setName(String name) {
		this.name = name;
	}
}