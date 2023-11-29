package my.greenCarson.administradorCentros;

public class Item {
    private String name;
    private String imageUrl;

    public Item(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
