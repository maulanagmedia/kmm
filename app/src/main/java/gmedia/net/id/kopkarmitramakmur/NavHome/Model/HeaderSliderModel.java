package gmedia.net.id.kopkarmitramakmur.NavHome.Model;

/**
 * Created by Shin on 3/2/2017.
 */

public class HeaderSliderModel {

    private String id, urlGambar, title, keterangan;

    public HeaderSliderModel(String id, String gambar, String linkGambar, String keterangan) {
        this.id = id;
        this.urlGambar = gambar;
        this.title = linkGambar;
        this.keterangan = keterangan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlGambar() {
        return urlGambar;
    }

    public void setUrlGambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
