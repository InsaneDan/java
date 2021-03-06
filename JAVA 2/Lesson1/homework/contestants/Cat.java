package Lesson1.homework.contestants;

public class Cat implements Contestant {

    private final String name;
    private final int runMaxDistance;
    private final double jumpMaxHeight;
    private final String kindName = Kinds.CAT.toString();
    // private boolean isOut = false; // OKrylov: не используется

    public Cat(String name, int runMaxDistance, double jumpMaxHeight) {
        this.name = name;
        this.runMaxDistance = runMaxDistance;
        this.jumpMaxHeight = jumpMaxHeight;
    }

    @Override
    public String getInfo() {
        return kindName + " " + name;
    }

    @Override
    public int run() {
        System.out.printf("%s %s может пробежать %d м. ", kindName, name, runMaxDistance);
        return runMaxDistance;
    }

    @Override
    public double jump() {
        System.out.printf("%s %s может прыгнуть на %.1f м. ", kindName, name, jumpMaxHeight);
        return jumpMaxHeight;
    }
}
