package com.example.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExcelDataListener<E> extends AnalysisEventListener<E> {

    private final List<E> dataList = new ArrayList<>();

    @Override
    public void invoke(E excelData, AnalysisContext analysisContext) {
        dataList.add(excelData);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<E> getDataList() {
        return dataList;
    }
}
