package com.anbdevelopers.piechartgenerator;

public class DataList {
   public String labelText;
   public String value;
   public String colorCode;

    public DataList() {
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public DataList(String labelText, String value, String colorCode) {
        this.labelText = labelText;
        this.value = value;
        this.colorCode = colorCode;
    }
}
