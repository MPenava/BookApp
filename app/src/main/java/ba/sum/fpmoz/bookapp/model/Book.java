package ba.sum.fpmoz.bookapp.model;

public class Book {
    private String title;
    private String description;
    private String author;
    private String pdf;

    public Book() {
    }

    public Book(String title, String description, String author, String pdf) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.pdf = pdf;
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

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
}
