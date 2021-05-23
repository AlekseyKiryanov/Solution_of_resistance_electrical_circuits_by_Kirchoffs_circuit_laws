package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;

public class Save {
    private String name;
    private int height;
    private int width;
    private int solved;
    private String visibly_name;
    private int liked;

    public Save(String name, int height, int width, int solved, String visibly_name, int liked) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.solved = solved;
        this.visibly_name = visibly_name;
        this.liked = liked;
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

    public String getVisibly_name() {
        return visibly_name;
    }

    public int getLiked() {
        return liked;
    }
}
