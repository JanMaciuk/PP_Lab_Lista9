import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;


public class Main {
    public static void main(String[] args) {

        Scene scene = new Scene();
        scene.addItem(new Segment(new Point(10, 10), new Point(500, 10)));
        scene.addItem(new Rectangle(new Point(100, 300), true, 100, 100));
        scene.addItem(new Triangle(new Point(400, 400), new Point(500, 500), new Point(600, 400), false));
        scene.addItem(new Triangle(new Point(400, 400), new Point(500, 500), new Point(600, 400), true)); // Overwrites the previous triangle
        scene.drawBoundingBox(2);
        ArrayList<Item> balwanek = new ArrayList<>();
        balwanek.add(new TextItem(new Point(275, 415), "Bałwanek"));
        balwanek.add(new Circle(new Point(300, 300), false, 50));
        balwanek.add(new Circle(new Point(300, 415), false, 65));
        balwanek.add(new Circle(new Point(280, 290), true, 10));
        balwanek.add(new Circle(new Point(320, 290), true, 10));
        balwanek.add(new Segment(new Point(280, 320), new Point(320, 320)));
        scene.addItem(new ComplexItem(new Point(0, 0), balwanek));
        ArrayList<Point> customFigurePoints = new ArrayList<>();
        customFigurePoints.add(new Point(100, 0));
        customFigurePoints.add(new Point(179, 29));
        customFigurePoints.add(new Point(200, 100));
        customFigurePoints.add(new Point(179, 171));
        customFigurePoints.add(new Point(100, 200));
        customFigurePoints.add(new Point(29, 171));
        customFigurePoints.add(new Point(0, 100));
        customFigurePoints.add(new Point(29, 29));
        scene.addItem(new customFigure(new Point(100, 0), true, customFigurePoints));
        scene.drawBoundingBox(3);

        scene.items.get(3).translate(new Point(100, -200)); // move the bałwanek
        scene.items.get(0).translate(new Point(200, 10)); // move the line, it doesn't start on the left edge of the screen anymore

        // display the scene
        JFrame frame = new JFrame("Simple Shapes");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(scene);
        frame.setVisible(true);
    }
}



class Point {
    int x;
    int y;
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void translate(Point vector) {
        this.x += vector.x;
        this.y += vector.y;
    }
}

class Scene extends JPanel{
    final ArrayList<Item> items;

    void addItem(Item item) {
        // if the item is a singleton, remove the previous singleton
        if(item instanceof Singleton) {
            for (Item i : this.items) {
                if (i.getClass() == item.getClass()) {
                    this.items.remove(i);
                    break;
                }
            }
        }
        this.items.add(item);
    }

    Scene() {
        items = new ArrayList<>();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.items.isEmpty()) {items.add(new TextItem(new Point(350, 400), "No items to draw"));}

        for (Item item : this.items) {
            item.draw(g);
        }
    }
     void drawBoundingBox(int itemIndex) {
        if ( itemIndex < items.size() && itemIndex >= 0) {
            this.items.set(itemIndex, new ConcreteDecorator(this.items.get(itemIndex)));
        }
    }
}

abstract class Item {
    Point position;

    void translate(Point vector) {
        this.position.translate(vector);
    }
    Item(Point position) {
        this.position = position;
    }
    abstract void draw(Graphics g);
    abstract Point[] boundingBox();
    static Point[] getBoundingBoxFromList(ArrayList<Point> list) {
        int minX = list.get(0).x;
        int minY = list.get(0).y;
        int maxX = list.get(0).x;
        int maxY = list.get(0).y;
        for (Point point : list) {
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
        }
        Point p1 = new Point(minX, minY);
        Point p2 = new Point(minX, maxY);
        Point p3 = new Point(maxX, maxY);
        Point p4 = new Point(maxX, minY);
        return new Point[] {p1, p2, p3, p4};
    }
}

class Decorator extends Item {
    Item item;
    Decorator(Item item) {
        super(item.position);
        this.item = item;
    }
    void draw(Graphics g) {
        this.item.draw(g);
    }
    Point[] boundingBox() {
        return this.item.boundingBox();
    }
}

class ConcreteDecorator extends Decorator {
    ConcreteDecorator(Item item) {
        super(item);
    }
    void draw(Graphics g) {
        super.draw(g);
        // Show the bounding box
        Point[] boundingBox = super.boundingBox();
        g.drawRect(boundingBox[0].x, boundingBox[0].y, boundingBox[2].x - boundingBox[0].x, boundingBox[2].y - boundingBox[0].y);
    }
    Point[] boundingBox() {
        return super.boundingBox();
    }
}

class TextItem extends Item {
    String text;

    Point[] textBoundingBox;
    TextItem(Point position, String text) {
        super(position);
        this.text = text;
        this.textBoundingBox = new Point[] {position, position, position, position}; // fill textBoundingBox before it is calculated
    }
    Point[] boundingBox() {
        return this.textBoundingBox;
    }

    void draw(Graphics g) {
        g.drawString(this.text, this.position.x, this.position.y);
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(this.text, g);
        // set this.textBoundingBox to the bounds
        this.textBoundingBox = new Point[] {
            new Point(this.position.x, this.position.y),
            new Point(this.position.x, this.position.y + (int) bounds.getHeight()),
            new Point(this.position.x + (int) bounds.getWidth(), this.position.y + (int) bounds.getHeight()),
            new Point(this.position.x + (int) bounds.getWidth(), this.position.y)
        };
    }
}

abstract class Primitive extends Item {
    Primitive(Point position) {
        super(position);
    }
}

