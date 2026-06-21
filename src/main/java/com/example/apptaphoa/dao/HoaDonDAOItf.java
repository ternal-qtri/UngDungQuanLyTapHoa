package com.example.apptaphoa.dao;

import java.util.Date;
import java.util.List;
import com.example.apptaphoa.model.HoaDon;

public interface HoaDonDAOItf extends CrudDAO<HoaDon, String> {
    List<HoaDon> findByDateRange(Date tuNgay, Date denNgay);
}
