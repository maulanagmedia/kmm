package gmedia.net.id.kopkarmitramakmur.Util;

/**
 * Created by Shin on 4/19/2017.
 */

public class Converter {

    public static ItemValidation iv = new ItemValidation();

    public static String convertBulan(String s){

        int bulan = iv.parseNullInteger(s);
        switch (bulan){
            case 1:
                return "Januari";
            case 2:
                return "Februari";
            case 3:
                return "Maret";
            case 4:
                return "April";
            case 5:
                return "Mei";
            case 6:
                return "Juni";
            case 7:
                return "Juli";
            case 8:
                return "Agustus";
            case 9:
                return "September";
            case 10:
                return "Oktober";
            case 11:
                return "November";
            case 12:
                return "Desember";
            default:
                return "Januari";
        }
    }

    public static int convertJenisTabungan(String jenis){

        jenis = jenis.toLowerCase();
        int hasil = 999;
        switch (jenis){
            case "simpanan sukarela":
                hasil = 1;
                break;
            case "simpanan pendidikan":
                hasil = 2;
                break;
            case "simpanan brj":
                hasil = 3;
                break;
            case "simpanan rekreasi":
                hasil = 4;
                break;
            case "simpanan ibadah":
                hasil = 5;
                break;
            case "sukarela":
                hasil = 1;
                break;
            case "pendidikan":
                hasil = 2;
                break;
            case "brj":
                hasil = 3;
                break;
            case "rekreasi":
                hasil = 4;
                break;
            case "ibadah":
                hasil = 5;
                break;
            default:
                hasil = 0;
        }

        if(hasil == 0){
            if(jenis.contains("sukarela")){
                hasil = 1;
            }else if(jenis.contains("pendidikan")){
                hasil = 2;
            }else if(jenis.contains("brj")){
                hasil = 3;
            }else if(jenis.contains("rekreasi")){
                hasil = 4;
            }else if(jenis.contains("ibadah")){
                hasil = 5;
            }
        }

        return hasil;
    }
}
