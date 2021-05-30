package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;

public class Save {
    private String name;
    private int height;
    private int width;
    private int solved;
    private int liked;
    private int start;

    public Save(String name, int height, int width, int solved, int liked, int start) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.solved = solved;
        this.liked = liked;
        this.start = start;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getSolved() {
        return solved;
    }

    public int getLiked() {
        return liked;
    }

    public int getStart() {
        return start;
    }
}
