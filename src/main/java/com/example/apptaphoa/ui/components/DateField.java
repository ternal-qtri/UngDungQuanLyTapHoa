package com.example.apptaphoa.ui.components;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/** Ô chọn ngày định dạng dd/MM/yyyy. */
public final class DateField extends JSpinner {

    public DateField(LocalDate initial) {
        super(new SpinnerDateModel());
        setEditor(new JSpinner.DateEditor(this, "dd/MM/yyyy"));
        setValue(initial);
    }

    public LocalDate value() {
        Date date = (Date) getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setValue(LocalDate date) {
        super.setValue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
}
