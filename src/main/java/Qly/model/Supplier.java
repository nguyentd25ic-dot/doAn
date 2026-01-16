package Qly.model;

// Lưu thông tin nhà cung cấp dụng cụ y tế.
public class Supplier {
    private String supplierID;
    private String supplierName;
    private String phone;
    private String email;
    private String address;

    // Constructor rỗng để tiện bind dữ liệu từ database.
    public Supplier() {}

    // Khởi tạo nhà cung cấp với các thông tin liên hệ đầy đủ.
    public Supplier(String supplierID, String supplierName, String phone, String email, String address) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