class Segment extends Primitive {
    Point end;
    Segment(Point start, Point end) {
        super(start);
        this.end = end;
    }
    int length() {
        return (int) Math.sqrt(Math.pow(this.position.x - this.end.x, 2) + Math.pow(this.position.y - this.end.y, 2));
    }

    void translate(Point vector) {
        super.translate(vector);
        this.end.translate(vector);
    }

    Point[] boundingBox() {
        return new Point[] {this.position, new Point(this.position.x, this.end.y), this.end, new Point(this.end.x, this.position.y)};
    }

    void draw(Graphics g) {
        g.drawLine(this.position.x, this.position.y, this.end.x, this.end.y);
    }
}

abstract class Shape extends Primitive {
    boolean filled;
    Shape(Point position, boolean filled) {
        super(position);
        this.filled = filled;
    }
}

class Circle extends Shape {
    int radius;
    Circle(Point position, boolean filled, int radius) {
        super(position, filled);
        this.radius = radius;
    }

    Point[] boundingBox() {
        Point p1 = new Point(this.position.x - this.radius, this.position.y - this.radius);
        Point p2 = new Point(this.position.x - this.radius, this.position.y + this.radius);
        Point p3 = new Point(this.position.x + this.radius, this.position.y + this.radius);
        Point p4 = new Point(this.position.x + this.radius, this.position.y - this.radius);
        return new Point[] {p1, p2, p3, p4};
    }

    void draw(Graphics g) {
        if (this.filled) {
            g.fillOval(this.position.x - this.radius, this.position.y - this.radius, this.radius * 2, this.radius * 2);
        } else {
            g.drawOval(this.position.x - this.radius, this.position.y - this.radius, this.radius * 2, this.radius * 2);
        }
    }
}

// objects implementing this interface can only have one instance
interface Singleton {}

class Triangle extends Shape implements Singleton{
    ArrayList<Point> points;
    Triangle(Point position, Point p1, Point p2, boolean filled) {
        super(position, filled);
        this.points = new ArrayList<>();
        this.points.add(position);
        this.points.add(p1);
        this.points.add(p2);
    }

    @Override
    void translate(Point vector) {
        super.translate(vector);
        for (Point point : this.points) {
            point.translate(vector);
        }
    }

    Point[] boundingBox() {
        return getBoundingBoxFromList(this.points);
    }

    void draw(Graphics g) {
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        for (int i = 0; i < 3; i++) {
            xPoints[i] = this.points.get(i).x;
            yPoints[i] = this.points.get(i).y;
        }
        if (this.filled) {
            g.fillPolygon(xPoints, yPoints, 3);
        } else {
            g.drawPolygon(xPoints, yPoints, 3);
        }
    }
}

class Rectangle extends Shape {
    int width;
    int height;
    Rectangle(Point position, boolean filled, int width, int height) {
        super(position,filled);
        this.width = width;
        this.height = height;
    }

    Point[] boundingBox() {
        Point p1 = new Point(this.position.x, this.position.y);
        Point p2 = new Point(this.position.x, this.position.y + this.height);
        Point p3 = new Point(this.position.x + this.width, this.position.y + this.height);
        Point p4 = new Point(this.position.x + this.width, this.position.y);
        return new Point[] {p1, p2, p3, p4};
    }

    void draw(Graphics g) {
        if (this.filled) {
            g.fillRect(this.position.x, this.position.y, this.width, this.height);
        } else {
            g.drawRect(this.position.x, this.position.y, this.width, this.height);
        }
    }
}

class customFigure extends Shape {
    ArrayList<Point> points;
    customFigure(Point position, boolean filled, ArrayList<Point> points) {
        super(position, filled);
        points.add(0,position);  // position is the first element of points
        this.points = points;
    }

    void translate(Point vector) {
        super.translate(vector); // translate position
        for (Point point : this.points) {
            point.translate(vector);
        }
    }

    Point[] boundingBox() {
        return getBoundingBoxFromList(this.points);
    }

    void draw(Graphics g) {
        int[] xPoints = new int[this.points.size()];
        int[] yPoints = new int[this.points.size()];
        for (int i = 0; i < this.points.size(); i++) {
            xPoints[i] = this.points.get(i).x;
            yPoints[i] = this.points.get(i).y;
        }
        if (this.filled) {
            g.fillPolygon(xPoints, yPoints, this.points.size());
        } else {
            g.drawPolygon(xPoints, yPoints, this.points.size());
        }
    }
}

class ComplexItem extends Item {
    ArrayList<Item> children;

    ComplexItem(Point position, ArrayList<Item> children) {
        super(position);
        this.children = children;
    }

    void translate(Point vector) {
        super.translate(vector);
        for (Item child : this.children) {
            child.translate(vector);
        }
    }

    Point[] boundingBox() {
        // biggest and smallest x and y of all children's bounding boxes
        int minX = this.children.get(0).boundingBox()[0].x;
        int minY = this.children.get(0).boundingBox()[0].y;
        int maxX = this.children.get(0).boundingBox()[0].x;
        int maxY = this.children.get(0).boundingBox()[0].y;
        for (Item child : this.children) {
            Point[] boundingBox = child.boundingBox();
            for (Point point : boundingBox) {
                minX = Math.min(minX, point.x);
                minY = Math.min(minY, point.y);
                maxX = Math.max(maxX, point.x);
                maxY = Math.max(maxY, point.y);
            }
        }
        Point p1 = new Point(minX, minY);
        Point p2 = new Point(minX, maxY);
        Point p3 = new Point(maxX, maxY);
        Point p4 = new Point(maxX, minY);
        return new Point[] {p1, p2, p3, p4};
    }

    void draw(Graphics g) {
        for (Item child : this.children) {
            child.draw(g);
        }
    }
}



