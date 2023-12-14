import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
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

abstract class Item {
    Point position;

    void translate(Point vector) {
        this.position.translate(vector);
    }
    Item(Point position) {
        this.position = position;
    }
    void draw() {
        //TODO
    }
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

class TextItem extends Item {
    String text;
    TextItem(Point position, String text) {
        super(position);
        this.text = text;
    }
    void draw() {
        //TODO: display text
    }
    Point[] boundingBox() {
        //TODO: bounding box based on text size
        return new Point[0];
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
}

class Triangle extends Shape {
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
}



