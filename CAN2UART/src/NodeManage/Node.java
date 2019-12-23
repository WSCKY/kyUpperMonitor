package NodeManage;

public class Node {
	public int id;
	public float fval1;
	public float fval2;
	public int rate = 0;
	private int counter = 0;

	public Node(int id) {
		this.id = id;
	}

	public Node(int id, float f1, float f2) {
		this.id = id;
		this.fval1 = f1;
		this.fval2 = f2;
	}

	public Node(Node n) {
		this.id = n.id;
		this.fval1 = n.fval1;
		this.fval2 = n.fval2;
		this.counter = 1;
	}

	public void update(float f1, float f2) {
		fval1 = f1;
		fval2 = f2;
		counter ++;
	}

	public void compRate() {
		this.rate = counter;
		counter = 0;
	}
}
