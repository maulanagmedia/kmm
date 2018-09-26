package gmedia.net.id.kopkarmitramakmur.Util;

/**
 * Created by Shin on 4/18/2017.
 */

public class WebServiceURL {

    public static String baseURL = "http://kopkarapi.gmedia.bz/";
    //public static String baseURL = "http://kopkarapi.homitech.com/";

    public static String login = baseURL + "Auth/login/";
    public static String resetPassword = baseURL + "Auth/reset_password/";
    public static String getPeserta = baseURL + "Data_peserta/get_peserta/"; // nik
    public static String getSimpanan = baseURL + "Data_simpanan/get_simpanan/";
    public static String getTabungan = baseURL + "Data_tabungan/get_tabungan/";
    public static String getPotongan = baseURL + "Potongan/get_potongan_nik/";
    public static String getPinjaman = baseURL + "Pinjaman/get_pinjaman_nik/";
    public static String getTransaksi = baseURL + "Data_transaksi/get_transaksi_nik/";
    public static String getJaket = baseURL + "Jaket/get_jaket_nik/";
    public static String changePassword = baseURL + "Auth/change_password/";
    public static String getKMMProfile = baseURL + "Profil/get_profil/";

    public static String updateFCMID = baseURL + "Auth/change_fcm_id/";
    public static String getJenisTabungan = baseURL + "Data_tabungan/get_master_tabungan/";
    public static String getNoTabungan = baseURL + "Data_tabungan/get_notabungan/";

    public static String insertSetoran = baseURL + "Setoran/insert_data/";
    public static String insertPenarikan = baseURL + "Penarikan/insert_data/";
    public static String insertPinjaman = baseURL + "Pengajuan_pinjaman/insert_data/";

    public static String getSetoranByNIK = baseURL + "Setoran/get_data/";
    public static String getPenarikanByNIK = baseURL + "Penarikan/get_data/";
    public static String getPinjamanByNIK = baseURL + "Pengajuan_pinjaman/get_data/";

    public static String deleteSetoran = baseURL + "Setoran/delete_data/";
    public static String deletePenarikan = baseURL + "Penarikan/delete_data/";
    public static String deletePinjaman = baseURL + "Pengajuan_pinjaman/delete_data/";

    public static String getDetailStatus = baseURL + "Pengajuan_pinjaman/get_detail/";

    public static String getNews = baseURL + "News/get_all/";
    public static String getNewsActive = baseURL + "News/get_aktif/";
    public static String getListBarang = baseURL + "Data_barang/get_barang/";
    public static String insertCartBarang = baseURL + "Belanja/insert_data/";
    public static String getRiwayatBelanja = baseURL + "Belanja/get_data/";
    public static String getDetailRiwayatBelanja = baseURL + "Belanja/get_detail/";

    public static String deleteRiwayatBelanja = baseURL + "Belanja/delete_data/";

    public static String getLatestVersion = baseURL + "Mobile_version/get_latest_version";

    public static String getKategoriBarang = baseURL + "Data_barang/get_master_kategori";
    public static String downloadMutasiPDF = baseURL + "Data_transaksi/cetak_pdf";
    public static String deletePDF = baseURL + "Data_transaksi/del_data";

}
