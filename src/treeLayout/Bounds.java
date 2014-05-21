package treeLayout;

public class Bounds {

	private double x;
	private double y;
	private double width;
	private double height;
	
	public Bounds(double x, double y, double d, double e) {
		this.x = x;
		this.y = y;
		this.width = d;
		this.height = e;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	public void setBounds(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String toString() {
		return getX() + " " + getY() + " " + getWidth() + " " + getHeight();
	}
}
