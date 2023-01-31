package ba.sum.fpmoz.bookapp.model;

public class Book {
    String title,description,author,url,image;
    String timestamp,viewsCount,downloadsCount;


    public Book() {
    }

    public Book(String title, String description, String author, String url, String image, String timestamp, String viewsCount, String downloadsCount) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.url = url;
        this.image = image;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
        this.downloadsCount = downloadsCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(String viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(String downloadsCount) {
        this.downloadsCount = downloadsCount;
    }
}
