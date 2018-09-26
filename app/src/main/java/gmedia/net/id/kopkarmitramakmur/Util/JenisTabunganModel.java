package gmedia.net.id.kopkarmitramakmur.Util;

/**
 * Created by Shin on 4/25/2017.
 */

public class JenisTabunganModel {

    private String id, jenis;

    public JenisTabunganModel(String id, String no) {
        this.id = id;
        this.jenis = no;
    }

    public String toString(){
        return this.id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }
}
