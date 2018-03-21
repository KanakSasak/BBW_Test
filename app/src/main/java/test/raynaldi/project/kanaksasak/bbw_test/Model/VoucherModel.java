package test.raynaldi.project.kanaksasak.bbw_test.Model;

/**
 * Created by IT on 3/21/2018.
 */

public class VoucherModel {

    String pulsa, harga;

    public VoucherModel() {
    }

    public VoucherModel(String pulsa, String harga) {
        this.pulsa = pulsa;
        this.harga = harga;
    }

    public String getPulsa() {
        return pulsa;
    }

    public void setPulsa(String pulsa) {
        this.pulsa = pulsa;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }
}
