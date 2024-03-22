// Person.txt
public class Person {

    private int year = 0;

    public void getOlder(int year) {
        if(year <= 0) {
            return;
        }
        this.year += year;
    }
    public String eat() {
        return eat("chicken");
    }
    public String eat(String food) {
        if(year <= 1) {
            return "eating milk...";
        }
        return "eating " + food + "...";
    }
}