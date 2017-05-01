package com.vne.medicinealert.Modal;

import java.sql.Timestamp;

/**
 * Created by Volkan Şahin on 19.04.2017.
 */

public class Medicine {
    private String medicineName;
    private String medicineTime;

    public Medicine() {

    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineTime() {
        return medicineTime;
    }

    public void setMedicineTime(String medicineTime) {
        this.medicineTime = medicineTime;
    }
}
