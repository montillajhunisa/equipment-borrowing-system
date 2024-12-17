package Data_Objects;

import java.sql.Date;

public class Equipment {
    private String serialNumber;
    private String equipmentName;
    private String brandName;
    private Date dateAcquired;
    private String equipmentCondition;
    private String availability;

    public Equipment() {
        serialNumber = "";
        equipmentName = "";
        brandName = "";
        dateAcquired = null;
        equipmentCondition = "";
    }

    public Equipment(String serialNumber, String equipmentName, String brandName, Date dateAcquired, String equipmentCondition, String availability) {
        this.serialNumber = serialNumber;
        this.equipmentName = equipmentName;
        this.brandName = brandName;
        this.dateAcquired = dateAcquired;
        this.equipmentCondition = equipmentCondition;
        this.availability = availability;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getBrandName() {
        return brandName;
    }

    public Date getDateAcquired() {
        return dateAcquired;
    }

    public String getEquipmentCondition() {
        return equipmentCondition;
    }

    public String getAvailability() {
        return availability;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setDateAcquired(Date dateAcquired) {
        this.dateAcquired = dateAcquired;
    }

    public void setEquipmentCondition(String equipmentCondition) {
        this.equipmentCondition = equipmentCondition;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}

