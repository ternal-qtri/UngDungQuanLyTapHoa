package com.example.apptaphoa.dao;

import com.example.apptaphoa.model.DoanhThu;
import java.util.Date;
import java.util.List;

public interface DoanhThuDAO{
    List<DoanhThu> DoanhThuTheoNgay (Date tuNgay, Date denNgay);
    DoanhThu findbyTime(Date ngay);
    List<DoanhThu> findAll();
}
